package ng.codehaven.eko.services;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ng.codehaven.eko.Application;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.utils.UrlUtil;

public class ContactsSyncAdapterService extends Service {
    private static SyncAdapterImpl sSyncAdapter = null;
    private static ContentResolver mContentResolver = null;
    private static String UsernameColumn = ContactsContract.RawContacts.SYNC1;
    private static String PhotoUrlColumn = ContactsContract.RawContacts.SYNC2;
    private static String PhotoTimestampColumn = ContactsContract.RawContacts.SYNC3;
    private static Integer syncSchema = 2;

    protected static ParseRelation<ParseUser> mFriendsRelation;
    protected static ParseUser mCurrentUser;
    protected static List<ParseUser> mFriends = null;

    public ContactsSyncAdapterService() {
        super();
    }

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private Context mContext;

        public SyncAdapterImpl(Context context) {
            super(context, true);
            mContext = context;
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            try {
                ContactsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);
            } catch (OperationCanceledException e) {
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        ret = getSyncAdapter().getSyncAdapterBinder();
        return ret;
    }

    private SyncAdapterImpl getSyncAdapter() {
        if (sSyncAdapter == null)
            sSyncAdapter = new SyncAdapterImpl(this);
        return sSyncAdapter;
    }

    private static long addContact(Account account, String name, String username) {
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
        builder.withValue(RawContacts.ACCOUNT_NAME, account.name);
        builder.withValue(RawContacts.ACCOUNT_TYPE, account.type);
        builder.withValue(UsernameColumn, username);
        operationList.add(builder.build());

        if (name.length() > 0 && PreferenceManager.getDefaultSharedPreferences(Application.getInstance()).getBoolean("sync_names", true)) {
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
            operationList.add(builder.build());
        } else {
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            builder.withValueBackReference(ContactsContract.CommonDataKinds.Nickname.RAW_CONTACT_ID, 0);
            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.Nickname.NAME, username);
            operationList.add(builder.build());
        }

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.fm.last.android.profile");
        builder.withValue(ContactsContract.Data.DATA1, username);
        builder.withValue(ContactsContract.Data.DATA2, "Last.fm Profile");
        builder.withValue(ContactsContract.Data.DATA3, "View profile");
        operationList.add(builder.build());

        if (!PreferenceManager.getDefaultSharedPreferences(Application.getInstance()).getBoolean("sync_website", true)) {
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_PROFILE);
            builder.withValue(ContactsContract.CommonDataKinds.Website.URL, "http://www.eko.ng/user/" + username);
            operationList.add(builder.build());
        }

        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
            // Load the local Last.fm contacts
            Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name).appendQueryParameter(
                    RawContacts.ACCOUNT_TYPE, account.type).build();
            Cursor c1 = mContentResolver.query(rawContactUri, new String[]{BaseColumns._ID, UsernameColumn}, UsernameColumn + " = '" + username + "'", null, null);
            if (c1.moveToNext()) {
                return c1.getLong(0);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return -1;
    }

    private static void updateContactName(ArrayList<ContentProviderOperation> operationList, long rawContactId, String name, String username) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.RAW_CONTACT_ID + " = '" + rawContactId
                + "' AND (" + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"
                + " OR " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "')", null);
        operationList.add(builder.build());

        if (name.length() > 0 && PreferenceManager.getDefaultSharedPreferences(Application.getInstance()).getBoolean("sync_names", true)) {
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, rawContactId);
            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
            operationList.add(builder.build());
        } else {
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            builder.withValue(ContactsContract.CommonDataKinds.Nickname.RAW_CONTACT_ID, rawContactId);
            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.Nickname.NAME, username);
            operationList.add(builder.build());
        }
    }

    private static void updateContactPhoto(ArrayList<ContentProviderOperation> operationList, long rawContactId, String url) {
        byte[] image;
        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.RAW_CONTACT_ID + " = '" + rawContactId
                + "' AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null);
        operationList.add(builder.build());

        if (!PreferenceManager.getDefaultSharedPreferences(Application.getInstance()).getBoolean("sync_icons", true)) {
            return;
        }

        try {
            if (url != null && url.length() > 0) {
                image = UrlUtil.doGetAndReturnBytes(new URL(url), 1048576);
                if (image != null && image.length > 0) {
                    builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
                    builder.withValue(ContactsContract.CommonDataKinds.Photo.RAW_CONTACT_ID, rawContactId);
                    builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                    builder.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, image);
                    operationList.add(builder.build());

                    builder = ContentProviderOperation.newUpdate(ContactsContract.RawContacts.CONTENT_URI);
                    builder.withSelection(ContactsContract.RawContacts.CONTACT_ID + " = '" + rawContactId + "'", null);
                    builder.withValue(PhotoUrlColumn, url);
                    builder.withValue(PhotoTimestampColumn, String.valueOf(System.currentTimeMillis()));
                    operationList.add(builder.build());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void deleteContact(Context context, long rawContactId) {
        Uri uri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId).buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
        ContentProviderClient client = context.getContentResolver().acquireContentProviderClient(ContactsContract.AUTHORITY_URI);
        try {
            client.delete(uri, null, null);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        client.release();
    }

    private static class SyncEntry {
        public Long raw_id = 0L;
        public String photo_url = null;
        public Long photo_timestamp = null;
    }

    private static void performSync(Context context, final Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
            throws OperationCanceledException {
        final HashMap<String, SyncEntry> localContacts = new HashMap<String, SyncEntry>();
        ArrayList<String> ekoFriends = new ArrayList<String>();
        mContentResolver = context.getContentResolver();

        //If our app has requested a full sync, we're going to delete all our local contacts and start over
        boolean is_full_sync = PreferenceManager.getDefaultSharedPreferences(Application.getInstance()).getBoolean("do_full_sync", false);

        //If our schema is out-of-date, do a fresh sync
        if (PreferenceManager.getDefaultSharedPreferences(Application.getInstance()).getInt("sync_schema", 0) < syncSchema)
            is_full_sync = true;

        // Load the local Last.fm contacts
        Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name).appendQueryParameter(
                RawContacts.ACCOUNT_TYPE, account.type).build();
        Cursor c1 = mContentResolver.query(rawContactUri, new String[]{BaseColumns._ID, UsernameColumn, PhotoUrlColumn, PhotoTimestampColumn}, null, null, null);
        while (c1 != null && c1.moveToNext()) {
            if (is_full_sync) {
                deleteContact(context, c1.getLong(0));
            } else {
                SyncEntry entry = new SyncEntry();
                entry.raw_id = c1.getLong(0);
                entry.photo_url = c1.getString(2);
                entry.photo_timestamp = c1.getLong(3);
                localContacts.put(c1.getString(1), entry);
            }
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Application.getInstance()).edit();
        editor.remove("do_full_sync");
        editor.putInt("sync_schema", syncSchema);
        editor.commit();

//        LastFmServer server = AndroidLastFmServerFactory.getServer();

        mFriends = new ArrayList<ParseUser>();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(Constants.KEY_FRIENDS_RELATION);

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                mFriends = friends;

                mFriends.add(mCurrentUser);

                for (ParseUser friend : mFriends) {
                    if (!localContacts.containsKey(friend.getUsername())) {
                        long id = addContact(account, friend.getString("full_name"), friend.getUsername());
                        if (id != -1) {
                            SyncEntry entry = new SyncEntry();
                            entry.raw_id = id;
                            localContacts.put(friend.getUsername(), entry);
                        }
                    }
                }

            }
        });


        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        for (ParseUser user : mFriends) {
            String username = user.getUsername();
            ekoFriends.add(username);

            if (localContacts.containsKey(username)) {
                SyncEntry entry = localContacts.get(username);

                if (entry.photo_timestamp == null || System.currentTimeMillis() > (entry.photo_timestamp + 604800000L)) {
                    String url = null;
                    if (!user.getParseFile("avatar").getUrl().isEmpty())
                        url = user.getParseFile("avatar").getUrl();
                    if (!entry.photo_url.equals(url))
                        updateContactPhoto(operationList, entry.raw_id, url);
                }
            }

            if (operationList.size() > 0) {
                try {
                    mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                operationList.clear();
            }
        }

        if (operationList.size() > 0) {
            try {
                mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (String user : localContacts.keySet()) {
            if (!ekoFriends.contains(user))
                deleteContact(context, localContacts.get(user).raw_id);
        }
    }
}

package ng.codehaven.eko.adapters;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SyncResult;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import ng.codehaven.eko.models.Contact;
import ng.codehaven.eko.utils.ContactFetcher;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.UIUtils;

/**
 * Created by Thompson on 09/03/2015.
 */
public class ContactsSyncAdapter extends AbstractThreadedSyncAdapter {

    private final AccountManager mAccountManager;
    private final Context mContext;
    private static ContentResolver mContentResolver = null;

    Phonenumber.PhoneNumber locale;
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    ArrayList<Contact> contacts;
    JSONArray friends;

    public ContactsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        mContext = context;

        contacts = new ContactFetcher(this.mContext).fetchAll();

        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        performSync(mContext, account, extras, authority, provider, syncResult);
    }

    private void performSync(Context mContext, final Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        for (int i = 1; i < contacts.size(); i++) {
            final Contact c = contacts.get(i);
            if (c.numbers.size() > 0) {
                try {
                    doQuery(account, c);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doQuery(final Account account, Contact c) throws NullPointerException, JSONException {
        String newNumber;
        try {
            locale = phoneUtil.parse(c.numbers.get(0).number, UIUtils.getCountryIso(mContext));
        } catch (NumberParseException e) {
            e.printStackTrace();
            locale = null;
        }

        newNumber = "+" + (locale != null ? locale.getCountryCode() : 0) + (locale != null ? locale.getNationalNumber() : 0);


        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("localFriends", 0);
        final String getFriends = sharedPreferences.getString("friends", "");

        Logger.m("Friends list " + getFriends);

        ParseQuery<ParseUser> contact = ParseUser.getQuery();
        contact.whereStartsWith("phone", newNumber);
        contact.whereNotContainedIn("username", Arrays.asList(getFriends.split(",")));
        contact.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    Logger.m(String.valueOf(contacts.size()));
                    Logger.m(parseUser.getUsername());
                    Editor editor = sharedPreferences.edit();

                    if (getFriends.isEmpty()) {
                        editor.putString("friends", parseUser.getUsername());
                    }else {
                        editor.putString("friends", getFriends+","+parseUser.getUsername());
                    }

                    editor.apply();

                    doContactOp(parseUser, account);
                } else {
                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND){
                        Logger.m(e.getMessage());
                    }
                }
            }
        });
    }

    private void doContactOp(ParseUser parseUser, Account account) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);

        builder.withValue(RawContacts.ACCOUNT_NAME, account.name);
        builder.withValue(RawContacts.ACCOUNT_TYPE, account.type);
        builder.withValue(RawContacts.SYNC1, parseUser.getUsername());

        ops.add(builder.build());


        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, parseUser.getString("full_name"));
        ops.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.ng.codehaven.eko.profile");
        builder.withValue(ContactsContract.Data.DATA1, parseUser.getUsername());
        builder.withValue(ContactsContract.Data.DATA2, "Eko Profile");
        builder.withValue(ContactsContract.Data.DATA3, "Send Funds");

        ops.add(builder.build());

        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            Logger.m("success");
        } catch (Exception e1) {
            e1.printStackTrace();

        }
    }
}

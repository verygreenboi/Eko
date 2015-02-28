package ng.codehaven.eko.services;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;

import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

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

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        return ret;
    }

}

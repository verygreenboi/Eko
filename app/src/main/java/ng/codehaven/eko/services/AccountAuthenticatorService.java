package ng.codehaven.eko.services;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.AccountFailActivity;
import ng.codehaven.eko.ui.activities.AuthActivity;
import ng.codehaven.eko.utils.MD5;

public class AccountAuthenticatorService extends Service {
    private static final String TAG = "AccountAuthenticatorService";
    private static AccountAuthenticatorImpl sAccountAuthenticator = null;

    public AccountAuthenticatorService() {
        super();
    }

    private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
        private Context mContext;

        public AccountAuthenticatorImpl(Context context) {
            super(context);
            mContext = context;
        }

        public static Bundle addAccount(Context ctx, String username, String password) {
            Bundle result = null;
            Account account = new Account(username, ctx.getString(R.string.ACCOUNT_TYPE));
            AccountManager am = AccountManager.get(ctx);
            if (am.addAccountExplicitly(account, MD5.getInstance().hash(password), null)) {
                result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            }
            return result;
        }

        public static Boolean hasEkoAccount(Context ctx) {
            AccountManager am = AccountManager.get(ctx);
            Account[] accounts = am.getAccountsByType(ctx.getString(R.string.ACCOUNT_TYPE));
            if(accounts != null && accounts.length > 0)
                return true;
            else
                return false;
        }

        public static void removeLastfmAccount(Context ctx) {
            AccountManager am = AccountManager.get(ctx);
            Account[] accounts = am.getAccountsByType(ctx.getString(R.string.ACCOUNT_TYPE));
            for(Account account : accounts) {
                am.removeAccount(account, null, null);
            }
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#addAccount(android.accounts.AccountAuthenticatorResponse, java.lang.String, java.lang.String, java.lang.String[], android.os.Bundle)
         */
        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {
            Bundle result;

            if(hasEkoAccount(mContext)) {
                result = new Bundle();
                Intent i = new Intent(mContext, AccountFailActivity.class);
                i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                result.putParcelable(AccountManager.KEY_INTENT, i);
                return result;
            } else {
                result = new Bundle();
                Intent i = new Intent(mContext, AuthActivity.class);
                i.setAction("ng.codehaven.eko.sync.LOGIN");
                i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                result.putParcelable(AccountManager.KEY_INTENT, i);
            }
            return result;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#confirmCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, android.os.Bundle)
         */
        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
            // TODO Auto-generated method stub
            Log.i(TAG, "confirmCredentials");
            return null;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#editProperties(android.accounts.AccountAuthenticatorResponse, java.lang.String)
         */
        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            // TODO Auto-generated method stub
            Log.i(TAG, "editProperties");
            return null;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#getAuthToken(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, java.lang.String, android.os.Bundle)
         */
        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
//            String api_key = options.getString("api_key");
//            String api_secret = options.getString("api_secret");
//
//            AccountManager am = AccountManager.get(mContext);
//            String user = account.name.toLowerCase().trim();
//            String md5Password = am.getPassword(account);
//            String authToken = MD5.getInstance().hash(user + md5Password);
//
//            Bundle result = new Bundle();
//            Intent i = new Intent(mContext, AccountAccessPrompt.class);
//            i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
//            i.putExtra("api_key", api_key);
//            i.putExtra("api_secret", api_secret);
//            i.putExtra("user", user);
//            i.putExtra("authToken", authToken);
//            result.putParcelable(AccountManager.KEY_INTENT, i);
            return null;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#getAuthTokenLabel(java.lang.String)
         */



        @Override
        public String getAuthTokenLabel(String authTokenType) {
            // TODO Auto-generated method stub
            Log.i(TAG, "getAuthTokenLabel");
            return null;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#hasFeatures(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, java.lang.String[])
         */
        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
            // TODO Auto-generated method stub
            Log.i(TAG, "hasFeatures: " + features);
            return null;
        }

        /* (non-Javadoc)
         * @see android.accounts.AbstractAccountAuthenticator#updateCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, java.lang.String, android.os.Bundle)
         */
        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
            // TODO Auto-generated method stub
            Log.i(TAG, "updateCredentials");
            return null;
        }
    }

    public static void addAccount(Context ctx, String username, String password, Parcelable response) {
        AccountAuthenticatorResponse authResponse = (AccountAuthenticatorResponse)response;
        Bundle result = AccountAuthenticatorImpl.addAccount(ctx, username, password);
        if(authResponse != null)
            authResponse.onResult(result);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
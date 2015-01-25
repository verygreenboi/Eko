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
import android.support.annotation.NonNull;

public class AuthenticatorService extends Service {

    private static class Authenticator extends AbstractAccountAuthenticator{
        private final Context context;

        public Authenticator(Context context) {
            super(context);
            this.context = context;
        }

        /**
         * Adds an account of the specified accountType.
         *
         * @param response         to send the result back to the AccountManager, will never be null
         * @param accountType      the type of account to add, will never be null
         * @param authTokenType    the type of auth token to retrieve after adding the account, may be null
         * @param requiredFeatures a String array of authenticator-specific features that the added
         *                         account must support, may be null
         * @param options          a Bundle of authenticator-specific options, may be null
         * @return a Bundle result or null if the result is to be returned via the response. The result
         * will contain either:
         * <ul>
         * <li> {@link AccountManager#KEY_INTENT}, or
         * <li> {@link AccountManager#KEY_ACCOUNT_NAME} and {@link AccountManager#KEY_ACCOUNT_TYPE} of
         * the account that was added, or
         * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
         * indicate an error
         * </ul>
         * @throws android.accounts.NetworkErrorException if the authenticator could not honor the request due to a
         *                                                network error
         */
        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
            return null;
        }

        @NonNull
        @Override
        public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account) throws NetworkErrorException {
            return super.getAccountRemovalAllowed(response, account);
        }

        /**
         * Checks that the user knows the credentials of an account.
         *
         * @param response to send the result back to the AccountManager, will never be null
         * @param account  the account whose credentials are to be checked, will never be null
         * @param options  a Bundle of authenticator-specific options, may be null
         * @return a Bundle result or null if the result is to be returned via the response. The result
         * will contain either:
         * <ul>
         * <li> {@link AccountManager#KEY_INTENT}, or
         * <li> {@link AccountManager#KEY_BOOLEAN_RESULT}, true if the check succeeded, false otherwise
         * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
         * indicate an error
         * </ul>
         * @throws android.accounts.NetworkErrorException if the authenticator could not honor the request due to a
         *                                                network error
         */
        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
            return null;
        }

        /**
         * Returns a Bundle that contains the Intent of the activity that can be used to edit the
         * properties. In order to indicate success the activity should call response.setResult()
         * with a non-null Bundle.
         *
         * @param response    used to set the result for the request. If the Constants.INTENT_KEY
         *                    is set in the bundle then this response field is to be used for sending future
         *                    results if and when the Intent is started.
         * @param accountType the AccountType whose properties are to be edited.
         * @return a Bundle containing the result or the Intent to start to continue the request.
         * If this is null then the request is considered to still be active and the result should
         * sent later using response.
         */
        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            return null;
        }

        /**
         * Gets the authtoken for an account.
         *
         * @param response      to send the result back to the AccountManager, will never be null
         * @param account       the account whose credentials are to be retrieved, will never be null
         * @param authTokenType the type of auth token to retrieve, will never be null
         * @param options       a Bundle of authenticator-specific options, may be null
         * @return a Bundle result or null if the result is to be returned via the response. The result
         * will contain either:
         * <ul>
         * <li> {@link AccountManager#KEY_INTENT}, or
         * <li> {@link AccountManager#KEY_ACCOUNT_NAME}, {@link AccountManager#KEY_ACCOUNT_TYPE},
         * and {@link AccountManager#KEY_AUTHTOKEN}, or
         * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
         * indicate an error
         * </ul>
         * @throws android.accounts.NetworkErrorException if the authenticator could not honor the request due to a
         *                                                network error
         */
        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            return null;
        }

        /**
         * Ask the authenticator for a localized label for the given authTokenType.
         *
         * @param authTokenType the authTokenType whose label is to be returned, will never be null
         * @return the localized label of the auth token type, may be null if the type isn't known
         */
        @Override
        public String getAuthTokenLabel(String authTokenType) {
            return null;
        }

        /**
         * Checks if the account supports all the specified authenticator specific features.
         *
         * @param response to send the result back to the AccountManager, will never be null
         * @param account  the account to check, will never be null
         * @param features an array of features to check, will never be null
         * @return a Bundle result or null if the result is to be returned via the response. The result
         * will contain either:
         * <ul>
         * <li> {@link AccountManager#KEY_INTENT}, or
         * <li> {@link AccountManager#KEY_BOOLEAN_RESULT}, true if the account has all the features,
         * false otherwise
         * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
         * indicate an error
         * </ul>
         * @throws android.accounts.NetworkErrorException if the authenticator could not honor the request due to a
         *                                                network error
         */
        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
            return null;
        }

        /**
         * Update the locally stored credentials for an account.
         *
         * @param response      to send the result back to the AccountManager, will never be null
         * @param account       the account whose credentials are to be updated, will never be null
         * @param authTokenType the type of auth token to retrieve after updating the credentials,
         *                      may be null
         * @param options       a Bundle of authenticator-specific options, may be null
         * @return a Bundle result or null if the result is to be returned via the response. The result
         * will contain either:
         * <ul>
         * <li> {@link AccountManager#KEY_INTENT}, or
         * <li> {@link AccountManager#KEY_ACCOUNT_NAME} and {@link AccountManager#KEY_ACCOUNT_TYPE} of
         * the account that was added, or
         * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
         * indicate an error
         * </ul>
         * @throws android.accounts.NetworkErrorException if the authenticator could not honor the request due to a
         *                                                network error
         */
        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            return null;
        }
    }

    private static Authenticator authenticator = null;

    protected Authenticator getAuthenticator() {
        if (authenticator == null) {
            authenticator = new Authenticator(this);
        }
        return authenticator;
    }

    public AuthenticatorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            return getAuthenticator().getIBinder();
        } else {
            return null;
        }
    }
}

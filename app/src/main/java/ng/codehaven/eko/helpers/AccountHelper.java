package ng.codehaven.eko.helpers;

import android.accounts.AccountManager;
import android.content.Context;

/**
 * Created by Thompson on 27/02/2015.
 */
public class AccountHelper {

    private Context mContext;
    private AccountManager mAccountManager;

    public AccountHelper(Context context) {
        this.mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    public void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {

    }

}

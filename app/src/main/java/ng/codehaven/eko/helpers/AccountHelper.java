package ng.codehaven.eko.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import ng.codehaven.eko.R;

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

    public static boolean hasAccount(Context ctx) {
        Account[] accounts = getAccountManager(ctx).getAccountsByType(ctx.getString(R.string.ACCOUNT_TYPE));
        return accounts != null && accounts.length > 0;
    }

    public static void removeAccount(Context ctx) {
        Account[] accounts = getAccountManager(ctx).getAccountsByType(ctx.getString(R.string.ACCOUNT_TYPE));
        for(Account account : accounts) {
            getAccountManager(ctx).removeAccount(account, null, null);
        }
    }

    public static Account getAccount(Context ctx) {
        Account[] accounts = getAccountManager(ctx).getAccountsByType(ctx.getString(R.string.ACCOUNT_TYPE));
        if (accounts != null && accounts.length > 0 ){
            return accounts[0];
        }
        return null;
    }

    private static AccountManager getAccountManager(Context c){
        return AccountManager.get(c);
    }
}

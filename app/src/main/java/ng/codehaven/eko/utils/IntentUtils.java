package ng.codehaven.eko.utils;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import ng.codehaven.eko.Constants;
import ng.codehaven.eko.helpers.AccountHelper;
import ng.codehaven.eko.ui.activities.AuthActivity;

/**
 * Created by mrsmith on 11/27/14.
 * Intent Utility
 */
public class IntentUtils {
    private static Intent i;
    private static PendingIntent pIntent;
    private static ParseUser mCurrentUser = ParseUser.getCurrentUser();
    private static AccountManager mAccountManager;

    public static void startActivity(Context ctx, Class activity) {
        i = new Intent(ctx, activity);
        ctx.startActivity(i);
    }

    public static void startActivityWithJSON(Context ctx, JSONObject jsonObject, Class activity) {
        i = new Intent(ctx, activity);
        i.putExtra("jsonObject", jsonObject.toString());
        ctx.startActivity(i);
    }

    public static void startActivityWithStringExtra(Context ctx, Class activity, String key, String extra) {
        i = new Intent(ctx, activity);
        i.putExtra(key, extra);
        ctx.startActivity(i);
    }

    public static void sendBroadcast(Context ctx, String intentFilter) {
        i = new Intent(intentFilter);
        ctx.sendBroadcast(i);
    }

    public static void logout(Context ctx, Activity a) {
        ParseUser.logOut();
        mCurrentUser = null;
        if (AccountHelper.hasAccount(ctx)) {
            AccountHelper.removeAccount(ctx);
        }
        SharedPreferences isSeenPref = ctx.getSharedPreferences(Constants.ACCOUNT_TYPE, 0);
        boolean isSeen = isSeenPref.getBoolean("seen", false);

        if (isSeen) {
            getTokenForAccountCreateIfNeeded(a, Constants.ACCOUNT_TYPE, Constants.AUTHTOKEN_TYPE_FULL_ACCESS);
        } else {
            startActivity(ctx, AuthActivity.class);
        }
    }

    public static void startActivityWithParseObject(Context ctx, ParseObject business, Class activity) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("title", business.get("title"));
        obj.put("id", business.getObjectId());
        obj.put("isTransport", business.get("type") == 1);
        obj.put("logoUrl", business.getParseFile("logo").getUrl());
        i = new Intent(ctx, activity);
        i.putExtra("jsonObject", obj.toString());
        ctx.startActivity(i);

    }

    private static void getTokenForAccountCreateIfNeeded(Activity c, String accountType, String authTokenType) {
        mAccountManager = AccountManager.get(c);
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, c, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();
                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                            showMessage(((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL"));
//                            Log.d("udinic", "GetTokenForAccount Bundle is " + bnd);

                        } catch (Exception e) {
                            e.printStackTrace();
//                            showMessage(e.getMessage());
                        }
                    }
                }
                , null);
    }
}

package ng.codehaven.eko.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import ng.codehaven.eko.ui.activities.AuthActivity;

/**
 * Created by mrsmith on 11/27/14.
 * Intent Utility
 */
public class IntentUtils {
    private static Intent i;
    private static PendingIntent pIntent;
    private static ParseUser mCurrentUser = ParseUser.getCurrentUser();

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

    public static void logout(Context ctx) {
        ParseUser.logOut();
        mCurrentUser = ParseUser.getCurrentUser();
        startActivity(ctx, AuthActivity.class);
    }

    public static void startActivityWithParseObject(Context ctx, ParseObject business, Class activity) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("title", business.get("title"));
        obj.put("id", business.getObjectId());
        obj.put("isTransport", business.get("type")==1);
        obj.put("logoUrl", business.getParseFile("logo").getUrl());
        i = new Intent(ctx, activity);
        i.putExtra("jsonObject", obj.toString());
        ctx.startActivity(i);

    }
}

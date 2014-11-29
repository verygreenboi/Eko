package ng.codehaven.eko.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

/**
 * Created by mrsmith on 11/27/14.
 * Intent Utility
 */
public class IntentUtils {
    private static Intent i;
    private static PendingIntent pIntent;

    public static void startActivity(Context ctx, Class activity){
        i = new Intent(ctx, activity);
        ctx.startActivity(i);
    }

    public static void startActivityWithJSON(Context ctx, JSONObject user, Class activity) {
        i = new Intent(ctx, activity);
        i.putExtra("user", user.toString());
        ctx.startActivity(i);
    }

    public static void startActivityWithStringExtra(Context ctx, Class activity, String key, String extra){
        i = new Intent(ctx, activity);
        i.putExtra(key, extra);
        ctx.startActivity(i);
    }

}

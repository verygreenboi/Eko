package ng.codehaven.eko.utils;

import android.content.Context;

import com.parse.ParseUser;

import ng.codehaven.eko.BuildType;
import ng.codehaven.eko.Constants;

/**
 * Created by Thompson on 16/01/2015.
 */
public class UIUtils {

    public static String getImageUrl(String id, Context ctx) {
        String email;
        String hash;
        String url;

        if (BuildType.type == 0) {
            email = "debug-" + id + "-" + Utils.IMEI(ctx) + "@eko.ng";
        } else {
            email = ParseUser.getCurrentUser().getEmail();
        }

        hash = MD5Util.md5Hex(email);

        url = Constants.KEY_GRAVATAR_URL + hash + "?d=identicon";

        return url;
    }

}

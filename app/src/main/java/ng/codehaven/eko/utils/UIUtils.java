package ng.codehaven.eko.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import ng.codehaven.eko.BuildType;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;

/**
 * Created by Thompson on 16/01/2015.
 */
public class UIUtils {

    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static String getImageUrl(String id, Context ctx) {
        String email;
        String hash;
        String url;

        if (BuildType.type == 0) {
            email = "debug-" + id + "-" + Utils.IMEI(ctx) + "@eko.ng";
            hash = MD5Util.md5Hex(email);

            url = Constants.KEY_GRAVATAR_URL + hash + "?d=wavatar";
        } else {
            url = id;
        }


        return url;
    }

    public static String unescape(String s) {
        StringBuffer sbuf = new StringBuffer () ;
        int l  = s.length() ;
        int ch = -1 ;
        int b, sumb = 0;
        for (int i = 0, more = -1 ; i < l ; i++) {
      /* Get next byte b from URL segment s */
            switch (ch = s.charAt(i)) {
                case '%':
                    ch = s.charAt (++i) ;
                    int hb = (Character.isDigit ((char) ch)
                            ? ch - '0'
                            : 10+Character.toLowerCase((char) ch) - 'a') & 0xF ;
                    ch = s.charAt (++i) ;
                    int lb = (Character.isDigit ((char) ch)
                            ? ch - '0'
                            : 10+Character.toLowerCase ((char) ch)-'a') & 0xF ;
                    b = (hb << 4) | lb ;
                    break ;
                case '+':
                    b = ' ' ;
                    break ;
                default:
                    b = ch ;
            }
      /* Decode byte b as UTF-8, sumb collects incomplete chars */
            if ((b & 0xc0) == 0x80) {			// 10xxxxxx (continuation byte)
                sumb = (sumb << 6) | (b & 0x3f) ;	// Add 6 bits to sumb
                if (--more == 0) sbuf.append((char) sumb) ; // Add char to sbuf
            } else if ((b & 0x80) == 0x00) {		// 0xxxxxxx (yields 7 bits)
                sbuf.append((char) b) ;			// Store in sbuf
            } else if ((b & 0xe0) == 0xc0) {		// 110xxxxx (yields 5 bits)
                sumb = b & 0x1f;
                more = 1;				// Expect 1 more byte
            } else if ((b & 0xf0) == 0xe0) {		// 1110xxxx (yields 4 bits)
                sumb = b & 0x0f;
                more = 2;				// Expect 2 more bytes
            } else if ((b & 0xf8) == 0xf0) {		// 11110xxx (yields 3 bits)
                sumb = b & 0x07;
                more = 3;				// Expect 3 more bytes
            } else if ((b & 0xfc) == 0xf8) {		// 111110xx (yields 2 bits)
                sumb = b & 0x03;
                more = 4;				// Expect 4 more bytes
            } else /*if ((b & 0xfe) == 0xfc)*/ {	// 1111110x (yields 1 bit)
                sumb = b & 0x01;
                more = 5;				// Expect 5 more bytes
            }
      /* We don't test if the UTF-8 encoding is well-formed */
        }
        return sbuf.toString() ;
    }

    public static String GetCountryZipCode(Context c){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl= c.getResources().getStringArray(R.array.CountryCodes);
        for (String aRl : rl) {
            String[] g = aRl.split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    public static String getCountryIso(Context c){
        TelephonyManager manager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        return manager.getSimCountryIso().toUpperCase();
    }

    public boolean isValidNumber(String number){
        boolean isLengthValid = isValidLength(number);
        return false;
    }

    private boolean isValidLength(String number){
        boolean isLocal = isLocal(number);
        boolean isNational;
        boolean isInternational;

        return false;
    }

    private boolean isLocal(String number){
        return number.length() == 7;
    }

    private boolean isNational (String number){
        if (number.length() == 9){
            String split = number.substring(0, Math.min(number.length(), 2));
            if (!split.isEmpty()){

            }
        }
        return false;
    }

}

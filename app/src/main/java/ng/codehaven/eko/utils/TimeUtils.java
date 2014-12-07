package ng.codehaven.eko.utils;

import com.ocpsoft.pretty.time.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mrsmith on 10/26/14.
 * PrettyTime Utility
 */
public class TimeUtils {

    public static String timeAgo(String time) {
        PrettyTime mPtime = new PrettyTime();

        long timeAgo = timeStringtoMilis(time);

        return mPtime.format(new Date(timeAgo));
    }

    public static long timeStringtoMilis(String time) {
        long milis = 0;

        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = sd.parse(time);
            milis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return milis;
    }

    public static long getCurrentTime(){
        return new Date().getTime();
    }

    public static String timeFromMilli(long time){

        PrettyTime mPtime = new PrettyTime();

        return mPtime.format(new Date(time));
    }

}

package ng.codehaven.eko.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by mrsmith on 11/25/14.
 */
public class RequestManager {
    /**
     * the queue :-)
     */
    private static RequestQueue mRequestQueue;


    /**
     * Nothing to see here.
     */
    private RequestManager() {
        // no instances
    }


    /**
     * @param context
     * 			application context
     */
    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }


    /**
     * @return
     * 		instance of the queue
     * @throws
     * 		IllegalStatException if init has not yet been called
     */
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }
}

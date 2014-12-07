package ng.codehaven.eko.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by mrsmith on 11/13/14.
 * Conectivity utility
 */
public class Connectivity {
    private Context _context;

    public Connectivity(Context context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public String getCarrierName(){
        TelephonyManager manager = (TelephonyManager)_context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }
}

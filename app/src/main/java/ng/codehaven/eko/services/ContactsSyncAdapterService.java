package ng.codehaven.eko.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import ng.codehaven.eko.adapters.ContactsSyncAdapter;

public class ContactsSyncAdapterService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static ContactsSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new ContactsSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

}

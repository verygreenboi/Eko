package ng.codehaven.eko.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import ng.codehaven.eko.helpers.Authenticator;

public class AccountAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        Authenticator authenticator = new Authenticator(this);
        return authenticator.getIBinder();
    }
}

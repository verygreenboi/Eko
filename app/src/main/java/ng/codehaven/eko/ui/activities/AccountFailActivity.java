package ng.codehaven.eko.ui.activities;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;

import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.Logger;

/**
 * Created by Thompson on 09/02/2015.
 */
public class AccountFailActivity extends AccountAuthenticatorActivity {
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Logger.s(this, getString(R.string.sync_only_one_account));
        finish();
    }
}

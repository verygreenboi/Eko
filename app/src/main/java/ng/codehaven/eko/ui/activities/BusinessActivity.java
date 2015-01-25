package ng.codehaven.eko.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.BuildType;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.adapters.BusinessAdapter;
import ng.codehaven.eko.ui.fragments.BusinessFragment;
import ng.codehaven.eko.ui.fragments.dialogFragments.AddBusinessFragment;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;

public class BusinessActivity extends ActionBarActivity implements AddBusinessFragment.NoticeDialogListener {

    private boolean mAddBusinessIsRegistered;

    private BroadcastReceiver mAddBusinessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FragmentManager fm = BusinessActivity.this.getSupportFragmentManager();
            AddBusinessFragment dialog = AddBusinessFragment.newInstance("New Business");
            dialog.show(fm, "fragment_add_business");
        }
    };

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolBar;
    @InjectView(R.id.fragmentContainer)
    protected FrameLayout mContainer;

    protected BusinessFragment mFragment = new BusinessFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        ButterKnife.inject(this);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportFragmentManager().beginTransaction().replace(mContainer.getId(), mFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mAddBusinessIsRegistered){
            registerReceiver(mAddBusinessReceiver, new IntentFilter(BusinessAdapter.ADD_BUSINESS_INTENT));
            mAddBusinessIsRegistered = !mAddBusinessIsRegistered;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAddBusinessIsRegistered){
            unregisterReceiver(mAddBusinessReceiver);
            mAddBusinessIsRegistered = !mAddBusinessIsRegistered;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_business, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        IntentUtils.startActivity(BusinessActivity.this, EnterNewBusinessActivity.class);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    private void pushBusinessToParse(final DialogFragment dialog, String name, String address, String phone, boolean isTransport) {
        final ParseObject newBusiness = new ParseObject(Constants.CLASS_BUSINESSES);
        newBusiness.put("business_name", name);
        newBusiness.put("business_address", address);
        newBusiness.put("business_phone", phone);
        newBusiness.put("isTransport", isTransport);
        if (BuildType.type == 0) {
            if (ParseUser.getCurrentUser() != null) {
                newBusiness.put("business_owner", ParseUser.getCurrentUser());
            } else {
                throw new RuntimeException(getResources().getString(R.string.no_user_logged_in_error_message));
            }
        }

        // Push to parse
        newBusiness.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    dialog.dismiss();
                    Logger.s(BusinessActivity.this, "Your business " + newBusiness.getString("business_name") + " has been created.");
                    // TODO: Add it to local DB using SugarORM
                } else {
                    switch (e.getCode()) {
                        case ParseException.TIMEOUT:
                        case ParseException.CONNECTION_FAILED:
                            Logger.s(BusinessActivity.this, getResources().getString(R.string.network_timeout_message_txt));
                            break;
                        default:
                            Logger.s(BusinessActivity.this, getResources().getString(R.string.general_error_message_txt));
                            break;
                    }
                }

            }
        });
    }

}

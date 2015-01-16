package ng.codehaven.eko.ui.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.SwipeDismissRecyclerViewTouchListener;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.BuildType;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.adapters.BusinessAdapter;
import ng.codehaven.eko.adapters.RecyclerGridAdapter;
import ng.codehaven.eko.models.mTransaction;
import ng.codehaven.eko.ui.BaseToolbarActivity;
import ng.codehaven.eko.ui.fragments.BusinessFragment;
import ng.codehaven.eko.ui.fragments.dialogFragments.AddBusinessFragment;
import ng.codehaven.eko.utils.Logger;

public class BusinessActivity extends ActionBarActivity implements AddBusinessFragment.NoticeDialogListener {

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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportFragmentManager().beginTransaction().replace(mContainer.getId(), mFragment).commit();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name, String address, String phone, boolean isTransport) {
        if (!name.isEmpty() && !address.isEmpty() && !phone.isEmpty()) {
            Logger.s(BusinessActivity.this, name + " " + address + " " + phone + " " + String.valueOf(isTransport));
            try {
                pushBusinessToParse(dialog, name, address, phone, isTransport);
            } catch (RuntimeException e) {
                Logger.s(this, e.getMessage());
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

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

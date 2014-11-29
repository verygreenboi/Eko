package ng.codehaven.eko.ui.activities;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.BaseToolbarActivity;
import ng.codehaven.eko.ui.fragments.ScanFragment;
import ng.codehaven.eko.ui.fragments.TapToScanFragment;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.ui.widgets.BezelImageView;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.MD5Util;


public class HomeActivity extends BaseToolbarActivity implements
        BaseToolbarActivity.onListItemClickListener,
        AdapterView.OnItemClickListener,
        TapToScanFragment.onViewsClicked {

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolbar;

    private Intent intent;

    @InjectView(R.id.profile_image)
    protected BezelImageView mProfileImage;
    @InjectView(R.id.profile_name_text)
    protected TextView mProfileName;
    @InjectView(R.id.profile_email)
    protected CustomTextView mProfileEmail;

    private int position;
    ImageLoader mImageLoader;

    Fragment tapToScanFragment;

    Bundle user;

    private ParseUser mCurrentUser = ParseUser.getCurrentUser();

    onListItemClickListener handler;

    TapToScanFragment.onViewsClicked viewClickHandler;

    boolean mFragmentTransactionReceiverRegistered,
            isScanFragmentLoaded,
            isHomeReceiverRegistered;

    public static final String SCANNER_INTENT_FILTER = "ng.codehaven.eko_SCANNER_INTENT_FILTER";
    public static final String HOME_FRAGMENT_INTENT_FILTER = "ng.codehaven.eko_HOME_FRAGMENT_INTENT_FILTER";

    private final BroadcastReceiver mScanFragmentTransactionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadScanFragment();
        }
    };

    public final BroadcastReceiver mHomeFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (tapToScanFragment != null) {
                loadHomeFragment();
                defaultToolBarState();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (mCurrentUser == null) {
            navigateToLogin();
        } else {

            init();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mFragmentTransactionReceiverRegistered) {
            registerReceiver(mScanFragmentTransactionReceiver, new IntentFilter(SCANNER_INTENT_FILTER));
            mFragmentTransactionReceiverRegistered = true;
        }

        if (!isHomeReceiverRegistered) {
            registerReceiver(mHomeFragmentReceiver, new IntentFilter(HOME_FRAGMENT_INTENT_FILTER));
            isHomeReceiverRegistered = true;
        } else {
            altToolBarState();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mFragmentTransactionReceiverRegistered) {
            unregisterReceiver(mScanFragmentTransactionReceiver);
            mFragmentTransactionReceiverRegistered = false;
        }
        if (isHomeReceiverRegistered) {
            unregisterReceiver(mHomeFragmentReceiver);
            isHomeReceiverRegistered = false;
        }
    }

    /**
     * @return layoutId
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        switch (id) {
            case R.id.action_settings:
                IntentUtils.startActivity(HomeActivity.this, SettingsActivity.class);
                break;
            case R.id.action_signOut:
                if (mCurrentUser.isAuthenticated()) {
                    ParseUser.logOut();
                    mCurrentUser = ParseUser.getCurrentUser();
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        handler.onListItemClick(position);
    }

    @Override
    public void onListItemClick(int position) {
        switch (position) {
            case 0:
                intent = new Intent(this, RequestFundsActivity.class);
                break;
            case 1:
                intent = new Intent(this, CashInActivity.class);
                break;
            case 2:
                intent = new Intent(this, HistoryActivity.class);
                break;
            case 3:
                intent = new Intent(this, PromoActivity.class);
                break;
            case 4:
                break;
            case 5:
                intent = new Intent(this, BusinessActivity.class);
                break;
            case 6:
                intent = new Intent(this, SettingsActivity.class);
                break;
            default:
                Logger.s(this, String.valueOf(position));
                break;
        }
        if (intent != null) {
            startActivity(intent);
        } else {
            Logger.m(String.valueOf(position));
        }
    }

    private void init() {
        try {
            handler = this;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        String email = mCurrentUser.getEmail();
        String hash = MD5Util.md5Hex(email);

        mImageLoader = ImageCacheManager.getInstance().getImageLoader();

        mProfileImage.setImageUrl(
                Constants.KEY_GRAVATAR_URL + hash + "?d=identicon", mImageLoader
        );

        mProfileName.setText(mCurrentUser.getString("full_name"));
        mProfileName.setTextColor(getResources().getColor(R.color.primary_text_default_material_dark));
        mProfileEmail.setText(email);
        mProfileEmail.setTextColor(getResources().getColor(R.color.secondary_text_default_material_dark));

        mToolbar.setLogo(R.drawable.ic_android);
        defaultToolBarState();
        loadHomeFragment();
        mNavListView.setOnItemClickListener(this);

        Log.d("TAG", mNavDrawerItems.toString());
    }

    private void loadHomeFragment() {
        user = new Bundle();
        JSONObject qrObject = new JSONObject();
        JSONObject userObject = new JSONObject();
        try {
            qrObject.put(Constants.KEY_QR_TYPE, Constants.KEY_QR_TYPE_PERSONAL);
            userObject.put("username", mCurrentUser.getUsername());
            qrObject.put("_User", userObject);

            user.putString("userQR", qrObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int mCurrentFragment = 0;
        tapToScanFragment = new TapToScanFragment();

        tapToScanFragment.setArguments(user);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, tapToScanFragment)
                .commit();
    }


    private void loadScanFragment() {
        isScanFragmentLoaded = true;
        Fragment scanFragment = new ScanFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, scanFragment)
                .commit();
        altToolBarState();
    }

    private void defaultToolBarState() {
        mToolbar.setNavigationIcon(R.drawable.ic_drawer);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void onClick(View view) {
                mNavDrawer.openDrawer(Gravity.START);
            }
        });
    }

    private void altToolBarState() {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isScanFragmentLoaded = false;
                Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });
    }

    private void navigateToLogin() {
        Intent i = new Intent(HomeActivity.this, AuthActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onClicked(int view) {
        viewClickHandler.onClicked(view);
    }

}

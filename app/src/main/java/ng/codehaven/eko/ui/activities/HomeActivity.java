package ng.codehaven.eko.ui.activities;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import ng.codehaven.eko.BuildType;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.BaseToolbarActivity;
import ng.codehaven.eko.ui.fragments.ScanFragment;
import ng.codehaven.eko.ui.fragments.TapToScanFragment;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.ui.widgets.BezelImageView;
import ng.codehaven.eko.utils.FontCache;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.MD5Util;
import ng.codehaven.eko.utils.Utils;

import static ng.codehaven.eko.Constants.ABC_FONT;
import static ng.codehaven.eko.Constants.KEY_GRAVATAR_URL;
import static ng.codehaven.eko.Constants.KEY_QR_TYPE;
import static ng.codehaven.eko.Constants.KEY_QR_TYPE_PERSONAL;


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


    String email, hash, username, fullname;

    private int position;
    private int fragment = -1;
    ImageLoader mImageLoader;

    Fragment tapToScanFragment;

    SharedPreferences prefs;
    String prefName = "mCurrentFragment";

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
//            loadScanFragment();
            intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        }
    };

    public final BroadcastReceiver mHomeFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (tapToScanFragment != null) {
                loadHomeFragment(username);
                defaultToolBarState();
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                // Handle successful scan

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Log.i("App", "Scan unsuccessful");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (BuildType.type == 0) {
            fullname = "Debug Man";
            email = "debug-"+ Utils.IMEI(this)+"@eko.ng";
            hash = MD5Util.md5Hex(email);
            username = "debug";
            init(email, hash, fullname, username);
        } else {
            if (mCurrentUser == null) {
                navigateToLogin();
            } else {
                fullname = mCurrentUser.getString("full_name");
                email = mCurrentUser.getEmail();
                hash = MD5Util.md5Hex(email);
                username = mCurrentUser.getUsername();
                init(email, hash, fullname, username);
            }
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

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);

        if (prefs.contains("mCurrentFragment")){
            fragment = prefs.getInt("mCurrentFragment", -1);
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

        if (fragment != -1){
            prefs = getSharedPreferences(prefName, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("mCurrentFragment", fragment).apply();
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
                    IntentUtils.logout(HomeActivity.this);
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
                intent = new Intent(this, ContactsActivity.class);
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

    private void init(String email, String emailHash, String fullName, final String username) {
        try {
            handler = this;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

//        String email = mCurrentUser.getEmail();
//        String hash = MD5Util.md5Hex(email);

        mImageLoader = ImageCacheManager.getInstance().getImageLoader();

        mProfileImage.setImageUrl(
                KEY_GRAVATAR_URL + emailHash + "?d=identicon", mImageLoader
        );

        mProfileName.setText(fullName);
        mProfileName.setTextColor(getResources().getColor(R.color.primary_text_default_material_dark));
        mProfileEmail.setText(email);
        mProfileEmail.setTextColor(getResources().getColor(R.color.secondary_text_default_material_dark));

//        mToolbar.setTitle(getString(R.string.app_name));

        TextView mToolBarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mToolBarTitle.setTypeface(FontCache.get(ABC_FONT, HomeActivity.this));

        if (BuildType.type == 0) {
            mToolBarTitle.setText("Eko");
        }else {
            mToolBarTitle.setText(getString(R.string.app_name));
        }

        defaultToolBarState();
        loadHomeFragment(username);
        mNavListView.setOnItemClickListener(this);
    }

    private void loadHomeFragment(String username) {
        user = new Bundle();
        JSONObject qrObject = new JSONObject();
        JSONObject userObject = new JSONObject();
        String qrData = KEY_QR_TYPE_PERSONAL+","+username;
        user.putString("userQR", qrData);
        try {
            qrObject.put(KEY_QR_TYPE, KEY_QR_TYPE_PERSONAL);
            userObject.put("username", username);
            qrObject.put("_User", userObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        fragment = 0;
        tapToScanFragment = new TapToScanFragment();

        tapToScanFragment.setArguments(user);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, tapToScanFragment)
                .commit();
    }


    private void loadScanFragment() {
        isScanFragmentLoaded = true;
        fragment = 1;
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
                finish();
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

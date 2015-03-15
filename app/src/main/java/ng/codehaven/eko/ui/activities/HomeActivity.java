package ng.codehaven.eko.ui.activities;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.BuildType;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.helpers.AccountHelper;
import ng.codehaven.eko.helpers.QRCodeHelper;
import ng.codehaven.eko.ui.BaseToolbarActivity;
import ng.codehaven.eko.ui.fragments.TapToScanFragment;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.ui.widgets.BezelImageView;
import ng.codehaven.eko.utils.FontCache;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.MD5Util;
import ng.codehaven.eko.utils.UIUtils;
import ng.codehaven.eko.utils.Utils;

import static ng.codehaven.eko.Constants.ABC_FONT;
import static ng.codehaven.eko.Constants.KEY_GRAVATAR_URL;
import static ng.codehaven.eko.Constants.KEY_QR_TYPE;
import static ng.codehaven.eko.Constants.KEY_QR_TYPE_PERSONAL;


public class HomeActivity extends BaseToolbarActivity implements
        BaseToolbarActivity.onListItemClickListener,
        AdapterView.OnItemClickListener, TapToScanFragment.ScanClickHandler {

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
    ImageLoader mImageLoader;

    Fragment tapToScanFragment;

    Bundle user;

    private ParseUser mCurrentUser = ParseUser.getCurrentUser();

    onListItemClickListener handler;

    private AccountHelper mAccountHelper;

    private AccountManager mAccountManager;
    ContentResolver mResolver;

    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        mAccountManager = AccountManager.get(this);
        // Get the content resolver for your app
        mResolver = getContentResolver();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (BuildType.type == 0) {
            fullname = "Debug Man";
            email = "debug-" + Utils.IMEI(this) + "@eko.ng";
            hash = MD5Util.md5Hex(email);
            username = "debug";
            init(email, hash, fullname, username);
        } else {
            if (!AccountHelper.hasAccount(this) || mCurrentUser == null) {
                navigateToLogin();
            } else {

//                Logger.m(AccountHelper.getAccount(this).name);

                if (ContentResolver.getSyncAutomatically(AccountHelper.getAccount(this),"ng.codehaven.eko.notifications")){
                    ContentResolver.setIsSyncable(AccountHelper.getAccount(this),"ng.codehaven.eko.notifications",1);
                    ContentResolver.setSyncAutomatically(AccountHelper.getAccount(this),"ng.codehaven.eko.notifications", true);

                    ContentResolver.addPeriodicSync(AccountHelper.getAccount(this),"ng.codehaven.eko.notifications",new Bundle(),SYNC_INTERVAL);
                }
                // Hide Splash with shared pref
                SharedPreferences isSplashSeen = getSharedPreferences(Constants.ACCOUNT_TYPE, 0);

                Editor editor = isSplashSeen.edit();

                editor.putBoolean("seen", true);

                editor.apply();


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
    }


    @Override
    protected void onPause() {
        super.onPause();
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
        switch (id) {
            case R.id.action_settings:
                IntentUtils.startActivity(HomeActivity.this, SettingsActivity.class);
                break;
            case R.id.action_signOut:
                if (mCurrentUser.isAuthenticated()) {
                    IntentUtils.logout(HomeActivity.this, this);
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
                Logger.m(String.valueOf(position));
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

        mImageLoader = ImageCacheManager.getInstance().getImageLoader();

        mProfileImage.setImageUrl(
                KEY_GRAVATAR_URL + emailHash + "?d=identicon", mImageLoader
        );

        mProfileName.setText(fullName);
        mProfileName.setTextColor(getResources().getColor(R.color.primary_text_default_material_dark));
        mProfileEmail.setText(email);
        mProfileEmail.setTextColor(getResources().getColor(R.color.secondary_text_default_material_dark));

        TextView mToolBarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mToolBarTitle.setTypeface(FontCache.get(ABC_FONT, HomeActivity.this));

        if (BuildType.type == 0) {
            mToolBarTitle.setText("Eko");
        } else {
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
        String qrData = "http://eko.ng/" + KEY_QR_TYPE_PERSONAL + "/" + username;


        String[] firstSplit = qrData.split("http://eko.ng/");

        Logger.m(String.valueOf(firstSplit[1]));

        user.putString("userQR", qrData);
        try {
            qrObject.put(KEY_QR_TYPE, KEY_QR_TYPE_PERSONAL);
            userObject.put("username", username);
            qrObject.put("_User", userObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tapToScanFragment = new TapToScanFragment();

        tapToScanFragment.setArguments(user);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, tapToScanFragment)
                .commit();
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

    private void navigateToLogin() {
        Intent i = new Intent(HomeActivity.this, AuthActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onScanClickHandler(View v) {
        if (v.getId() == R.id.qrImageView) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 2207);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 2207:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    String qrURL = b.getString("qrData");

                    // Get QR type
                    int mQRTYpe = QRCodeHelper.getQRType(UIUtils.unescape(qrURL));

                    // QR Actions
                    switch (mQRTYpe) {
                        case 0:
                            // Do personal QR alert
                            Logger.m("doing personal");
                            doPersonal(UIUtils.unescape(qrURL));
                            break;
                        case 1:
                            // Do business
                            break;
                        case 2:
                            // Do product
                            break;
                        case 3:
                            // Do service
                            break;
                        default:
                            // An error has occurred.
                            break;
                    }
                }
                break;
        }

    }

    private void doPersonal(String qrData) {
        String[] secondSplit = QRCodeHelper.getSecondSplitArray(qrData);
        Logger.m(String.valueOf(secondSplit.length)+" "+secondSplit[0]+" "+secondSplit[1]);
        switch (secondSplit.length){
            case 2:
                Intent i = new Intent(HomeActivity.this, SendFundActivity.class);
                i.setAction("ng.codehaven.eko.TRANSFER");
                i.putExtra("username", secondSplit[1]);
                startActivity(i);
                break;
        }
    }

    private void initTransfer(final String[] secondSplit) {
        ParseQuery<ParseObject> mAccount = ParseQuery.getQuery("Accounts");
        mAccount.whereEqualTo(Constants.KEY_ACCOUNT_HOLDERS_RELATION, mCurrentUser);
        mAccount.whereEqualTo(Constants.KEY_QR_TYPE, Constants.KEY_QR_TYPE_PERSONAL);
        mAccount.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                int balance;
                if (e == null) {
                    balance = parseObject.getInt("currentBalance");
                    if (doTransfer(secondSplit[2], Integer.parseInt(secondSplit[3]), balance)) {
                        //Transfer done
                        Logger.s(HomeActivity.this, "Transfer complete");
                    } else if (Integer.parseInt(secondSplit[3]) > balance) {
                        Logger.s(HomeActivity.this, "Insufficient funds.");
                    } else {
                        Logger.s(HomeActivity.this, getString(R.string.general_error_message_txt));
                    }

                }
            }
        });
    }

    private boolean doTransfer(String userId, final int amount, int balance) {
        final boolean[] isBalanceSufficient = new boolean[1];
        if (balance >= amount) {
            ParseQuery<ParseUser> getUser = ParseUser.getQuery();
            getUser.whereEqualTo("objectId", userId);
            getUser.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    ParseQuery<ParseObject> mAccount = ParseQuery.getQuery("Accounts");
                    mAccount.whereEqualTo(Constants.KEY_ACCOUNT_HOLDERS_RELATION, parseUser);
                    mAccount.whereEqualTo(Constants.KEY_QR_TYPE, Constants.KEY_QR_TYPE_PERSONAL);
                    mAccount.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            int oldBal = parseObject.getInt("currentBalance");
                            int newBal = oldBal + amount;
                            parseObject.put("currentBalance", newBal);
                            parseObject.saveInBackground();
                            if (!parseObject.isDirty()) {
                                isBalanceSufficient[0] = true;
                            }
                        }
                    });
                }
            });
        } else {
            // Insufficient balance available
            isBalanceSufficient[0] = false;
        }
        return isBalanceSufficient[0];
    }

    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();
                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                            showMessage(((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL"));
//                            Log.d("udinic", "GetTokenForAccount Bundle is " + bnd);

                        } catch (Exception e) {
                            e.printStackTrace();
//                            showMessage(e.getMessage());
                        }
                    }
                }
                , null);
    }
}

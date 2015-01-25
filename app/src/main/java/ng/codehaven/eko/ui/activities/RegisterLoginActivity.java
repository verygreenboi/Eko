package ng.codehaven.eko.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.R;
import ng.codehaven.eko.helpers.QRCodeHelper;
import ng.codehaven.eko.ui.fragments.LoginFragment;
import ng.codehaven.eko.ui.fragments.RegisterFragment;
import ng.codehaven.eko.ui.fragments.SignInFragment;
import ng.codehaven.eko.utils.Logger;

public class RegisterLoginActivity extends ActionBarActivity {

    @InjectView(R.id.previewTextView)
    protected TextView mPreview;
    @InjectView(R.id.pgBar)
    protected ProgressBar mProgressBar;
    @InjectView(R.id.retryBtn)
    protected Button mRetryBtn;
    @InjectView(R.id.fragLayout)
    protected FrameLayout mFragLayout;

    Context ctx;

    Bundle userId;

    JSONObject mQRData;

    private FragmentTransaction ft;

    String user;

    boolean mIsQRValid;

    int mQRType;

    int errorType = -1;

    int mFragment = -1;

    private SharedPreferences prefs;
    private String prefName = "loginRegStatePref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        ButterKnife.inject(this);

        ctx = RegisterLoginActivity.this;

        ft = getSupportFragmentManager().beginTransaction();

        mRetryBtn.setEnabled(false);

        Intent intent = getIntent();
        final String qrData = intent.getStringExtra(LoginFragment.EXTRA_MESSAGE);
        Logger.s(this, qrData);


        mIsQRValid = QRCodeHelper.isValidQRCode(qrData);

        if (mIsQRValid) {
            mQRType = QRCodeHelper.getQRType(qrData);

            if (mQRType != 0) {
                errorType = 0;
                doQRError(errorType);
            } else {

                user = QRCodeHelper.getSecondSplitArray(qrData)[1];
                Logger.s(this, user);
            }


        } else {
            doQRError(1);
            Logger.s(RegisterLoginActivity.this, "QR Code is invalid");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (user != null) {
            doQuery(user);

            mRetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRetryBtn.setEnabled(false);
                    mPreview.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mPreview.setText(getText(R.string.loading_profile));
                    mProgressBar.setVisibility(View.VISIBLE);
                    doQuery(user);
                }
            });
        } else {
            Logger.s(this, getString(R.string.general_error_message_txt));
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void doQRError(int errorType) {
        Logger.m("QR error -> " + String.valueOf(errorType));
    }

    private void doQuery(final String mUser) {
        ParseQuery<ParseUser> getUserQuery = ParseUser.getQuery();
        getUserQuery.whereEqualTo("username", mUser);
        getUserQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                userId = new Bundle();
                userId.putString("user_id", user);
                if (e != null) {
                    if (e.getCode() == ParseException.TIMEOUT || e.getCode() == ParseException.CONNECTION_FAILED) {
                        Logger.m("Network Timeout");
                        mPreview.setText(getString(R.string.network_timeout_message_txt));
                        mPreview.setTextColor(getResources().getColor(R.color.color_orange));
                        mRetryBtn.setEnabled(true);
                    } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        Logger.s(ctx, mUser);
                        createNewAccount(userId);
                    }
                } else {
                    Logger.s(ctx, "Account taken.");
                    login(userId);
                }
            }
        });
    }

    private void login(Bundle userId) {
        mPreview.setVisibility(View.GONE);
        mRetryBtn.setVisibility(View.GONE);
        mFragLayout.setVisibility(View.VISIBLE);

        Fragment signInFragment;
        signInFragment = new SignInFragment();
        signInFragment.setArguments(userId);

        ft.replace(mFragLayout.getId(), signInFragment).addToBackStack(null).commit();

        mFragment = 1;
    }

    private void createNewAccount(Bundle userId) {
        mPreview.setVisibility(View.GONE);
        mRetryBtn.setVisibility(View.GONE);
        mFragLayout.setVisibility(View.VISIBLE);

        Fragment registerFragment;
        registerFragment = new RegisterFragment();
        registerFragment.setArguments(userId);

        ft.replace(mFragLayout.getId(), registerFragment).addToBackStack(null).commit();

        mFragment = 2;
    }

}

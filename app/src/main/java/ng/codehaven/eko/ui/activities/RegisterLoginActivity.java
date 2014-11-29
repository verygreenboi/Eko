package ng.codehaven.eko.ui.activities;

import android.content.Context;
import android.content.Intent;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.R;
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

    private FragmentTransaction ft;

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

//        mPreview.setText(qrData);

        doQuery(qrData);

        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRetryBtn.setEnabled(false);
                mPreview.setTextColor(getResources().getColor(R.color.colorPrimary));
                mPreview.setText(getText(R.string.loading_profile));
                mProgressBar.setVisibility(View.VISIBLE);
                doQuery(qrData);
            }
        });

    }

    private void doQuery(final String qrData) {
        ParseQuery<ParseUser> getUserQuery = ParseUser.getQuery();
        getUserQuery.whereEqualTo("username", qrData);
        getUserQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                userId = new Bundle();
                userId.putString("user_id", qrData);
                if (e != null) {
                    if (e.getCode() == ParseException.TIMEOUT || e.getCode() == ParseException.CONNECTION_FAILED) {
//                        Logger.s(RegisterLoginActivity.this, "Network Timeout");
                        Logger.m("Network Timeout");
                        mPreview.setText(getString(R.string.network_timeout_message_txt));
                        mPreview.setTextColor(getResources().getColor(R.color.color_orange));
                        mRetryBtn.setEnabled(true);
                    } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        Logger.s(ctx, "Create new account");
                        createNewAccount();
                    }
                } else {
                    Logger.s(ctx, "Account taken.");
                    login();
                }
            }
        });
    }

    private void login() {
        mPreview.setVisibility(View.GONE);
        mRetryBtn.setVisibility(View.GONE);
        mFragLayout.setVisibility(View.VISIBLE);

        Fragment signInFragment;
        signInFragment = new SignInFragment();
        signInFragment.setArguments(userId);

        ft.replace(mFragLayout.getId(), signInFragment).addToBackStack(null).commit();
    }

    private void createNewAccount() {
        mPreview.setVisibility(View.GONE);
        mRetryBtn.setVisibility(View.GONE);
        mFragLayout.setVisibility(View.VISIBLE);

        Fragment registerFragment;
        registerFragment = new RegisterFragment();
        registerFragment.setArguments(userId);

        ft.replace(mFragLayout.getId(), registerFragment).addToBackStack(null).commit();
    }

}

package ng.codehaven.eko.ui.activities.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.MD5Util;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements View.OnClickListener, View.OnKeyListener {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";


    private ProgressDialog mLoading;

    private final int REQ_SIGNUP = 1;

    @InjectView(R.id.usernameEditText)
    protected EditText mUsername;
    @InjectView(R.id.passwordEditText)
    protected EditText mPassword;
    @InjectView(R.id.LoginButton)
    protected Button mLoginBtn;
    @InjectView(R.id.createAccount)
    protected TextView mSignUpText;
    @InjectView(R.id.haveAccount) protected TextView mHaveAccount;

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

        if (mAuthTokenType == null)
            mAuthTokenType = Constants.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null)
            mUsername.setText(accountName);

        mLoginBtn.setOnClickListener(this);
        mSignUpText.setOnClickListener(this);
        mPassword.setOnKeyListener(this);
        mHaveAccount.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.LoginButton) {
            submit();
        } else if (id == R.id.createAccount || id == R.id.haveAccount) {
            Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
            signup.putExtras(getIntent().getExtras());
            startActivityForResult(signup, REQ_SIGNUP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void submit() {
        final String userName = mUsername.getText().toString().trim();
        final String userPass = mPassword.getText().toString().trim();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        ParseUser.logInInBackground(userName, userPass, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                String authtoken = null;
                Bundle data = new Bundle();

                if (e == null) {
                    authtoken = user.getSessionToken();
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, MD5Util.md5Hex(userPass));

                } else {
                    switch (e.getCode()) {
                        case ParseException.CONNECTION_FAILED:
                        case ParseException.TIMEOUT:
                            data.putString(KEY_ERROR_MESSAGE, getResources().getString(R.string.network_timeout_message_txt));
                            break;
                        case ParseException.OBJECT_NOT_FOUND:
                            data.putString(KEY_ERROR_MESSAGE, getString(R.string.login_error_txt));
                            break;
                        default:
                            data.putString(KEY_ERROR_MESSAGE, getResources().getString(R.string.general_error_message_txt));
                            break;
                    }

                }

                final Intent res = new Intent();
                res.putExtras(data);
                if (res.hasExtra(KEY_ERROR_MESSAGE)) {
                    Logger.s(getBaseContext(), res.getStringExtra(KEY_ERROR_MESSAGE));
                } else {
                    finishLogin(res);
                }

            }
        });
    }

    private void finishLogin(Intent intent) {
        final Account account;

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);

        account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Logger.m("finishLogin > addAccountExplicitly");

            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            Logger.m("finishLogin > setPassword");

            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_ENTER:
                mLoginBtn.setPressed(true);
                mLoginBtn.performClick();
                return true;
        }
        return false;
    }
}

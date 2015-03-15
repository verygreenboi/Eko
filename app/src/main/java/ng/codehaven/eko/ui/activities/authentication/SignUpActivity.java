package ng.codehaven.eko.ui.activities.authentication;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.Application;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.MD5Util;
import ng.codehaven.eko.utils.UIUtils;

import static ng.codehaven.eko.ui.activities.authentication.AuthenticatorActivity.ARG_ACCOUNT_TYPE;
import static ng.codehaven.eko.ui.activities.authentication.AuthenticatorActivity.KEY_ERROR_MESSAGE;
import static ng.codehaven.eko.ui.activities.authentication.AuthenticatorActivity.PARAM_USER_PASS;

public class SignUpActivity extends ActionBarActivity implements View.OnClickListener {

    private String mAccountType;

    @InjectView(R.id.firstNameEditText)
    protected EditText mFirstName;
    @InjectView(R.id.lastNameEditText)
    protected EditText mLastName;
    @InjectView(R.id.emailEditText)
    protected EditText mEmail;
    @InjectView(R.id.password)
    protected EditText mPassword;
    @InjectView(R.id.confirmPassword)
    protected EditText mConfirmPassword;
    @InjectView(R.id.phoneNumber)
    protected EditText mPhone;
    @InjectView(R.id.registerBtn)
    protected Button mRegisterBtn;
    @InjectView(R.id.login)
    protected TextView mLogin;

    private ProgressDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.inject(this);

        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        mLogin.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.registerBtn:
                if (mPassword.getText().toString().trim().equals(mConfirmPassword.getText().toString().trim())) {
                    mLoading = ProgressDialog.show(SignUpActivity.this, "Please wait", "Creating Account...");
                    createAccount();
                } else {
                    mPassword.setText("");
                    mConfirmPassword.setText("");
                    mPassword.requestFocus();
                    Logger.s(this, "Your password did not match.");
                }
                break;

        }
    }

    private void createAccount() {

        Context c = SignUpActivity.this;

        Phonenumber.PhoneNumber locale;

        String first = mFirstName.getText().toString().trim();
        String last = mLastName.getText().toString().trim();
        String name = first + " " + last;
        final String accountName = mEmail.getText().toString().toLowerCase().trim();
        final String accountPassword = mPassword.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            locale = phoneUtil.parse(phone, UIUtils.getCountryIso(c));
        } catch (NumberParseException e) {
            e.printStackTrace();
            locale = null;
        }

        String newNumber = "+" + (locale != null ? locale.getCountryCode() : 0) + (locale != null ? locale.getNationalNumber() : 0);

        Logger.m(newNumber);
        phone = newNumber;

        final ParseUser newUser = new ParseUser();
        newUser.setUsername(accountName);
        newUser.setPassword(accountPassword);
        newUser.setEmail(accountName);
        newUser.put("first_name", first);
        newUser.put("last_name", last);
        newUser.put("full_name", name);
        newUser.put("phone", phone);

        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseRelation<ParseUser> mUserRelation;
                    ParseObject mAccount = new ParseObject("Accounts");
                    mAccount.put("type", Constants.KEY_QR_TYPE_PERSONAL);
                    mAccount.put("currentBalance", 0);
                    mAccount.put("active", true);
                    mAccount.put("defaultAdmin", newUser.getUsername());
                    mUserRelation = mAccount.getRelation(Constants.KEY_ACCOUNT_HOLDERS_RELATION);
                    mUserRelation.add(newUser);

                    mAccount.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (ParseUser.getCurrentUser() != null) {
                                    ParseUser.logOut();
                                }
                                doLogin(accountName, accountPassword);
                            } else {
                                mLoading.dismiss();
                            }
                        }
                    });

                } else {
                    mLoading.dismiss();
                }
            }
        });

    }

    private void doLogin(final String accountName, final String accountPassword) {
        ParseUser.logInInBackground(accountName, accountPassword, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                Intent res = new Intent();
                Bundle data = new Bundle();

                mLoading.dismiss();

                if (e == null) {
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, user.getUsername());
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, user.getSessionToken());
                    data.putString(PARAM_USER_PASS, MD5Util.md5Hex(accountPassword));

                    Application.UpdateParseInstallation(user);

                    res.putExtras(data);

                    setResult(RESULT_OK, res);
                    finish();
                } else {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }


            }
        });
    }
}

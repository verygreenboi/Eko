package ng.codehaven.eko.ui.activities.authentication;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.Logger;

import static ng.codehaven.eko.Constants.sServerAuthenticate;
import static ng.codehaven.eko.ui.activities.authentication.AuthenticatorActivity.ARG_ACCOUNT_TYPE;
import static ng.codehaven.eko.ui.activities.authentication.AuthenticatorActivity.KEY_ERROR_MESSAGE;
import static ng.codehaven.eko.ui.activities.authentication.AuthenticatorActivity.PARAM_USER_PASS;

public class SignUpActivity extends ActionBarActivity implements View.OnClickListener {

    private String mAccountType;

    @InjectView(R.id.firstNameEditText) protected EditText mFirstName;
    @InjectView(R.id.lastNameEditText) protected EditText mLastName;
    @InjectView(R.id.emailEditText) protected EditText mEmail;
    @InjectView(R.id.password) protected EditText mPassword;
    @InjectView(R.id.confirmPassword) protected EditText mConfirmPassword;
    @InjectView(R.id.phoneNumber) protected EditText mPhone;
    @InjectView(R.id.registerBtn) protected Button mRegisterBtn;
    @InjectView(R.id.login) protected TextView mLogin;

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
        switch (id){
            case R.id.login:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.registerBtn:
                createAccount();
                break;

        }
    }

    private void createAccount() {

        new AsyncTask<String, Void, Intent>() {

            String first = mFirstName.getText().toString().trim();
            String last = mLastName.getText().toString().trim();
            String name = first + " "+ last;
            String accountName = mEmail.getText().toString().trim();
            String accountPassword = mPassword.getText().toString().trim();
            String phone = mPhone.getText().toString().trim();

            @Override
            protected Intent doInBackground(String... params) {
                String authtoken = null;
                Bundle data = new Bundle();

                try{
                    authtoken = sServerAuthenticate.userSignUp(name, accountName, accountPassword, first, last, phone, Constants.AUTHTOKEN_TYPE_FULL_ACCESS);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, accountPassword);

                }catch (Exception e){
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mLoading = ProgressDialog.show(SignUpActivity.this,"Please wait", "Creating Account...");
            }

            @Override
            protected void onPostExecute(Intent intent) {
                super.onPostExecute(intent);

                mLoading.dismiss();
                if (intent.hasExtra(KEY_ERROR_MESSAGE)){
                    Logger.s(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE));
                }else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();


    }
}

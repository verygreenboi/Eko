package ng.codehaven.eko.ui.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    @InjectView(R.id.usernameEditText) protected EditText mUsername;
    @InjectView(R.id.passwordEditText) protected  EditText mPassword;
    @InjectView(R.id.LoginButton) protected Button mLoginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        mPassword.setImeActionLabel("Login", KeyEvent.KEYCODE_ENTER);
        mPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    performLogin(LoginActivity.this, mUsername.getText().toString().trim(), mPassword.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        mLoginBtn.setOnClickListener(this);

    }

    private void performLogin(final Context c, String u, String p) {
        Logger.s(c, "Username = "+u+" & password = "+p);
        ParseUser.logInInBackground(u, p, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Account a = new Account(user.getEmail(), Constants.KEY_ACCOUNT_TYPE);
                    AccountManager am = (AccountManager) c.getSystemService(ACCOUNT_SERVICE);

                    if (am.addAccountExplicitly(a, null, null)) {

                    }

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    public void onClick(View v) {
        performLogin(LoginActivity.this, mUsername.getText().toString().trim(), mPassword.getText().toString().trim());
    }
}

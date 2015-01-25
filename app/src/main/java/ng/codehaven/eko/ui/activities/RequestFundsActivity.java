package ng.codehaven.eko.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.BuildType;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.models.mTransaction;
import ng.codehaven.eko.ui.fragments.ShowQR;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.TimeUtils;

public class RequestFundsActivity extends ActionBarActivity {

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolbar;

    @InjectView(R.id.requestFundsSubmitBtn)
    protected Button mButton;
    @InjectView(R.id.amountEditText)
    protected EditText mAmountEditText;

    @InjectView(R.id.container)
    protected FrameLayout mContainer;

    @InjectView(R.id.formWrap)
    protected LinearLayout mFormWrap;

    @InjectView(R.id.progress_spinner)
    protected ProgressBar mProgressBar;

    // Is the button now checked?
    boolean checked;
    int requestSource;

    JSONObject bankRequestObject;

    ParseUser mCurrentUser = ParseUser.getCurrentUser();
    String mUserId;
    boolean isCheckShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_funds);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);

        init();

        if (isCheckShowing) {
            mFormWrap.setVisibility(View.GONE);
            showDone();
        }

        if (BuildType.type == 0) {
            mUserId = "debug";
        } else {
            mUserId = mCurrentUser.getObjectId();
        }

    }


    private void init() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButton.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                hideSoftKeyBoard();
                String amount = mAmountEditText.getText().toString().trim();
                if (!amount.isEmpty()) {
                    switch (requestSource) {
                        case 0:
                            doBankRequest(amount);

                            break;
                        case 1:
                            doAgentRequest(amount);

                            break;
                    }
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Logger.s(getApplicationContext(), "Amount cannot be bank!");
                }
            }
        });
    }

    private void doAgentRequest(final String amount) {
        // Create new transaction qr and display here.

        bankRequestObject = new JSONObject();
        try {
            bankRequestObject.put(Constants.CLASS_TRANSACTIONS_FROM, mCurrentUser);
            bankRequestObject.put(Constants.CLASS_TRANSACTIONS_TYPE, Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_AGENT);
            bankRequestObject.put(Constants.CLASS_TRANSACTIONS_AMOUNT, Integer.parseInt(amount));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mProgressBar.setVisibility(View.INVISIBLE);

        saveTransaction(amount, Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_AGENT);

        Fragment showQRFragment = new ShowQR();
        Bundle qrContent = new Bundle();
        qrContent.putString("qrData", bankRequestObject.toString());
        showQRFragment.setArguments(qrContent);
        mFormWrap.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(mContainer.getId(), showQRFragment).commit();
        showDone();

    }

    private void showDone() {
        isCheckShowing = true;
        mToolbar.setNavigationIcon(R.drawable.ic_check_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parentIntent = getSupportParentActivityIntent();
                startActivity(parentIntent);
            }
        });
    }

    private void doBankRequest(final String amount) {
        // Create new transaction and save on Parse.com
        ParseObject bankRequest = new ParseObject(Constants.CLASS_TRANSACTIONS);
        bankRequest.put(Constants.CLASS_TRANSACTIONS_FROM, mCurrentUser);
        bankRequest.put(Constants.CLASS_TRANSACTIONS_TYPE, Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_BANK);
        bankRequest.put(Constants.CLASS_TRANSACTIONS_AMOUNT, Integer.parseInt(amount));
        bankRequest.put(Constants.CLASS_TRANSACTIONS_RESOLUTION, false);
        bankRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {

                    bankRequestObject = new JSONObject();
                    try {
                        bankRequestObject.put(Constants.CLASS_TRANSACTIONS_FROM, mUserId);
                        bankRequestObject.put(Constants.CLASS_TRANSACTIONS_TYPE, Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_BANK);
                        bankRequestObject.put(Constants.CLASS_TRANSACTIONS_AMOUNT, Integer.parseInt(amount));
                    } catch (JSONException el) {
                        el.printStackTrace();
                    }

                    saveTransaction(amount, Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_BANK);

                    Logger.s(getApplicationContext(), getString(R.string.request_sent_message_txt));
                    IntentUtils.startActivity(RequestFundsActivity.this, HomeActivity.class);
                    finish();
                } else if (e.getCode() == ParseException.CONNECTION_FAILED || e.getCode() == ParseException.TIMEOUT) {
                    mButton.setEnabled(true);
                    Logger.s(getApplicationContext(), getString(R.string.network_timeout_message_txt));
                } else {
                    mButton.setEnabled(true);
                    Logger.s(getApplicationContext(), getString(R.string.general_error_message_txt));
                }
            }
        });
    }

    private void saveTransaction(String amount, int type) {
        mTransaction transaction = new mTransaction(
                mUserId + "-" + String.valueOf(TimeUtils.getCurrentTime()),
                mUserId,
                "agent",
                type,
                Integer.parseInt(amount),
                false,
                false,
                TimeUtils.getCurrentTime()
        );

        transaction.save();
    }

    public void onRadioButtonClicked(View view) {
        checked = ((RadioButton) view).isChecked();
        if (checked) {
            mButton.setEnabled(true);
        }
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.bankRadioBtn:
                requestSource = 0;
                break;
            case R.id.agentRadioBtn:
                requestSource = 1;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request_funds, menu);
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

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isChecked", isCheckShowing);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isCheckShowing = savedInstanceState.getBoolean("isChecked");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

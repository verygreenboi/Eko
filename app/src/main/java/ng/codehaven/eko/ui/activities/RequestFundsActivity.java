package ng.codehaven.eko.ui.activities;

import android.os.Bundle;
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
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.fragments.ShowQR;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;

public class RequestFundsActivity extends ActionBarActivity {

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolbar;

    @InjectView(R.id.requestFundsSubmitBtn)
    protected Button mButton;
    @InjectView(R.id.amountEditText)
    protected EditText mAmountEditText;

    @InjectView(R.id.container)
    protected FrameLayout mContainer;

    @InjectView(R.id.formWrap) protected LinearLayout mFormWrap;

    @InjectView(R.id.progress_spinner) protected ProgressBar mProgressBar;

    // Is the button now checked?
    boolean checked;
    int requestSource;

    JSONObject agentRequest;

    ParseUser mCurrentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_funds);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);

        init();

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

    private void doAgentRequest(String amount) {
        // Create new transaction qr and display here.

        agentRequest = new JSONObject();
        try {
            agentRequest.put(Constants.CLASS_TRANSACTIONS_FROM, mCurrentUser);
            agentRequest.put(Constants.CLASS_TRANSACTIONS_TYPE, Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_AGENT);
            agentRequest.put(Constants.CLASS_TRANSACTIONS_AMOUNT, Integer.parseInt(amount));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParseObject agentTransactionRequest = new ParseObject(Constants.CLASS_TRANSACTIONS);
        agentTransactionRequest.put(Constants.CLASS_TRANSACTIONS_FROM, mCurrentUser);
        agentTransactionRequest.put(Constants.CLASS_TRANSACTIONS_TYPE, Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_AGENT);
        agentTransactionRequest.put(Constants.CLASS_TRANSACTIONS_AMOUNT, Integer.parseInt(amount));
        agentTransactionRequest.put(Constants.CLASS_TRANSACTIONS_RESOLUTION, false);
        agentTransactionRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Fragment showQRFragment = new ShowQR();
                Bundle qrContent = new Bundle();
                qrContent.putString("qrData", agentRequest.toString());

                showQRFragment.setArguments(qrContent);

                mFormWrap.setVisibility(View.GONE);

                getSupportFragmentManager().beginTransaction().replace(mContainer.getId(), showQRFragment).commit();
            }
        });

    }

    private void doBankRequest(String amount) {
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
                    Logger.s(getApplicationContext(), getString(R.string.request_sent_message_txt));
                    IntentUtils.startActivity(getApplicationContext(), HomeActivity.class);
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

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}

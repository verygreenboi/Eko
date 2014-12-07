package ng.codehaven.eko.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.utils.QRCodeHelper;

public class TransactionDetailActivity extends ActionBarActivity {

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolbar;

    PrettyTime mPtime = new PrettyTime();


    private Typeface mButtonFont;

    String
            mTitle,
            mObject,
            mCreatedAt;

    boolean isResolved;

    private Bitmap mQRBitmap;

    ImageView qrImageView;

    private JSONObject jsonObject;
    private int
            mTransactionType,
            layoutId,
            mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent extra = getIntent();
        String jsonData = extra.getStringExtra("jsonObject");

        try {
            jsonObject = new JSONObject(jsonData);
            mObject = jsonObject.getString("id");
            mTransactionType = jsonObject.getInt("tType");
            mAmount = jsonObject.getInt("amount");
            mCreatedAt = mPtime.format(new Date(jsonObject.getLong("createdAt")));
            isResolved = jsonObject.getBoolean("isResolved");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (mTransactionType) {
            case Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_AGENT:
                // Agent Details
                layoutId = R.layout.agent_transaction_details_layout;
                mTitle = "Agent";
                break;
            case Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_BANK:
                layoutId = R.layout.agent_transaction_details_layout;
                mTitle = "Bank";
                break;
            default:
                layoutId = R.layout.activity_transaction_detail;
                mTitle = "Request";
                break;
        }

        init(layoutId);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);


        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getSupportParentActivityIntent();
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mToolbar.setTitle(mTitle);

        switch (mTransactionType) {
            case Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_BANK:
            case Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_AGENT:
//                Button test = (Button)findViewById(R.id.testBtn);
//                test.setTypeface(mButtonFont);
                CustomTextView objectNum = (CustomTextView) findViewById(R.id.objectNum);
                CustomTextView amountTV = (CustomTextView) findViewById(R.id.amount);
                qrImageView = (ImageView) findViewById(R.id.qrImageView);
                CustomTextView timestamp = (CustomTextView) findViewById(R.id.timestamp);
                FrameLayout resolutionWrap = (FrameLayout)findViewById(R.id.resolutionWrap);
                CustomTextView resolved = (CustomTextView)findViewById(R.id.resolved);
                new loadBitmapTask().execute();

                objectNum.setText(mObject);
                timestamp.setText(mCreatedAt);
                amountTV.setText(String.valueOf(mAmount));

                if (isResolved){
                    resolutionWrap.setBackgroundColor(getResources().getColor(R.color.md_teal_A400));
                    resolved.setText("Resolved");
                }
                break;

        }

    }

    private class loadBitmapTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... json) {
            try {
                return QRCodeHelper.generateQRCode(
                        jsonObject.toString(),
                        TransactionDetailActivity.this,
                        android.R.color.white,
                        R.color.colorPrimary, 512, 512
                );
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            qrImageView.setImageBitmap(bitmap);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transaction_detail, menu);
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

    private void init(int layoutId) {
        setContentView(layoutId);
        mButtonFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
    }

}

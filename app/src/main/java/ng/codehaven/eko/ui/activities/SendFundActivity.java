package ng.codehaven.eko.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.FontCache;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.MD5Util;

import static ng.codehaven.eko.Constants.ABC_FONT;

public class SendFundActivity extends ActionBarActivity implements View.OnClickListener {

    String email;
    ParseObject mReceiver;

    ImageLoader imageLoader;
    ParseException mException = null;

    private ProgressDialog mLoading;

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolBar;
    @InjectView(R.id.profile_image)
    protected NetworkImageView mProfileImage;
    @InjectView(R.id.enterAmount)
    protected EditText mAmount;
    @InjectView(R.id.sendButton)
    protected Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = ImageCacheManager.getInstance().getImageLoader();

        setContentView(R.layout.activity_send_fund);

        ButterKnife.inject(this);

        setupToolBar(this, mToolBar, HomeActivity.class, R.drawable.ic_arrow_back);

        TextView mToolBarTitle = (TextView) mToolBar.findViewById(R.id.toolbar_title);
        mToolBarTitle.setTypeface(FontCache.get(ABC_FONT,SendFundActivity.this));
        mToolBarTitle.setText("Send Funds");

        Intent intent = getIntent();

        if (intent.getData() != null) {
            Cursor c = getContentResolver().query(intent.getData(), null, null, null, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    email = c.getString(c.getColumnIndex("DATA1"));

                    initAccount();
                }
                c.close();
            }
        } else if(intent.getAction().equals("ng.codehaven.eko.TRANSFER")){
            email = intent.getStringExtra("username");

            initAccount();
        }

    }

    private void initAccount() {
        mProfileImage.setImageUrl("http://www.gravatar.com/avatar/" + MD5Util.md5Hex(email) + "?d=mm", imageLoader);


        ParseQuery<ParseObject> account = ParseQuery.getQuery("Accounts");
        account.whereEqualTo("defaultAdmin", email);
        account.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject account, ParseException e) {
                if (e == null) {
                    mReceiver = account;
                } else {
                    mException = e;
                }
            }
        });
    }

    private void setupToolBar(final Context ctx, Toolbar toolbar, final Class destination, int icon) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.startActivity(ctx, destination);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSendButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_fund, menu);
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

    @Override
    public void onClick(View v) {
        if (mException != null && mReceiver != null && !mAmount.getText().toString().isEmpty()) {
            mLoading = ProgressDialog.show(SendFundActivity.this, "Please wait", "Transferring Funds ...");
            //TODO: Handle transfer
            mReceiver.increment("balance", Integer.parseInt(mAmount.getText().toString()));
            mReceiver.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        mLoading.dismiss();
                        IntentUtils.startActivity(SendFundActivity.this, HomeActivity.class);
                        finish();
                    } else {
                        mLoading.dismiss();
                        handleException(e);
                    }
                }
            });
        } else {
            Logger.e(mException != null ? mException.getMessage() : "Error");
            handleException(mException);
        }
    }

    private void handleException(ParseException e) {
        if (e != null) {
            switch (e.getCode()) {
                case ParseException.CONNECTION_FAILED:
                case ParseException.TIMEOUT:
                    Logger.s(SendFundActivity.this, getString(R.string.network_timeout_message_txt));
                    break;
                case ParseException.OTHER_CAUSE:
                    Logger.s(SendFundActivity.this, getString(R.string.general_error_message_txt));
                    break;
            }
        } else {
            Logger.s(SendFundActivity.this, getString(R.string.general_error_message_txt));
            IntentUtils.startActivity(SendFundActivity.this, HomeActivity.class);
            finish();
        }
    }
}

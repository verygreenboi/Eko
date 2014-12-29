package ng.codehaven.eko.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.Connectivity;
import ng.codehaven.eko.utils.FontCache;

public class CashInActivity extends ActionBarActivity {

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolbar;
    @InjectView(R.id.sendBtn)
    protected Button mSendBtn;

    private int mCurrentBalance = 0;
    private ParseUser mCurrentUser;
    private Connectivity cd;
    private Typeface mFont;

    @Override
    public Intent getSupportParentActivityIntent() {
        return super.getSupportParentActivityIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);
        ButterKnife.inject(this);

        // Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parentIntent = getSupportParentActivityIntent();
                startActivity(parentIntent);
            }
        });

        // Instantiate connectivity utility

        cd = new Connectivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSendBtn.setTypeface(FontCache.get(Constants.ABC_FONT, this));

        if (!cd.isConnectingToInternet()) {
            mSendBtn.setEnabled(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cash_in, menu);
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
}

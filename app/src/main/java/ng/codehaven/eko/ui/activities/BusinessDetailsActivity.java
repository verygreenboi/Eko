package ng.codehaven.eko.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.R;
import ng.codehaven.eko.adapters.BusinessDetailsAdapter;

public class BusinessDetailsActivity extends ActionBarActivity implements BusinessDetailsAdapter.OnSwatchGenerated, BusinessDetailsAdapter.OnBusinessClickListeners {


//    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
//    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
//
//    private static final int ANIM_DURATION = 500;

    private JSONObject jsonObject;
    private String
            mTitle,
            mObject,
            imageUrl;

    boolean isTransport;

    private BusinessDetailsAdapter mAdapter;

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolBar;

    @InjectView(R.id.businessDetailRecyclerView)
    protected SuperRecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);
        ButterKnife.inject(this);

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(layoutManager);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        Intent extra = getIntent();
        String jsonData = extra.getStringExtra("jsonObject");
        parseJSON(jsonData);

        ParseQuery<ParseObject> pQuery = ParseQuery.getQuery("Products");
        pQuery
                .fromLocalDatastore()
                .whereEqualTo("parent_id", mObject)
                .orderByDescending("created_at").findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> products, ParseException e) {
                mAdapter = new BusinessDetailsAdapter(BusinessDetailsActivity.this, jsonObject, products);
                mAdapter.setmSwatchHandler(BusinessDetailsActivity.this);
                mAdapter.setOnBusinessClickListeners(BusinessDetailsActivity.this);
                mRecycler.setAdapter(mAdapter);
            }
        });
    }

    private void parseJSON(String jsonData) {
        try {
            jsonObject = new JSONObject(jsonData);
            mObject = jsonObject.getString("id");
            mTitle = jsonObject.getString("title");
            isTransport = jsonObject.getBoolean("isTransport");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_business_details, menu);
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
    public void swatch(Palette.Swatch swatch) {
        mToolBar.setBackgroundColor(swatch.getRgb());
        mToolBar.setTitleTextColor(swatch.getTitleTextColor());
    }

    @Override
    public void onMoreOptionsClickListener(View v, int position) {

    }
}

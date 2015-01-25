package ng.codehaven.eko.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.utils.UIUtils;

public class BusinessDetailsActivity extends ActionBarActivity {

    private JSONObject jsonObject;
    private String
            mTitle,
            mObject,
            mCreatedAt;

    private int
            mTransactionType,
            layoutId,
            mAmount;

    PrettyTime mPtime = new PrettyTime();

    boolean isResolved;

    private Bitmap mImageBitmap;
    private float mScrimAlpha = 0.9F;

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolBar;
    @InjectView(R.id.businessLogo)
    protected ImageView mLogo;
    @InjectView(R.id.scroller)
    protected ScrollView mScroller;
    @InjectView(R.id.metaWrap)
    protected RelativeLayout mMetaScrim;
    @InjectView(R.id.title)
    protected CustomTextView mTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);
        ButterKnife.inject(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

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

        String url = UIUtils.getImageUrl(mObject, this);
        mTitleView.setText(mObject);

        RequestQueue rq = Volley.newRequestQueue(this);

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mImageBitmap = response;
                mLogo.setImageBitmap(response);
                Palette p = getPalette(response);
                Palette.Swatch vibrant = p.getDarkVibrantSwatch();
                if (vibrant != null) {
                    mToolBar.setBackgroundColor(vibrant.getRgb());
                    mScroller.setBackgroundColor(vibrant.getRgb());
                    mMetaScrim.setBackgroundColor(vibrant.getRgb());
                    mToolBar.setTitleTextColor(vibrant.getTitleTextColor());

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                        mMetaScrim.setAlpha(mScrimAlpha);
//                    } else {
//                        setAlphaForView(mMetaScrim, mScrimAlpha);
//                    }
                }
            }

        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_android);
            }
        });

        rq.add(imageRequest);

    }

    private void setAlphaForView(View v, float f) {
        AlphaAnimation animation = new AlphaAnimation(f, f);
        animation.setDuration(0);
        animation.setFillAfter(true);
        v.startAnimation(animation);
    }

    private Palette getPalette(Bitmap response) {
        return Palette.generate(response);
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

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

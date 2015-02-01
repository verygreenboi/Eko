package ng.codehaven.eko.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.R;
import ng.codehaven.eko.helpers.ImageHelper;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.UIUtils;

public class BusinessDetailsActivity extends ActionBarActivity {

    private ImageLoader mImageLoader;

    private JSONObject jsonObject;
    private String
            mTitle,
            mObject,
            imageUrl;

    private int
            mTransactionType,
            layoutId,
            mAmount;

    PrettyTime mPtime = new PrettyTime();

    boolean isTransport;

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
        mImageLoader = ImageCacheManager.getInstance().getImageLoader();
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
            mTitle = jsonObject.getString("title");
            imageUrl = jsonObject.getString("logoUrl");
            isTransport = jsonObject.getBoolean("isTransport");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = UIUtils.getImageUrl(imageUrl, this);
        mTitleView.setText(mTitle);

        if (ImageHelper.fileExists(this, mObject)) {
            Uri uri = ImageHelper.getImageURI(this, mObject);
            mLogo.setImageURI(uri);
            Bitmap bitmap = ImageHelper.getImage(uri);
            setPalette(bitmap);
        } else {
            mImageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    mLogo.setImageBitmap(response.getBitmap());
                    setPalette(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }
    private void setPalette(Bitmap bitmap) {
        Palette p = getPalette(bitmap);
        Palette.Swatch vibrant = p.getDarkVibrantSwatch();
        if (vibrant != null) {
            mToolBar.setBackgroundColor(vibrant.getRgb());
            mLogo.setBackgroundColor(vibrant.getRgb());
            mMetaScrim.setBackgroundColor(vibrant.getRgb());
            mToolBar.setTitleTextColor(vibrant.getTitleTextColor());
            mTitleView.setTextColor(vibrant.getTitleTextColor());
        }
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

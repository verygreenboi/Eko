package ng.codehaven.eko.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ng.codehaven.eko.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

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

        RequestQueue rq = Volley.newRequestQueue(this);

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mImageBitmap = response;
            }

        },0 , 0 , null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_android);
            }
        });

        rq.add(imageRequest);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

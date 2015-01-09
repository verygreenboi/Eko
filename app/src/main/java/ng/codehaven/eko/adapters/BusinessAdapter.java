package ng.codehaven.eko.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.codehaven.eko.BuildType;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.BusinessDetailsActivity;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.MD5Util;
import ng.codehaven.eko.utils.Utils;

/**
 * Created by Thompson on 30/12/2014.
 */
public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {
    protected ArrayList<JSONObject> businesses;
    protected Context ctx;
    protected ImageLoader imageLoader;

    public BusinessAdapter(Context c, ArrayList<JSONObject> businesses){
        this.businesses = businesses;
        this.ctx = c;
        setHasStableIds(true);
        this.imageLoader = ImageCacheManager.getInstance().getImageLoader();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_item, parent, false);
        return new ViewHolder(ctx, v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {
            viewHolder.mTitle.setText(businesses.get(i).getString("id"));
            viewHolder.mImage.setImageUrl(getImageUrl(businesses.get(i).getString("id")), imageLoader);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    private String getImageUrl(String id) {
        String email;
        String hash;
        String url;

        if (BuildType.type == 0) {
            email = "debug-"+ id+ "-" + Utils.IMEI(ctx) + "@eko.ng";
        } else {
            email = ParseUser.getCurrentUser().getEmail();
        }

        hash = MD5Util.md5Hex(email);

        url = Constants.KEY_GRAVATAR_URL + hash+ "?d=identicon";

        return url;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public NetworkImageView mImage;
        public CustomTextView mTitle;
        public TextView mId;

        private Context ctx;


        public ViewHolder( Context c, View itemView) {
            super(itemView);
            this.ctx = c;
            mImage = (NetworkImageView)itemView.findViewById(R.id.mBusinessLogo);
            mTitle = (CustomTextView)itemView.findViewById(R.id.businessTitle);
            mId = (TextView)itemView.findViewById(R.id.businessId);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.mBusinessLogo ||
                    v.getId() == R.id.businessTitle) {
                IntentUtils.startActivityWithStringExtra(
                        ctx,
                        BusinessDetailsActivity.class,
                        Constants.BUSINESS_ID_KEY,
                        mId.getText().toString().trim()
                );
            }
        }
    }
}

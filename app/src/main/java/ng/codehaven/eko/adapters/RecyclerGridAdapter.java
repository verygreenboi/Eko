package ng.codehaven.eko.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONObject;

import java.util.ArrayList;

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.BusinessDetailsActivity;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.utils.IntentUtils;

/**
 * Created by Thompson on 31/12/2014.
 *
 */
public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerGridAdapter.ViewHolder> {

    public static final String BUSINESS_ID_KEY = "business_id";
    protected Context ctx;
    protected ArrayList<JSONObject> businesses;
    protected PrettyTime mPtime = new PrettyTime();

    public RecyclerGridAdapter(Context ctx, ArrayList<JSONObject> businesses) {
        this.ctx = ctx;
        this.businesses = businesses;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public NetworkImageView mImage;
        public CustomTextView mTitle;
        public TextView mId;

        private Context ctx;

        public ViewHolder(View itemView, Context c) {
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
                        BUSINESS_ID_KEY,
                        mId.getText().toString().trim()
                );
            }
        }
    }

}

package ng.codehaven.eko.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.BusinessDetailsActivity;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.UIUtils;

/**
 * Created by Thompson on 30/12/2014.
 * Business Adapter
 */
public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {

    protected ArrayList<JSONObject> businesses;
    protected Context ctx;
    protected ImageLoader imageLoader;
    protected String mBusinessTitle;

    // Adapter constructor

    public BusinessAdapter(Context c, ArrayList<JSONObject> businesses) {
        this.businesses = businesses;
        this.ctx = c;
        setHasStableIds(true);
        this.imageLoader = ImageCacheManager.getInstance().getImageLoader();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_item, parent, false);
        return new ViewHolder(ctx, v, businesses);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {
            mBusinessTitle = businesses.get(i).getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            mBusinessTitle = null;
        }
        viewHolder.getmTitle().setText(mBusinessTitle);
        viewHolder.getmImage().setDefaultImageResId(R.id.qrImageView);
        viewHolder.getmImage().setErrorImageResId(R.id.qrImageView);
        viewHolder.getmImage().setImageUrl(UIUtils.getImageUrl(mBusinessTitle, ctx), imageLoader);

    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    // Helper methods

    public void addBusiness(int position, JSONObject data) {
        businesses.add(position, data);
        notifyItemInserted(position);
    }

    public void addAll(ArrayList<JSONObject> txList){
        int startIndex = businesses.size();
        businesses.addAll(startIndex, txList);
        notifyItemRangeInserted(startIndex, txList.size());
    }

    public void removeBusiness(int position) {
        businesses.remove(position);
        notifyItemRemoved(position);
    }

    public void clear(){
        int size = businesses.size();
        businesses.clear();
        notifyItemRangeRemoved(0, size);
    }

    // ViewHolder Class

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private Context ctx;

        public NetworkImageView mImage;
        public CustomTextView mTitle;
        public ImageView mSecondaryAction;

        private int position;
        private String id;

        private JSONObject business;
        private ArrayList<JSONObject> businesses;

        public NetworkImageView getmImage() {
            return mImage;
        }

        public CustomTextView getmTitle() {
            return mTitle;
        }

        public ImageView getmSecondaryAction() {
            return mSecondaryAction;
        }

        public ViewHolder(Context c, View itemView, ArrayList<JSONObject> businesses) {
            super(itemView);
            this.ctx = c;
            this.businesses = businesses;
            mImage = (NetworkImageView) itemView.findViewById(R.id.mBusinessLogo);
            mTitle = (CustomTextView) itemView.findViewById(R.id.businessTitle);
            mSecondaryAction = (ImageView) itemView.findViewById(R.id.secondaryAction);

            mSecondaryAction.setOnClickListener(this);
            mImage.setOnClickListener(this);
            mTitle.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            position = getPosition();
            business = businesses.get(position);
            try {
                id = businesses.get(position).getString("id");
                Logger.s(ctx, id);
            } catch (JSONException e) {
                id = null;
                e.printStackTrace();
            }
            switch (v.getId()) {
                case R.id.secondaryAction:
                    // TODO: Show business edit functions
                    break;
                default:
                    Logger.m(String.valueOf(v.getId()));
                    IntentUtils.startActivityWithJSON(ctx, business, BusinessDetailsActivity.class);
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

    }

}

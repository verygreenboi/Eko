package ng.codehaven.eko.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ng.codehaven.eko.R;
import ng.codehaven.eko.helpers.ImageHelper;
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

    public static final String ADD_BUSINESS_INTENT = "ng.codehaven.eko.ADD_BUSINESS";

    protected List<ParseObject> businesses;
    protected Context ctx;
    protected ImageLoader imageLoader;
    protected String mBusinessTitle;

    // Adapter constructor

    public BusinessAdapter(Context c, List<ParseObject> businesses) {
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
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        mBusinessTitle = businesses.get(i).getString("title");
        final String mBusinessId = businesses.get(i).getObjectId();

        viewHolder.getmTitle().setText(mBusinessTitle);
        Logger.m(businesses.get(i).getParseFile("logo").getUrl());

        viewHolder.getmImage().setImageResource(R.drawable.person_image_empty);

        if (ImageHelper.fileExists(ctx, mBusinessId)) {
            viewHolder.getmImage().setImageURI(ImageHelper.getImageURI(ctx, mBusinessId));
        } else {
            imageLoader.get(UIUtils.getImageUrl(
                    businesses.get(i).getParseFile("logo").getUrl(), ctx), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        viewHolder.getmImage().setImageBitmap(response.getBitmap());
                        ImageHelper.storeImage(ctx, response.getBitmap(), mBusinessId);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    viewHolder.getmImage().setImageResource(R.drawable.person_image_empty);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    // Helper methods

    public void addBusiness(int position, ParseObject data) {
        businesses.add(position, data);
        notifyItemInserted(position);
    }

    public void addAll(List<ParseObject> txList) {
        int startIndex = businesses.size();
        businesses.addAll(startIndex, txList);
        notifyItemRangeInserted(startIndex, txList.size());
    }

    public void removeBusiness(int position) {
        businesses.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = businesses.size();
        businesses.clear();
        notifyItemRangeRemoved(0, size);
    }

    // ViewHolder Class

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private Context ctx;

        public ImageView mImage;
        public CustomTextView mTitle;
        public ImageView mSecondaryAction;

        private int position;
        private String id;

        private ParseObject business;
        private List<ParseObject> businesses;

        public ImageView getmImage() {
            return mImage;
        }

        public CustomTextView getmTitle() {
            return mTitle;
        }

        public ImageView getmSecondaryAction() {
            return mSecondaryAction;
        }

        public ViewHolder(Context c, View itemView, List<ParseObject> businesses) {
            super(itemView);
            this.ctx = c;
            this.businesses = businesses;
            mImage = (ImageView) itemView.findViewById(R.id.mBusinessLogo);
            mTitle = (CustomTextView) itemView.findViewById(R.id.businessTitle);
            mSecondaryAction = (ImageView) itemView.findViewById(R.id.secondaryAction);

            mSecondaryAction.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
            mImage.setOnClickListener(this);
            mTitle.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            position = getPosition();
            business = businesses.get(position);
            id = businesses.get(position).getObjectId();
            switch (v.getId()) {
                case R.id.secondaryAction:
                    // TODO: Show business edit functions
                    break;
                default:
                    Logger.m(String.valueOf(v.getId()));
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("title", business.get("title"));
                        obj.put("id", business.getObjectId());
                        obj.put("isTransport", business.get("type") == 1);
                        obj.put("logoUrl", business.getParseFile("logo").getUrl());
                        IntentUtils.startActivityWithJSON(ctx, obj, BusinessDetailsActivity.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

    }

}

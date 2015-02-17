package ng.codehaven.eko.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.parse.ParseObject;

import java.util.List;

import ng.codehaven.eko.R;
import ng.codehaven.eko.helpers.ImageHelper;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.UIUtils;

/**
 * Created by Thompson on 30/12/2014.
 * Business Adapter
 */
public class BusinessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    public static final String ADD_BUSINESS_INTENT = "ng.codehaven.eko.ADD_BUSINESS";

    protected List<ParseObject> businesses;
    protected Context ctx;
    protected ImageLoader imageLoader;
    protected String mBusinessTitle;

    private OnBusinessItemClick onBusinessItemClick;

    public BusinessAdapter(Context c, List<ParseObject> businesses) {
        this.businesses = businesses;
        this.ctx = c;
        setHasStableIds(true);
        this.imageLoader = ImageCacheManager.getInstance().getImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_item, parent, false);
        BusinessViewHolder businessViewHolder = new BusinessViewHolder(ctx, v, businesses);
        businessViewHolder.getmImage().setOnClickListener(this);
        businessViewHolder.getmImage().setOnLongClickListener(this);
        businessViewHolder.getmSecondaryAction().setOnClickListener(this);
        return businessViewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        bindBusiness((BusinessViewHolder) holder, position);
    }

    private void bindBusiness(BusinessViewHolder holder, int position) {
        mBusinessTitle = businesses.get(position).getString("title");
        final String mBusinessId = businesses.get(position).getObjectId();

        final BusinessViewHolder viewHolder = (BusinessViewHolder) holder;

        viewHolder.getmTitle().setText(mBusinessTitle);
        Logger.m(businesses.get(position).getParseFile("logo").getUrl());

        viewHolder.getmImage().setImageResource(R.drawable.person_image_empty);

        if (ImageHelper.fileExists(ctx, mBusinessId)) {
            viewHolder.getmImage().setImageURI(ImageHelper.getImageURI(ctx, mBusinessId));
        } else {
            imageLoader.get(UIUtils.getImageUrl(
                    businesses.get(position).getParseFile("logo").getUrl(), ctx), new ImageLoader.ImageListener() {
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
        viewHolder.getmImage().setTag(position);
        viewHolder.getmTitle().setTag(position);
        viewHolder.getmSecondaryAction().setTag(position);
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
        businesses.get(position).deleteEventually();
        businesses.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = businesses.size();
        businesses.clear();
        notifyItemRangeRemoved(0, size);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        ParseObject business = businesses.get(position);
        int[] screenLocation = new int[2];
        v.getLocationOnScreen(screenLocation);
        int orientation = ctx.getResources().getConfiguration().orientation;

        Bundle info = new Bundle();

        info.putInt("orientation", orientation);
        info.putInt("left", screenLocation[0]);
        info.putInt("top", screenLocation[1]);
        info.putInt("width", v.getWidth());
        info.putInt("height", v.getHeight());
        switch (v.getId()) {
            case R.id.secondaryAction:
                // TODO: Show business edit functions
                if (onBusinessItemClick != null) {
                    onBusinessItemClick.onSecondaryItemCLick(v, position);
                }
                break;
            case R.id.mBusinessLogo:
                if (onBusinessItemClick != null){
                    onBusinessItemClick.onBusinessImageClick(v, position, business, info);
                }
                break;
        }
    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View v) {
        int position = (Integer) v.getTag();

        if (v.getId() == R.id.mBusinessLogo) {
            if (onBusinessItemClick != null) {
                onBusinessItemClick.onBusinessImageLongClick(v, position);
            }
            return true;
        }
        return false;
    }

    // ViewHolder Class

    public class BusinessViewHolder extends RecyclerView.ViewHolder {

        private Context ctx;

        public ImageView mImage;

        public CustomTextView mTitle;

        public ImageView mSecondaryAction;
        public View v;
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

        public BusinessViewHolder(Context c, View itemView, List<ParseObject> businesses) {
            super(itemView);
            this.v = itemView;
            this.ctx = c;
            this.businesses = businesses;


            mImage = (ImageView) itemView.findViewById(R.id.mBusinessLogo);
            mTitle = (CustomTextView) itemView.findViewById(R.id.businessTitle);
            mSecondaryAction = (ImageView) itemView.findViewById(R.id.secondaryAction);
        }

    }

    public void setOnBusinessItemClickListener(OnBusinessItemClick onBusinessItemClick) {
        this.onBusinessItemClick = onBusinessItemClick;
    }

    public interface OnBusinessItemClick {
        public void onBusinessImageClick(View view, int position, ParseObject business,Bundle info);
        public void onSecondaryItemCLick(View view, int position);

        public void onBusinessImageLongClick(View view, int position);
    }

}

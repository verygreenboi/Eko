package ng.codehaven.eko.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.R;
import ng.codehaven.eko.helpers.ImageHelper;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.UIUtils;

/**
 * Created by Thompson on 07/02/2015.
 */
public class BusinessDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    public static final int MIN_ITEM_COUNT = 2;

    public static final int TYPE_DETAIL_HEADER = 0;
    public static final int TYPE_DETAIL_OPTIONS = 1;
    public static final int TYPE_PRODUCTS = 2;

    private final Context context;
    protected ImageLoader imageLoader;

    private List<ParseObject> products;

    private JSONObject business;

    private View v;

    private Bitmap mBitmap;

    private OnSwatchGenerated mSwatchHandler;

    private OnBusinessClickListeners onBusinessClickListeners;

    private static StaggeredGridLayoutManager.LayoutParams layoutParams;

    public void setOnBusinessClickListeners(OnBusinessClickListeners onBusinessClickListeners) {
        this.onBusinessClickListeners = onBusinessClickListeners;
    }

    public void setmSwatchHandler(OnSwatchGenerated mSwatchHandler) {
        this.mSwatchHandler = mSwatchHandler;
    }

    public BusinessDetailsAdapter(Context context, JSONObject business, List<ParseObject> products) {
        this.context = context;
        this.business = business;
        this.products = products;
        this.imageLoader = ImageCacheManager.getInstance().getImageLoader();
    }

    @Override
    public int getItemViewType(int position) {
        int type = 2;
        switch (position) {
            case TYPE_DETAIL_HEADER:
                type = TYPE_DETAIL_HEADER;
                break;
            case TYPE_DETAIL_OPTIONS:
                type = TYPE_DETAIL_OPTIONS;
                break;
            case TYPE_PRODUCTS:
                type = TYPE_PRODUCTS;
                break;
        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_DETAIL_HEADER == viewType) {
            v = LayoutInflater.from(context).inflate(R.layout.view_business_detail_banner, parent, false);
            return new DetailsBannerViewHolder(
                    v = doLayoutParams(
                            context,
                            parent,
                            false,
                            R.layout.view_business_detail_banner,
                            true)
            );
        } else if (TYPE_DETAIL_OPTIONS == viewType) {

            return new DetailHeaderCardViewHolder(
                    v = doLayoutParams(
                            context,
                            parent,
                            false,
                            R.layout.view_business_detail_options_card,
                            true)
            );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (TYPE_DETAIL_HEADER == viewType) {
            bindDetailBanner((DetailsBannerViewHolder) holder);
        } else if (TYPE_DETAIL_OPTIONS == viewType) {
            bindDetailsHeaderCard((DetailHeaderCardViewHolder) holder);
        }
    }

    private void bindDetailBanner(final DetailsBannerViewHolder holder) {
        final String id = getBusinessId();
        setBanner(holder, id);
        holder.mTitle.setText(getStringFromJSONObject(business, "title"));
    }

    private void bindDetailsHeaderCard(DetailHeaderCardViewHolder holder) {
        if (mBitmap != null) {
            holder.mOverview.setTextColor(getSwatch(mBitmap).getRgb());
            holder.mSeeMore.setTextColor(getSwatch(mBitmap).getRgb());
        }
    }

    @Override
    public int getItemCount() {
        return MIN_ITEM_COUNT + products.size();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        switch (v.getId()){
            case R.id.btn_seeMore:
                onBusinessClickListeners.onMoreOptionsClickListener(v, position);
        }
    }

    static class DetailHeaderCardViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.summary_card_view)
        protected CardView mRoot;

        @InjectView(R.id.overviewText) protected TextView mOverview;

        @InjectView(R.id.btn_accounts)
        protected ImageButton mAccountBtn;
        @InjectView(R.id.btn_reps)
        protected ImageButton mRepsBtn;
        @InjectView(R.id.btn_reports)
        protected ImageButton mReportsBtn;
        @InjectView(R.id.btn_seeMore) protected Button mSeeMore;

        public DetailHeaderCardViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    static class DetailsBannerViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.banner_root)
        protected LinearLayout mRoot;

        @InjectView(R.id.bannerImageView)
        protected ImageView mBanner;
        @InjectView(R.id.titleWrap)
        protected FrameLayout mTitleWrap;
        @InjectView(R.id.bannerTitle)
        protected TextView mTitle;

        public DetailsBannerViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public String getStringFromJSONObject(JSONObject j, String key) {
        String s;
        try {
            s = j.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            s = null;
        }
        return s;
    }

    public String getBusinessId() {
        String id;
        try {
            id = business.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            id = null;
        }
        return id;
    }

    public String getBusinessImageUrl() {
        String url;
        try {
            url = business.getString("logoUrl");
        } catch (JSONException e) {
            e.printStackTrace();
            url = null;
        }
        return url;
    }

    public static View doLayoutParams(Context c, ViewGroup parent, boolean rootView, int layout, boolean isFullSpan) {
        View v = LayoutInflater.from(c).inflate(layout, parent, rootView);
        layoutParams = (StaggeredGridLayoutManager.LayoutParams) v.getLayoutParams();
        layoutParams.setFullSpan(isFullSpan);
        v.setLayoutParams(layoutParams);
        return v;
    }

    private void setBanner(final DetailsBannerViewHolder holder, final String id) {
        if (ImageHelper.fileExists(context, id)) {
            holder.mBanner.setImageURI(ImageHelper.getImageURI(context, id));
            Bitmap b = ImageHelper.getImage(ImageHelper.getImageURI(context, id));
            mBitmap = b;
            Palette.Swatch s = getSwatch(b);
            holder.mTitleWrap.setBackgroundColor(s.getRgb());
            holder.mTitle.setTextColor(s.getTitleTextColor());
            if (mSwatchHandler != null) {
                mSwatchHandler.swatch(s);
            }
        } else {
            imageLoader.get(UIUtils.getImageUrl(getBusinessImageUrl(), context), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        holder.mBanner.setImageBitmap(response.getBitmap());
                        ImageHelper.storeImage(context, response.getBitmap(), id);
                        Palette.Swatch s = getSwatch(response.getBitmap());
                        holder.mTitleWrap.setBackgroundColor(s.getRgb());
                        holder.mTitle.setTextColor(s.getTitleTextColor());

                        mBitmap = response.getBitmap();

                        if (mSwatchHandler != null) {
                            mSwatchHandler.swatch(s);
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    private Palette.Swatch getSwatch(Bitmap bitmap) {
        Palette mPalette = Palette.generate(bitmap);
        return mPalette.getVibrantSwatch();
    }

    public interface OnSwatchGenerated {
        public void swatch(Palette.Swatch swatch);
    }
    public interface OnBusinessClickListeners{
        public void onMoreOptionsClickListener(View v, int position);
    }
}

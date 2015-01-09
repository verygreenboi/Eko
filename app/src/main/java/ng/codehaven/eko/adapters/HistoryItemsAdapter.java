package ng.codehaven.eko.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ocpsoft.pretty.time.PrettyTime;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ng.codehaven.eko.BuildType;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.TransactionDetailActivity;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.ui.widgets.BezelImageView;
import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.MD5Util;
import ng.codehaven.eko.utils.Utils;

/**
 * Created by mrsmith on 12/20/14.
 */
public class HistoryItemsAdapter extends RecyclerView.Adapter<HistoryItemsAdapter.ViewHolder> {


    protected ArrayList<JSONObject> transactions;
    protected String id,
            createdAt;

    boolean isResolved;

    protected Context ctx;

    protected int mLayoutType;

    protected PrettyTime mPtime = new PrettyTime();

    private boolean isEmpty;

    ImageLoader imageLoader;

    private int setLayoutId() {
        int layoutId = 0;
        if (!isEmpty) {
            switch (mLayoutType) {
                case 0:
                    layoutId = R.layout.transaction_item;
                    break;
                case 1:
                    layoutId = R.layout.business_item;
                    break;
            }
        } else {
            layoutId = R.layout.empty_view;
        }
        Logger.m(String.valueOf(mLayoutType) + " " + String.valueOf(layoutId));
        return layoutId;
    }

    public HistoryItemsAdapter(Context context, ArrayList<JSONObject> transactions, int layoutType) {
        this.transactions = transactions;
        this.ctx = context;
        this.mLayoutType = layoutType;
        setHasStableIds(true);
        this.isEmpty = transactions.isEmpty();
        this.imageLoader = ImageCacheManager.getInstance().getImageLoader();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(setLayoutId(), parent, false);
        return new ViewHolder(ctx, v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!isEmpty) {
            try {
                id = transactions.get(position).getString("id");
                createdAt = mPtime.format(new Date(transactions.get(position).getLong("createdAt")));
                isResolved = transactions.get(position).getBoolean("isResolved");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (setLayoutId()) {
                case 0:
                    setupHistoryFields(holder);
                    break;
                case 1:
                    setupBusinessFields(holder, position);
                    break;
            }
        }

    }

    private void setupBusinessFields(ViewHolder holder, int position) {
        String email;

        if (BuildType.type == 0) {
            email = "debug-" + Utils.IMEI(ctx) + "@eko.ng";
        } else {
            email = ParseUser.getCurrentUser().getEmail();
        }

        holder.mBusinessLogo.setImageUrl(
                Constants.KEY_GRAVATAR_URL
                        + MD5Util.md5Hex(email)
                        + "?d=identicon",
                imageLoader
        );

        holder.businessTitle.setText("Test " +String.valueOf(position));

        Logger.s(ctx, "business item");


    }

    private void setupHistoryFields(ViewHolder holder) {
        holder.objId.setText(id);
        holder.tStamp.setText(createdAt);

        if (isResolved) {
            holder.resolution.setText("Resolved");
            holder.resolution.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.resolution.setText("Unresolved");
            holder.resolution.setTextColor(ctx.getResources().getColor(R.color.md_red_500));
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeViaSwipe(int position) {
        transactions.remove(position);

        //Because item is already out of view via swipe do not animate this with default animator
        notifyDataSetChanged();
    }

    public void clear() {
        int size = transactions.size();
        transactions.clear();
        notifyItemRangeRemoved(0, size);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public BezelImageView image;

        public CustomTextView
                objId,
                tStamp,
                resolution,
                businessTitle;

        private Context context;

        public NetworkImageView mBusinessLogo;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            businessTitle = (CustomTextView) itemView.findViewById(R.id.businessTitle);
            mBusinessLogo = (NetworkImageView) itemView.findViewById(R.id.mBusinessLogo);
            objId = (CustomTextView) itemView.findViewById(R.id.objectId);
            tStamp = (CustomTextView) itemView.findViewById(R.id.timestamp);
            resolution = (CustomTextView) itemView.findViewById(R.id.resolution);
            image = (BezelImageView) itemView.findViewById(R.id.logo);

            itemView.setOnClickListener(this);

            this.context = context;
        }


        @Override
        public void onClick(View view) {
            IntentUtils
                    .startActivityWithStringExtra(
                            context,
                            TransactionDetailActivity.class,
                            "id",
                            objId.getText().toString()
                    );
        }
    }
}

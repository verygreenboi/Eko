package ng.codehaven.eko.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.TransactionDetailActivity;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.ui.widgets.BezelImageView;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;

/**
 * Created by mrsmith on 12/20/14.
 */
public class HistoryItemsAdapter extends RecyclerView.Adapter<HistoryItemsAdapter.ViewHolder> {


    protected ArrayList<JSONObject> transactions;
    protected String id,
            createdAt;

    boolean isResolved;

    protected Context ctx;

    protected PrettyTime mPtime = new PrettyTime();

    public HistoryItemsAdapter(Context context, ArrayList<JSONObject> transactions) {
        this.transactions = transactions;
        this.ctx = context;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(ctx, v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            id = transactions.get(position).getString("id");
            createdAt = mPtime.format(new Date(transactions.get(position).getLong("createdAt")));
            isResolved = transactions.get(position).getBoolean("isResolved");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                resolution;

        private Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
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

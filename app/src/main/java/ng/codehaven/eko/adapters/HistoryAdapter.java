package ng.codehaven.eko.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.views.CustomTextView;
import ng.codehaven.eko.ui.widgets.BezelImageView;

/**
 * Created by mrsmith on 12/1/14.
 */
public class HistoryAdapter extends ArrayAdapter<JSONObject> {

    private ArrayList<JSONObject> transactions;
    private Context context;

    PrettyTime mPtime = new PrettyTime();

    boolean isResolved = false;

    String
            objNum,
            createdAt;

    ListItemClickListener listener = new ListItemClickListener();

    public HistoryAdapter(Context context, ArrayList<JSONObject> transactions) {
        super(context, 0, transactions);
        this.context = getContext();
        this.transactions = transactions;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        JSONObject transaction = transactions.get(i);

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.transaction_item, parent, false);

            holder.image = (BezelImageView) convertView.findViewById(R.id.logo);
            holder.objId = (CustomTextView) convertView.findViewById(R.id.objectId);
            holder.tStamp = (CustomTextView) convertView.findViewById(R.id.timestamp);
            holder.resolution = (CustomTextView) convertView.findViewById(R.id.resolution);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setDefaultImageResId(R.drawable.ic_android);

        try {
            objNum = transaction.getString("id");
            createdAt = mPtime.format(new Date(transaction.getLong("createdAt")));

            isResolved = transaction.getBoolean("isResolved");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.objId.setText(objNum);
        holder.tStamp.setText(createdAt);

        if (isResolved) {
            holder.resolution.setText("Resolved");
            holder.resolution.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.resolution.setText("Unresolved");
            holder.resolution.setTextColor(context.getResources().getColor(R.color.md_red_500));
        }

        return convertView;
    }

    private class ListItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int id = view.getId();
        }
    }

    public static class ViewHolder {
        public BezelImageView image;

        public CustomTextView
                objId,
                tStamp,
                resolution;
    }
}

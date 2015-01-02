package ng.codehaven.eko.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.codehaven.eko.R;

/**
 * Created by Thompson on 30/12/2014.
 */
public class BusinessAdapter implements StickyHeadersAdapter<BusinessAdapter.ViewHolder> {
    protected ArrayList<JSONObject> businesses;

    public BusinessAdapter(ArrayList<JSONObject> businesses){
        this.businesses = businesses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.business_header, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {
            viewHolder.letter.setText(businesses.get(i).getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getHeaderId(int i) {
        return businesses.get(i).length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView letter;


        public ViewHolder(View itemView) {
            super(itemView);
            letter = (TextView) itemView.findViewById(R.id.letter);
        }
    }
}

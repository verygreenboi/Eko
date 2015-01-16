package ng.codehaven.eko.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Thompson on 16/01/2015.
 */
public class InitialHeaderAdapter implements StickyHeadersAdapter<InitialHeaderAdapter.ViewHolder> {

    private ArrayList<JSONObject> businesses;

    public InitialHeaderAdapter(ArrayList<JSONObject> businesses) {
        this.businesses = businesses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

    }

    @Override
    public long getHeaderId(int i) {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}

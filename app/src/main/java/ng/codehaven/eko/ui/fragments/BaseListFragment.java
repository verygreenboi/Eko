package ng.codehaven.eko.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.SwipeDismissRecyclerViewTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.codehaven.eko.adapters.HistoryItemsAdapter;

/**
 * Created by mrsmith on 12/20/14.
 */
public abstract class BaseListFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        OnMoreListener,
        SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    protected SuperRecyclerView mRecycler;
    protected HistoryItemsAdapter mAdapter;
    protected View v;
    protected JSONArray transactions;
    protected JSONArray mTXlist;

    private ArrayList<JSONObject> txList;
    private JSONArray items;

    protected abstract boolean getDismissStatus();
    protected abstract int getRecyclerView();
    protected abstract int getLayout();

    protected abstract JSONArray getTransactionArrays() throws JSONException;
    protected abstract RecyclerView.LayoutManager getLayoutManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(getLayout(), container, false);
        mRecycler = (SuperRecyclerView)v.findViewById(getRecyclerView());
        mRecycler.setLayoutManager(getLayoutManager());

        if (getDismissStatus()) mRecycler.setupSwipeToDismiss(this);
//        mRecycler.setAdapter(mAdapter);

        try {
            items = getTransactionArrays();
            txList = new ArrayList<JSONObject>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject transaction = items.getJSONObject(i);
                txList.add(transaction);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new HistoryItemsAdapter(getActivity(), txList);
        mRecycler.setAdapter(mAdapter);

//        mRecycler.setOnClickListener(this);

        return v;
    }

    /**
     * Called to determine whether the given position can be dismissed.
     *
     * @param position          Item position
     *
     */
    @Override
    public boolean canDismiss(int position) {
        return getDismissStatus();
    }

    /**
     * Called when the user has indicated they she would like to dismiss one or more list item
     * positions.
     *
     * @param recyclerView           The originating {@link android.widget.ListView}.
     * @param reverseSortedPositions An array of positions to dismiss, sorted in descending
     */
    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mAdapter.removeViaSwipe(position);
        }
    }

    /**
     * @param overallItemsCount Total count
     * @param itemsBeforeMore Items before more is called
     * @param maxLastVisiblePosition for staggered grid this is max of all spans
     */
    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

    }

    @Override
    public void onRefresh() {

    }
}

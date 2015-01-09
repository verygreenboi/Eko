package ng.codehaven.eko.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.SwipeDismissRecyclerViewTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.codehaven.eko.R;
import ng.codehaven.eko.adapters.BusinessAdapter;
import ng.codehaven.eko.adapters.RecyclerGridAdapter;
import ng.codehaven.eko.models.mTransaction;
import ng.codehaven.eko.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessFragment extends BaseListFragment {
    private ArrayList<JSONObject> txList;


    public BusinessFragment() {
        // Required empty public constructor
    }

    @Override
    protected boolean getDismissStatus() {
        return true;
    }

    @Override
    protected int getRecyclerView() {
        return R.id.businessList;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_history;
    }

    @Override
    protected int getAdapterType() {
        return 1;
    }

    @Override
    protected JSONArray getTransactionArrays() throws JSONException {
        return mTransaction
                .getTransactions(
                        "select * from M_TRANSACTION where M_TO = "
                                + "'agent' "
                                + "or M_TO = "
                                + "'bank' "
                                + " order by id desc limit"
                                + " 8"
                );
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getActivity(), 3);
    }

    @Override
    protected boolean getOptionsMenuStatus() {
        return true;
    }

    @Override
    protected int getMenuResId() {
        return R.menu.business_list_menu;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecycler.setRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        Logger.s(getActivity(), getActivity().getString(R.string.refresh_message));
    }


}

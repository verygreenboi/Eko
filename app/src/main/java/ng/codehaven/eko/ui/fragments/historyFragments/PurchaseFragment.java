package ng.codehaven.eko.ui.fragments.historyFragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.adapters.HistoryItemsAdapter;
import ng.codehaven.eko.models.mTransaction;
import ng.codehaven.eko.ui.fragments.BaseListFragment;
import ng.codehaven.eko.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 */
public class PurchaseFragment extends BaseListFragment {


    private HistoryItemsAdapter mAdapter;

    public PurchaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new HistoryItemsAdapter(getActivity(), txList);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Logger.s(getActivity(), "Is refreshing.");
            }
        });
    }

    @Override
    protected boolean getDismissStatus() {
        return true;
    }

    @Override
    protected int getRecyclerView() {
        return R.id.purchaseList;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_purchase;
    }

    @Override
    protected JSONArray getTransactionArrays() throws JSONException {
        return mTransaction
                .getTransactions(
                        "select * from M_TRANSACTION where T_TYPE = "
                                + Constants.CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_PRODUCT_PURCHASE
                                + " and IS_TICKET = "
                                + 0
                                +" order by id desc limit"
                                +" 8"
                );
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected boolean getOptionsMenuStatus() {
        return false;
    }

    @Override
    protected int getMenuResId() {
        return 0;
    }
}

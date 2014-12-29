package ng.codehaven.eko.ui.fragments.historyFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import ng.codehaven.eko.R;
import ng.codehaven.eko.models.mTransaction;
import ng.codehaven.eko.ui.fragments.BaseListFragment;
import ng.codehaven.eko.utils.Logger;


public class FundRequestHistory extends BaseListFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        return R.id.fundRequestList;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_fund_request_history;
    }

    @Override
    protected JSONArray getTransactionArrays() throws JSONException {
        return mTransaction
                .getTransactions(
                        "select * from M_TRANSACTION where M_TO = "+"'agent' "+"or M_TO = "+"'bank' "+" order by id desc limit"+" 8"
                );
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
}

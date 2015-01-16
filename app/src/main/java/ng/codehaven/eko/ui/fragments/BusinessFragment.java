package ng.codehaven.eko.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.codehaven.eko.R;
import ng.codehaven.eko.adapters.InitialHeaderAdapter;
import ng.codehaven.eko.models.mTransaction;
import ng.codehaven.eko.ui.fragments.dialogFragments.AddBusinessFragment;
import ng.codehaven.eko.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessFragment extends BaseListFragment  {
    private ArrayList<JSONObject> txList;
    private StickyHeadersItemDecoration top;


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
        return new GridLayoutManager(getActivity(), 2);
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
        mRecycler.setRefreshingColorResources(
                R.color.md_orange_400,
                R.color.md_blue_400,
                R.color.md_green_400,
                R.color.md_red_400);
        top = new StickyHeadersBuilder().
                setAdapter(businessAdapter).
                setRecyclerView(mRecycler.getRecyclerView()).
                setStickyHeadersAdapter(new InitialHeaderAdapter(txList)).build();

//        mRecycler.addItemDecoration(top);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.addBusinessAction) {
            showDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AddBusinessFragment dialog = AddBusinessFragment.newInstance("New Business");
        dialog.show(fm, "fragment_add_business");
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        Logger.s(getActivity(), getActivity().getString(R.string.refresh_message));
        businessAdapter.clear();

        try {
            JSONArray items = getTransactionArrays();
            txList = new ArrayList<JSONObject>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject transaction = items.getJSONObject(i);
                txList.add(transaction);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        businessAdapter.addAll(txList);

        if (mRecycler.getSwipeToRefresh().isRefreshing()){
            mRecycler.getSwipeToRefresh().setRefreshing(false);
        }

    }
}

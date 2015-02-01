package ng.codehaven.eko.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import ng.codehaven.eko.R;
import ng.codehaven.eko.adapters.BusinessAdapter;
import ng.codehaven.eko.models.mTransaction;
import ng.codehaven.eko.ui.fragments.dialogFragments.AddBusinessFragment;
import ng.codehaven.eko.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessFragment extends BaseListFragment  {
    private BusinessAdapter mAdapter;
    private StickyHeadersItemDecoration top;
    private int page = 0;
    private int number = 12;


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
        ParseQuery<ParseObject> getBusiness = ParseQuery.getQuery("Businesses");
        getBusiness.whereEqualTo("ceo", ParseUser.getCurrentUser());
        getBusiness.setLimit(number);
        getBusiness.fromLocalDatastore();
        getBusiness.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                mAdapter = new BusinessAdapter(getActivity(), parseObjects);
                mRecycler.setAdapter(mAdapter);
                Logger.m(String.valueOf(parseObjects.size()));
            }
        });
        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(
                R.color.md_orange_400,
                R.color.md_blue_400,
                R.color.md_green_400,
                R.color.md_red_400);
//        top = new StickyHeadersBuilder().
//                setAdapter(mAdapter).
//                setRecyclerView(mRecycler.getRecyclerView()).
//                setStickyHeadersAdapter(new InitialHeaderAdapter(txList)).build();

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
        mAdapter.clear();
        ParseQuery<ParseObject> getBusiness = ParseQuery.getQuery("Businesses");
        getBusiness.whereEqualTo("ceo", ParseUser.getCurrentUser());
        getBusiness.setLimit(number);
        getBusiness.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> businesses, ParseException e) {
                ParseObject.unpinAllInBackground("myBusiness", new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        ParseObject.pinAllInBackground("myBusiness", businesses);
                        mAdapter.addAll(businesses);
                    }
                });
            }
        });

        if (mRecycler.getSwipeToRefresh().isRefreshing()){
            mRecycler.getSwipeToRefresh().setRefreshing(false);
        }

    }

}

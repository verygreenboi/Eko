package ng.codehaven.eko.ui;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.Adapter;
import android.widget.ListView;

import java.util.ArrayList;

import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.adapters.NavListAdapter;

/**
 * Created by mrsmith on 11/9/14.
 * BaseToolBar Activity
 */

public abstract class BaseToolbarActivity extends ActionBarActivity {

    // list of navdrawer items that were actually added to the navdrawer, in order
    protected ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();
    protected DrawerLayout mNavDrawer;
    protected ListView mNavListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        mNavDrawerItems.add(Constants.NAVDRAWER_ITEM_REQUEST_FUNDS);
        mNavDrawerItems.add(Constants.NAVDRAWER_ITEM_CASH_IN);
        mNavDrawerItems.add(Constants.NAVDRAWER_ITEM_TRANSACTION_HISTORY);
        mNavDrawerItems.add(Constants.NAVDRAWER_ITEM_CONTACTS);
        mNavDrawerItems.add(Constants.NAVDRAWER_ITEM_SEPARATOR);
        mNavDrawerItems.add(Constants.NAVDRAWER_ITEM_BUSINESSES);
        mNavDrawerItems.add(Constants.NAVDRAWER_ITEM_SETTINGS);

        Adapter mNavAdapter = new NavListAdapter(this, mNavDrawerItems);
        mNavDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        mNavListView = (ListView)findViewById(R.id.navdrawer_items_list);

//        mNavListView.setItemsCanFocus(true);



        mNavListView.setAdapter((android.widget.ListAdapter) mNavAdapter);
    }

    protected abstract int getLayoutResource();

    public interface onListItemClickListener{
        public void onListItemClick(int position);
    }

}

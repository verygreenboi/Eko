package ng.codehaven.eko.ui.fragments.historyFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ng.codehaven.eko.R;
import ng.codehaven.eko.adapters.HistoryAdapter;
import ng.codehaven.eko.models.mTransaction;
import ng.codehaven.eko.ui.activities.TransactionDetailActivity;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;


public class FundRequestHistory extends Fragment {

    ListView listView;

    ArrayList<JSONObject> txList;

    SwipeRefreshLayout swipeRefreshLayout;

    public FundRequestHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fund_request_history, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        swipeRefreshLayout.setColorSchemeColors(
                R.color.md_teal_500,
                R.color.md_teal_300,
                R.color.md_teal_400,
                R.color.md_teal_500
        );

        listView = (ListView) v.findViewById(R.id.request_funds_history_list);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveRecords();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private JSONArray getTransactions() throws JSONException {
        JSONArray transactions = new JSONArray();

        List<mTransaction> transactionsList = mTransaction.findWithQuery(
                mTransaction.class,
                "select * from M_TRANSACTION where M_TO = ? or M_TO = ? order by id desc limit ?", "agent", "bank", "20"
        );

        for (mTransaction t : transactionsList) {
            JSONObject tx = new JSONObject();
            String id = t.getObjectNum();
            String from = t.getmFrom();
            String to = t.getmTo();
            int tType = t.gettType();
            int amount = t.getAmount();
            boolean isResolved = t.isResolved();
            long createdAt = t.getCreatedAt();

            tx.put("id", id);
            tx.put("from", from);
            tx.put("to", to);
            tx.put("tType", tType);
            tx.put("amount", amount);
            tx.put("isResolved", isResolved);
            tx.put("createdAt", createdAt);
            transactions.put(tx);

        }

        return transactions;
    }


    private class onListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            try {
                JSONObject transaction = new JSONObject(getTransactions().get(i).toString());
                IntentUtils.startActivityWithJSON(getActivity(), transaction, TransactionDetailActivity.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    private void retrieveRecords() {

        try {
            JSONArray items = getTransactions();

            txList = new ArrayList<JSONObject>();

            for (int i = 0; i < items.length(); i++) {
                JSONObject transaction = items.getJSONObject(i);
                txList.add(transaction);
                HistoryAdapter mAdapter = new HistoryAdapter(getActivity(), txList);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                listView.setAdapter(mAdapter);
                listView.setOnItemClickListener(new onListItemClickListener());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveRecords();
        }
    };


}

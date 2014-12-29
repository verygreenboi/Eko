package ng.codehaven.eko.models;

import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ng.codehaven.eko.utils.Logger;

/**
 * Created by mrsmith on 11/30/14.
 */
public class mTransaction extends SugarRecord<mTransaction> {
    public String getObjectNum() {
        return objectNum;
    }

    public String getmFrom() {
        return mFrom;
    }

    public String getmTo() {
        return mTo;
    }

    public int gettType() {
        return tType;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isResolved() {
        return resolution;
    }
    public boolean isTicket(){return isTicket;}

    public long getCreatedAt(){
        return createdAt;
    }

    public static ArrayList<mTransaction> fromJSON(JSONArray j) throws JSONException {
        ArrayList<mTransaction> tx = new ArrayList<mTransaction>();

        for (int i = 0; i < j.length(); i++){
            Logger.m(j.toString());
            mTransaction tsts = new mTransaction(j.getJSONObject(i));
            tx.add(tsts);
            Logger.m(tsts.toString());
        }

        return tx;

    }

    String objectNum;
    String mFrom;
    String mTo;
    int tType;
    int amount;
    boolean resolution, isTicket;
    long createdAt;

    public mTransaction() {
    }

    public mTransaction(String objectID, String from, String to, int tType, int amount, boolean resolution, boolean isTicket, long createdAt) {
        this.objectNum = objectID;
        this.mFrom = from;
        this.mTo = to;
        this.tType = tType;
        this.amount = amount;
        this.resolution = resolution;
        this.isTicket = isTicket;
        this.createdAt = createdAt;
    }

    public mTransaction(JSONObject j) throws JSONException {
        this.objectNum = j.getString("id");
        this.mFrom = j.getString("from");
        this.mTo = j.getString("to");
        this.tType = j.getInt("tType");
        this.amount = j.getInt("amount");
        this.resolution = j.getBoolean("resolution");
        this.isTicket = j.getBoolean("isTicket");
        this.createdAt = j.getLong("createdAt");
    }

    public static JSONArray getTransactions(String query) throws JSONException{
        JSONArray transactions = new JSONArray();
        List<mTransaction> transactionsList = mTransaction.findWithQuery(
                mTransaction.class,
                query
        );
        for (mTransaction t : transactionsList) {
            JSONObject tx = new JSONObject();
            String id = t.getObjectNum();
            String from = t.getmFrom();
            String to = t.getmTo();
            int tType = t.gettType();
            int amount = t.getAmount();
            boolean isResolved = t.isResolved();
            boolean isTicket = t.isTicket();
            long createdAt = t.getCreatedAt();

            tx.put("id", id);
            tx.put("from", from);
            tx.put("to", to);
            tx.put("tType", tType);
            tx.put("amount", amount);
            tx.put("isResolved", isResolved);
            tx.put("isTicket", isTicket);
            tx.put("createdAt", createdAt);
            transactions.put(tx);

        }

        return transactions;
    }

}

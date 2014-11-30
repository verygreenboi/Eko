package ng.codehaven.eko.models;

import com.orm.SugarRecord;
import com.parse.ParseUser;

/**
 * Created by mrsmith on 11/30/14.
 */
public class mTransaction extends SugarRecord<mTransaction> {
    String objectNum;
    String mFrom;
    String mTo;
    int tType;
    int amount;
    boolean resolution;

    public mTransaction(){}

    public mTransaction(String objectID, String from, String to, int tType, int amount, boolean resolution){
        this.objectNum = objectID;
        this.mFrom = from;
        this.mTo = to;
        this.tType = tType;
        this.amount = amount;
        this.resolution = resolution;
    }

}

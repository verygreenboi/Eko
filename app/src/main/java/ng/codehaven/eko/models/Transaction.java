package ng.codehaven.eko.models;

import com.orm.SugarRecord;
import com.parse.ParseUser;

/**
 * Created by mrsmith on 11/30/14.
 */
public class Transaction extends SugarRecord<Transaction> {
    String objectID;
    ParseUser from;
    ParseUser to;
    int tType;
    int amount;
    boolean resolution;

    public Transaction(){}

    public Transaction(String objectID, ParseUser from, ParseUser to, int tType, int amount, boolean resolution){
        this.objectID = objectID;
        this.from = from;
        this.to = to;
        this.tType = tType;
        this.amount = amount;
        this.resolution = resolution;
    }

}

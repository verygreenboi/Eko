package ng.codehaven.eko.utils;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;


/**
 * Created by Thompson on 23/01/2015.
 */
public class Transactions {
    private Context ctx;
    private ParseUser mCurrentUser;
    private String mReceiver;
    private ParseObject mAccount;

    public Transactions() {
    }

    public Transactions(Context ctx, ParseUser mCurrentUser, String mReceiver) {
        this.ctx = ctx;
        this.mCurrentUser = mCurrentUser;
        this.mReceiver = mReceiver;
    }
    public boolean DoP2PTransfer() throws ParseException{
        return false;
    }
}

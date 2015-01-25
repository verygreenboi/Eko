package ng.codehaven.eko.helpers;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import ng.codehaven.eko.Constants;

/**
 * Created by Thompson on 25/01/2015.
 */
public class AccountHelper {
    public static void activateAccount(ParseUser user){
        ParseQuery<ParseObject> mAccount = ParseQuery.getQuery("Accounts");
        mAccount.whereEqualTo(Constants.KEY_ACCOUNT_HOLDERS_RELATION, user);
        mAccount.whereEqualTo(Constants.KEY_QR_TYPE, Constants.KEY_QR_TYPE_PERSONAL);
    }
}

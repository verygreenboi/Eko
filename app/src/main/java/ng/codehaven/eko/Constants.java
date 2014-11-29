package ng.codehaven.eko;

/**
 * Created by mrsmith on 11/9/14.
 * Constants class
 */
public class Constants {

    public static final String KEY_USER = "_user";
    public static final String KEY_USER_ID = "userId";

    public static final int NAVDRAWER_ITEM_REQUEST_FUNDS = 0;
    public static final int NAVDRAWER_ITEM_CASH_IN = 1;
    public static final int NAVDRAWER_ITEM_TRANSACTION_HISTORY = 2;
    public static final int NAVDRAWER_ITEM_PROMO = 3;
    public static final int NAVDRAWER_ITEM_BUSINESSES = 4;
    public static final int NAVDRAWER_ITEM_SETTINGS = 5;
    public static final int NAVDRAWER_ITEM_INVALID = -1;
    public static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    public static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

    public static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_item_request_funds,
            R.string.navdrawer_item_cash_in,
            R.string.navdrawer_item_transaction_history,
            R.string.navdrawer_item_promo,
            R.string.navdrawer_item_businesses,
            R.string.navdrawer_item_settings
    };

    public static final int[] NAVDRAWER_ICON_RES_ID = new int[]{
            R.drawable.ic_request_funds,
            R.drawable.ic_cash_in,
            R.drawable.ic_history,
            R.drawable.ic_promo,
            R.drawable.ic_businesses,
            R.drawable.ic_settings
    };

    public static final String CUSTOM_CSS = "@font-face {\n" +
            "    font-family: 'feast';\n" +
            "    src: url('fonts/feasfbrg.ttf');\n" +
            "}\n" +
            "\n" +
            "body {font-family: 'feast';}";



    public static final String KEY_GRAVATAR_URL = "http://www.gravatar.com/avatar/";

    public static final String KEY_QR_TYPE = "qr_type";

    public static final String KEY_QR_TYPE_PERSONAL = "personal";
    public static final String KEY_QR_TYPE_PROMOTION = "promotion";
    public static final String KEY_QR_TYPE_BUSINESS = "business";
    public static final String KEY_QR_TYPE_PRODUCT = "product";
    public static final String KEY_QR_TYPE_SERVICE = "service";

    // Parse.com Classes

    public static final String CLASS_TRANSACTIONS = "transactions";

    /**
     * Transaction class objects
     */

    public static final String CLASS_TRANSACTIONS_FROM = "from";
    public static final String CLASS_TRANSACTIONS_TO = "to";
    public static final String CLASS_TRANSACTIONS_TYPE = "type";
    public static final String CLASS_TRANSACTIONS_AMOUNT = "amount";
    public static final String CLASS_TRANSACTIONS_RESOLUTION = "resolved";

    // Transaction types
    public static final int CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_AGENT = 0;
    public static final int CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_BANK = 1;
    public static final int CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_P2P = 2;
    public static final int CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_PRODUCT_PURCHASE = 3;
    public static final int CLASS_TRANSACTIONS_TYPE_FUNDS_REQUEST_SERVICE_PURCHASE = 4;

}

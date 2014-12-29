package ng.codehaven.eko.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import ng.codehaven.eko.Constants;

/**
 * Created by mrsmith on 11/24/14.
 * QR Encoding/Decoding Utility
 */
public class QRCodeHelper {

    public static Bitmap generateQRCode(String data, Context context, int color1, int color2, int w, int h) throws WriterException {
        com.google.zxing.Writer writer = new QRCodeWriter();
        String finalData = Uri.encode(data, "utf-8");
        BitMatrix bm = writer.encode(finalData, BarcodeFormat.QR_CODE, w, h);
        Bitmap QRBitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < w; i++) {//width
            for (int j = 0; j < h; j++) {//height
                QRBitMap.setPixel(i, j, bm.get(i, j) ? context.getResources().getColor(color1) : context.getResources().getColor(color2));
            }
        }
        return QRBitMap;
    }

    public static boolean isValidQRCode(JSONObject qrJSONObject) throws JSONException {
        String qr_type = getQRTypeString(qrJSONObject);
        return !qr_type.isEmpty();
    }

    public static int getQRType(JSONObject qrJSONObject) throws JSONException {
        String qr_type = getQRTypeString(qrJSONObject);
        if (qr_type != null) {
            if (qr_type.equals(Constants.KEY_QR_TYPE_PERSONAL)) {
                return 0;
            } else if (qr_type.equals(Constants.KEY_QR_TYPE_CONTACTS)) {
                return 1;
            } else if (qr_type.equals(Constants.KEY_QR_TYPE_BUSINESS)) {
                return 2;
            } else if (qr_type.equals(Constants.KEY_QR_TYPE_PRODUCT)) {
                return 3;
            } else if (qr_type.equals(Constants.KEY_QR_TYPE_SERVICE)) {
                return 4;
            }
        }
        return -1;
    }

    private static String getQRTypeString(JSONObject qrJSONObject) throws JSONException {
        return qrJSONObject.getString(Constants.KEY_QR_TYPE);
    }

    public static String[] getValuesFromString(String data){
        return data.split("\\s*,\\s*");
    }

}

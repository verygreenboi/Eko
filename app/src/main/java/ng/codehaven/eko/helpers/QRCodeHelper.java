package ng.codehaven.eko.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import ng.codehaven.eko.Constants;
import ng.codehaven.eko.utils.Logger;

/**
 * Created by mrsmith on 11/24/14.
 * QR Encoding/Decoding Utility
 */
public class QRCodeHelper {

    private static boolean isValid = false;

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

    public static boolean isValidQRCode(String QRURI) {
        String[] firstSplit = QRURI.split("http://eko.ng/");
        Logger.m(String.valueOf(firstSplit.length) + " -- " + firstSplit[1]);
        if (firstSplit.length >= 2) {
            isValid = true;
        }
        return isValid;
    }

    private static String[] getSecondSplit(String qrData) {
        String[] firstSplit = qrData.split("http://eko.ng/");
        return firstSplit[1].split("/");
    }

    public static int getQRType(String QRURI) {
        if (QRURI != null && isValidQRCode(QRURI)) {
            String qr_type = getSecondSplit(QRURI)[0];
            switch (qr_type) {
                case Constants.KEY_QR_TYPE_PERSONAL:
                    return 0;
                case Constants.KEY_QR_TYPE_BUSINESS:
                    return 1;
                case Constants.KEY_QR_TYPE_PRODUCT:
                    return 2;
                case Constants.KEY_QR_TYPE_SERVICE:
                    return 3;
            }
        }
        return -1;
    }

    private static String getQRTypeString(JSONObject qrJSONObject) throws JSONException {
        return qrJSONObject.getString(Constants.KEY_QR_TYPE);
    }

    public static String[] getValuesFromString(String data) {
        return data.split("\\s*,\\s*");
    }

    public static String[] getSecondSplitArray(String qrData) {
        return getSecondSplit(qrData);
    }
}

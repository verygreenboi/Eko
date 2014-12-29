package ng.codehaven.eko.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ng.codehaven.eko.Application;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.BusinessAction;
import ng.codehaven.eko.ui.activities.ContactsActivity;
import ng.codehaven.eko.ui.activities.PersonalUserQR;
import ng.codehaven.eko.ui.activities.ProductsAction;
import ng.codehaven.eko.ui.activities.ServiceAction;
import ng.codehaven.eko.ui.widgets.CameraPreview;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.QRCodeHelper;

/**
 * Created by mrsmith on 11/17/14.
 * Scanner Fragment
 * TODO: Fix camera preview bug
 */
public class ScanFragment extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    TextView scanText;
    Button scanButton;

    ImageScanner scanner;

    AlertDialog.Builder mAlertDialog;
    Intent i;

    Context ctx;
    View v;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private boolean isPreviewAllowed = false;

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
//                    scanText.setText("barcode result " + sym.getData());
                    Logger.s(getActivity(), sym.getData());

                    // Get QR data to List<String>
                    String[] values = QRCodeHelper.getValuesFromString(sym.getData());
                    Logger.m("Size = "+String.valueOf(values.length) + " "+ values[0]);
                    Logger.s(ctx, "Size = "+String.valueOf(values.length) + " "+ values[0]);
                    try {
                        JSONObject qrJSONObject = new JSONObject(sym.getData());
                        if (QRCodeHelper.isValidQRCode(qrJSONObject)) {
                            checkQRType(qrJSONObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    };

    private void checkQRType(JSONObject qrJSONObject) {
        int qrType = -1;
        try {
            qrType = QRCodeHelper.getQRType(qrJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (qrType) {
            case -1:
                // Erroneous JSONObject. Handle error.
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(ctx);
                errorDialog.setIcon(R.drawable.ic_launcher);
                errorDialog.setMessage(R.string.bad_qr_error_message);
                errorDialog.create().show();
                break;
            case 0:
                // Personal QRCode
                IntentUtils.startActivityWithJSON(ctx, qrJSONObject, PersonalUserQR.class);
                break;
            case 1:
                // Promotion QRCode
                IntentUtils.startActivityWithJSON(ctx, qrJSONObject, ContactsActivity.class);
                break;
            case 2:
                // Business QRCode
                IntentUtils.startActivityWithJSON(ctx, qrJSONObject, BusinessAction.class);
                break;
            case 3:
                // Product QRCode
                IntentUtils.startActivityWithJSON(ctx, qrJSONObject, ProductsAction.class);
                break;
            case 4:
                // Service QRCode
                IntentUtils.startActivityWithJSON(ctx, qrJSONObject, ServiceAction.class);
                break;
        }
    }

    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean b, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };


    static {
        System.loadLibrary("iconv");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ctx = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_scanner, container, false);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        autoFocusHandler = new Handler();
        mCamera = Application.getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(getActivity(), mCamera, previewCb, autoFocusCB);

        FrameLayout previewLayout = (FrameLayout) v.findViewById(R.id.scannerPreview);

        previewLayout.addView(mPreview);
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPreview != null){
            mPreview = null;
        }
    }

    /**
     * Clear any existing preview / camera.
     */
    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    private void doScan() {

        if (barcodeScanned) {
            barcodeScanned = false;
            scanText.setText("Scanning...");
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }
    }


}

package ng.codehaven.eko.ui.fragments;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
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

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.widgets.CameraPreview;
import ng.codehaven.eko.utils.IntentUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public final static String EXTRA_MESSAGE = "ng.codehaven.eko.QR_DATA";


    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    private boolean isPreviewAllowed = false;

    private View v;

    private String TAG = LoginFragment.class.getSimpleName();

    TextView scanText;

    Button scanButton;
    ImageScanner scanner;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

//        Logger.s(getActivity(), TAG + " onAttach");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        Logger.s(getActivity(), TAG + " onCreateView");
        v = inflater.inflate(R.layout.fragment_login, container, false);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
//        Logger.s(getActivity(), TAG + " onResume");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(getActivity(), mCamera, previewCb, autoFocusCB);
        final FrameLayout preview = (FrameLayout) v.findViewById(R.id.cameraPreview);
        scanText = (TextView) v.findViewById(R.id.scanText);

        scanButton = (Button) v.findViewById(R.id.ScanButton);
        if (isPreviewAllowed) {
            preview.addView(mPreview);
        } else {
            scanText.setVisibility(View.INVISIBLE);
        }


        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isPreviewAllowed) {
                    doScan();
                } else {
                    isPreviewAllowed = true;
                    preview.addView(mPreview);
                    scanText.setVisibility(View.VISIBLE);
                    doScan();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
//        Logger.s(getActivity(), TAG + " onPause");
        releaseCamera();
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
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

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
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
                    IntentUtils.startActivityWithStringExtra(getActivity(),
                            RegisterFragment.class,
                            EXTRA_MESSAGE,
                            sym.getData());

                    barcodeScanned = true;
                    getActivity().finish();
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
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

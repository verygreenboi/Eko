package ng.codehaven.eko.ui.fragments;


import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.RegisterLoginActivity;
import ng.codehaven.eko.utils.IntentUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    public final static String EXTRA_MESSAGE = "ng.codehaven.eko.QR_DATA";

    private View v;

    Button scanButton;
    ImageScanner scanner;

    DoScanQR handler;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        handler = (DoScanQR) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        Logger.s(getActivity(), TAG + " onCreateView");
        v = inflater.inflate(R.layout.fragment_login, container, false);
        scanButton = (Button) v.findViewById(R.id.ScanButton);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        scanButton.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    IntentUtils.startActivityWithStringExtra(getActivity(),
                            RegisterLoginActivity.class,
                            EXTRA_MESSAGE,
                            sym.getData());
                    getActivity().finish();
                }
            }
        }
    };

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ScanButton){
            handler.doScan(v);
        }
    }

    public interface DoScanQR{
        public void doScan(View v);
    }
}

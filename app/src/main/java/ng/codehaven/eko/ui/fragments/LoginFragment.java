package ng.codehaven.eko.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.sourceforge.zbar.ImageScanner;

import ng.codehaven.eko.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    public final static String EXTRA_MESSAGE = "ng.codehaven.eko.QR_DATA";

    private View v;

    Button scanButton;
    Button loginButton;
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
        loginButton = (Button) v.findViewById(R.id.LoginButton);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        scanButton.setOnClickListener(this);

        loginButton.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ScanButton){
            handler.doScan(v);
        }else if (v.getId() == R.id.LoginButton){
            handler.doLogin(v);
//            IntentUtils.startActivity(getActivity(), AuthenticatorActivity.class);
        }
    }

    public interface DoScanQR{
        public void doScan(View v);
        public void doLogin(View v);
    }
}

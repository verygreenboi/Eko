package ng.codehaven.eko.ui.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.QRCodeHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowQR extends Fragment {


    public ShowQR() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_qr, container, false);
        Bundle qrData = getArguments();
        String qrDataString = qrData.getString("qrData");

        ImageView qrImageView = (ImageView)v.findViewById(R.id.qrImageView);

        try {
            Bitmap qrBitmap = QRCodeHelper.generateQRCode(qrDataString, getActivity(), R.color.colorPrimary, android.R.color.transparent, 240, 240);
            qrImageView.setImageBitmap(qrBitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return v;
    }


}

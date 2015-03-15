package ng.codehaven.eko.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import ng.codehaven.eko.R;
import ng.codehaven.eko.helpers.QRCodeHelper;
import ng.codehaven.eko.utils.Logger;

/**
 * Created by mrsmith on 11/26/14.
 */
public class TapToScanFragment extends Fragment implements View.OnClickListener {
    Context ctx;
    Bundle user;
    ImageView qrImageView;
    //    CustomTextView tapToScanTextView;
    FrameLayout tapCameraPreview;

    Animation
            moveAnimation,
            fadeOutAnimation,
            mvAnim;

    ScanClickHandler mHandler;

    public TapToScanFragment() {
    }

    private Animation moveAnimation() {
        return moveAnimation = AnimationUtils.loadAnimation(ctx, R.anim.move);
    }

    private Animation fadeOutAnimation() {
        return fadeOutAnimation = AnimationUtils.loadAnimation(ctx, R.anim.fadeout);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ctx = getActivity();
        user = getArguments();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        String data = user.getString("userQR");
        Logger.m(data);
        tapCameraPreview = (FrameLayout) v.findViewById(R.id.tapCameraPreview);

//        mScanButton.setTypeface(FontCache.get("fonts/Roboto-Medium.ttf", getActivity()));
//        mScanButton.setTextColor(getResources().getColor(R.color.primary_text_default_material_dark));

        try {
            Bitmap qrBitmap = QRCodeHelper.generateQRCode(
                    data,
                    getActivity(),
                    R.color.colorPrimary,
                    R.color.background_material_light,
                    340,
                    340
            );
            qrImageView = (ImageView) v.findViewById(R.id.qrImageView);
            qrImageView.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

//        qrImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clearOutViewsAnimations();
//
//            }
//        });

        qrImageView.setOnClickListener(this);

        return v;
    }
    private void clearOutViewsAnimations() {
        qrImageView.setVisibility(View.GONE);
        tapCameraPreview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        mHandler.onScanClickHandler(v);
    }

    public interface ScanClickHandler{
        public void onScanClickHandler(View v);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mHandler = (ScanClickHandler) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}

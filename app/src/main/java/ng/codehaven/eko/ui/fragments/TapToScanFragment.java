package ng.codehaven.eko.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.HomeActivity;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.QRCodeHelper;

/**
 * Created by mrsmith on 11/26/14.
 */
public class TapToScanFragment extends Fragment {
    Context ctx;
    Bundle user;
    ImageView qrImageView;
    Button mScanButton;
    //    CustomTextView tapToScanTextView;
    FrameLayout tapCameraPreview;

    Animation
            moveAnimation,
            fadeOutAnimation,
            mvAnim;

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
//        tapToScanTextView = (CustomTextView) v.findViewById(R.id.tapToScanTextView);
        tapCameraPreview = (FrameLayout) v.findViewById(R.id.tapCameraPreview);
        mScanButton = (Button)v.findViewById(R.id.ScanButton);

        try {
            Bitmap qrBitmap = QRCodeHelper.generateQRCode(
                    data,
                    getActivity(),
                    android.R.color.white,
                    R.color.colorPrimary,
                    240,
                    240
            );
            qrImageView = (ImageView) v.findViewById(R.id.qrImageView);
            qrImageView.setImageBitmap(qrBitmap);
            Logger.m(data);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        qrImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearOutViewsAnimations();

            }
        });
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearOutViewsAnimations();
            }
        });

//        tapToScanTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clearOutViewsAnimations();
//            }
//        });

        return v;
    }

    private void clearOutViewsAnimations() {
//        qrImageView.startAnimation(fadeOutAnimation());
        qrImageView.setVisibility(View.GONE);
//        tapToScanTextView.startAnimation(moveAnimation());
//        tapToScanTextView.setVisibility(View.GONE);
        tapCameraPreview.setVisibility(View.VISIBLE);

        Intent i = new Intent(HomeActivity.SCANNER_INTENT_FILTER);
        ctx.sendBroadcast(i);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(500);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    public interface onViewsClicked {
        public void onClicked(int view);
    }
}

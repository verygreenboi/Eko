package ng.codehaven.eko.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import ng.codehaven.eko.R;

public class CaptureActivity extends Activity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    @InjectView(R.id.scannerView) protected ZXingScannerView mScannerView;
    List<BarcodeFormat> formats;
    @InjectView(R.id.flashButton) protected ImageButton mFlashButton;
    private boolean mFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formats = new ArrayList<BarcodeFormat>();

        formats.add(BarcodeFormat.QR_CODE);
        setContentView(R.layout.activity_capture);                // Set the scanner view as the content view
        ButterKnife.inject(this);

        mFlashButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.setFormats(formats);
        mScannerView.startCamera();          // Start camera
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {
        Bundle b = new Bundle();
        b.putString("qrData", result.getText());

        Intent i = new Intent();
        i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        mFlash = !mFlash;
        if (mFlash){
            mFlashButton.setImageResource(R.drawable.ic_flash_on_white);
        }else {
            mFlashButton.setImageResource(R.drawable.ic_flash_off_white);
        }

        mScannerView.setFlash(mFlash);
    }
}

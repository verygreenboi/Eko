package ng.codehaven.eko.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.helpers.FileHelper;
import ng.codehaven.eko.helpers.ImageResizer;
import ng.codehaven.eko.ui.widgets.RoundedImageView;
import ng.codehaven.eko.utils.FontCache;
import ng.codehaven.eko.utils.Logger;

public class EnterNewBusinessActivity extends ActionBarActivity implements View.OnClickListener {

    public static final int KEY_IMAGE_REQUEST_CODE = 2200;
    public static final int KEY_FILE_SIZE_LIMIT = 1024 * 1024 * 10;
    public int mBusinessType = 0;

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolBar;

    private TextView mToolBarTitle;

    @InjectView(R.id.businessTitle) protected EditText mTitle;
    @InjectView(R.id.businessPhone) protected EditText mPhone;
    @InjectView(R.id.businessAddress) protected EditText mAddress;
    @InjectView(R.id.transportSwitch) protected SwitchCompat mTransport;

    @InjectView(R.id.businessLogo)
    protected RoundedImageView mBusinessLogo;

    @InjectView(R.id.createBusinessBtn)
    protected Button mCreateBtn;
    private Uri mMediaUri;
    private byte[] fileBytes;

    private ProgressDialog mLoading;

    private boolean isLogo, isTitle, isPhone, isAddress, isTransport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_business_layout);
        ButterKnife.inject(this);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolBarTitle = (TextView) mToolBar.findViewById(R.id.toolbar_title);
        mToolBarTitle.setTypeface(FontCache.get(Constants.ABC_FONT, EnterNewBusinessActivity.this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolBarTitle.setText(R.string.app_name);
        mBusinessLogo.setOnClickListener(this);
        mCreateBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_new_business, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.businessLogo:
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, KEY_IMAGE_REQUEST_CODE);
                break;
            case R.id.createBusinessBtn:
                Logger.m(mTitle.getText().toString()+" "+mPhone.getText().toString()+" "+mAddress.getText().toString()+" "+mTransport.isChecked());
                isTitle = mTitle.getText().toString().isEmpty();
                isPhone = mPhone.getText().toString().isEmpty();
                isAddress = mAddress.getText().toString().isEmpty();
                isTransport = mTransport.isChecked();

                if (isTransport){
                    mBusinessType = 1;
                }

                if (mMediaUri != null && fileBytes != null) {
                    if (isAddress) {
                        showAlertDialog(R.string.error_address_title, R.string.error_address_message);
                        mAddress.requestFocus();
                    }else if(isTitle){
                        showAlertDialog(R.string.error_title_title, R.string.error_title_message);
                        mTitle.requestFocus();
                    }else if(isPhone){
                        showAlertDialog(R.string.error_phone_title, R.string.error_phone_message);
                        mPhone.requestFocus();
                    } else {
                        final ParseObject business = createBusiness(mMediaUri, fileBytes);
                        mLoading = ProgressDialog.show(EnterNewBusinessActivity.this,"Please wait", "Creating business...");
                        business.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Logger.m("business created");
                                    createBusinessAccount(business);
                                } else {
                                    mLoading.dismiss();
                                    Logger.m(e.getMessage());
                                    showAlertDialog(R.string.general_error_message_txt, R.string.create_business_error_message);
                                }
                            }
                        });
                    }
                } else {
                    showAlertDialog(R.string.create_business_error_title, R.string.create_business_error_message);
                }
                break;
        }
    }

    private void showAlertDialog(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(message))
                .setTitle(getString(title))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createBusinessAccount(final ParseObject business) {
        business.pinInBackground("myBusiness");
        ParseRelation<ParseObject> mBusinessRelation;
        ParseObject mAccount = new ParseObject("Accounts");
        mAccount.put("type", Constants.KEY_QR_TYPE_BUSINESS);
        mAccount.put("currentBalance", 0);
        mAccount.put("active", true);
        mBusinessRelation = mAccount.getRelation(Constants.KEY_BUSINESS_ACCOUNT_HOLDERS_RELATION);
        mBusinessRelation.add(business);
        mAccount.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mLoading.dismiss();
                    Logger.s(EnterNewBusinessActivity.this, "Your business " + business.getString("title") + " has been created.");
                    NavUtils.navigateUpFromSameTask(EnterNewBusinessActivity.this);
                } else {
                    mLoading.dismiss();
                    Logger.m(e.getMessage());
                    showAlertDialog(R.string.create_business_error_title, R.string.general_error_message_txt);
                }
            }
        });
        mAccount.saveEventually();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case KEY_IMAGE_REQUEST_CODE:
                    if (data != null) {
                        mMediaUri = data.getData();
                        fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
                        Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(fileBytes, FileHelper.SHORT_SIDE_TARGET);
                        mBusinessLogo.setImageBitmap(bitmap);
                        if (!isLogo) {
                            isLogo = true;
                            mCreateBtn.setEnabled(true);
                        }
                    }
                    break;
            }
        } else if (resultCode != RESULT_CANCELED) {
            Logger.s(this, getString(R.string.general_error_message_txt));
        }
    }

    private ParseObject createBusiness(Uri mMediaUri, byte[] fileBytes) {
        ParseObject business = new ParseObject("Businesses");
        business.put("ceo", ParseUser.getCurrentUser());
        business.put("type", mBusinessType);
        business.put("title", mTitle.getText().toString().trim());
        business.put("phone", mPhone.getText().toString().trim());
        business.put("address", mAddress.getText().toString().trim());

        if (fileBytes == null) {
            return null;
        } else {
            fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            String fileName = FileHelper.getFileName(this, mMediaUri, Constants.TYPE_IMAGE);
            ParseFile file = new ParseFile(fileName, fileBytes);
            business.put("logo", file);
            return business;
        }
    }
}

package ng.codehaven.eko.ui.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.fragments.IntroAFragment;
import ng.codehaven.eko.ui.fragments.IntroBFragment;
import ng.codehaven.eko.ui.fragments.IntroCFragment;
import ng.codehaven.eko.ui.fragments.LoginFragment;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;
import ng.codehaven.eko.utils.UIUtils;

public class AuthActivity extends ActionBarActivity implements LoginFragment.DoScanQR {

    @InjectView(R.id.authViewPager)
    protected ViewPager mViewPager;
    @InjectView(R.id.containerView)
    protected RelativeLayout mContainerView;
    @InjectView(R.id.landing_bg_2)
    protected ImageView mLoadingBg2;
    @InjectView(R.id.logo)
    protected ImageView mLogo;
    @InjectView(R.id.img_bg_slide_3) protected ImageView mBottomImage;

    int mCurrentPage = 1;
    float x;
    float initX;


    AccelerateDecelerateInterpolator sInterpolator = new AccelerateDecelerateInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.inject(this);
        mLoadingBg2.setAlpha(0f);
        mBottomImage.setAlpha(0f);
        initX = mLogo.getX();

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i2) {
                Logger.m("Position --> " + String.valueOf(i) + " Position offset --> " + String.valueOf(v) + " Position Offset Pixels --> " + String.valueOf(i2));
                if (i == 0) {
                    mLoadingBg2.setAlpha(v);
                }
                if (i == 1 && i2 > 0) {
                    mLoadingBg2.setAlpha(1 - v);
                    mLogo.setTranslationX(1 - v);
                }
                if (i== 1){
                    mBottomImage.setAlpha(v);
                }
                if (i == 2 && i2 > 0){
                    mBottomImage.setAlpha(1-v);
                }
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        mCurrentPage = 1;
                        if (mLogo.getX() > 55){
                            TranslateAnimation ta = new TranslateAnimation(180, 0 , 0, 0);
                            ta.setDuration(500);
                            ta.setInterpolator(sInterpolator);
                            ta.setFillAfter(true);
                            mLogo.setAnimation(ta);
                        }
                        break;
                    case 1:
                        if (mCurrentPage == 1) {
                            TranslateAnimation ta = new TranslateAnimation(0, 180, 0, 0);
                            ta.setDuration(500);
                            ta.setInterpolator(sInterpolator);
                            ta.setFillAfter(true);
                            mLogo.setAnimation(ta);
                        }
                        mCurrentPage = 2;
                        x = mLogo.getX();
                        break;
                    case 2:
                        if (mCurrentPage != 4) {
                            mLogo.setX(x);
                        } else {
                            TranslateAnimation ta = new TranslateAnimation(0, 180, 0, 0);
                            ta.setDuration(500);
                            ta.setInterpolator(sInterpolator);
                            ta.setFillAfter(true);
                            mLogo.setAnimation(ta);
                        }
                        break;
                    case 3:
                        mLogo.setX(x);
                        TranslateAnimation ta = new TranslateAnimation(x, initX, 0, 0);
                        ta.setDuration(500);
                        ta.setInterpolator(sInterpolator);
                        ta.setFillAfter(true);
                        mLogo.setAnimation(ta);
                        mCurrentPage = 4;
                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doScan(View v) {
        if (v.getId() == R.id.ScanButton) {
            Intent i = new Intent("com.google.zxing.client.android.SCAN");
            i.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(i, 2204);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2204) {
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras();
                String qrURL = UIUtils.unescape(b.getString("qrData"));

                IntentUtils.startActivityWithStringExtra(this,
                        RegisterLoginActivity.class,
                        LoginFragment.EXTRA_MESSAGE,
                        qrURL);
            }
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
                case 0:
                    fragment = new IntroAFragment();
                    break;
                case 1:
                    fragment = new IntroBFragment();
                    break;
                case 2:
                    fragment = new IntroCFragment();
                    break;
                case 3:
                    fragment = new LoginFragment();
                    break;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}

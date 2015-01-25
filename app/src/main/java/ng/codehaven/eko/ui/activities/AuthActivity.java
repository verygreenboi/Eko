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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.fragments.IntroAFragment;
import ng.codehaven.eko.ui.fragments.IntroBFragment;
import ng.codehaven.eko.ui.fragments.IntroCFragment;
import ng.codehaven.eko.ui.fragments.LoginFragment;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.UIUtils;

public class AuthActivity extends ActionBarActivity implements LoginFragment.DoScanQR {

    @InjectView(R.id.authViewPager)
    protected ViewPager mViewPager;
    @InjectView(R.id.containerView)
    protected RelativeLayout mContainerView;
    @InjectView(R.id.bgIntroA)
    View mIntroA;
    @InjectView(R.id.bgIntroB)
    protected LinearLayout mIntroB;

    @InjectViews({R.id.circleA,
            R.id.circleb,
            R.id.circlec})
    protected FrameLayout[] circle;


    private Animation mFadeAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.inject(this);


        mIntroB.setVisibility(View.INVISIBLE);

        for (FrameLayout fl : circle) {
            fl.setVisibility(View.INVISIBLE);
        }
        int duration = 0;
        fadeInAnimation(duration);

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int mCurrentPage;

            @Override
            public void onPageScrolled(int i, float v, int i2) {
//                Logger.s(AuthActivity.this, String.valueOf(i));
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageSelected(int i) {
//                Logger.s(AuthActivity.this, String.valueOf(i));
                mCurrentPage = i;
                if (mCurrentPage == 3) {
                    mIntroA.setVisibility(View.INVISIBLE);
                } else {
                    switch (mCurrentPage) {
                        case 0:
                            if (mIntroA.getVisibility() != View.VISIBLE) {
                                mIntroA.startAnimation(fadeInAnimation(500));
                                mIntroA.setVisibility(View.VISIBLE);
                            } else {
                                mIntroA.setVisibility(View.VISIBLE);
                            }
                            fadeOutFragB();
                            break;
                        case 1:
                            if (isVisible(mIntroA)) {
                                mIntroA.startAnimation(fadeOutAnimation(300));
                                mIntroA.setVisibility(View.INVISIBLE);
                            }
                            if (mIntroB.getVisibility() == View.INVISIBLE) {
                                animateCirclesIn();
                            }
                            break;
                        case 2:
                            fadeOutFragB();
                            break;


                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    private void fadeOutFragB() {
        if (isVisible(mIntroB)) {
            mIntroB.startAnimation(fadeOutAnimation(300));
            mIntroB.setVisibility(View.INVISIBLE);
        }
    }

    private void animateCirclesIn() {
        mIntroB.setVisibility(View.VISIBLE);
        // TODO: Animate circles in

        circle[0].startAnimation(fadeInAnimation(500));
        circle[0].setVisibility(View.VISIBLE);
        circle[1].startAnimation(fadeInAnimation(1000));
        circle[1].setVisibility(View.VISIBLE);
        circle[2].startAnimation(fadeInAnimation(1500));
        circle[2].setVisibility(View.VISIBLE);
    }

    private Animation fadeInAnimation(int duration) {
        mFadeAnimation = new AlphaAnimation(0f, 1f);
        mFadeAnimation.setDuration(duration);
        return mFadeAnimation;
    }

    private Animation fadeOutAnimation(int duration) {
        mFadeAnimation = new AlphaAnimation(1f, 0f);
        mFadeAnimation.setDuration(duration);
        return mFadeAnimation;
    }

    private boolean isVisible(View v) {
        return v.getVisibility() == View.VISIBLE;
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

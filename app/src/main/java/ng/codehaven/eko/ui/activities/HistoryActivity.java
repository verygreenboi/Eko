package ng.codehaven.eko.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.fragments.historyFragments.FundRequestHistory;
import ng.codehaven.eko.ui.fragments.historyFragments.PurchaseFragment;
import ng.codehaven.eko.ui.fragments.historyFragments.TicketsFragment;
import ng.codehaven.eko.ui.fragments.historyFragments.TransferFragment;
import ng.codehaven.eko.ui.views.SlidingTabLayout;
import ng.codehaven.eko.utils.FontCache;

public class HistoryActivity extends ActionBarActivity {

    public static final int[] mTabIndicatorColors =
            {
                    R.color.colorPrimaryDark,

                    R.color.colorPrimary,

                    R.color.colorPrimaryLight,

                    R.color.colorPrimaryAccent_1
            };

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolbar;

    @InjectView(R.id.pager)
    protected ViewPager mViewPager;

    @InjectView(R.id.tabs)
    protected SlidingTabLayout mSlidingTabs;

    private TextView mToolBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.inject(this);

        // Setup toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parentIntent = getSupportParentActivityIntent();
                startActivity(parentIntent);
            }
        });

        mToolBarTitle = (TextView)mToolbar.findViewById(R.id.toolbar_title);
        mToolBarTitle.setTypeface(FontCache.get(Constants.ABC_FONT, HistoryActivity.this));
        mToolBarTitle.setText(getString(R.string.title_activity_history));

        HistoryPagerAdapter adapter = new HistoryPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mSlidingTabs.setViewPager(mViewPager);
        mSlidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.colorPrimaryDark));
        mSlidingTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                    mSlidingTabs.setSelectedIndicatorColors(getResources().getColor(mTabIndicatorColors[i]));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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

    public class HistoryPagerAdapter extends FragmentPagerAdapter {

        private String[] TITLES = {"Purchases", "Transfers", "Tickets", "Fund Requests"};

        public HistoryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            switch (i) {
                case 0:
                    fragment = new PurchaseFragment();
                    break;
                case 1:
                    fragment = new TransferFragment();
                    break;
                case 2:
                    fragment = new TicketsFragment();
                    break;
                case 3:
                    fragment = new FundRequestHistory();
                    break;
                default:
                    fragment = new PurchaseFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }
}

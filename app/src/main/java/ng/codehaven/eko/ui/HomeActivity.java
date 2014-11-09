package ng.codehaven.eko.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.R;


public class HomeActivity extends BaseToolbarActivity {

    @InjectView(R.id.homeToolBar) protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                .add( R.id.container, new PlaceholderFragment() )
//                .commit();
//        }
    }

    /**
     *
     * @return layoutId
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    /**
     *
     * @return toolbar id
     */
    @Override
    protected int getToolbarResource() {
        return mToolbar.getId();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_home, menu );
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

        return super.onOptionsItemSelected( item );
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate( R.layout.fragment_home, container, false );
            return rootView;
        }
    }
}

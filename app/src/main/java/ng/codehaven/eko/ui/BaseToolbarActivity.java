package ng.codehaven.eko.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by mrsmith on 11/9/14.
 * BaseToolBar Activity
 */

public abstract class BaseToolbarActivity extends ActionBarActivity{

    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        mToolbar = (Toolbar)findViewById(getToolbarResource());

        setSupportActionBar(mToolbar);

    }

    protected abstract int getLayoutResource();
    protected abstract int getToolbarResource();
}

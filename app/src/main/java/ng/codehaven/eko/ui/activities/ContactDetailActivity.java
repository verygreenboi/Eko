package ng.codehaven.eko.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.BuildConfig;
import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Utils;

/**
 * This class defines a simple FragmentActivity as the parent of {@link ng.codehaven.eko.ui.fragments.ContactDetailFragment}.
 */
public class ContactDetailActivity extends ActionBarActivity {

    @InjectView(R.id.homeToolBar)
    protected Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            // Enable strict mode checks when in debug modes
            Utils.enableStrictMode();
        }

        setContentView(R.layout.content_detail);

        ButterKnife.inject(this);

        setupToolBar(this, mToolBar, ContactsActivity.class, R.drawable.ic_arrow_back);
    }

    private void setupToolBar(final Context ctx, Toolbar toolbar, final Class destination, int icon) {
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.startActivity(ctx, destination);
            }
        });
    }
}

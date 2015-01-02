package ng.codehaven.eko.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.fragments.ContactDetailFragment;
import ng.codehaven.eko.ui.fragments.ContactListFragment;
import ng.codehaven.eko.utils.Utils;

public class ContactsActivity extends ActionBarActivity implements
        ContactListFragment.OnContactsInteractionListener {

    // If true, this is a larger screen device which fits two panes
    private boolean isTwoPaneLayout;

    private ContactDetailFragment mContactDetailFragment;

    @InjectView(R.id.homeToolBar) protected Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Constants.BUILD_DEBUG) {
            Utils.enableStrictMode();
        }

        isTwoPaneLayout = getResources().getBoolean(R.bool.large_layout);

        if (isTwoPaneLayout) {
            // If two pane layout, locate the contact detail fragment
            mContactDetailFragment = (ContactDetailFragment)
                    getSupportFragmentManager().findFragmentById(R.id.contact_detail);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        ButterKnife.inject(this);

        setSupportActionBar(mToolBar);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_promo, menu);
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

    /**
     * Called when a contact is selected from the ListView.
     *
     * @param contactUri The contact Uri.
     */
    @Override
    public void onContactSelected(Uri contactUri) {
        if (isTwoPaneLayout && mContactDetailFragment != null) {
            // If two pane layout then update the detail fragment to show the selected contact
            mContactDetailFragment.setContact(contactUri);
        } else {
            // Otherwise single pane layout, start a new ContactDetailActivity with
            // the contact Uri
            Intent intent = new Intent(this, ContactDetailActivity.class);
            intent.setData(contactUri);
            startActivity(intent);
        }
    }

    /**
     * Called when the ListView selection is cleared like when
     * a contact search is taking place or is finishing.
     */
    @Override
    public void onSelectionCleared() {
        if (isTwoPaneLayout && mContactDetailFragment != null) {
            mContactDetailFragment.setContact(null);
        }
    }
}

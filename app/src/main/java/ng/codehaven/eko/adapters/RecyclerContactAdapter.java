package ng.codehaven.eko.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import butterknife.ButterKnife;
import ng.codehaven.eko.R;
import ng.codehaven.eko.models.Contact;
import ng.codehaven.eko.utils.ImageCacheManager;

/**
 * Created by Thompson on 08/02/2015.
 */
public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MIN_COUNT = 1;
    private static final int[] VIEW_TYPE = {0,1,2};

    private Context mContext;

    private ArrayList<Contact> contacts;

    private ImageLoader imageLoader;

    private View v;


    public RecyclerContactAdapter(Context mContext,ArrayList<Contact> contacts) {
        setHasStableIds(true);
        this.contacts = contacts;
        this.mContext = mContext;
        this.imageLoader = ImageCacheManager.getInstance().getImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (VIEW_TYPE[0]== viewType){
            return new ContactItemHeaderHolder(doLayout(mContext, parent, false, R.layout.contact_list_invite));
        }else  if (VIEW_TYPE[1]==viewType){
            return new ContactDivider(doLayout(mContext, parent,false, R.layout.contact_list_divider));
        }else if (VIEW_TYPE[2]==viewType){
            return new ContactItemHolder(doLayout(mContext, parent, false, R.layout.contact_list_item)) ;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    static class ContactItemHeaderHolder extends RecyclerView.ViewHolder{

        public ContactItemHeaderHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    static class ContactDivider extends RecyclerView.ViewHolder{

        public ContactDivider(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    static class ContactItemHolder extends RecyclerView.ViewHolder{

        public ContactItemHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    @Override
    public int getItemCount() {
        return MIN_COUNT + contacts.size();
    }

    private View doLayout(Context c, ViewGroup parent, boolean rootView, int layout){
        return LayoutInflater.from(c).inflate(layout, parent, rootView);
    }
}

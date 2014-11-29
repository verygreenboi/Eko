package ng.codehaven.eko.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;

/**
 * Created by mrsmith on 11/11/14.
 */
public class NavListAdapter extends ArrayAdapter<Integer> {

    private ArrayList<Integer> items;

    private Context context;

    boolean isSeperator = true;

    public NavListAdapter(Context context, ArrayList<Integer> items) {
        super(context, 0, items);
        context = getContext();
    }

    @Override
    public int getPosition(Integer item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer item = getItem(position);

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            switch (getItem(position)) {
                case Constants.NAVDRAWER_ITEM_SEPARATOR:
                    convertView = inflater.inflate(R.layout.nav_drawer_seperator, parent, false);
                    break;
                case Constants.NAVDRAWER_ITEM_SEPARATOR_SPECIAL:
                    convertView = inflater.inflate(R.layout.nav_drawer_seperator, parent, false);
                    break;
                default:

                    convertView = inflater.inflate(R.layout.nav_drawer_item, parent, false);
                    holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                    holder.label = (TextView) convertView.findViewById(R.id.title);
            }

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Integer currentItem = getItem(position);

        switch (currentItem) {
            case Constants.NAVDRAWER_ITEM_SEPARATOR:
                break;
            case Constants.NAVDRAWER_ITEM_SEPARATOR_SPECIAL:
                break;
            default:
                holder.icon.setImageResource(Constants.NAVDRAWER_ICON_RES_ID[currentItem]);
                holder.label.setText(Constants.NAVDRAWER_TITLE_RES_ID[currentItem]);
                break;
        }

        Log.d("TAG", String.valueOf(currentItem));

        return convertView;
    }

    public static class ViewHolder {
        ImageView icon;
        TextView label;
    }

}

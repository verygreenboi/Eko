package ng.codehaven.eko.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ng.codehaven.eko.R;
import ng.codehaven.eko.models.Contact;

/**
 * Created by Thompson on 09/01/2015.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item
        Contact contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View view = convertView;

        final viewHolder holder;

        if (view == null) {
            holder = new viewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.adapter_contact_item, parent, false);
            // Populate the data into the template view using the data object
            holder.tvName = (TextView) view.findViewById(R.id.tvName);
            holder.tvEmail = (TextView) view.findViewById(R.id.tvEmail);
            holder.tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        holder.tvName.setText(contact.name);
        holder.tvEmail.setText("");
        holder.tvPhone.setText("");
        if (contact.emails.size() > 0 && contact.emails.get(0) != null) {
            holder.tvEmail.setText(contact.emails.get(0).address);
        }
        if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
            holder.tvPhone.setText(contact.numbers.get(0).number);
        }
        return view;
    }

    public static class viewHolder implements View.OnClickListener{
        TextView tvName, tvEmail, tvPhone;

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tvName:
                    // Go to contact detail
                    break;
                case R.id.tvEmail:
                    // Fire email intent
                    break;
                case R.id.tvPhone:
                    // Fire phone intent
                    break;
                default:
                    // Go to contact detail
                    break;
            }
        }
    }

}

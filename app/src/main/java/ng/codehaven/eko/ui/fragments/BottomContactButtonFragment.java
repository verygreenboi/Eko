package ng.codehaven.eko.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ng.codehaven.eko.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomContactButtonFragment extends Fragment {


    public BottomContactButtonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_contact_button, container, false);
    }


}

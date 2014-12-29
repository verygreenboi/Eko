package ng.codehaven.eko.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ng.codehaven.eko.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class IntroCFragment extends Fragment {


    public IntroCFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_intro_c, container, false);

        return v;
    }


}

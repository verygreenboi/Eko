package ng.codehaven.eko.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.views.CustomTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroAFragment extends Fragment {


    public IntroAFragment() {
        // Required empty public constructor
    }

    String bodyText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_intro_a, container, false);

        CustomTextView cTitle = (CustomTextView)v.findViewById(R.id.introTitle);
        CustomTextView cBody = (CustomTextView)v.findViewById(R.id.body);


        return v;
    }


}

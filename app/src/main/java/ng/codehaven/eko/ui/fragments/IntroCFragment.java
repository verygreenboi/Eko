package ng.codehaven.eko.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.Logger;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class IntroCFragment extends Fragment {

    protected FrameLayout mTitleLayout, mBodyLayout;

    View v;


    public IntroCFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_intro_c, container, false);

        mTitleLayout = (FrameLayout) v.findViewById(R.id.introTitle);
        mBodyLayout = (FrameLayout) v.findViewById(R.id.frameLayout);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.s(getActivity(), "Destroyed");
    }
}

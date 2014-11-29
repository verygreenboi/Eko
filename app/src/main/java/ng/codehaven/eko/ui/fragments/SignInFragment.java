package ng.codehaven.eko.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import ng.codehaven.eko.Application;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.HomeActivity;
import ng.codehaven.eko.utils.IntentUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
        final EditText pin = (EditText) v.findViewById(R.id.logicalPinEditText);
        Button signInBtn = (Button) v.findViewById(R.id.signInBtn);

        Bundle user = this.getArguments();
        final String userId = user.getString("user_id");

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getPin = pin.getText().toString().trim();
                ParseUser.logInInBackground(userId, getPin, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null){
                            Application.UpdateParseInstallation(parseUser);
                            IntentUtils.startActivity(getActivity(), HomeActivity.class);
                            getActivity().finish();
                        }
                    }
                });
            }
        });

        return v;
    }


}

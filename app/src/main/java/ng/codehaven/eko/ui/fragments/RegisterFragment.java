package ng.codehaven.eko.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import ng.codehaven.eko.Application;
import ng.codehaven.eko.Constants;
import ng.codehaven.eko.R;
import ng.codehaven.eko.ui.activities.HomeActivity;
import ng.codehaven.eko.utils.IntentUtils;
import ng.codehaven.eko.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements Validator.ValidationListener {

    View v;

    @Required(order = 1)
    private EditText
            firstNameEditText;
    @Required(order = 2)
    private EditText
            lastNameEditText;
    @Required(order = 3)
    @Email(order = 4)
    private EditText
            emailEditText;
    @Password(order = 5)
    @TextRule(order = 6, minLength = 6, message = "Enter at least 6 characters.")
    private EditText password;

    @ConfirmPassword(order = 7)
    private EditText confirmPassword;

    @Required(order = 8)
    private EditText
            phoneNumber;

    private  Validator validator;

    private Bundle user;
    private String psword;
    private String userId;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_register, container, false);

        validator = new Validator(this);
        validator.setValidationListener(this);

        user = getArguments();

        Button registerBtn = (Button) v.findViewById(R.id.registerBtn);
        firstNameEditText = (EditText)v.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText)v.findViewById(R.id.lastNameEditText);
        emailEditText = (EditText)v.findViewById(R.id.emailEditText);
        password = (EditText)v.findViewById(R.id.password);
        confirmPassword = (EditText)v.findViewById(R.id.confirmPassword);
        phoneNumber = (EditText)v.findViewById(R.id.phoneNumber);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        return v;
    }


    /**
     * Called when all the {@link Rule}s added to this Validator are valid.
     */
    @Override
    public void onValidationSucceeded() {
        if (user != null){
            userId = user.getString("user_id");
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String fullName = firstName+" "+lastName;
            String email = emailEditText.getText().toString().toLowerCase().trim();
            psword = password.getText().toString().trim();
            String phone = phoneNumber.getText().toString().trim();

            final ParseUser newUser = new ParseUser();
            newUser.setUsername(userId);
            newUser.setEmail(email);
            newUser.setPassword(psword);
            newUser.put("first_name", firstName);
            newUser.put("last_name", lastName);
            newUser.put("full_name", fullName);
            newUser.put("phone", phone);

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e ==null){
                        ParseRelation<ParseUser> mUserRelation;
                        ParseObject mAccount = new ParseObject("Accounts");
                        mAccount.put("type", Constants.KEY_QR_TYPE_PERSONAL);
                        mAccount.put("currentBalance", 0);
                        mAccount.put("active", true);
                        mUserRelation= mAccount.getRelation(Constants.KEY_ACCOUNT_HOLDERS_RELATION);
                        mUserRelation.add(newUser);
                        mAccount.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                doSignIn();
                            }
                        });

                    }else{
                        switch (e.getCode()){
                            case ParseException.CONNECTION_FAILED:
                            case ParseException.TIMEOUT:
                                Logger.s(getActivity(), getString(R.string.network_timeout_message_txt));
                                break;
                            case ParseException.EMAIL_TAKEN:
                                Logger.s(getActivity(), "Email already taken");
                                break;
                            case ParseException.USERNAME_TAKEN:
                                Logger.s(getActivity(), "An error occurred. We'll fix it shortly.");
                                Logger.m(e.getMessage());
                                break;
                        }
                    }
                }
            });

        }
    }

    private void doSignIn() {
        ParseUser.logInInBackground(userId, psword, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null){
                    Application.UpdateParseInstallation(parseUser);
                    IntentUtils.startActivity(getActivity(), HomeActivity.class);
                }
            }
        });
    }

    /**
     * Called if any of the {@link com.mobsandgeeks.saripaar.Rule}s fail.
     *
     * @param failedView The {@link android.view.View} that did not pass validation.
     * @param failedRule The failed {@link com.mobsandgeeks.saripaar.Rule} associated with the {@link android.view.View}.
     */
    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {

    }
}

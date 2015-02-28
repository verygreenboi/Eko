package ng.codehaven.eko.interfaces;

/**
 * Created by Thompson on 27/02/2015.
 */
public interface ServerAuthenticate {
    public String userSignUp(final String name, final String email, final String pass, final String firstName, final String lastName, final String phone, String authType) throws Exception;
    public String userSignIn(final String user, final String pass, String authType) throws Exception;
}

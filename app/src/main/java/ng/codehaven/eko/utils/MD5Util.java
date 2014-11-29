package ng.codehaven.eko.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mrsmith on 10/24/14.
 * MD5 Hashing utility
 */
public class MD5Util {
    public static String hex(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte anArray : array) {
            sb.append(Integer.toHexString((anArray
                    & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static String md5Hex(String message) {
        try {
            MessageDigest md =
                    MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException e) {
            Logger.m(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Logger.m(e.getMessage());
        }
        return null;
    }
}

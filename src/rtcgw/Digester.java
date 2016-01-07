package rtcgw;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digester {
    public static String getMD5(String in) {
        String retval = in;

        try {
            byte[] a = in.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(a);

            byte[] b = md.digest();
            StringBuffer s = new StringBuffer();

            for (int i = 0; i < b.length; i++) {
                s.append(Integer.toString((b[i] & 0xf0) >> 4, 16));
                s.append(Integer.toString(b[i] & 0x0f, 16));
            }

            return s.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            retval = in;
        }

        return retval;
    }

    public static String getSHA(String in) {
        String retval = in;

        try {
            byte[] a = in.getBytes();
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(a);

            byte[] b = md.digest();
            StringBuffer s = new StringBuffer();

            for (int i = 0; i < b.length; i++) {
                s.append(Integer.toString((b[i] & 0xf0) >> 4, 16));
                s.append(Integer.toString(b[i] & 0x0f, 16));
            }

            return s.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            retval = in;
        }

        return retval;
    }

}

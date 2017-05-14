package com.bookshare.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Darwin on 19/3/2017.
 */
public class StringUtil {

    public static boolean equalsWithoutNull(String str1, String str2) {
        if (str1 != null && str1.equals(str2))
            return true;
        else
            return false;
    }

    public static String toString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}

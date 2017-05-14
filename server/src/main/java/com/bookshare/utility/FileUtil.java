package com.bookshare.utility;

public class FileUtil {

    public static String getSuffix(String fileUrl) {
        int i = fileUrl.lastIndexOf('/');
        if (i > 0) {
            fileUrl = fileUrl.substring(i + 1);
        }
        i = fileUrl.lastIndexOf('.');
        if (i > 0) {
            return fileUrl.substring(i);
        } else {
            return "";
        }
    }

}

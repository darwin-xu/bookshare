package com.bookshare.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestCaseUtil {

    public static List<String> sortedStringList(List<String> strs) {
        Collections.sort(strs);
        return strs;
    }

    public static List<String> sortedStringList(String strs[]) {
        List<String> sl = Arrays.asList(strs);
        Collections.sort(sl);
        return sl;
    }
}

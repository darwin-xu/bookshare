package com.bookshare.utility;

public class TimeUtil {

    public static void delay(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
        }
    }

}

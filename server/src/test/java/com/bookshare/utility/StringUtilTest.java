package com.bookshare.utility;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;

public class StringUtilTest {

    @Test
    public void getSuffix() {
        assertEquals(".mpg", FileUtil.getSuffix("k/ee.j/this.mpg"));
        assertEquals(".jpg", FileUtil.getSuffix("kkkkk.jpg"));
        assertEquals("", FileUtil.getSuffix("kkkkk"));
        assertEquals(".jpg", FileUtil.getSuffix("http://123.43.78.9:80/uname/file.jpg"));
        assertEquals("", FileUtil.getSuffix("http://123.43.78.9:80/uname/filename"));
        assertEquals("", FileUtil.getSuffix("http://123.43.78.9:80/uname.jpg/filename"));
        assertEquals(".mkv", FileUtil.getSuffix("http://123.43.78.9:80/uname.jpg/filename.mkv"));
    }

}

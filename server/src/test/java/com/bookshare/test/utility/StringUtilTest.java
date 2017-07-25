package com.bookshare.test.utility;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.bookshare.utility.FileUtil;

@Test
public class StringUtilTest {

    @Test
    public void getSuffix() {
        assertEquals(FileUtil.getSuffix("k/ee.j/this.mpg"), ".mpg");
        assertEquals(FileUtil.getSuffix("kkkkk.jpg"), ".jpg");
        assertEquals(FileUtil.getSuffix("kkkkk"), "");
        assertEquals(FileUtil.getSuffix("http://123.43.78.9:80/uname/file.jpg"), ".jpg");
        assertEquals(FileUtil.getSuffix("http://123.43.78.9:80/uname/filename"), "");
        assertEquals(FileUtil.getSuffix("http://123.43.78.9:80/uname.jpg/filename"), "");
        assertEquals(FileUtil.getSuffix("http://123.43.78.9:80/uname.jpg/filename.mkv"), ".mkv");
    }

}

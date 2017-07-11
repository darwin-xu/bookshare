package com.bookshare.test.utility;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.bookshare.utility.FileUtil;

@Test
public class StringUtilTest {

    @Test
    public void getSuffix() {
        AssertJUnit.assertEquals(".mpg", FileUtil.getSuffix("k/ee.j/this.mpg"));
        AssertJUnit.assertEquals(".jpg", FileUtil.getSuffix("kkkkk.jpg"));
        AssertJUnit.assertEquals("", FileUtil.getSuffix("kkkkk"));
        AssertJUnit.assertEquals(".jpg", FileUtil.getSuffix("http://123.43.78.9:80/uname/file.jpg"));
        AssertJUnit.assertEquals("", FileUtil.getSuffix("http://123.43.78.9:80/uname/filename"));
        AssertJUnit.assertEquals("", FileUtil.getSuffix("http://123.43.78.9:80/uname.jpg/filename"));
        AssertJUnit.assertEquals(".mkv", FileUtil.getSuffix("http://123.43.78.9:80/uname.jpg/filename.mkv"));
    }

}

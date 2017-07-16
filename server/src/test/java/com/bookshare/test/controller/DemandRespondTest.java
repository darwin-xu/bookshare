package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import com.bookshare.domain.Demand;
import com.bookshare.domain.Respond;
import com.bookshare.domain.User;
import com.bookshare.utility.TestCaseUtil;

@Test
@SpringBootTest
@AutoConfigureMockMvc
public class DemandRespondTest extends AbstractMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    String books[] = { "9787500648192", "9787505417731", "9787508622545", "9787301150894", "9787516810941",
            "9787509766989", "9787553805900", "9787550278998", "9787508665450" };

    // ----------- A -- B -- C -- D -- E -- F -- G -- H -- I
    char v1[] = { '+', '+', '+', '+', '+', ' ', ' ', ' ', ' ' };
    char w1[] = { '+', ' ', '+', ' ', '+', ' ', ' ', ' ', ' ' };
    char x1[] = { ' ', ' ', ' ', ' ', '+', '+', '+', '+', '+' };
    char y1[] = { ' ', ' ', ' ', ' ', '+', ' ', '+', ' ', '+' };
    char z1[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

    // ----------- A -- B -- C -- D -- E -- F -- G -- H -- I
    char v2[] = { ' ', ' ', ' ', ' ', ' ', '*', '*', '*', ' ' };
    char w2[] = { ' ', ' ', ' ', ' ', ' ', ' ', '*', '*', '*' };
    char x2[] = { '*', '*', '*', ' ', ' ', ' ', ' ', ' ', ' ' };
    char y2[] = { ' ', ' ', '*', '*', '*', ' ', ' ', ' ', ' ' };
    char z2[] = { ' ', '*', '*', ' ', '*', '*', '*', ' ', ' ' };
    // --------------------------------------------------------
    // ----------- 1 -- 2 -- 3 -- 1 -- 2 -- 2 -- 3 -- 2 -- 1

    // ----------- A -- B -- C -- D -- E -- F -- G -- H -- I
    char v3[] = { '1', '2', '3', '1', '2', ' ', ' ', ' ', ' ' };
    char w3[] = { '1', ' ', '3', ' ', '2', ' ', ' ', ' ', ' ' };
    char x3[] = { ' ', ' ', ' ', ' ', '2', '2', '3', '2', '1' };
    char y3[] = { ' ', ' ', ' ', ' ', '2', ' ', '3', ' ', '1' };
    char z3[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

    // ----------- A -- B -- C -- D -- E -- F -- G -- H -- I
    char v4[] = { '1', '2', '3', '1', '2', ' ', ' ', ' ', ' ' };
    char w4[] = { '1', ' ', '3', ' ', '2', ' ', ' ', ' ', ' ' };
    char x4[] = { ' ', ' ', ' ', ' ', '2', '2', '3', '2', '1' };
    char y4[] = { ' ', ' ', ' ', ' ', '2', ' ', '3', ' ', '1' };
    char z4[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

    Map<String, Cookie> userCookieMap = new HashMap<String, Cookie>();

    @Test(groups = "prepare", timeOut = timeout_ms)
    public void prepareUserShelf() throws Exception {
        prepareUserShelfFor("v", v1);
        prepareUserShelfFor("w", w1);
        prepareUserShelfFor("x", x1);
        prepareUserShelfFor("y", y1);
        prepareUserShelfFor("z", z1);
    }

    @Test(groups = "issue", dependsOnGroups = "prepare", timeOut = timeout_ms)
    public void issueDemand() throws Exception {
        issueDemandFor("v", v2);
        issueDemandFor("w", w2);
        issueDemandFor("x", x2);
        issueDemandFor("y", y2);
        issueDemandFor("z", z2);
    }

    @Test(groups = "check", dependsOnGroups = "issue", timeOut = timeout_ms)
    public void checkDemand() throws Exception {
        checkDemandFor("v", v2);
        checkDemandFor("w", w2);
        checkDemandFor("x", x2);
        checkDemandFor("y", y2);
        checkDemandFor("z", z2);
    }

    @Test(groups = "check", dependsOnGroups = "issue", timeOut = timeout_ms)
    public void checkRespond() throws Exception {
        // Make sure the dispatcher
        perform(mockMvc, Method.GET, "/background/waitForDispatch", null, null, status().isOk(), null);

        checkRespondFor("v", v3);
        checkRespondFor("w", w3);
        checkRespondFor("x", x3);
        checkRespondFor("y", y3);
        checkRespondFor("z", z3);
    }

    private void checkRespondFor(String userName, char bookData[]) throws Exception {
        Cookie cookie = userCookieMap.get(userName);

        Respond responds[] = perform(mockMvc, Method.GET, "/sharing/responds", cookie, null, status().isOk(),
                Respond[].class);

        assertEquals(TestCaseUtil.sortedStringList(getIsbns(bookData)),
                TestCaseUtil.sortedStringList(getIsbns(responds)));
    }

    private void checkDemandFor(String userName, char bookData[]) throws Exception {
        Cookie cookie = userCookieMap.get(userName);

        Demand demands[] = perform(mockMvc, Method.GET, "/sharing/demands", cookie, null, status().isOk(),
                Demand[].class);

        assertEquals(TestCaseUtil.sortedStringList(getIsbns(bookData)),
                TestCaseUtil.sortedStringList(getIsbns(demands)));
    }

    private List<String> getIsbns(char bookData[]) {
        List<String> isbns = new ArrayList<String>();
        for (int i = 0; i < bookData.length; ++i) {
            String isbn = getBook(i, bookData);
            if (isbn != null) {
                int n = getBookCount(i, bookData);
                for (int c = 0; c < n; ++c)
                    isbns.add(isbn);
            }
        }
        return isbns;
    }

    private List<String> getIsbns(Respond responds[]) {
        List<String> isbns = new ArrayList<String>();
        for (Respond r : responds) {
            isbns.add(r.getDemand().getIsbn());
        }
        return isbns;
    }

    private List<String> getIsbns(Demand demands[]) {
        List<String> isbns = new ArrayList<String>();
        for (Demand d : demands) {
            isbns.add(d.getIsbn());
        }
        return isbns;
    }

    private void prepareUserShelfFor(String userName, char bookData[]) throws Exception {
        // Create a user
        Cookie cookie = createAndLogin(userName);
        userCookieMap.put(userName, cookie);

        for (int i = 0; i < bookData.length; ++i) {
            String isbn = getBook(i, bookData);
            if (isbn != null)
                postUsersBookshelf(isbn, cookie);
        }
    }

    private void postUsersBookshelf(String isbn, Cookie cookie) throws Exception {
        perform(mockMvc, Method.POST, "/users/bookshelf/" + isbn, cookie, null, status().isOk(), null);
    }

    private Cookie createAndLogin(String userName) throws Exception {
        User user = new User();
        user.setUsername(userName);
        perform(mockMvc, Method.POST, "/users/getVerifyCode", null, user, status().isCreated(), null);
        user.setVerifyCode("112233");
        user.setPassword("123");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isOk(), null);
        return perform(mockMvc, Method.POST, "/sessions/login", null, user, status().isOk(), Cookie.class);
    }

    private int getBookCount(int i, char bookData[]) {
        if ('0' < bookData[i] && bookData[i] < '9')
            return (int) (bookData[i] - '0');
        else if (bookData[i] != ' ')
            return 1;
        else
            return 0;
    }

    private String getBook(int i, char bookData[]) {
        if (bookData[i] != ' ')
            return books[i];
        else
            return null;
    }

    private void issueDemandFor(String userName, char bookData[]) throws Exception {
        Cookie cookie = userCookieMap.get(userName);

        for (int i = 0; i < bookData.length; ++i) {
            String isbn = getBook(i, bookData);
            if (isbn != null)
                postSharingDemandsBook(isbn, cookie);
        }
    }

    private void postSharingDemandsBook(String isbn, Cookie cookie) throws Exception {
        perform(mockMvc, Method.POST, "/sharing/demands/book/" + isbn, cookie, null, status().isCreated(), null);
    }

}

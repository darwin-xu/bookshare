package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
import com.bookshare.domain.Demand1;
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

    // Prepare --- A -- B -- C -- D -- E -- F -- G -- H -- I
    char v1[] = { '+', '+', '+', '+', '+', ' ', ' ', ' ', ' ' };
    char w1[] = { '+', ' ', '+', ' ', '+', '+', ' ', ' ', ' ' };
    char x1[] = { ' ', ' ', ' ', ' ', '+', '+', '+', '+', '+' };
    char y1[] = { ' ', ' ', ' ', ' ', '+', ' ', '+', ' ', '+' };
    char z1[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

    // Demand ---- A -- B -- C -- D -- E -- F -- G -- H -- I
    char v2[] = { ' ', ' ', ' ', ' ', ' ', '*', '*', '*', ' ' };
    char w2[] = { ' ', ' ', ' ', ' ', ' ', ' ', '*', '*', '*' };
    char x2[] = { '*', '*', '*', ' ', ' ', ' ', ' ', ' ', ' ' };
    char y2[] = { ' ', ' ', '*', '*', '*', ' ', ' ', ' ', ' ' };
    char z2[] = { ' ', '*', '*', ' ', '*', '*', '*', ' ', ' ' };
    // --------------------------------------------------------
    // ----------- 1 -- 2 -- 3 -- 1 -- 2 -- 2 -- 3 -- 2 -- 1

    // Respond --- A -- B -- C -- D -- E -- F -- G -- H -- I
    char v3[] = { '1', '2', '3', '1', '2', ' ', ' ', ' ', ' ' };
    char w3[] = { '1', ' ', '3', ' ', '2', '2', ' ', ' ', ' ' };
    char x3[] = { ' ', ' ', ' ', ' ', '2', '2', '3', '2', '1' };
    char y3[] = { ' ', ' ', ' ', ' ', '2', ' ', '3', ' ', '1' };
    char z3[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

    // Cancel ---- A -- B -- C -- D -- E -- F -- G -- H -- I
    char v4[] = { ' ', ' ', ' ', ' ', ' ', ' ', 'x', ' ', ' ' };
    char w4[] = { ' ', ' ', ' ', ' ', ' ', ' ', 'x', 'x', 'x' };
    char x4[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
    char y4[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
    char z4[] = { ' ', ' ', ' ', ' ', ' ', ' ', 'x', ' ', ' ' };

    // Answer ---- A -- B -- C -- D -- E -- F -- G -- H -- I
    char v5[] = { '!', '!', '!', ' ', '!', ' ', ' ', ' ', ' ' };
    char w5[] = { '!', ' ', '!', ' ', '!', '!', ' ', ' ', ' ' };
    char x5[] = { ' ', ' ', ' ', ' ', '!', '!', '!', ' ', ' ' };
    char y5[] = { ' ', ' ', ' ', ' ', '!', ' ', ' ', ' ', ' ' };
    char z5[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

    // Select ---- A -- B -- C -- D -- E -- F -- G -- H -- I
    // Demand
    char v6[] = { ' ', ' ', ' ', ' ', ' ', '2', '1', ' ', ' ' };
    char w6[] = { ' ', ' ', ' ', ' ', ' ', ' ', '1', ' ', ' ' };
    char x6[] = { '1', '1', '2', ' ', ' ', ' ', ' ', ' ', ' ' };
    char y6[] = { ' ', ' ', '2', ' ', '2', ' ', ' ', ' ', ' ' };
    char z6[] = { ' ', '1', '2', ' ', '2', '2', '1', ' ', ' ' };
    // Respond
    char v7[] = { '1', '1', '2', ' ', ' ', ' ', ' ', ' ', ' ' };
    char w7[] = { '1', ' ', '2', ' ', '2', '2', ' ', ' ', ' ' };
    char x7[] = { ' ', ' ', ' ', ' ', '2', '2', '1', ' ', ' ' };
    char y7[] = { ' ', ' ', ' ', ' ', '2', ' ', ' ', ' ', ' ' };
    char z7[] = { ' ', ' ', ' ', ' ', '2', ' ', ' ', ' ', ' ' };

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

    // @Test(groups = "change", dependsOnGroups = "check", timeOut = timeout_ms)
    // public void changeDemand() throws Exception {
    // changeDemandFor("v", v4);
    // changeDemandFor("w", w4);
    // changeDemandFor("x", x4);
    // changeDemandFor("y", y4);
    // changeDemandFor("z", z4);
    // }
    //
    // // @Test(groups = "checkChange", dependsOnGroups = "change", timeOut = timeout_ms)
    // public void checkChangedDemand() throws Exception {
    // checkChangedDemand("v", v2, v4);
    // checkChangedDemand("w", w2, w4);
    // checkChangedDemand("x", x2, x4);
    // checkChangedDemand("y", y2, y4);
    // checkChangedDemand("z", z2, z4);
    // }

    // @Test(groups = "checkChange", dependsOnGroups = "change", timeOut = timeout_ms)
    // public void answerRespond() throws Exception {
    // answerRespondFor("v", v5);
    // answerRespondFor("w", w5);
    // answerRespondFor("x", x5);
    // answerRespondFor("y", y5);
    // answerRespondFor("z", z5);
    // }

    private void answerRespondFor(String username, char bookData[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Respond responds[] = perform(mockMvc, Method.GET, "/sharing/responds", cookie, null, status().isOk(),
                Respond[].class);

        List<String> isbns = getBooks(bookData);
        for (Respond r : responds) {
            if (isbns.contains(r.getDemand().getIsbn())) {
                r.setAgreed(true);
                perform(mockMvc, Method.PUT, "/sharing/responds/" + r.getId(), cookie, r, status().isOk(), null);
            }
        }
    }

    private void checkChangedDemand(String username, char bookDataDemand[], char bookDataCancelled[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Demand demands[] = perform(mockMvc, Method.GET, "/sharing/demands", cookie, null, status().isOk(),
                Demand[].class);

        List<String> isbnsDemand = getBooks(bookDataDemand);
        List<String> isbnsCancelled = getBooks(bookDataCancelled);

        for (Demand d : demands) {
            assertTrue(isbnsDemand.contains(d.getIsbn()));
            if (isbnsCancelled.contains(d.getIsbn()))
                assertTrue(d.getCancalled());
            else
                assertFalse(d.getCancalled());
        }
    }

    private void changeDemandFor(String username, char bookData[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Demand demands[] = perform(mockMvc, Method.GET, "/sharing/demands", cookie, null, status().isOk(),
                Demand[].class);

        List<String> isbns = getBooks(bookData);
        for (Demand d : demands) {
            if (isbns.contains(d.getIsbn())) {
                d.setCancalled(true);
                perform(mockMvc, Method.PUT, "/sharing/demands/" + d.getId(), cookie, d, status().isOk(), null);
            }
        }
    }

    private void checkRespondFor(String username, char bookData[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Respond responds[] = perform(mockMvc, Method.GET, "/sharing/responds", cookie, null, status().isOk(),
                Respond[].class);

        assertEquals(TestCaseUtil.sortedStringList(getIsbns(responds)),
                TestCaseUtil.sortedStringList(getBooks(bookData)));
    }

    private void checkDemandFor(String username, char bookData[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Demand1 demands[] = perform(mockMvc, Method.GET, "/sharing/demands", cookie, null, status().isOk(),
                Demand1[].class);

        assertEquals(TestCaseUtil.sortedStringList(getIsbns(demands)),
                TestCaseUtil.sortedStringList(getBooks(bookData)));
    }

    private List<String> getIsbns(Respond responds[]) {
        List<String> isbns = new ArrayList<String>();
        for (Respond r : responds) {
            isbns.add(r.getDemand().getIsbn());
        }
        return isbns;
    }

    private List<String> getIsbns(Demand1 demands[]) {
        List<String> isbns = new ArrayList<String>();
        for (Demand1 d : demands) {
            isbns.add(d.getIsbn());
        }
        return isbns;
    }

    private void prepareUserShelfFor(String username, char bookData[]) throws Exception {
        // Create a user
        Cookie cookie = createAndLogin(username);
        userCookieMap.put(username, cookie);

        List<String> isbns = getBooks(bookData);
        for (String isbn : isbns) {
            postUsersBookshelf(isbn, cookie);
        }
    }

    private void postUsersBookshelf(String isbn, Cookie cookie) throws Exception {
        perform(mockMvc, Method.POST, "/users/bookshelf/" + isbn, cookie, null, status().isCreated(), null);
    }

    private Cookie createAndLogin(String username) throws Exception {
        User user = new User();
        user.setUsername(username);
        perform(mockMvc, Method.POST, "/users/getVerifyCode", null, user, status().isCreated(), null);
        user.setVerifyCode("112233");
        user.setPassword("123");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isOk(), null);
        return perform(mockMvc, Method.POST, "/sessions/login", null, user, status().isOk(), Cookie.class);
    }

    private List<String> getBooks(char bookData[]) {
        List<String> isbns = new ArrayList<String>();
        for (int i = 0; i < bookData.length; ++i)
            if ('0' < bookData[i] && bookData[i] < '9')
                for (int c = 0; c < bookData[i] - '0'; ++c)
                    isbns.add(books[i]);
            else if (bookData[i] != ' ')
                isbns.add(books[i]);
        return isbns;
    }

    private void issueDemandFor(String username, char bookData[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        List<String> isbns = getBooks(bookData);
        for (String isbn : isbns) {
            postSharingDemandsBook(isbn, cookie);
        }
    }

    private void postSharingDemandsBook(String isbn, Cookie cookie) throws Exception {
        perform(mockMvc, Method.POST, "/sharing/demands/book/" + isbn, cookie, null, status().isCreated(), null);
    }

}

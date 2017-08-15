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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.bookshare.domain.Bookshelf;
import com.bookshare.domain.Demand;
import com.bookshare.domain.Respond;
import com.bookshare.utility.TestCaseUtil;

@Test
@SpringBootTest
@AutoConfigureMockMvc
public class DemandRespondTest extends AbstractMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    String books[] = { "9787500648192", "9787505417731", "9787508622545", "9787301150894", "9787516810941",
            "9787509766989", "9787553805900", "9787550278998", "9787508665450" };

    // ---------- "9787500648192"
    // ----------- | - "9787505417731"
    // ----------- | -- | - "9787508622545"
    // ----------- | -- | -- | - "9787301150894"
    // ----------- | -- | -- | -- | - "9787516810941"
    // ----------- | -- | -- | -- | -- | - "9787509766989"
    // ----------- | -- | -- | -- | -- | -- | - "9787553805900"
    // ----------- | -- | -- | -- | -- | -- | -- | - "9787550278998"
    // ----------- | -- | -- | -- | -- | -- | -- | -- | - "9787508665450"
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
    char v6[] = { ' ', ' ', ' ', ' ', ' ', '1', '1', ' ', ' ' };
    char w6[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
    char x6[] = { '1', '1', '1', ' ', ' ', ' ', ' ', ' ', ' ' };
    char y6[] = { ' ', ' ', '1', ' ', '1', ' ', ' ', ' ', ' ' };
    char z6[] = { ' ', ' ', ' ', ' ', '1', '1', ' ', ' ', ' ' };
    // Respond
    char v7[] = { '1', '1', '1', ' ', '1', ' ', ' ', ' ', ' ' };
    char w7[] = { ' ', ' ', '1', ' ', '1', '1', ' ', ' ', ' ' };
    char x7[] = { ' ', ' ', ' ', ' ', ' ', '1', '1', ' ', ' ' };
    char y7[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
    char z7[] = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

    Map<String, Cookie> userCookieMap = new HashMap<String, Cookie>();

    @BeforeMethod
    public void init() throws Exception {
        preGetBooks(mockMvc);
    }

    @Test(groups = "prepare", timeOut = int_timeout_ms)
    public void prepareUserShelf() throws Exception {
        prepareUserShelfFor("v", v1, 5);
        prepareUserShelfFor("w", w1, 4);
        prepareUserShelfFor("x", x1, 3);
        prepareUserShelfFor("y", y1, 2);
        prepareUserShelfFor("z", z1, 1);
    }

    @Test(groups = "issue", dependsOnGroups = "prepare", timeOut = int_timeout_ms)
    public void issueDemand() throws Exception {
        issueDemandFor("v", v2);
        issueDemandFor("w", w2);
        issueDemandFor("x", x2);
        issueDemandFor("y", y2);
        issueDemandFor("z", z2);
    }

    @Test(groups = "check", dependsOnGroups = "issue", timeOut = int_timeout_ms)
    public void checkDemand() throws Exception {
        checkDemandFor("v", v2);
        checkDemandFor("w", w2);
        checkDemandFor("x", x2);
        checkDemandFor("y", y2);
        checkDemandFor("z", z2);
    }

    @Test(groups = "check", dependsOnGroups = "issue", timeOut = int_timeout_ms)
    public void checkRespond() throws Exception {
        // Make sure the dispatcher
        perform(mockMvc, Method.GET, "/background/waitForDispatch", null, null, status().isOk(), null);

        checkRespondFor("v", v3);
        checkRespondFor("w", w3);
        checkRespondFor("x", x3);
        checkRespondFor("y", y3);
        checkRespondFor("z", z3);
    }

    @Test(groups = "change", dependsOnGroups = "check", timeOut = int_timeout_ms)
    public void changeDemand() throws Exception {
        changeDemandFor("v", v4);
        changeDemandFor("w", w4);
        changeDemandFor("x", x4);
        changeDemandFor("y", y4);
        changeDemandFor("z", z4);
    }

    @Test(groups = "checkChange", dependsOnGroups = "change", timeOut = int_timeout_ms)
    public void checkChangedDemand() throws Exception {
        checkChangedDemand("v", v2, v4);
        checkChangedDemand("w", w2, w4);
        checkChangedDemand("x", x2, x4);
        checkChangedDemand("y", y2, y4);
        checkChangedDemand("z", z2, z4);
    }

    @Test(groups = "checkChange", dependsOnGroups = "change", timeOut = int_timeout_ms)
    public void agreeBookshelf() throws Exception {
        agreeBookshelfFor("v", v5);
        agreeBookshelfFor("w", w5);
        agreeBookshelfFor("x", x5);
        agreeBookshelfFor("y", y5);
        agreeBookshelfFor("z", z5);
        
        // Make sure the dispatcher run once.
        perform(mockMvc, Method.GET, "/background/waitForDispatch", null, null, status().isOk(), null);
    }

    @Test(groups = "checkMatch", dependsOnGroups = "checkChange", timeOut = int_timeout_ms)
    public void checkMatchedDemand() throws Exception {
        checkMatchedDemandFor("v", v6);
        checkMatchedDemandFor("w", w6);
        checkMatchedDemandFor("x", x6);
        checkMatchedDemandFor("y", y6);
        checkMatchedDemandFor("z", z6);
    }

    @Test(groups = "checkMatch", dependsOnGroups = "checkChange", timeOut = int_timeout_ms)
    public void checkMatchedBookshelf() throws Exception {
        checkMatchedBookshelfFor("v", v7);
        checkMatchedBookshelfFor("w", w7);
        checkMatchedBookshelfFor("x", x7);
        checkMatchedBookshelfFor("y", y7);
        checkMatchedBookshelfFor("z", z7);
    }

    private void checkMatchedBookshelfFor(String username, char mask[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Bookshelf bookshelfs[] = perform(mockMvc, Method.GET, "/users/bookshelf", cookie, null, status().isOk(),
                Bookshelf[].class);

        List<String> isbns = getBooks(mask, books);
        for (Bookshelf bookshelf : bookshelfs) {
            if (bookshelf.getDemand() != null) {
                assertTrue(isbns.remove(bookshelf.getIsbn()));
            }
        }
        assertEquals(isbns.size(), 0);
    }

    private void checkMatchedDemandFor(String username, char mask[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Demand demands[] = perform(mockMvc, Method.GET, "/sharing/demands", cookie, null, status().isOk(),
                Demand[].class);

        List<String> isbns = getBooks(mask, books);
        for (Demand demand : demands) {
            Bookshelf bookshelf = demand.getBookshelf();
            if (bookshelf != null) {
                assertTrue(isbns.remove(demand.getIsbn()));
            }
        }
        assertEquals(isbns.size(), 0);
    }

    private void agreeBookshelfFor(String username, char mask[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        List<String> isbns = getBooks(mask, books);
        for (String isbn : isbns) {
            Bookshelf bookshelf = perform(mockMvc, Method.GET, "/users/bookshelf/search/" + isbn, cookie, null,
                    status().isOk(), Bookshelf.class);
            bookshelf.setAgreed(true);
            perform(mockMvc, Method.PUT, "/users/bookshelf/" + bookshelf.getId(), cookie, bookshelf, status().isOk(),
                    null);
        }
    }

    private void checkChangedDemand(String username, char maskDemand[], char maskCanceled[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Demand demands[] = perform(mockMvc, Method.GET, "/sharing/demands", cookie, null, status().isOk(),
                Demand[].class);

        List<String> isbnsDemand = getBooks(maskDemand, books);
        List<String> isbnsCanceled = getBooks(maskCanceled, books);

        for (Demand d : demands) {
            assertTrue(isbnsDemand.contains(d.getIsbn()));
            if (isbnsCanceled.contains(d.getIsbn()))
                assertTrue(d.getCanceled());
            else
                assertFalse(d.getCanceled());
        }

        for (Demand dExp : demands) {
            Demand dAct = perform(mockMvc, Method.GET, "/sharing/demands/" + dExp.getId(), cookie, null,
                    status().isOk(), Demand.class);
            assertEquals(dAct.getId(), dExp.getId());
            assertEquals(dAct.getCanceled(), dExp.getCanceled());
            assertEquals(dAct.getIsbn(), dExp.getIsbn());
        }
    }

    private void changeDemandFor(String username, char mask[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Demand demands[] = perform(mockMvc, Method.GET, "/sharing/demands", cookie, null, status().isOk(),
                Demand[].class);

        List<String> isbns = getBooks(mask, books);
        for (Demand d : demands) {
            if (isbns.contains(d.getIsbn())) {
                d.setCanceled(true);
                perform(mockMvc, Method.PUT, "/sharing/demands/" + d.getId(), cookie, d, status().isOk(), null);
            }
        }
    }

    private void checkRespondFor(String username, char mask[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Respond responds[] = perform(mockMvc, Method.GET, "/sharing/responds", cookie, null, status().isOk(),
                Respond[].class);

        assertEquals(TestCaseUtil.sortedStringList(getIsbns(responds)),
                TestCaseUtil.sortedStringList(getBooks(mask, books)));
    }

    private void checkDemandFor(String username, char mask[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        Demand demands[] = perform(mockMvc, Method.GET, "/sharing/demands", cookie, null, status().isOk(),
                Demand[].class);

        assertEquals(TestCaseUtil.sortedStringList(getIsbns(demands)),
                TestCaseUtil.sortedStringList(getBooks(mask, books)));
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

    private void prepareUserShelfFor(String username, char mask[], int sharingIndex) throws Exception {
        // Create a user
        Cookie cookie = createAndLogin(mockMvc, username, sharingIndex);
        userCookieMap.put(username, cookie);

        List<String> isbns = getBooks(mask, books);
        for (String isbn : isbns) {
            postUsersBookshelf(isbn, cookie);
        }
    }

    private void postUsersBookshelf(String isbn, Cookie cookie) throws Exception {
        perform(mockMvc, Method.POST, "/users/bookshelf/books/" + isbn, cookie, null, status().isCreated(), null);
    }

    private void issueDemandFor(String username, char mask[]) throws Exception {
        Cookie cookie = userCookieMap.get(username);

        List<String> isbns = getBooks(mask, books);
        for (String isbn : isbns) {
            postSharingDemandsBook(isbn, cookie);
        }
    }

    private void postSharingDemandsBook(String isbn, Cookie cookie) throws Exception {
        perform(mockMvc, Method.POST, "/sharing/demands/book/" + isbn, cookie, null, status().isCreated(), null);
    }

}

package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.bookshare.domain.Bookshelf;
import com.bookshare.utility.TestCaseUtil;

@Test
@SpringBootTest
@AutoConfigureMockMvc
public class UserBookshelfTest extends AbstractMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    private String books[] = { "9787514610307", "9787550284340", "9787550217454", "9787030324672", "9787569914061",
            "9787550278998" };

    char s1[] = { '+', '+', '+', '+', '+', ' ' };
    char s2[] = { ' ', '+', ' ', '+', ' ', ' ' };
    char s3[] = { ' ', ' ', '-', '-', ' ', ' ' };
    char s4[] = { ' ', ' ', '-', ' ', ' ', '-' };
    char s5[] = { '+', '+', ' ', ' ', '+', ' ' };
    char s6[] = { '+', ' ', ' ', ' ', '+', ' ' };

    Cookie cookie = null;

    @BeforeMethod
    public void init() throws Exception {
        preGetBooks(mockMvc);
    }

    @Test(priority = 1, timeOut = int_timeout_ms)
    public void getCookie() throws Exception {
        cookie = createAndLogin(mockMvc, "UserBookshelfTest", 0);
    }

    @Test(priority = 2, timeOut = int_timeout_ms)
    public void uploadBooks() throws Exception {
        List<String> isbns = getBooks(s1, books);
        for (String isbn : isbns) {
            perform(mockMvc, Method.POST, "/users/bookshelf/books/" + isbn, cookie, null, status().isCreated(), null);
        }
    }

    @Test(priority = 3, timeOut = int_timeout_ms)
    public void uploadBooksAgain() throws Exception {
        List<String> isbns = getBooks(s2, books);
        for (String isbn : isbns) {
            perform(mockMvc, Method.POST, "/users/bookshelf/books/" + isbn, cookie, null, status().isNotAcceptable(),
                    null);
        }
    }

    @Test(priority = 4, timeOut = int_timeout_ms)
    public void checkBooksAfterUploading() throws Exception {
        String actualIsbns[] = perform(mockMvc, Method.GET, "/users/bookshelf/books/", cookie, null, status().isOk(),
                String[].class);
        assertEquals(TestCaseUtil.sortedStringList(actualIsbns), TestCaseUtil.sortedStringList(getBooks(s1, books)));
    }

    @Test(priority = 5, timeOut = int_timeout_ms)
    public void removeBooks() throws Exception {
        List<String> isbns = getBooks(s3, books);
        for (String isbn : isbns) {
            perform(mockMvc, Method.DELETE, "/users/bookshelf/books/" + isbn, cookie, null, status().isOk(), null);
        }
    }

    @Test(priority = 6, timeOut = int_timeout_ms)
    public void removeBooksAgain() throws Exception {
        List<String> isbns = getBooks(s4, books);
        for (String isbn : isbns) {
            perform(mockMvc, Method.DELETE, "/users/bookshelf/books/" + isbn, cookie, null, status().isNotAcceptable(),
                    null);
        }
    }

    @Test(priority = 7, timeOut = int_timeout_ms)
    public void checkBooksAfterDeletion() throws Exception {
        String actualIsbns[] = perform(mockMvc, Method.GET, "/users/bookshelf/books/", cookie, null, status().isOk(),
                String[].class);
        assertEquals(TestCaseUtil.sortedStringList(actualIsbns), TestCaseUtil.sortedStringList(getBooks(s5, books)));
    }

    @Test(priority = 8, timeOut = int_timeout_ms)
    public void checkBooksSearch() throws Exception {
        List<String> isbns = getBooks(s5, books);
        for (String toSearch : isbns) {
            Bookshelf bookshelf = perform(mockMvc, Method.GET, "/users/bookshelf/search/" + toSearch, cookie, null,
                    status().isOk(), Bookshelf.class);
            assertEquals(bookshelf.getBook().getIsbn13(), toSearch);
        }
    }

    @Test(priority = 9, timeOut = int_timeout_ms)
    public void checkBooksSearchNotExist() throws Exception {
        List<String> isbns = getBooks(s4, books);
        for (String toSearch : isbns) {
            perform(mockMvc, Method.GET, "/users/bookshelf/search/" + toSearch, cookie, null, status().isNotFound(),
                    null);
        }
    }

    @Test(priority = 10, timeOut = int_timeout_ms)
    public void checkGetBookshelf() throws Exception {
        Bookshelf bookshelfs[] = perform(mockMvc, Method.GET, "/users/bookshelf", cookie, null, status().isOk(),
                Bookshelf[].class);
        assertEquals(TestCaseUtil.sortedStringList(getIsbns(bookshelfs)), getBooks(s5, books));
    }

    @Test(priority = 11, timeOut = int_timeout_ms)
    public void checkGetBookshelfByID() throws Exception {
        Bookshelf bookshelfs[] = perform(mockMvc, Method.GET, "/users/bookshelf", cookie, null, status().isOk(),
                Bookshelf[].class);
        for (Bookshelf bExpected : bookshelfs) {
            Bookshelf bActual = perform(mockMvc, Method.GET, "/users/bookshelf/" + bExpected.getId(), cookie, null,
                    status().isOk(), Bookshelf.class);
            assertEquals(bActual.getAgreed(), Boolean.FALSE);
            assertEquals(bActual, bExpected);
        }
    }

    @Test(priority = 12, timeOut = int_timeout_ms)
    public void agreedForBookshelf() throws Exception {
        List<String> isbnsToAgree = getBooks(s6, books);
        for (String isbn : isbnsToAgree) {
            Bookshelf bookshelf = perform(mockMvc, Method.GET, "/users/bookshelf/search/" + isbn, cookie, null,
                    status().isOk(), Bookshelf.class);
            bookshelf.setAgreed(true);
            perform(mockMvc, Method.PUT, "/users/bookshelf/" + bookshelf.getId(), cookie, bookshelf, status().isOk(),
                    null);
        }
        Bookshelf bookshelfs[] = perform(mockMvc, Method.GET, "/users/bookshelf", cookie, null, status().isOk(),
                Bookshelf[].class);
        for (Bookshelf b : bookshelfs) {
            if (isbnsToAgree.contains(b.getBook().getIsbn13()))
                assertTrue(b.getAgreed());
            else
                assertFalse(b.getAgreed());
        }
    }

    @Test(priority = 13, timeOut = int_timeout_ms)
    public void removeAll() throws Exception {
        Bookshelf bookshelfsBefore[] = perform(mockMvc, Method.GET, "/users/bookshelf", cookie, null, status().isOk(),
                Bookshelf[].class);
        for (Bookshelf b : bookshelfsBefore) {
            perform(mockMvc, Method.DELETE, "/users/bookshelf/" + b.getId(), cookie, null, status().isOk(), null);
        }

        Bookshelf bookshelfsAfter[] = perform(mockMvc, Method.GET, "/users/bookshelf", cookie, null, status().isOk(),
                Bookshelf[].class);
        assertEquals(bookshelfsAfter, new Bookshelf[0]);
        String isbns[] = perform(mockMvc, Method.GET, "/users/bookshelf/books", cookie, null, status().isOk(),
                String[].class);
        assertEquals(isbns, new String[0]);
    }

    private List<String> getIsbns(Bookshelf bookshelfs[]) {
        List<String> sl = new ArrayList<String>();
        for (Bookshelf b : bookshelfs) {
            sl.add(b.getBook().getIsbn13());
        }
        return sl;
    }

}

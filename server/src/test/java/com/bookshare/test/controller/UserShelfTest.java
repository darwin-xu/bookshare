package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import com.bookshare.domain.User;

@Test
@SpringBootTest
@AutoConfigureMockMvc
public class UserShelfTest extends AbstractMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void usersBooksheef() throws Exception {
        User user;

        // Create a new user.
        user = new User();
        user.setUsername("userabc");
        perform(mockMvc, Method.POST, "/users/getVerifyCode", null, user, status().isCreated(), null);

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("userabc");
        user.setVerifyCode("112233");
        user.setPassword("newpassword123");
        perform(mockMvc, Method.PATCH, "/users/changePassword", null, user, status().isOk(), null);

        // Use username and password to login.
        user = new User();
        user.setUsername("userabc");
        user.setPassword("newpassword123");
        Cookie cookie = perform(mockMvc, Method.POST, "/sessions/login", null, user, status().isOk(), Cookie.class);

        // Post the new books to this user's shelf.
        String isbns[] = { "9787514610307", "9787550284340", "9787550217454", "9787030324672", "9787569914061" };
        Set<String> booksISBN = new HashSet<String>(Arrays.asList(isbns));
        for (String isbn : booksISBN) {
            perform(mockMvc, Method.POST, "/users/bookshelf/" + isbn, cookie, null, status().isOk(), null);
        }

        // Post again.
        for (String isbn : booksISBN) {
            perform(mockMvc, Method.POST, "/users/bookshelf/" + isbn, cookie, null, status().isNotAcceptable(), null);
        }

        // Get the books from user's shelf.
        String booksISBNActual1[] = perform(mockMvc, Method.GET, "/users/bookshelf/", cookie, null, status().isOk(),
                String[].class);

        assertEquals(booksISBN, new HashSet<String>(Arrays.asList(booksISBNActual1)));

        // Remove some books from user's shelf.
        String toRemove = "9787550217454";
        perform(mockMvc, Method.DELETE, "/users/bookshelf/" + toRemove, cookie, null, status().isOk(), null);

        // Get the books from user's shelf.
        String booksISBNActual2[] = perform(mockMvc, Method.GET, "/users/bookshelf/", cookie, null, status().isOk(),
                String[].class);

        booksISBN.remove(toRemove);
        assertEquals(booksISBN, new HashSet<String>(Arrays.asList(booksISBNActual2)));
    }

}

package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.bookshare.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@Test
@SpringBootTest
@AutoConfigureMockMvc
public class UserShelfTest extends AbstractTestNGSpringContextTests {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void usersBooksheef() throws Exception {
        User user;

        // Create a new user.
        user = new User();
        user.setUsername("userabc");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isCreated());

        // Use modify to set a password for user.
        user = new User();
        user.setUsername("userabc");
        user.setVerifyCode("112233");
        user.setPassword("newpassword123");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isOk());

        // Use username and password to login.
        user = new User();
        user.setUsername("userabc");
        user.setPassword("newpassword123");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/sessions/login")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk()).andReturn();

        // Get the cookie from response.
        Cookie cookie = result.getResponse().getCookies()[0];

        // Post the new books to this user's shelf.
        String isbns[] = { "9787514610307", "9787550284340", "9787550217454", "9787030324672", "9787569914061" };
        Set<String> booksISBN = new HashSet<String>(Arrays.asList(isbns));
        for (String isbn : booksISBN) {
            mockMvc.perform(MockMvcRequestBuilders.post("/users/bookshelf/" + isbn).cookie(cookie))
                    .andExpect(status().isOk());
        }

        // Post again.
        for (String isbn : booksISBN) {
            mockMvc.perform(MockMvcRequestBuilders.post("/users/bookshelf/" + isbn).cookie(cookie))
                    .andExpect(status().isNotAcceptable());
        }

        // Get the books from user's shelf.
        String booksISBNActual1[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/users/bookshelf").cookie(cookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), String[].class);

        AssertJUnit.assertEquals(booksISBN, new HashSet<String>(Arrays.asList(booksISBNActual1)));

        // Remove some books from user's shelf.
        String toRemove = "9787550217454";
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/bookshelf/" + toRemove).cookie(cookie))
                .andExpect(status().isOk());

        // Get the books from user's shelf.
        String booksISBNActual2[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/users/bookshelf").cookie(cookie)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), String[].class);

        booksISBN.remove(toRemove);
        AssertJUnit.assertEquals(booksISBN, new HashSet<String>(Arrays.asList(booksISBNActual2)));
    }

}

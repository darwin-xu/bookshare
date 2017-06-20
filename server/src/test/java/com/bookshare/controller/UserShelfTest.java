package com.bookshare.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bookshare.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserShelfTest {

    ObjectMapper mapper = new ObjectMapper();

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

        // Post the new book for this user.
        String books[] = { "9787514610307", "9787550284340", "9787550217454", "9787030324672", "9787569914061" };
        mockMvc.perform(MockMvcRequestBuilders.post("/users/postBooks").cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(books)))
                .andExpect(status().isOk());
    }

}

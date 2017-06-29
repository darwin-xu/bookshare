package com.bookshare.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;

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
public class SharingTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    private Cookie cookie1;
    private Cookie cookie2;
    private Cookie cookie3;

    public void prepareUsershelf() throws Exception {
        User user = new User();
        MvcResult result;

        // Create first user.
        user.setUsername("st1");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isCreated());

        user.setVerifyCode("112233");
        user.setPassword("123");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isOk());

        user.setPassword("123");
        result = mockMvc.perform(MockMvcRequestBuilders.post("/sessions/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isOk()).andReturn();

        cookie1 = result.getResponse().getCookies()[0];

        // Add books for first user.
        String isbn1[] = { "9787550263932", "9787535491657" };
        for (String isbn : isbn1) {
            mockMvc.perform(MockMvcRequestBuilders.post("/users/bookshelf/" + isbn).cookie(cookie1))
                    .andExpect(status().isOk());
        }

        // Create second user.
        user.setUsername("st2");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isCreated());

        user.setVerifyCode("112233");
        user.setPassword("123");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isOk());

        user.setPassword("123");
        result = mockMvc.perform(MockMvcRequestBuilders.post("/sessions/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isOk()).andReturn();

        cookie2 = result.getResponse().getCookies()[0];

        // Add books for second user.
        String isbn2[] = { "9787550291782", "9787535491657", "9787507546460", "9787544270472" };
        for (String isbn : isbn2) {
            mockMvc.perform(MockMvcRequestBuilders.post("/users/bookshelf/" + isbn).cookie(cookie2))
                    .andExpect(status().isOk());
        }

        // Create third user.
        user.setUsername("st3");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/getVerifyCode").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isCreated());

        user.setVerifyCode("112233");
        user.setPassword("123");
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/changePassword").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isOk());

        user.setPassword("123");
        result = mockMvc.perform(MockMvcRequestBuilders.post("/sessions/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))).andExpect(status().isOk()).andReturn();

        cookie3 = result.getResponse().getCookies()[0];

        // Add books for third user.
        String isbn3[] = { "9787544270472", "9787535491657", "9787514122756" };
        for (String isbn : isbn3) {
            mockMvc.perform(MockMvcRequestBuilders.post("/users/bookshelf/" + isbn).cookie(cookie3))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void testDemand() throws Exception {
        prepareUsershelf();

        mockMvc.perform(MockMvcRequestBuilders.post("/sharing/demand/9787535491657").cookie(cookie1))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/sharing/demand/9787544270472").cookie(cookie2))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/sharing/demand/9787514122756").cookie(cookie3))
                .andExpect(status().isOk());

        // Get responds for the first session.
        String isbnsActual1[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/sharing/getResponds").cookie(cookie1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), String[].class);

        String isbnsExpect1[] = { "9787535491657" };
        assertEquals(new HashSet<String>(Arrays.asList(isbnsExpect1)),
                new HashSet<String>(Arrays.asList(isbnsActual1)));

        // Get responds for the second session.
        String isbnsActual2[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/sharing/getResponds").cookie(cookie2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), String[].class);

        String isbnsExpect2[] = { "9787535491657", "9787544270472" };
        assertEquals(new HashSet<String>(Arrays.asList(isbnsExpect2)),
                new HashSet<String>(Arrays.asList(isbnsActual2)));

        // Get responds for the third session.
        String isbnsActual3[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/sharing/getResponds").cookie(cookie3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), String[].class);

        String isbnsExpect3[] = { "9787535491657", "9787544270472", "9787514122756" };
        assertEquals(new HashSet<String>(Arrays.asList(isbnsExpect3)),
                new HashSet<String>(Arrays.asList(isbnsActual3)));
    }

}

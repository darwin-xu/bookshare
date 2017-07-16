package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.Test;

@Test
@SpringBootTest
@AutoConfigureMockMvc
public class Tryouts extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void tryDemand() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/sharing/test")).andExpect(status().isOk());
//        mockMvc.perform(MockMvcRequestBuilders.get("/sharing/test1")).andExpect(status().isOk());
    }

}

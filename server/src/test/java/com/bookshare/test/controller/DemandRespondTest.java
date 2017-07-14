package com.bookshare.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

@Test
@SpringBootTest
@AutoConfigureMockMvc
public class DemandRespondTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getBook() throws Exception {
        String books[]={""};
        
        //           'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'
        char v1[] = {'+', '+', '+', '+', '+', ' ', ' ', ' ', ' '};
        char w1[] = {'+', ' ', '+', ' ', '+', ' ', ' ', ' ', ' '};
        char x1[] = {' ', ' ', ' ', ' ', '+', '+', '+', '+', '+'};
        char y1[] = {' ', ' ', ' ', ' ', '+', ' ', '+', ' ', '+'};
        char z1[] = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
        
        //           'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'
        char v2[] = {' ', ' ', ' ', ' ', ' ', '*', '*', '*', ' '};
        char w2[] = {' ', ' ', ' ', ' ', ' ', ' ', '*', '*', '*'};
        char x2[] = {'*', '*', '*', ' ', ' ', ' ', ' ', ' ', ' '};
        char y2[] = {' ', ' ', '*', '*', '*', ' ', ' ', ' ', ' '};
        char z2[] = {' ', '*', '*', ' ', '*', '*', '*', ' ', ' '};
        // ------------------------------------------------------
        //            1    2    3    1    2    2    3    2    1
        
        //           'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'
        char v3[] = {' ', ' ', ' ', ' ', ' ', '*', '*', '*', ' '};
        char w3[] = {' ', ' ', ' ', ' ', ' ', ' ', '*', '*', '*'};
        char x3[] = {'*', '*', '*', ' ', ' ', ' ', ' ', ' ', ' '};
        char y3[] = {' ', ' ', '*', '*', '*', ' ', ' ', ' ', ' '};
        char z3[] = {' ', '*', '*', ' ', '*', '*', '*', ' ', ' '};
    }

}

package com.bookshare.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.bookshare.domain.Demand;
import com.bookshare.domain.Respond;
import com.bookshare.domain.User;
import com.bookshare.utility.TestCaseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class SharingTest extends AbstractTestNGSpringContextTests {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    private Cookie cookie1;
    private Cookie cookie2;
    private Cookie cookie3;

    @BeforeClass
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

    @Test(groups = "create")
    public void createDemands() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/sharing/demands/book/9787535491657").cookie(cookie1))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/sharing/demands/book/9787535491657").cookie(cookie2))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/sharing/demands/book/9787544270472").cookie(cookie2))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/sharing/demands/book/9787514122756").cookie(cookie3))
                .andExpect(status().isCreated());
    }

    @Test(groups = "check", dependsOnGroups = "create")
    public void checkDemands() throws Exception {
        // Get demands for the first session.
        Demand demandsActual1[] = mapper
                .readValue(
                        mockMvc.perform(MockMvcRequestBuilders.get("/sharing/demands").cookie(cookie1))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                        Demand[].class);
        String isbnsDemandExpect1[] = { "9787535491657" };
        assertEquals(TestCaseUtil.sortedStringList(isbnsDemandExpect1),
                TestCaseUtil.sortedStringList(getIsbns(demandsActual1)));

        // Get demands for the first session.
        Demand demandsActual2[] = mapper
                .readValue(
                        mockMvc.perform(MockMvcRequestBuilders.get("/sharing/demands").cookie(cookie2))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                        Demand[].class);
        String isbnsDemandExpect2[] = { "9787535491657", "9787544270472" };
        assertEquals(TestCaseUtil.sortedStringList(isbnsDemandExpect2),
                TestCaseUtil.sortedStringList(getIsbns(demandsActual2)));

        // Get demands for the first session.
        Demand demandsActual3[] = mapper
                .readValue(
                        mockMvc.perform(MockMvcRequestBuilders.get("/sharing/demands").cookie(cookie3))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                        Demand[].class);
        String isbnsDemandExpect3[] = { "9787514122756" };
        assertEquals(TestCaseUtil.sortedStringList(isbnsDemandExpect3),
                TestCaseUtil.sortedStringList(getIsbns(demandsActual3)));
    }

    @Test(groups = "check", dependsOnGroups = "create")
    public void checkResponds() throws Exception {
        // Get responds for the first session.
        Respond respondsActual1[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/sharing/responds").cookie(cookie1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Respond[].class);
        String isbnsRespondExpect1[] = { "9787535491657", "9787535491657" };
        assertEquals(TestCaseUtil.sortedStringList(isbnsRespondExpect1),
                TestCaseUtil.sortedStringList(getIsbns(respondsActual1)));
        for (Respond r : respondsActual1) {
            assertEquals(Boolean.FALSE, r.getCancalled());
        }

        // Get responds for the second session.
        Respond respondsActual2[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/sharing/responds").cookie(cookie2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Respond[].class);
        String isbnsRespondExpect2[] = { "9787535491657", "9787544270472", "9787535491657" };
        assertEquals(TestCaseUtil.sortedStringList(isbnsRespondExpect2),
                TestCaseUtil.sortedStringList(getIsbns(respondsActual2)));
        for (Respond r : respondsActual2) {
            assertEquals(Boolean.FALSE, r.getCancalled());
        }

        // Get responds for the third session.
        Respond respondsActual3[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/sharing/responds").cookie(cookie3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Respond[].class);
        String isbnsRespondExpect3[] = { "9787535491657", "9787544270472", "9787514122756", "9787535491657" };
        assertEquals(TestCaseUtil.sortedStringList(isbnsRespondExpect3),
                TestCaseUtil.sortedStringList(getIsbns(respondsActual3)));
        for (Respond r : respondsActual3) {
            assertEquals(Boolean.FALSE, r.getCancalled());
        }
    }

    @Test(groups = "change", dependsOnGroups = "check")
    public void changeDemands() throws Exception {
        // Get and change the demands for the first session.
        Demand demand1old[] = mapper
                .readValue(
                        mockMvc.perform(MockMvcRequestBuilders.get("/sharing/demands").cookie(cookie1))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                        Demand[].class);
        for (Demand d : demand1old) {
            assertEquals(Boolean.FALSE, d.getCancalled());
            d.setCancalled(true);
            mockMvc.perform(MockMvcRequestBuilders.put("/sharing/demands/" + d.getId()).cookie(cookie1)
                    .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(d)))
                    .andExpect(status().isOk());
        }
        Demand demand1new[] = mapper
                .readValue(
                        mockMvc.perform(MockMvcRequestBuilders.get("/sharing/demands").cookie(cookie1))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                        Demand[].class);
        for (Demand d : demand1new) {
            assertEquals(Boolean.TRUE, d.getCancalled());
        }

        // Get and change the demands for the second session.
        Demand demand2old[] = mapper
                .readValue(
                        mockMvc.perform(MockMvcRequestBuilders.get("/sharing/demands").cookie(cookie2))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                        Demand[].class);
        String bookToBeCancalled = "9787535491657";
        boolean match1 = false;
        boolean match2 = false;
        for (Demand d : demand2old) {
            assertEquals(Boolean.FALSE, d.getCancalled());
            if (d.getIsbn().equals(bookToBeCancalled)) {
                d.setCancalled(true);
                mockMvc.perform(MockMvcRequestBuilders.put("/sharing/demands/" + d.getId()).cookie(cookie2)
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(d)))
                        .andExpect(status().isOk());
                match1 = true;
            } else {
                match2 = true;
            }
        }
        assertEquals(true, match1);
        assertEquals(true, match2);
        Demand demand2new[] = mapper
                .readValue(
                        mockMvc.perform(MockMvcRequestBuilders.get("/sharing/demands").cookie(cookie2))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                        Demand[].class);
        match1 = false;
        match2 = false;
        for (Demand d : demand2new) {
            if (d.getIsbn().equals(bookToBeCancalled)) {
                assertEquals(Boolean.TRUE, d.getCancalled());
                match1 = true;
            } else {
                assertEquals(Boolean.FALSE, d.getCancalled());
                match2 = true;
            }
        }
        assertEquals(true, match1);
        assertEquals(true, match2);
    }

    @Test(groups = "change", dependsOnGroups = "check")
    public void changeResponds() throws Exception {
        // Get responds for the second session.
        Respond respondsOld[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/sharing/responds").cookie(cookie2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Respond[].class);
        String bookToBeAgreed = "9787535491657";
        boolean match1 = false;
        boolean match2 = false;
        for (Respond r : respondsOld) {
            assertEquals(Boolean.FALSE, r.getAgreed());
            if (r.getDemand().getIsbn().equals(bookToBeAgreed)) {
                r.setAgreed(true);
                assertEquals(Boolean.TRUE, r.getCancalled());
                mockMvc.perform(MockMvcRequestBuilders.put("/sharing/responds/" + r.getId()).cookie(cookie2)
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(r)))
                        .andExpect(status().isOk());
                match1 = true;
            } else {
                assertEquals(Boolean.FALSE, r.getCancalled());
                match2 = true;
            }
        }
        assertEquals(true, match1);
        assertEquals(true, match2);
        Respond respondsNew[] = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/sharing/responds").cookie(cookie2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Respond[].class);
        match1 = false;
        match2 = false;
        for (Respond r : respondsNew) {
            if (r.getDemand().getIsbn().equals(bookToBeAgreed)) {
                assertEquals(Boolean.TRUE, r.getAgreed());
                match1 = true;
            } else {
                assertEquals(Boolean.FALSE, r.getAgreed());
                match2 = true;
            }
        }
        assertEquals(true, match1);
        assertEquals(true, match2);
    }

}

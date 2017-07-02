package com.bookshare.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.Test;

import com.bookshare.business.AuditManager;
import com.bookshare.domain.Audit;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class BookBackendTest extends AbstractTestNGSpringContextTests {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getBook() throws Exception {
        // 1. Get the ISBNQueryCount for the first time.
        Audit audit = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/audit/" + AuditManager.isbnQueryCount)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Audit.class);
        int baseCount = audit.getCount();

        // 2. Get the ISBNQueryCount after GET books/{isbn}.
        mockMvc.perform(MockMvcRequestBuilders.get("/books/9787500648192").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        audit = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/audit/" + AuditManager.isbnQueryCount)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Audit.class);
        assertEquals(baseCount + 1, audit.getCount());

        // 3. Get the ISBNQueryCount again after GET books/{isbn}.
        mockMvc.perform(MockMvcRequestBuilders.get("/books/9787500648192").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        audit = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/audit/" + AuditManager.isbnQueryCount)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Audit.class);
        assertEquals(baseCount + 1, audit.getCount());
    }

}

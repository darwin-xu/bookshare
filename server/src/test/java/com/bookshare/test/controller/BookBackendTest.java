package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import com.bookshare.business.AuditManager;
import com.bookshare.domain.Audit;

@Test
@SpringBootTest
@AutoConfigureMockMvc
public class BookBackendTest extends AbstractMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test(timeOut = timeout_ms)
    public void getBook() throws Exception {
        // 1. Get the ISBNQueryCount for the first time.
        Audit audit = perform(mockMvc, Method.GET, "/audit/" + AuditManager.isbnQueryCount, null, null, status().isOk(),
                Audit.class);
        int baseCount = audit.getCount();

        // 2. Get the ISBNQueryCount after GET books/{isbn}.
        perform(mockMvc, Method.GET, "/books/9787500648192", null, null, status().isOk(), null);
        audit = perform(mockMvc, Method.GET, "/audit/" + AuditManager.isbnQueryCount, null, null, status().isOk(),
                Audit.class);
        assertEquals(baseCount + 1, audit.getCount());

        // 3. Get the ISBNQueryCount again after GET books/{isbn}.
        perform(mockMvc, Method.GET, "/books/9787500648192", null, null, status().isOk(), null);
        audit = perform(mockMvc, Method.GET, "/audit/" + AuditManager.isbnQueryCount, null, null, status().isOk(),
                Audit.class);
        assertEquals(baseCount + 1, audit.getCount());
    }

}

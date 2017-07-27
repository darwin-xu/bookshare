package com.bookshare.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertEquals;

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

    @Test(groups = "init", timeOut = ext_timeout_ms)
    public void getBook1() throws Exception {
        String isbns[] = { "9787514610307", "9787550284340", "9787550217454", "9787030324672", "9787569914061",
                "9787505417731", "9787508622545", "9787301150894", "9787516810941", "9787509766989", "9787553805900",
                "9787550278998", "9787508665450" };
        for (String isbn : isbns) {
            perform(mockMvc, Method.GET, "/books/" + isbn, null, null, status().isOk(), null);
        }
    }

    @Test(dependsOnGroups = "init", timeOut = ext_timeout_ms)
    public void getBook() throws Exception {
        // 1. Get the ISBNQueryCount for the first time.
        Audit audit = perform(mockMvc, Method.GET, "/audit/" + AuditManager.isbnQueryCount, null, null, status().isOk(),
                Audit.class);
        int baseCount = audit.getCount();

        // 2. Get the ISBNQueryCount after GET books/{isbn}.
        perform(mockMvc, Method.GET, "/books/9787500648192", null, null, status().isOk(), null);
        audit = perform(mockMvc, Method.GET, "/audit/" + AuditManager.isbnQueryCount, null, null, status().isOk(),
                Audit.class);
        assertEquals(audit.getCount(), baseCount + 1);

        // 3. Get the ISBNQueryCount again after GET books/{isbn}.
        perform(mockMvc, Method.GET, "/books/9787500648192", null, null, status().isOk(), null);
        audit = perform(mockMvc, Method.GET, "/audit/" + AuditManager.isbnQueryCount, null, null, status().isOk(),
                Audit.class);
        assertEquals(audit.getCount(), baseCount + 1);
    }

}

package com.bookshare;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AppPortalTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getSheet() throws Exception {
        com.bookshare.dto.Sheet sheetActual = mapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.get("/app/sheet/Library").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                com.bookshare.dto.Sheet.class);

        com.bookshare.dto.Sheet sheetExpect = new com.bookshare.dto.Sheet();

        sheetExpect.setName("Library");
        String sections[] = { "热门", "经典", "流行", "青春" };
        sheetExpect.setSections(sections);
        assertEquals(sheetExpect, sheetActual);
    }

    @Test
    public void getSection() throws Exception {
        com.bookshare.dto.Section sectionActual = mapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.get("/app/section/经典").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                com.bookshare.dto.Section.class);

        com.bookshare.dto.Section sectionExpect = new com.bookshare.dto.Section();

        sectionExpect.setName("经典");
        String isbns[] = { "9787516810941", "9787509766989", "9787553805900", "9787550278998", "9787508665450",
                "9787301268711" };
        sectionExpect.setIsbns(isbns);
        assertEquals(sectionExpect, sectionActual);
    }

}

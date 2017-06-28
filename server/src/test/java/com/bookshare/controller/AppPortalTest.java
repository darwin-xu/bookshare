package com.bookshare.controller;

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

import com.bookshare.dto.SectionDto;
import com.bookshare.dto.SheetDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AppPortalTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getSheet() throws Exception {
        String sheetName = "Library";

        SheetDto sheetActual = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/app/sheets/" + sheetName).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), SheetDto.class);

        SheetDto sheetExpect = new SheetDto();
        sheetExpect.setName(sheetName);
        String sections[] = { "热门", "经典", "流行", "青春" };
        sheetExpect.setSections(sections);
        assertEquals(sheetExpect, sheetActual);
    }

    @Test
    public void postExistingSheet() throws Exception {
        String sheetName = "Library";

        SheetDto sheet = new SheetDto();
        sheet.setName(sheetName);
        String sections[] = { "Science fiction", "Drama", "古典", "Health", "Religion, Spirituality & New Age", "Science",
                "History", "Guide" };
        sheet.setSections(sections);

        mockMvc.perform(MockMvcRequestBuilders.post("/app/sheets/" + sheetName).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sheet))).andExpect(status().isConflict());
    }

    @Test
    public void postNonexistentSheet() throws Exception {
        String sheetName = "推荐";

        SheetDto sheet = new SheetDto();
        sheet.setName(sheetName);
        String sections[] = { "Science fiction", "Drama", "古典", "Health", "Religion, Spirituality & New Age", "Science",
                "History", "Guide" };
        sheet.setSections(sections);

        mockMvc.perform(MockMvcRequestBuilders.post("/app/sheets/" + sheetName).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sheet))).andExpect(status().isOk());

        SheetDto sheetActual = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/app/sheets/" + sheetName).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), SheetDto.class);
        assertEquals(sheet, sheetActual);
    }

    @Test
    public void patchExistingSheet() throws Exception {
        String sheetName = "Library库";

        SheetDto sheet = new SheetDto();
        sheet.setName(sheetName);
        String sections1[] = { "xxx" };
        sheet.setSections(sections1);
        mockMvc.perform(MockMvcRequestBuilders.post("/app/sheets/" + sheetName).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sheet))).andExpect(status().isOk());

        String sections2[] = { "1. 法学", "2. 医学", "天文学书籍‎", "旅游指南‎", "軍事書籍‎ Military" };
        sheet.setSections(sections2);
        mockMvc.perform(MockMvcRequestBuilders.patch("/app/sheets/" + sheetName).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sheet))).andExpect(status().isOk());

        SheetDto sheetActual = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/app/sheets/" + sheetName).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), SheetDto.class);
        assertEquals(sheet, sheetActual);
    }

    @Test
    public void patchNonexistentSheet() throws Exception {
        String sheetName = "Nonexistent";

        SheetDto sheet = new SheetDto();
        sheet.setName(sheetName);
        String sections1[] = { "xxx" };
        sheet.setSections(sections1);
        mockMvc.perform(MockMvcRequestBuilders.patch("/app/sheets/" + sheetName).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sheet))).andExpect(status().isNotFound());
    }

    @Test
    public void getSection() throws Exception {
        String sectionName = "经典";

        SectionDto sectionActual = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/app/sections/" + sectionName).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), SectionDto.class);

        SectionDto sectionExpect = new SectionDto();
        sectionExpect.setName(sectionName);
        String isbns[] = { "9787516810941", "9787509766989", "9787553805900", "9787550278998", "9787508665450",
                "9787301268711" };
        sectionExpect.setIsbns(isbns);
        assertEquals(sectionExpect, sectionActual);
    }

    @Test
    public void postExistingSection() throws Exception {
        String sectionName = "热门";

        SectionDto section = new SectionDto();
        section.setName(sectionName);
        String isbns[] = { "1", "2", "3", "4", "5", "6" };
        section.setIsbns(isbns);

        mockMvc.perform(MockMvcRequestBuilders.post("/app/sections/" + sectionName)
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(section)))
                .andExpect(status().isConflict());
    }

    @Test
    public void postNonexistentSection() throws Exception {
        String sectionName = "New section";

        SectionDto section = new SectionDto();
        section.setName(sectionName);
        String isbns[] = { "5", "4", "3", "2", "1" };
        section.setIsbns(isbns);

        mockMvc.perform(MockMvcRequestBuilders.post("/app/sections/" + sectionName)
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(section)))
                .andExpect(status().isOk());

        SectionDto sectionActual = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/app/sections/" + sectionName).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), SectionDto.class);
        assertEquals(section, sectionActual);
    }

    @Test
    public void patchExistingSection() throws Exception {
        String sectionName = "热门";

        SectionDto section = new SectionDto();
        section.setName(sectionName);
        String isbns[] = { "11", "22", "33", "44", "55", "66" };
        section.setIsbns(isbns);
        mockMvc.perform(MockMvcRequestBuilders.patch("/app/sections/" + sectionName)
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(section)))
                .andExpect(status().isOk());

        SectionDto sectionActual = mapper.readValue(mockMvc
                .perform(MockMvcRequestBuilders.get("/app/sections/" + sectionName).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), SectionDto.class);
        assertEquals(section, sectionActual);
    }

    @Test
    public void patchNonexistentSection() throws Exception {
        String sectionName = "不存在";

        SectionDto section = new SectionDto();
        section.setName(sectionName);
        String isbns[] = { "11", "22", "33", "44", "55", "66" };
        section.setIsbns(isbns);
        mockMvc.perform(MockMvcRequestBuilders.patch("/app/sections/" + sectionName)
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(section)))
                .andExpect(status().isNotFound());
    }

}

package com.bookshare.controller;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.SheetRepository;
import com.bookshare.domain.app.Sheet;
import com.bookshare.dto.SheetDto;

@RestController
@RequestMapping(value = "app/sheets")
public class SheetController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private SheetRepository sheetRepository;

    @Autowired
    public void setUserRepository(SheetRepository sheetRepository) {
        this.sheetRepository = sheetRepository;
    }

    @RequestMapping(value = "{sheetName}", method = RequestMethod.GET, produces = "application/json")
    public SheetDto getByName(@PathVariable(value = "sheetName") String sheetName) {
        logger.debug("getByName-> SheetName: " + sheetName);
        Sheet sheets[] = sheetRepository.findBySheetName(sheetName);
        Vector<String> sectionVector = new Vector<String>();
        for (Sheet s : sheets) {
            logger.debug("    " + s.getSectionName());
            sectionVector.add(s.getSectionName());
        }
        SheetDto returnSheet = new SheetDto();
        returnSheet.setName(sheetName);
        returnSheet.setSections(sectionVector.toArray(new String[sectionVector.size()]));
        return returnSheet;
    }

    @Transactional
    @RequestMapping(value = "{sheetName}", method = RequestMethod.POST)
    public void postByName(@PathVariable(value = "sheetName") String sheetName, @RequestBody SheetDto sheet,
            HttpServletResponse response) {
        logger.debug("postByName-> SheetName: " + sheetName);
        // 1. Check if the old data exists.
        Sheet sheets[] = sheetRepository.findBySheetName(sheetName);
        if (sheets.length == 0) {
            // 2. Convert Sheet into com.bookshare.domain.app.Sheet
            Vector<Sheet> sheetVector = new Vector<Sheet>();
            for (String section : sheet.getSections()) {
                Sheet s = new Sheet();
                s.setSheetName(sheetName);
                s.setSectionName(section);
                sheetVector.add(s);
            }
            // 3. Save it into repository
            sheetRepository.save(sheetVector);
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

    @Transactional
    @RequestMapping(value = "{sheetName}", method = RequestMethod.PATCH)
    public void patchByName(@PathVariable(value = "sheetName") String sheetName, @RequestBody SheetDto sheet,
            HttpServletResponse response) {
        logger.debug("patchByName-> SheetName: " + sheetName);
        // 1. Check if the old data exists.
        Sheet sheets[] = sheetRepository.findBySheetName(sheetName);
        if (sheets.length != 0) {
            // 2. Remove the old data.
            sheetRepository.delete(Arrays.asList(sheets));
            // 3. Convert Sheet into com.bookshare.domain.app.Sheet
            Vector<Sheet> sheetVector = new Vector<Sheet>();
            for (String section : sheet.getSections()) {
                Sheet s = new Sheet();
                s.setSheetName(sheetName);
                s.setSectionName(section);
                sheetVector.add(s);
            }
            // 4. Save it into repository
            sheetRepository.save(sheetVector);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}

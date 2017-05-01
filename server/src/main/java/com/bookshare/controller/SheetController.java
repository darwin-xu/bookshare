package com.bookshare.controller;

import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.SheetRepository;
import com.bookshare.domain.Sheet;

@RestController
@RequestMapping(value = "app/sheet")
public class SheetController {

    private static final Logger logger = LoggerFactory.getLogger(SheetController.class);

    private SheetRepository sheetRepository;

    @Autowired
    public void setUserRepository(SheetRepository sheetRepository) {
        this.sheetRepository = sheetRepository;
    }

    @RequestMapping(value = "{sheetName}", method = RequestMethod.GET, produces = "application/json")
    public com.bookshare.dto.Sheet getByName(@PathVariable(value = "sheetName") String sheetName,
            HttpServletResponse response) {
        logger.debug("SheetName: " + sheetName);
        Sheet sheets[] = sheetRepository.findBySheetName(sheetName);
        Vector<String> columnVector = new Vector<String>();
        for (Sheet s : sheets) {
            logger.debug("    " + s.getColumnName());
            columnVector.add(s.getColumnName());
        }

        com.bookshare.dto.Sheet returnSheet = new com.bookshare.dto.Sheet();
        returnSheet.setName(sheetName);
        returnSheet.setColumns(columnVector.toArray(new String[columnVector.size()]));
        return returnSheet;
    }
}

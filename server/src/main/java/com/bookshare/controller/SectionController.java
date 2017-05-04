package com.bookshare.controller;

import java.lang.invoke.MethodHandles;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.SectionRepository;
import com.bookshare.domain.app.Section;

@RestController
@RequestMapping(value = "app/section")
public class SectionController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private SectionRepository sectionRepository;

    @Autowired
    public void setUserRepository(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @RequestMapping(value = "{sectionName}", method = RequestMethod.GET, produces = "application/json")
    public com.bookshare.dto.Section getByName(@PathVariable(value = "sectionName") String sectionName) {
        logger.debug("SectionName: " + sectionName);
        Section sections[] = sectionRepository.findBySectionName(sectionName);
        Vector<String> sectionVector = new Vector<String>();
        for (Section s : sections) {
            logger.debug("    " + s.getIsbn());
            sectionVector.add(s.getIsbn());
        }
        com.bookshare.dto.Section returnSection = new com.bookshare.dto.Section();
        returnSection.setName(sectionName);
        returnSection.setIsbns(sectionVector.toArray(new String[sectionVector.size()]));
        return returnSection;
    }

}

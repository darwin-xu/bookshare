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

import com.bookshare.dao.SectionRepository;
import com.bookshare.domain.app.Section;
import com.bookshare.dto.SectionDto;

@RestController
@RequestMapping(value = "app/sections")
public class SectionController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private SectionRepository sectionRepository;

    @RequestMapping(value = "{sectionName}", method = RequestMethod.GET, produces = "application/json")
    public SectionDto getByName(@PathVariable(value = "sectionName") String sectionName) {
        logger.debug("SectionName: " + sectionName);
        Section sections[] = sectionRepository.findBySectionName(sectionName);
        Vector<String> sectionVector = new Vector<String>();
        for (Section s : sections) {
            logger.debug("    " + s.getIsbn());
            sectionVector.add(s.getIsbn());
        }
        SectionDto returnSection = new SectionDto();
        returnSection.setName(sectionName);
        returnSection.setIsbns(sectionVector.toArray(new String[sectionVector.size()]));
        return returnSection;
    }

    @Transactional
    @RequestMapping(value = "{sectionName}", method = RequestMethod.POST)
    public void postByName(@PathVariable(value = "sectionName") String sectionName, @RequestBody SectionDto section,
            HttpServletResponse response) {
        logger.debug("SectionName: " + sectionName);
        // 1. Check if the old data exists.
        Section sections[] = sectionRepository.findBySectionName(sectionName);
        if (sections.length == 0) {
            // 2. Convert Section into com.bookshare.domain.app.Section
            Vector<Section> sectionVector = new Vector<Section>();
            for (String isbn : section.getIsbns()) {
                Section s = new Section();
                s.setSectionName(sectionName);
                s.setIsbn(isbn);
                sectionVector.add(s);
            }
            // 3. Save it into repository
            sectionRepository.save(sectionVector);
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

    @Transactional
    @RequestMapping(value = "{sectionName}", method = RequestMethod.PATCH)
    public void patchByName(@PathVariable(value = "sectionName") String sectionName, @RequestBody SectionDto section,
            HttpServletResponse response) {
        logger.debug("SectionName: " + sectionName);
        // 1. Check if the old data exists.
        Section sections[] = sectionRepository.findBySectionName(sectionName);
        if (sections.length != 0) {
            // 2. Remove the old data.
            sectionRepository.delete(Arrays.asList(sections));
            // 3. Convert Section into com.bookshare.domain.app.Section
            Vector<Section> sectionVector = new Vector<Section>();
            for (String isbn : section.getIsbns()) {
                Section s = new Section();
                s.setSectionName(sectionName);
                s.setIsbn(isbn);
                sectionVector.add(s);
            }
            // 4. Save it into repository
            sectionRepository.save(sectionVector);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}

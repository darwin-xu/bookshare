package com.bookshare.controller;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.AuditRepository;
import com.bookshare.domain.Audit;

@RestController
@RequestMapping("audit")
public class AuditController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private AuditRepository auditRepository;

    @RequestMapping(value = "{auditItem}", method = RequestMethod.GET, produces = "application/json")
    public Audit getAuditData(@PathVariable String auditItem) {
        logger.debug("getAuditData:" + auditItem);
        Audit audit = auditRepository.findByItem(auditItem);
        if (audit == null) {
            audit = new Audit();
            audit.setItem(auditItem);
            audit.setCount(0);
        }
        return audit;
    }

}

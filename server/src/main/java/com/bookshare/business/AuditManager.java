package com.bookshare.business;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.bookshare.dao.AuditRepository;
import com.bookshare.domain.Audit;

@Service
@Configurable
public class AuditManager {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String isbnQueryCount = "ISBNQueryCount";

    @Autowired
    private AuditRepository auditRepository;

    public void isbnQueryCountIncrease() {
        Audit audit = auditRepository.findByItem(isbnQueryCount);
        if (audit == null) {
            audit = new Audit();
            audit.setItem(isbnQueryCount);
            audit.setCount(0);
            auditRepository.save(audit);
        }
        audit.setCount(audit.getCount() + 1);
        auditRepository.save(audit);
        logger.debug(isbnQueryCount + ": " + audit.getCount());
    }

    public int getIsbnQueryCount() {
        Audit audit = auditRepository.findByItem(isbnQueryCount);
        if (audit == null) {
            return 0;
        } else {
            return audit.getCount();
        }
    }

}

package com.bookshare.background;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bookshare.dao.BookshelfRepository;
import com.bookshare.dao.DemandRepository;
import com.bookshare.dao.RespondRepository;
import com.bookshare.domain.Bookshelf;
import com.bookshare.domain.Demand;
import com.bookshare.domain.Respond;

@Component
@RequestMapping("background")
public class DemandDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${bookshare.book.dispatch.demand-expire-min}")
    private int demandExpireMin;

    @Value("${bookshare.book.dispatch.first-respond-trigger-min}")
    private int firstRespondTriggerMin;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private RespondRepository respondRepository;

    @Autowired
    BookshelfRepository bookshelfRepository;

    @Transactional
    @Scheduled(fixedDelayString = "${bookshare.book.dispatch.interval-sec:60}000")
    public void createRespondsForDemands() {
        synchronized (this) {
            logger.trace("=== Begin ===");

            List<Demand> ds = demandRepository.findByResponds_Id(null);
            for (Demand d : ds) {
                logger.trace("Find demands need to process for ISBN[" + d.getIsbn() + "]");
                List<Bookshelf> bookshelfs = bookshelfRepository.findAvailable(d.getIsbn());
                for (Bookshelf b : bookshelfs) {
                    logger.trace("   " + b.getUser().getUsername());
                    Respond r = new Respond(d, b);
                    respondRepository.save(r);
                }
            }

            logger.trace("=== End ====");
            notifyAll();
        }
    }

    @RequestMapping(value = "waitForDispatch", method = RequestMethod.GET)
    public void waitForDispatch() throws InterruptedException {
        synchronized (this) {
            this.wait();
        }
    }

}

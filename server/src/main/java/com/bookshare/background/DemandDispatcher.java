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
import com.bookshare.dao.Demand1Repository;
import com.bookshare.dao.DemandRepository;
import com.bookshare.dao.Respond1Repository;
import com.bookshare.dao.RespondRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Bookshelf;
import com.bookshare.domain.Demand;
import com.bookshare.domain.Demand1;
import com.bookshare.domain.Respond;
import com.bookshare.domain.Respond1;
import com.bookshare.domain.User;

@Component
@RequestMapping("background")
public class DemandDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${bookshare.book.dispatch.demand-expire-min}")
    private int demandExpireMin;

    @Value("${bookshare.book.dispatch.first-respond-trigger-min}")
    private int firstRespondTriggerMin;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private Demand1Repository demand1Repository;

    @Autowired
    private RespondRepository respondRepository;

    @Autowired
    private Respond1Repository respond1Repository;

    @Autowired
    BookshelfRepository bookshelfRepository;

    @Transactional
    @Scheduled(fixedDelayString = "${bookshare.book.dispatch.interval-sec:60}000")
    public void createRespondsForDemands() {
        synchronized (this) {
            logger.trace("=== Begin ===");
            logger.trace("=======" + demandExpireMin + "===" + firstRespondTriggerMin);
            // Find all demands without the corresponding responds yet.
            List<Demand> demands = demandRepository.findByResponds_Id(null);
            // Iterate over the demands.
            for (Demand d : demands) {
                // Find all users who have this book.
                List<User> users = userRepository.findByBookList_Isbn13(d.getIsbn());
                int priority = 0;
                for (User userHasTheBook : users) {
                    // Create a new respond for the user who has the book.
                    Respond rpd = new Respond();
                    rpd.setDemand(d);
                    rpd.setUser(userHasTheBook);
                    rpd.setPriority(priority++);
                    respondRepository.save(rpd);
                }
            }

            List<Demand1> ds = demand1Repository.findByResponds_Id(null);
            for (Demand1 d : ds) {
                logger.trace("SSS:    Process demand:" + d.getIsbn());
                List<Bookshelf> bookshelfs = bookshelfRepository.findAvailable(d.getIsbn());
                for (Bookshelf b : bookshelfs) {
                    logger.trace("SSS:    Process bookshelfs:" + b.getUser().getUsername());
                    Respond1 r = new Respond1(d, b);
                    respond1Repository.save(r);
                }
            }

            List<Respond> responds = respondRepository.findByAgreed();
            for (Respond r : responds) {
                logger.trace("EEE: " + r.getDemand().getUser() + " wants: " + r.getDemand().getIsbn() + " "
                        + r.getUser().getUsername() + " has it. " + r.getPriority() + " S:" + r.getSelected());
            }

            List<String> isbns = respondRepository.findAllAgreedIsbns();
            for (String isbn : isbns) {
                logger.trace("FFF: " + isbn);
                // respondRepository.selectRespond(isbn);
            }

            logger.trace("EEEAAA:");

            List<Respond> responds1 = respondRepository.findByAgreed();
            for (Respond r : responds1) {
                logger.trace("EEE: " + r.getDemand().getUser() + " wants: " + r.getDemand().getIsbn() + " "
                        + r.getUser().getUsername() + " has it. " + r.getPriority() + " S:" + r.getSelected());
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

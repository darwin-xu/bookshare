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

import com.bookshare.dao.DemandRepository;
import com.bookshare.dao.RespondRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Demand;
import com.bookshare.domain.Respond;
import com.bookshare.domain.User;

@Component
@RequestMapping("background")
public class DemandDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${bookshare.book.dispatch.demand-expire-min}")
    private int demandExpireMin;

    @Value("${bookshare.book.dispatch.respond-first-expire-min}")
    private int respondFirstExpireMin;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private RespondRepository respondRepository;

    @Transactional
    @Scheduled(fixedDelayString = "${bookshare.book.dispatch.interval-sec:60}000")
    public void createRespondsForDemands() {
        synchronized (this) {
            logger.trace("=== Begin ===");
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

            List<String> isbns = respondRepository.findAllAgreedIsbns();
            for (String isbn : isbns) {
                logger.trace("FFF: " + isbn);
            }

            List<Respond> responds = respondRepository.findByAgreed();
            for (Respond r : responds) {
                logger.trace("EEE: " + r.getDemand().getUser() + " wants: " + r.getDemand().getIsbn() + " "
                        + r.getUser().getUsername() + " has it. " + r.getPriority());
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

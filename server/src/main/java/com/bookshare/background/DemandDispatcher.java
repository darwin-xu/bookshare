package com.bookshare.background;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bookshare.dao.DemandRepository;
import com.bookshare.dao.RespondRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Demand;
import com.bookshare.domain.Respond;
import com.bookshare.domain.User;

@Component
public class DemandDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private RespondRepository respondRepository;

    @Transactional
    @Scheduled(fixedRateString = "${bookshare.book.dispatch-interval:60}000")
    public void createRespondsForDemands() {
        logger.trace("createRespondsForDemands");
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

        List<Respond> responds = respondRepository.findByAgreed();
        logger.trace("================================");
        for (Respond r : responds) {
            logger.trace("ISBN:" + r.getDemand().getIsbn() + " " + r.getAgreed() + " " + r.getAgreementDate());
        }
        logger.trace("================================");
    }

}

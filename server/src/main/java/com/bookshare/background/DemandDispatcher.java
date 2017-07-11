package com.bookshare.background;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Scheduled(fixedRateString = "${bookshare.book.dispatch-interval:60}000")
    public void createRespondsForDemands() {
        logger.trace("createRespondsForDemands");
        List<Demand> demands = demandRepository.findByResponds_Id(null);
        for (Demand d : demands) {
            List<User> users = userRepository.findByBookList_Isbn13(d.getIsbn());
            int priority = 0;
            for (User userHasTheBook : users) {
                List<Respond> responds = userHasTheBook.getResponds();
                Respond res = new Respond();
                res.setDemand(d);
                res.setPriority(priority++);
                respondRepository.save(res);
                responds.add(res);
                userHasTheBook.setResponds(responds);
                userRepository.save(userHasTheBook);
            }
        }
    }

}

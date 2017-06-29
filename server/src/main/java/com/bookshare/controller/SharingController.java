package com.bookshare.controller;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.DemandRepository;
import com.bookshare.dao.RespondRepository;
import com.bookshare.dao.SessionRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Demand;
import com.bookshare.domain.Respond;
import com.bookshare.domain.Session;
import com.bookshare.domain.User;

@RestController
@RequestMapping(value = "sharing")
public class SharingController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private RespondRepository respondRepository;

    @RequestMapping(value = "demand/{isbn}", method = RequestMethod.POST)
    public void demand(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();

            // Create a demand for the user.
            List<Demand> demands = user.getDemands();
            Demand demand = new Demand();
            demand.setIsbn(isbn);
            demandRepository.save(demand);
            demands.add(demand);
            user.setDemands(demands);
            userRepository.save(user);

            // Find the user who has this book.
            List<User> users = userRepository.findBybookList_isbn13(isbn);
            for (User userHasTheBook : users) {
                List<Respond> responds = userHasTheBook.getResponds();
                Respond res = new Respond();
                res.setDemand(demand);
                respondRepository.save(res);
                responds.add(res);
                userHasTheBook.setResponds(responds);
                userRepository.save(userHasTheBook);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "getResponds", method = RequestMethod.GET, produces = "application/json")
    public String[] getResponds(@CookieValue("session") String sessionID, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();

            // Get all responds from the user.
            List<Respond> responds = user.getResponds();
            List<String> isbns = new ArrayList<String>();
            for (Respond respond : responds) {
                isbns.add(respond.getDemand().getIsbn());
            }
            return isbns.toArray(new String[isbns.size()]);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }

}

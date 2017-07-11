package com.bookshare.controller;

import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public void test(HttpServletResponse response) {
        Demand demand = new Demand();
        demand.setIsbn("9787500648192");
        demandRepository.save(demand);
        List<Demand> ds = demandRepository.findByResponds_Id(null);
        for (Demand d : ds) {
            System.out.println(d.getId() + ":" + d.getIsbn());
        }
    }

    @RequestMapping(value = "test1", method = RequestMethod.GET)
    public void test1(HttpServletResponse response) {
        // logger.debug("test1 - 1");
        // List<Demand> demands = demandRepository.findByResponds_Id(null);
        // for (Demand d : demands) {
        // logger.debug("test1 - 2");
        // List<User> users = userRepository.findByBookList_Isbn13(d.getIsbn());
        // int priority = 0;
        // for (User userHasTheBook : users) {
        // logger.debug("test1 - 3");
        // List<Respond> responds = userHasTheBook.getResponds();
        // Respond res = new Respond();
        // res.setDemand(d);
        // res.setPriority(priority++);
        // respondRepository.save(res);
        // responds.add(res);
        // userHasTheBook.setResponds(responds);
        // userRepository.save(userHasTheBook);
        // }
        // }
    }

    @RequestMapping(value = "demands/book/{isbn}", method = RequestMethod.POST)
    public void postDemand(@CookieValue("session") String sessionID, @PathVariable String isbn,
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
            createResponds(isbn, demand);

            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void createResponds(String isbn, Demand demand) {
        List<User> users = userRepository.findByBookList_Isbn13(isbn);
        int priority = 0;
        for (User userHasTheBook : users) {
            List<Respond> responds = userHasTheBook.getResponds();
            Respond res = new Respond();
            res.setDemand(demand);
            res.setPriority(priority++);
            respondRepository.save(res);
            responds.add(res);
            userHasTheBook.setResponds(responds);
            userRepository.save(userHasTheBook);
        }
    }

    @RequestMapping(value = "demands", method = RequestMethod.GET, produces = "application/json")
    public List<Demand> getDemands(@CookieValue("session") String sessionID, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            return user.getDemands();
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }

    @RequestMapping(value = "demands/{id}", method = RequestMethod.PUT)
    public void putDemand(@CookieValue("session") String sessionID, @PathVariable String id, @RequestBody Demand demand,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            Demand oldDemand = demandRepository.findById(Long.valueOf(id));
            if (oldDemand != null && demand.getCancalled() == true) {
                oldDemand.setCancalled(true);
                demandRepository.save(oldDemand);
                List<Respond> responds = respondRepository.findByDemand_Id(oldDemand.getId());
                for (Respond r : responds) {
                    r.setCancalled(true);
                    respondRepository.save(r);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "responds", method = RequestMethod.GET, produces = "application/json")
    public List<Respond> getResponds(@CookieValue("session") String sessionID, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            // Get the responds of the user.
            return user.getResponds();
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }

    @RequestMapping(value = "responds/{id}", method = RequestMethod.PUT)
    public void putResponds(@CookieValue("session") String sessionID, @PathVariable String id,
            @RequestBody Respond respond, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            Respond oldRespond = respondRepository.findById(Long.valueOf(id));
            if (oldRespond != null) {
                oldRespond.setAgreed(respond.getAgreed());
                respondRepository.save(oldRespond);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}

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

    @RequestMapping(value = "demands/book/{isbn}", method = RequestMethod.POST)
    public void postDemand(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();

            // Create a demand for the user.
            Demand demand = new Demand();
            demand.setIsbn(isbn);
            demand.setUser(user);
            demandRepository.save(demand);

            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public void test() {
        System.out.println("QQQQQ: ===================================================");
        List<Demand> ds = demandRepository.findByR();
        for (Demand d : ds) {
            System.out.println("QQQQQ: " + d.getIsbn() + " " + d.getUser().getUsername());
            List<Respond> rs = d.getResponds();
            for (Respond r : rs) {
                System.out.println("QQQQQ: " + r.getId() + " " + r.getAgreed());
            }
        }
        {
            System.out.println("QQQQQ: +++++++++++++++++++");
            List<Demand> ds1 = demandRepository.findByR1();
            for (Demand d : ds1) {
                System.out.println("QQQQQ: " + d.getIsbn() + " " + d.getUser().getUsername());
                List<Respond> rs = d.getResponds();
                for (Respond r : rs) {
                    System.out.println("QQQQQ: " + r.getId() + " " + r.getAgreed());
                }
            }
            System.out.println("QQQQQ: +++++++++++++++++++");
        }
        System.out.println("QQQQQ: ===================================================");
    }

}

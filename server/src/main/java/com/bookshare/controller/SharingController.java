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

import com.bookshare.dao.Demand1Repository;
import com.bookshare.dao.Respond1Repository;
import com.bookshare.dao.SessionRepository;
import com.bookshare.domain.Demand1;
import com.bookshare.domain.Respond1;
import com.bookshare.domain.Session;
import com.bookshare.domain.User;

@RestController
@RequestMapping(value = "sharing")
public class SharingController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private Demand1Repository demand1Repository;

    @Autowired
    private Respond1Repository respond1Repository;

    @RequestMapping(value = "demands/book/{isbn}", method = RequestMethod.POST)
    public void postDemand(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            Demand1 d = new Demand1(user, isbn);
            demand1Repository.save(d);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "demands", method = RequestMethod.GET, produces = "application/json")
    public List<Demand1> getDemands(@CookieValue("session") String sessionID, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            return user.getDemand1s();
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }

    @RequestMapping(value = "demands/{id}", method = RequestMethod.PUT)
    public void putDemand(@CookieValue("session") String sessionID, @PathVariable String id,
            @RequestBody Demand1 demand, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            Demand1 d = demand1Repository.findByIdAndUser_Id(Long.parseLong(id), user.getId());
            if (d != null) {
                d.setCanceled(demand.getCanceled());
                demand1Repository.save(d);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "responds", method = RequestMethod.GET, produces = "application/json")
    public List<Respond1> getResponds(@CookieValue("session") String sessionID, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            // Get the responds of the user.
            return respond1Repository.findByBookshelf_User_Id(user.getId());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }

}

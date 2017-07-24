package com.bookshare.controller;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.Demand1Repository;
import com.bookshare.dao.DemandRepository;
import com.bookshare.dao.RespondRepository;
import com.bookshare.dao.SessionRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Demand;
import com.bookshare.domain.Demand1;
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

    @Autowired
    private Demand1Repository demand1Repository;

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

    void test(String s, Iterable<Demand1> ds) {
        System.out.println("===" + s + "=================");
        for (Demand1 demand : ds) {
            System.out.println(demand.getCreateDate());
        }
        System.out.println("======================");
    }

    @Transactional
    @RequestMapping(value = "test", method = RequestMethod.GET)
    public void test() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp ts = new Timestamp(System.currentTimeMillis() - 180000);
        System.out.println("+++++++" + ts);
        Calendar c = Calendar.getInstance();
        {
            Demand1 d = new Demand1();
            d.setIsbn("123");
            d.setCreateDate(new Timestamp(System.currentTimeMillis()));
            demand1Repository.save(d);

            Demand1 d1 = new Demand1();
            d1.setIsbn("122");
            d1.setCreateDate(new Timestamp(System.currentTimeMillis() - 180000));
            demand1Repository.save(d1);

            Demand1 d2 = new Demand1();
            d2.setIsbn("121");
            d2.setCreateDate(new Timestamp(System.currentTimeMillis() - 179999));
            demand1Repository.save(d2);

            Demand1 d3 = new Demand1();
            d3.setIsbn("120");
            d3.setCreateDate(new Timestamp(System.currentTimeMillis() - 90000));
            demand1Repository.save(d3);
        }
        {
            test("all", demand1Repository.findAll());
        }

        Timestamp ddd = new Timestamp(System.currentTimeMillis() - 60000);
        System.out.println("+++++++" + ddd);
        test("Expire", demand1Repository.findByExpire(ddd));
        test("before", demand1Repository.findByCreateDateBefore(ddd));
        test("after", demand1Repository.findByCreateDateAfter(ddd));

        // {
        // Date ddd = new Date(c.getTimeInMillis() + 200000000);
        // Iterable<Demand1> ds = demand1Repository.findAll();
        // for (Demand1 d : ds) {
        // System.out.println("=" + d.getId());
        // demand1Repository.updateFor(d.getId());
        // }
        //
        // System.out.println("===Modify=============");
        // Iterable<Demand1> ds1 = demand1Repository.findAll();
        // for (Demand1 d : ds1) {
        // System.out.println(df.format(d.getCreateDate()));
        // }
        // System.out.println("======================");
        // }

        {
        }
    }

}

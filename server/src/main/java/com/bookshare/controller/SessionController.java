package com.bookshare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.SessionRepository;
import com.bookshare.domain.Session;
import com.bookshare.domain.User;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private SessionRepository sessionRepository;

    @Autowired
    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public Session login(@CookieValue("theme") String themeCookie, @RequestBody User user) {
        System.out.println(user);
        System.out.println("============" + themeCookie);
        Session s = Session.createNewSession();
        sessionRepository.save(s);
        return s;
    }

}

package com.bookshare.controller;

import java.lang.invoke.MethodHandles;
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

import com.bookshare.dao.SessionRepository;
import com.bookshare.dao.UserRepository;
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

    @RequestMapping(value = "{isbn}", method = RequestMethod.GET)
    public void test(@PathVariable String isbn, HttpServletResponse response) {
        List<User> users = userRepository.findBybookList_isbn13(isbn);

        for (User user : users) {
            System.out.println(user.getUsername());
        }
    }

    @RequestMapping(value = "{isbn}", method = RequestMethod.POST)
    public void claim(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}

package com.bookshare.controller;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.SessionRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Session;
import com.bookshare.domain.User;

@RestController
@RequestMapping(value = "sessions")
public class SessionController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public void login(@RequestBody User user, HttpServletResponse response) {
        // Search the user in repository
        User userInRepo = userRepository.findByUsername(user.getUsername());
        Session newSession = null;
        if (userInRepo != null && userInRepo.authenticate(user)) {
            // Invalidate old session.
            Session oldSession = userInRepo.getSession();
            userInRepo.setSession(null);
            userRepository.save(userInRepo);
            if (oldSession != null)
                sessionRepository.delete(oldSession);

            // Create new session.
            newSession = Session.createNewSession();
            logger.debug("Create new session:" + newSession.getSessionID());
            newSession.setUser(userInRepo);
            sessionRepository.save(newSession);
            userInRepo.setSession(newSession);
            userRepository.save(userInRepo);

            // Set cookie for HTTP.
            Cookie cookie = new Cookie("session", newSession.getSessionID());
            cookie.setPath("/bookshare");
            response.addCookie(cookie);
        } else
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @RequestMapping(value = "check", method = RequestMethod.GET)
    public void check(@CookieValue("session") String sessionID, HttpServletResponse response) {
        logger.debug("Check session:" + sessionID);
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public void logout(@CookieValue("session") String sessionID, HttpServletResponse response) {
        logger.debug("Delete session:" + sessionID);
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            user.setSession(null);
            userRepository.save(user);
            sessionRepository.delete(session);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}

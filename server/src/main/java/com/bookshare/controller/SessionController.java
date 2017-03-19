package com.bookshare.controller;

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

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    private SessionRepository sessionRepository;

    private UserRepository userRepository;

    @Autowired
    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json")
    public void login(@RequestBody User user, HttpServletResponse response) {
        // Search the user in repository
        User userInRepo = userRepository.findByUsername(user.getUsername());
        Session newSession = null;
        if (userInRepo != null && userInRepo.authenticate(user)) {
            // Invalidate old session.
            Session oldSession = userInRepo.getSession();
            if (oldSession != null)
                sessionRepository.delete(oldSession);

            // Create new session.
            newSession = Session.createNewSession();
            logger.debug("Create new session:" + newSession.getSessionID());
            userInRepo.setSession(newSession);
            newSession.setUser(userInRepo);
            sessionRepository.save(newSession);

            // Set cookie for HTTP.
            response.addCookie(new Cookie("session", newSession.getSessionID()));
        } else
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST, produces = "application/json")
    public void logout(@CookieValue("session") String sessionID) {
        logger.debug("Delete session:" + sessionID);
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            user.setSession(null);
            userRepository.save(user);
            sessionRepository.delete(session);
        }
    }

}

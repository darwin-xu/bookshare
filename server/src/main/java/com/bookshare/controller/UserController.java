package com.bookshare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.SessionRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Session;
import com.bookshare.domain.User;

/**
 * Created by kevinzhong on 09/12/2016.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;

    private SessionRepository sessionRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public boolean getVerifyCode(@RequestBody User user) {
        User u = userRepository.findByUsername(user.getUsername());
        if (u == null) {
            u = new User();
            u.setUsername(user.getUsername());
        }
        u.generateVerifyCode();
        // TODO: Send to SMS center.
        userRepository.save(u);
        return true;
    }

    @RequestMapping(method = RequestMethod.PATCH, produces = "application/json")
    public boolean modify(@CookieValue(value = "session", required = false) String sessionID, @RequestBody User user) {
        User authUser = getAuthUser(sessionID, user);
        if (authUser != null) {
            authUser.setPassword(user.getPassword());
            userRepository.save(authUser);
            return true;
        }
        return false;
    }

    @RequestMapping(value = "{username}", method = RequestMethod.GET, produces = "application/json")
    public User logout(@PathVariable String username) {
        return userRepository.findByUsername(username);
    }

    @RequestMapping(value = "{username}", method = RequestMethod.DELETE, produces = "application/json")
    public boolean unregister(@PathVariable String username) {
        return false;
    }

    private User getAuthUser(String sessionID, User user) {
        if (sessionID != null) {
            Session s = sessionRepository.findBySessionID(sessionID);
            if (s != null)
                return s.getUser();
        }
        User u = userRepository.findByUsername(user.getUsername());
        if (u != null) {
            if (u.verify(user)) {
                // Invalidate VerifyCode after use.
                u.setVerifyCode("");
                u.setVerifyCodeValidty(0);
                userRepository.save(u);
                return u;
            }
        }
        return null;
    }

}

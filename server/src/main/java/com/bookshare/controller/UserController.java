package com.bookshare.controller;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.dao.UserRepository;
import com.bookshare.domain.User;

/**
 * Created by kevinzhong on 09/12/2016.
 */
@RestController
@RequestMapping(value = "users")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "getVerifyCode", method = RequestMethod.POST)
    public void getVerifyCode(@RequestBody User user, HttpServletResponse response) {
        if (!StringUtils.isBlank(user.getUsername())) {
            User u = userRepository.findByUsername(user.getUsername());
            if (u == null) {
                u = new User();
                u.setUsername(user.getUsername());
                logger.debug("Create a new user:" + user.getUsername());
            } else {
                logger.debug("Find an existing user:" + user.getUsername());
            }
            u.generateVerifyCode();
            // TODO: Send to SMS center.
            userRepository.save(u);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.PATCH, produces = "application/json")
    public void changePassword(@RequestBody User user, HttpServletResponse response) {
        User authUser = getAuthUser(user);
        if (authUser != null) {
            authUser.setPassword(user.getPassword());
            userRepository.save(authUser);
        } else
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * Get a authorized user either from sessionID or from verifyCode.
     * 
     * @param sessionID
     *            If sessionID is not null, use sessionID to do authenticate.
     * @param user
     *            If sessionID is null, use user.verifyCode to do authenticate for just once.
     * @return A authorized user.
     */
    private User getAuthUser(User user) {
        User u = userRepository.findByUsername(user.getUsername());
        if (u != null) {
            if (u.verify(user)) {
                // Invalidate VerifyCode after use.
                u.setVerifyCode("");
                u.setVerifyCodeValidty(0);
                userRepository.save(u);
                return u;
            } else if (u.authenticate(user)) {
                return u;
            }
        }
        return null;
    }

}

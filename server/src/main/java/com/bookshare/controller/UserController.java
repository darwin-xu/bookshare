package com.bookshare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = { "username" }, method = RequestMethod.POST, produces = "application/json")
    public boolean register(@PathVariable("username") String username) {
        // Check the new username exist or not in database
        if (null == userRepository.findByUsername(username)) {
            // TODO: Generate a random number and sent to SMS center to verify
            // the client
            int randomNumber = 333;
            User u = new User();
            return (null != userRepository.save(u));
        } else {
            return false;
        }
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
        System.out.println("====" + u.getVerifyCode() + "====");
        userRepository.save(u);
        return true;
    }

    @RequestMapping(method = RequestMethod.PATCH, produces = "application/json")
    public boolean modify(@RequestBody User user) {
        User u = userRepository.findByUsername(user.getUsername());
        if (u != null) {
            if (u.verify(user)) {
                u.setPassword(user.getPassword());
                userRepository.save(u);
                return true;
            }
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

}

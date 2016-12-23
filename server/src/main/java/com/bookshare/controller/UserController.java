package com.bookshare.controller;

import com.bookshare.dao.UserRepository;
import com.bookshare.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

    @RequestMapping(value = {"username"}, method = RequestMethod.POST, produces="application/json")
    public boolean register(@PathVariable("username") String username) {
        // Check the new username exist or not in database
        if (null == userRepository.findBookByUsername(username)) {
            // TODO: Generate a random number and sent to SMS center to verify the client
            int randomNumber = 333;
            User u = new User();
            return (null != userRepository.save(u));
        } else {
            return false;
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces="application/json")
    public boolean login(@RequestBody User user) {
        return false;
    }

    @RequestMapping(value="{username}", method = RequestMethod.GET, produces="application/json")
    public boolean logout(@PathVariable String username) {
        return false;
    }

    @RequestMapping(method = RequestMethod.PUT, produces="application/json")
    public boolean modify(@RequestBody User user) {
        return false;
    }

    @RequestMapping(value="{username}", method = RequestMethod.DELETE, produces="application/json")
    public boolean unregister(@PathVariable String username) {
        return false;
    }

}

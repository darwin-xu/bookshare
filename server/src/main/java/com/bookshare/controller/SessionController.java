package com.bookshare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.bookshare.dao.SessionRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Session;
import com.bookshare.domain.User;

@RestController
@RequestMapping("/sessions")
public class SessionController {

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

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ModelAndView login(@RequestBody User user) {
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
            userInRepo.setSession(newSession);
            newSession.setUser(userInRepo);
            sessionRepository.save(newSession);
        }
        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
        mav.addObject(newSession);
        return mav;
    }

}

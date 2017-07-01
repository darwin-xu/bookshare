package com.bookshare.controller;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.backend.BookBackend;
import com.bookshare.dao.SessionRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Book;
import com.bookshare.domain.Session;
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

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    BookBackend bookBackend;

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

    @RequestMapping(value = "changePassword", method = RequestMethod.PATCH)
    public void changePassword(@RequestBody User user, HttpServletResponse response) {
        User authUser = getAuthUser(user);
        if (authUser != null) {
            authUser.setPassword(user.getPassword());
            userRepository.save(authUser);
        } else
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @RequestMapping(value = "bookshelf", method = RequestMethod.GET, produces = "application/json")
    public String[] getBookshelf(@CookieValue("session") String sessionID, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        String isbns[] = null;
        if (session != null) {
            User user = session.getUser();
            List<Book> books = user.getBookList();
            isbns = new String[books.size()];
            for (int i = 0; i < books.size(); ++i) {
                isbns[i] = books.get(i).getIsbn13();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return isbns;
    }

    @RequestMapping(value = "bookshelf/{isbn}", method = RequestMethod.POST)
    public void postBookshelf(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();

            // Get user original book list.
            List<Book> userBooks = user.getBookList();
            Set<Book> userOriginBooks = new HashSet<Book>(userBooks);

            Book book = bookBackend.getBook(isbn);
            if (book != null && userOriginBooks.add(book)) {
                // Save it to database.
                user.setBookList(new ArrayList<Book>(userOriginBooks));
                userRepository.save(user);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "bookshelf/{isbn}", method = RequestMethod.DELETE)
    public void deleteBookshelf(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();

            // Get user original book list.
            List<Book> userBooks = user.getBookList();
            Set<Book> userOriginBooks = new HashSet<Book>(userBooks);

            Book book = bookBackend.getBook(isbn);
            if (book != null && userOriginBooks.remove(book)) {
                // Save it to database.
                user.setBookList(new ArrayList<Book>(userOriginBooks));
                userRepository.save(user);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
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
                u.setVerifyCodeValidity(0);
                userRepository.save(u);
                return u;
            } else if (u.authenticate(user)) {
                return u;
            }
        }
        return null;
    }

}

package com.bookshare.controller;

import java.lang.invoke.MethodHandles;
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
import com.bookshare.dao.BookshelfRepository;
import com.bookshare.dao.SessionRepository;
import com.bookshare.dao.UserRepository;
import com.bookshare.domain.Book;
import com.bookshare.domain.Bookshelf;
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
    private BookBackend bookBackend;

    @Autowired
    private BookshelfRepository bookshelfRepository;

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

    @RequestMapping(value = "bookshelf/books", method = RequestMethod.GET, produces = "application/json")
    public String[] getBookshelf(@CookieValue("session") String sessionID, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        String isbns[] = null;
        if (session != null) {
            User user = session.getUser();
            List<Bookshelf> bookshelfs = user.getBookshelfs();
            isbns = new String[bookshelfs.size()];
            for (int i = 0; i < bookshelfs.size(); ++i) {
                isbns[i] = bookshelfs.get(i).getBook().getIsbn13();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return isbns;
    }

    @RequestMapping(value = "bookshelf/books/{isbn}", method = RequestMethod.POST)
    public void postBookshelf(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            List<Bookshelf> bookshelfs = user.getBookshelfs();
            Set<Bookshelf> originBookshelfs = new HashSet<Bookshelf>(bookshelfs);
            Book book = bookBackend.getBook(isbn);
            Bookshelf bookshelf;
            if (book != null && originBookshelfs.add(bookshelf = new Bookshelf(user, book))) {
                bookshelfRepository.save(bookshelf);
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "bookshelf/{id}", method = RequestMethod.GET)
    public void getBookshelf(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {

    }

    @RequestMapping(value = "bookshelf/books/{isbn}", method = RequestMethod.DELETE)
    public void deleteBookshelf(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            User user = session.getUser();
            Bookshelf bookshelf = bookshelfRepository.findByUser_IdAndBook_Isbn13(user.getId(), isbn);
            if (bookshelf != null) {
                bookshelfRepository.delete(bookshelf);
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

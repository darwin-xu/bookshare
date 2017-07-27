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
    public String[] getBookshelfBook(@CookieValue("session") String sessionID, HttpServletResponse response) {
        String isbns[] = null;
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            List<Bookshelf> bookshelfs = session.getUser().getBookshelfs();
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
    public void postBookshelfBook(@CookieValue("session") String sessionID, @PathVariable String isbn,
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

    @RequestMapping(value = "bookshelf/books/{isbn}", method = RequestMethod.DELETE)
    public void deleteBookshelfBook(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            Bookshelf bookshelf = bookshelfRepository.findByUser_IdAndBook_Isbn13(session.getUser().getId(), isbn);
            if (bookshelf != null) {
                bookshelfRepository.delete(bookshelf);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "bookshelf/search/{isbn}", method = RequestMethod.GET, produces = "application/json")
    public Bookshelf searchBookshelf(@CookieValue("session") String sessionID, @PathVariable String isbn,
            HttpServletResponse response) {
        Bookshelf bookshelf = null;
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            bookshelf = bookshelfRepository.findByUser_IdAndBook_Isbn13(session.getUser().getId(), isbn);
            if (bookshelf == null)
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return bookshelf;
    }

    @RequestMapping(value = "bookshelf", method = RequestMethod.GET, produces = "application/json")
    public List<Bookshelf> getBookshelf(@CookieValue("session") String sessionID, HttpServletResponse response) {
        List<Bookshelf> bookshelfs = null;
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            bookshelfs = session.getUser().getBookshelfs();
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return bookshelfs;
    }

    @RequestMapping(value = "bookshelf/{id}", method = RequestMethod.GET, produces = "application/json")
    public Bookshelf getBookshelf(@CookieValue("session") String sessionID, @PathVariable String id,
            HttpServletResponse response) {
        Bookshelf bookshelf = null;
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            bookshelf = bookshelfRepository.findById(Long.parseLong(id));
            if (bookshelf == null)
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return bookshelf;
    }

    @RequestMapping(value = "bookshelf/{id}", method = RequestMethod.PUT)
    public void putBookshelf(@CookieValue("session") String sessionID, @PathVariable String id,
            @RequestBody Bookshelf bookshelf, HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            Bookshelf b = bookshelfRepository.findById(Long.parseLong(id));
            if (b != null) {
                b.setAgreed(bookshelf.getAgreed());
                bookshelfRepository.save(b);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "bookshelf/{id}", method = RequestMethod.DELETE)
    public void deleteBookshelf(@CookieValue("session") String sessionID, @PathVariable String id,
            HttpServletResponse response) {
        Session session = sessionRepository.findBySessionID(sessionID);
        if (session != null) {
            Bookshelf b = bookshelfRepository.findById(Long.parseLong(id));
            if (b != null) {
                bookshelfRepository.delete(b);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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

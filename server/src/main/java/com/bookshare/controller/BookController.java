package com.bookshare.controller;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookshare.backend.BookBackend;
import com.bookshare.domain.Book;

/**
 * Created by kevinzhong on 09/12/2016.
 */

@RestController
@RequestMapping("books")
public class BookController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    BookBackend bookBackend;

    @RequestMapping(value = "{isbn13}", method = RequestMethod.GET, produces = "application/json")
    public Book getBook(@PathVariable String isbn13) {
        logger.debug("getBook: " + isbn13);
        return bookBackend.getBook(isbn13);
    }

}

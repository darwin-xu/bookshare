package com.bookshare.controller;

import com.bookshare.dao.BookRepository;
import com.bookshare.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


/**
 * Created by kevinzhong on 09/12/2016.
 */
@RestController
@RequestMapping("/books")
public class BookController {
    private BookRepository bookRepository;

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    @RequestMapping(value="{isbn13}", method = RequestMethod.GET, produces="application/json")
    public Book getBook(@PathVariable String isbn13) {
        // Step 1. Check system cache
        // Step 2. Check database
        // Step 3. Request to ISBN service
        //         - persist to system database
        return bookRepository.findBookByIsbn13(isbn13);
    }

    @RequestMapping(method = RequestMethod.GET, produces="application/json")
    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}

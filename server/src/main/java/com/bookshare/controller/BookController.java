package com.bookshare.controller;

import com.bookshare.dao.BookRepository;
import com.bookshare.domain.Book;
import com.bookshare.dto.BookDto;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


/**
 * Created by kevinzhong on 09/12/2016.
 */
@RestController
@RequestMapping("/books")
public class BookController {
    private BookRepository bookRepository;

    //private static final String ISBN_URL = "http://localhost:8080/bookshare/books/";
    private static final String ISBN_URL ="http://feedback.api.juhe.cn/ISBN?key=c00c86633d0b3a7d13a850cbe87d1a98&sub=";

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

        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Get isbn from request :[" + isbn13 +"]");
        System.out.println("----------------------------------------------------------------------------");
        Book book = bookRepository.findBookByIsbn13(isbn13);

        if (null == book) {
            // Get the book info from 3rd party service
            RestTemplate restTemplate = new RestTemplate();
            BookDto bookDto = restTemplate.getForObject(ISBN_URL + isbn13, BookDto.class);

            System.out.println("request URL : " + ISBN_URL + isbn13);

            System.out.println("---------------------------------");
            System.out.println(bookDto.toString());
            System.out.println("---------------------------------");
            book = bookDto.getResult();

            if (null != book) {
                System.out.println("Save book");
                // Get image_url and save to database;
                bookRepository.save(book);
            } else {
                // Throw out an exception
                // Set HttpStatus 404 NOT_FOUND
                System.out.println("This book doesn't exist!!!");
            }

            // persis
        }
        return book;
    }

    @RequestMapping(method = RequestMethod.GET, produces="application/json")
    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }


}

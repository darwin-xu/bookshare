package com.bookshare.controller;

import com.bookshare.dao.BookRepository;
import com.bookshare.domain.Book;
import com.bookshare.dto.BookDto;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;


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
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------------");
        Book book = bookRepository.findBookByIsbn13(isbn13);

        if (null == book) {
            // Get the book info from 3rd party service
            RestTemplate restTemplate = new RestTemplate();
            BookDto bookDto = restTemplate.getForObject(ISBN_URL + isbn13, BookDto.class);
            if (0 == bookDto.getErrorCode()) {

                System.out.println("request URL : " + ISBN_URL + isbn13);

                System.out.println("---------------------------------");
                System.out.println(bookDto.toString());
                System.out.println("---------------------------------");
                book = bookDto.getBook();

                if (null != book) {
                    System.out.println("Save book");
                    book = bookRepository.save(book);

                    // Get image_url and save to database;
                    if (null != book.getImageMedium()) {
                        downloadImage(book.getImageMedium(), "C://Kevin/", book.getIsbn13() + "_medium");
                    }
                    if (null != book.getImageLarge()) {
                        downloadImage(book.getImageLarge(), "C://Kevin/", book.getIsbn13() + "_large");
                    }
                } else {
                    // Throw out an exception
                    // Set HttpStatus 404 NOT_FOUND
                    System.out.println("This book doesn't exist!!!");
                }
            } else {
                System.out.println("Get book from 3rd Party Failure, error code : [" + bookDto.getErrorCode() + "]");
            }

        }
        // TODO: set HTTP status for client check
        return book;
    }

    @RequestMapping(method = RequestMethod.GET, produces="application/json")
    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }


    @RequestMapping(method = RequestMethod.POST, produces="application/json")
    public Book addBook(@RequestBody Book book) {
        downloadImage(book.getImageLarge(),"C://Kevin/" , book.getIsbn13() + "_large");
        downloadImage(book.getImageMedium(),"C://Kevin/" , book.getIsbn13() + "_medium");

        return bookRepository.save(book);
    }

    private boolean downloadImage(String sourceUrl, String targetDirectory, String isbn13)
    {
        boolean result;
        URL imageUrl;
        InputStream imageReader  = null;
        OutputStream imageWriter = null;
        try {
            imageUrl    = new URL(sourceUrl);
            imageReader = new BufferedInputStream(imageUrl.openStream());
            imageWriter = new BufferedOutputStream(
                    new FileOutputStream(targetDirectory + File.separator + isbn13 + ".jpg"));
            int readByte;

            while ((readByte = imageReader.read()) != -1) {
                imageWriter.write(readByte);
            }

            result = true;
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        } finally {
            try {
                if (null != imageReader) {
                    imageReader.close();
                }
                if (null != imageWriter) {
                    imageWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;

    }
}

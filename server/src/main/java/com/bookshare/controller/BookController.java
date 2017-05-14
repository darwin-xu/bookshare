package com.bookshare.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bookshare.BookshareApplication;
import com.bookshare.dao.BookRepository;
import com.bookshare.domain.Book;
import com.bookshare.dto.BookDto;
import com.bookshare.utility.RandomUtil;
import com.bookshare.utility.StringUtil;

/**
 * Created by kevinzhong on 09/12/2016.
 */
@RestController
@RequestMapping("/books")
public class BookController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Path coverImageRoot = Paths.get(BookshareApplication.prop.getProperty("bookshare.book.cover.path"));
    private BookRepository bookRepository;

    private static final String ISBN_URL = "http://feedback.api.juhe.cn/ISBN?key=c00c86633d0b3a7d13a850cbe87d1a98&sub=";

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @RequestMapping(value = "{isbn13}", method = RequestMethod.GET, produces = "application/json")
    public Book getBook(@PathVariable String isbn13) {
        // Step 1. Check system cache
        // Step 2. Check database
        // Step 3. Request to ISBN service
        // - persist to system database

        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Get isbn from request :[" + isbn13 + "]");
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
                    Path coverPath = RandomUtil.genRandomFilePath(coverImageRoot, book.getImageMedium());
                    downloadImage(book.getImageMedium(), coverPath);
                    book.setImageMedium(coverImageRoot.relativize(coverPath).toString());
                    book = bookRepository.save(book);

                    // Get image_url and save to database;
                    // if (null != book.getImageMedium()) {
                    // downloadImage(book.getImageMedium(),
                    // RandomFile.genFilePath(coverImagePath, "*.jpg"));
                    // }
                    // if (null != book.getImageLarge()) {
                    // downloadImage(book.getImageLarge(),
                    // RandomFile.genFilePath(coverImagePath, "*.jpg"));
                    // }
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

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // @RequestMapping(method = RequestMethod.POST, produces =
    // "application/json")
    // public Book addBook(@RequestBody Book book) {
    // downloadImage(book.getImageLarge(), "C://Kevin/", book.getIsbn13() +
    // "_large");
    // downloadImage(book.getImageMedium(), "C://Kevin/", book.getIsbn13() +
    // "_medium");
    //
    // return bookRepository.save(book);
    // }

    private boolean downloadImage(String sourceUrl, Path targetFileName) {
        boolean result;
        URL imageUrl;
        InputStream imageReader = null;
        OutputStream imageWriter = null;
        try {
            imageUrl = new URL(sourceUrl);
            imageReader = new BufferedInputStream(imageUrl.openStream());
            targetFileName.toFile().getParentFile().mkdirs();
            imageWriter = new BufferedOutputStream(new FileOutputStream(targetFileName.toFile()));
            int readByte;

            while ((readByte = imageReader.read()) != -1) {
                imageWriter.write(readByte);
            }

            result = true;
        } catch (IOException e) {
            result = false;
            logger.warn(StringUtil.toString(e));
        } finally {
            try {
                if (null != imageReader) {
                    imageReader.close();
                }
                if (null != imageWriter) {
                    imageWriter.close();
                }
            } catch (IOException e) {
                logger.warn(StringUtil.toString(e));
            }
        }

        return result;
    }
}

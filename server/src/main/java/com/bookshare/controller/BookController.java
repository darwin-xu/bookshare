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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bookshare.business.AuditManager;
import com.bookshare.dao.BookRepository;
import com.bookshare.domain.Book;
import com.bookshare.dto.JuheBookDto;
import com.bookshare.utility.RandomUtil;
import com.bookshare.utility.StringUtil;

/**
 * Created by kevinzhong on 09/12/2016.
 */
@ConfigurationProperties("bookshare.book")
@RestController
@RequestMapping("books")
public class BookController {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuditManager auditManager;

    private String rootCoverPath;

    public void setRootCoverPath(String rootCoverPath) {
        this.rootCoverPath = rootCoverPath;
    }

    private static final String ISBN_URL = "http://feedback.api.juhe.cn/ISBN?key=c00c86633d0b3a7d13a850cbe87d1a98&sub=";

    @RequestMapping(value = "{isbn13}", method = RequestMethod.GET, produces = "application/json")
    public Book getBook(@PathVariable String isbn13) {
        logger.debug("getBook: " + isbn13);
        // Step 1. Check system cache
        // Step 2. Check database
        // Step 3. Request to ISBN service
        // - persist to system database

        Book book = bookRepository.findBookByIsbn13(isbn13);

        if (null == book) {
            // Get the book info from 3rd party service
            RestTemplate restTemplate = new RestTemplate();
            JuheBookDto bookDto = restTemplate.getForObject(ISBN_URL + isbn13, JuheBookDto.class);
            logger.debug("request URL: " + ISBN_URL + isbn13);
            if (0 == bookDto.getErrorCode()) {
                auditManager.isbnQueryCountIncrease();
                book = bookDto.getBook();

                if (null != book) {
                    Path coverPath = RandomUtil.genRandomFilePath(Paths.get(rootCoverPath), book.getImageMedium());
                    downloadImage(book.getImageMedium(), coverPath);
                    book.setImageMedium(Paths.get(rootCoverPath).relativize(coverPath).toString());
                    book = bookRepository.save(book);

                } else {
                    logger.error("Book [" + isbn13 + "] returns nothing!");
                }
            } else {
                logger.error("Get book from ISBN library failed, error code [" + bookDto.getErrorCode() + "]");
            }
        }
        return book;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

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

package com.bookshare.backend;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bookshare.business.AuditManager;
import com.bookshare.dao.BookRepository;
import com.bookshare.domain.Book;
import com.bookshare.dto.JuheBookDto;
import com.bookshare.utility.RandomUtil;
import com.bookshare.utility.StringUtil;

@Service
public class BookBackend {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${bookshare.book.backend-url}")
    private String backendUrl;

    @Value("${bookshare.book.root-cover-path}")
    private String rootCoverPath;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuditManager auditManager;

    public Book getBook(String isbn13) {
        Book book = bookRepository.findBookByIsbn13(isbn13);

        if (null == book) {
            // Get the book info from 3rd party service
            RestTemplate restTemplate = new RestTemplate();
            JuheBookDto bookDto = restTemplate.getForObject(backendUrl + isbn13, JuheBookDto.class);
            logger.debug("request URL: " + backendUrl + isbn13);
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

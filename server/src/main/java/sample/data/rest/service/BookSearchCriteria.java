package sample.data.rest.service;

import org.springframework.data.repository.query.Param;
import sample.data.rest.domain.Book;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ezhonke on 12/6/2016.
 */
public class BookSearchCriteria implements Serializable{

    public List<String> getColumnList(@Param("pageName") String pageName) {
        return null;
    }

    public List<String> getBookList(@Param("columnName") String columnName) {
        return null;
    }

    public Book getBook(@Param("isbn10") String isbn10) {
        Book book = new Book();
        book.setIsbn10("1234567890");
        return book;
    }
}

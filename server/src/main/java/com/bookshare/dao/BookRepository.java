package com.bookshare.dao;

import com.bookshare.domain.Book;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.PagingAndSortingRepository;


/**
 * Created by kevinzhong on 09/12/2016.
 */
@RepositoryRestResource(collectionResourceRel = "bookrepo", path = "bookrepo")
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    Book findBookByIsbn13(@Param("isbn13") String isbn13);

    List<Book> findAll();

}

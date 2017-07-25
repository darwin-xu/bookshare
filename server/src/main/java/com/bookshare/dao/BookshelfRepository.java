package com.bookshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bookshare.domain.Bookshelf;

public interface BookshelfRepository extends PagingAndSortingRepository<Bookshelf, Long> {

    Bookshelf findByUser_IdAndBook_Isbn13(Long userId, String bookIsbn);

    @Query("select bs from Bookshelf bs inner join bs.book b where b.isbn13 = ?1 and bs.demand is null")
    List<Bookshelf> findAvailable(String bookIsbn);

}

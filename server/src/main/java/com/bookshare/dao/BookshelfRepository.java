package com.bookshare.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bookshare.domain.Bookshelf;

public interface BookshelfRepository extends PagingAndSortingRepository<Bookshelf, Long> {

    Bookshelf findById(Long Id);

    Bookshelf findByUser_IdAndBook_Isbn13(Long userId, String bookIsbn);

    List<Bookshelf> findByBook_Isbn13AndDemandIsNull(String bookIsbn);

}

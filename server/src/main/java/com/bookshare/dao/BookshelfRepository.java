package com.bookshare.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bookshare.domain.Bookshelf;

public interface BookshelfRepository extends PagingAndSortingRepository<Bookshelf, Long> {

    Bookshelf findByUser_IdAndBook_Isbn13(Long userId, String bookIsbn);

}

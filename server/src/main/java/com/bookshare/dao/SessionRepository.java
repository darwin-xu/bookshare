package com.bookshare.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bookshare.domain.Session;

public interface SessionRepository extends PagingAndSortingRepository<Session, Long> {

}

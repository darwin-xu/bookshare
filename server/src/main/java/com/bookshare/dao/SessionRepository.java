package com.bookshare.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bookshare.domain.Session;

public interface SessionRepository extends PagingAndSortingRepository<Session, Long> {

    Session findBySessionID(@Param("sessionID") String sessionID);

    Session findByUser_username(@Param("username") String username);

}

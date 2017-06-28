package com.bookshare.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bookshare.domain.User;

/**
 * Created by kevinzhong on 09/12/2016.
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(@Param("username") String username);

    List<User> findBybookList_isbn13(String isbn);

}

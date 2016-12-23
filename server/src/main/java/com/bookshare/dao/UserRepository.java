package com.bookshare.dao;

import com.bookshare.domain.Book;
import com.bookshare.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


/**
 * Created by kevinzhong on 09/12/2016.
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Book findBookByUsername(@Param("username") String username);


}

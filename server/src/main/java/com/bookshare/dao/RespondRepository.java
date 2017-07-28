package com.bookshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bookshare.domain.Respond;

public interface RespondRepository extends PagingAndSortingRepository<Respond, Long> {

    @Query("select r from Respond r inner join r.demand d inner join r.bookshelf b "
            + "where d.bookshelf is null and b.demand is null and r.demand.isbn = ?1 order by r.priority")
    List<Respond> findByBook(String isbn);

    List<Respond> findByBookshelf_User_Id(Long userId);
}

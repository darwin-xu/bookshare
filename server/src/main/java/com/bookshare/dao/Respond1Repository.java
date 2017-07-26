package com.bookshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bookshare.domain.Respond1;

public interface Respond1Repository extends PagingAndSortingRepository<Respond1, Long> {

    @Query("select r from Respond1 r inner join r.demand d inner join r.bookshelf b "
            + "where d.bookshelf is null and b.demand is null and r.demand.isbn = ?1 order by r.priority")
    List<Respond1> findByBook(String isbn);

    List<Respond1> findByBookshelf_User_Id(Long userId);
}

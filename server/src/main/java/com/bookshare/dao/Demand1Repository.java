package com.bookshare.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bookshare.domain.Demand;
import com.bookshare.domain.Demand1;

public interface Demand1Repository extends PagingAndSortingRepository<Demand1, Long> {

    Demand1 findById(Long id);

//    @Query("select d from Demand1 d where d.responds is null")
//    List<Demand1> findUnlinked();
    
    List<Demand1> findByResponds_Id(Long id);

    // @Query("select d from Demand1 d where DATE_ADD(d.createDate, INTERVAL ?1 MINUTE) < NOW()")
    // @Query("select d from Demand1 d where addminutes(d.createDate, ?1) < current_time()")
    @Query("select d from Demand1 d where d.createdOn < ?1")
    List<Demand1> findByExpire(Timestamp exp);

    List<Demand1> findByCreatedOnBefore(Timestamp exp);

    List<Demand1> findByCreatedOnAfter(Timestamp exp);

    @Modifying
    @Query("update Demand1 d set d.createdOn = current_time() where d.id = ?1")
    int updateFor(Long id);

    // FindBy
    // bookshelf is null

}

package com.bookshare.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bookshare.domain.Demand;

public interface DemandRepository extends PagingAndSortingRepository<Demand, Long> {

    Demand findById(Long id);

    Demand findByIdAndUser_Id(Long id, Long userId);

    // @Query("select d from Demand d where d.responds is null")
    // List<Demand1> findUnlinked();

    List<Demand> findByResponds_Id(Long id);

    // @Query("select d from Demand d where DATE_ADD(d.createDate, INTERVAL ?1 MINUTE) < NOW()")
    // @Query("select d from Demand d where addminutes(d.createDate, ?1) < current_time()")
    @Query("select d from Demand d where d.createdOn < ?1")
    List<Demand> findByExpire(Timestamp exp);

    List<Demand> findByCreatedOnBefore(Timestamp exp);

    List<Demand> findByCreatedOnAfter(Timestamp exp);

    @Modifying
    @Query("update Demand d set d.createdOn = current_time() where d.id = ?1")
    int updateFor(Long id);

    // FindBy
    // bookshelf is null

}

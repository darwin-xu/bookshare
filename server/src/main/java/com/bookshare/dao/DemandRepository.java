package com.bookshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bookshare.domain.Demand;

public interface DemandRepository extends PagingAndSortingRepository<Demand, Long> {

    Demand findById(@Param("id") Long id);

    List<Demand> findByResponds_Id(Long id);

    @Query("select d from Demand as d inner join d.responds as r where r.agreed = 'true'")
    List<Demand> findByR();

    @Query("select d from Demand as d inner join d.responds as r where not exists (from Respond as r where r.agreed = 'true')")
    //@Query("select d from Demand d where not exists (from d inner join d.responds r where r.agreed = 'true')")
    List<Demand> findByR1();
    
    @Modifying
    void invalidRespondsFor(Long id);

}

package com.bookshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bookshare.domain.Respond;

public interface RespondRepository extends PagingAndSortingRepository<Respond, Long> {

    Respond findById(@Param("id") Long id);

    List<Respond> findByDemand_Id(Long id);

    @Query("select r from Respond r where r.agreed = true order by r.demand.isbn, r.priority")
    List<Respond> findByAgreed();
    
    @Query("select r.demand.isbn from Respond r where r.agreed = true group by r.demand.isbn")
    List<String> findAllAgreedIsbns();

//    @Query("select r from Respond r where ")
//    List<Respond> findByD();

}

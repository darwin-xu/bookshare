package com.bookshare.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bookshare.domain.Demand;

public interface DemandRepository extends PagingAndSortingRepository<Demand, Long> {

    Demand findById(@Param("id") Long id);

}

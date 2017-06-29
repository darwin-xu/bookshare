package com.bookshare.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bookshare.domain.Demand;

public interface DemandRepository extends PagingAndSortingRepository<Demand, Long> {

}

package com.bookshare.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bookshare.domain.Respond;

public interface RespondRepository extends PagingAndSortingRepository<Respond, Long> {

    Respond findById(@Param("id") Long id);

    List<Respond> findByDemand_Id(Long id);

}

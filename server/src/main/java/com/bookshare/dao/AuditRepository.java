package com.bookshare.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.bookshare.domain.Audit;

@Service
public interface AuditRepository extends CrudRepository<Audit, Long> {

    Audit findByItem(@Param("item") String item);

}

package com.bookshare.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bookshare.domain.app.Section;

public interface SectionRepository extends PagingAndSortingRepository<Section, Long> {

    Section[] findBySectionName(@Param("sectionName") String sectionName);

}

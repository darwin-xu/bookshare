package com.bookshare.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bookshare.domain.app.Sheet;

public interface SheetRepository extends PagingAndSortingRepository<Sheet, Long> {

    Sheet[] findBySheetName(@Param("sheetName") String sheetName);

}

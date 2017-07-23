package com.bookshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
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

    @Query("select r from Respond r where r.agreed = true")
    List<String> findUnresovedBook();

    @Modifying
    // @Query("update User u set u.firstname = ?1 where u.lastname = ?2")
    // @Query("update Respond r set r.selected = 'true' where r.demand_id in (select d.id from demand d where d.id =
    // ?1)")
    @Query("update Respond r set r.selected = 'true' where r.demand in (select d from Demand d where d.isbn = ?1)")
    int selectRespondFor(String isbn);

    // Used to invalid other responds which associated to the same book for a user.
//    @Modifying
//    void invalidRespondsFor(String username, String isbn);

}

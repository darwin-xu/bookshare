package com.bookshare.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Bookshelf implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    User user;

    @ManyToOne
    Book book;

    @OneToMany(mappedBy = "bookshelf")
    List<Respond1> responds;

    Date importDate;

    @OneToOne
    Demand demand;

}

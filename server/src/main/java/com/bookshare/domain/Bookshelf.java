package com.bookshare.domain;

import java.io.Serializable;
import java.sql.Timestamp;
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
    private User user;

    @ManyToOne
    private Book book;

    @OneToMany(mappedBy = "bookshelf")
    private List<Respond1> responds;

    private Timestamp importedOn;

    @OneToOne
    private Demand demand;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<Respond1> getResponds() {
        return responds;
    }

    public void setResponds(List<Respond1> responds) {
        this.responds = responds;
    }

    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getImportedOn() {
        return importedOn;
    }

    public Bookshelf() {

    }

    public Bookshelf(User user, Book book) {
        this.user = user;
        this.book = book;
        this.importedOn = new Timestamp(System.currentTimeMillis());
    }

}

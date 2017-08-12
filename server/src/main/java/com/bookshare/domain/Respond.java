package com.bookshare.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Respond implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Demand demand;

    @ManyToOne
    private Bookshelf bookshelf;

    @Column(nullable = false)
    private int priority;

    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public Bookshelf getBookshelf() {
        return bookshelf;
    }

    public void setBookshelf(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Respond() {
    }

    public Respond(Demand demand, Bookshelf bookshelf, int priority) {
        this.demand = demand;
        this.bookshelf = bookshelf;
        this.priority = priority;
    }

    public static Respond breakRecursiveRef(Respond respond) {
        Bookshelf bookshelf = respond.getBookshelf();
        if (bookshelf != null) {
            bookshelf.setDemand(null);
            bookshelf.setResponds(null);
            bookshelf.setUser(null);
        }
        Demand demand = respond.getDemand();
        if (demand != null) {
            demand.setBookshelf(null);
            demand.setResponds(null);
            demand.setUser(null);
        }
        return respond;
    }

    public static List<Respond> breakRecursiveRef(List<Respond> responds) {
        for (Respond respond : responds) {
            breakRecursiveRef(respond);
        }
        return responds;
    }

}

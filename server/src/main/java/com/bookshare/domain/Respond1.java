package com.bookshare.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Respond1 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Demand1 demand;

    @JsonIgnore
    @ManyToOne
    private Bookshelf bookshelf;

    @Column
    private int priority;

    public Demand1 getDemand() {
        return demand;
    }

    public void setDemand(Demand1 demand) {
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

    public Respond1() {

    }

    public Respond1(Demand1 demand, Bookshelf bookshelf) {
        this.demand = demand;
        this.bookshelf = bookshelf;
    }

}

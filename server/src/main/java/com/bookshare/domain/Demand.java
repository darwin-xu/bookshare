package com.bookshare.domain;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Demand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @JsonIgnoreProperties
    private Long id;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private Boolean cancalled;

    @Column(nullable = false)
    private Date createDate;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Boolean getCancalled() {
        return cancalled;
    }

    public void setCancalled(Boolean cancalled) {
        this.cancalled = cancalled;
    }

    public Demand() {
        cancalled = false;
        createDate = new Date(new java.util.Date().getTime());
    }

}

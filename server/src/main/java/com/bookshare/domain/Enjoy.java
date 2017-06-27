package com.bookshare.domain;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Enjoy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @JsonIgnoreProperties
    private Long id;

    @ManyToOne
    private Claim claim;

    @Column(nullable = false)
    private Boolean agree;

    @Column(nullable = false)
    private Boolean selected;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private Date createDate;

}

package com.bookshare.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Section implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String columnName;
}

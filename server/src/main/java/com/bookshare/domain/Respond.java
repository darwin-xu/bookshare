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
public class Respond implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @JsonIgnoreProperties
    private Long id;

    @ManyToOne
    private Demand demand;

    @Column(nullable = false)
    private Boolean agree;

    @Column(nullable = false)
    private Boolean selected;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private Date createDate;

    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Respond() {
        agree = false;
        selected = false;
        priority = 0;
        createDate = new Date(new java.util.Date().getTime());
    }

}

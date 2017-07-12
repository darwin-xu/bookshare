package com.bookshare.domain;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Respond implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Demand demand;

    @JsonIgnore
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private Boolean agreed;

    @JsonIgnore
    @Column
    private Date agreementDate;

    @Column(nullable = false)
    private Boolean cancalled;

    @Column(nullable = false)
    private Boolean selected;

    @JsonIgnore
    @Column(nullable = false)
    private Integer priority;

    @Column
    private String deliveryId;

    @Column(nullable = false)
    private Date creationDate;

    public Long getId() {
        return id;
    }

    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getAgreed() {
        return agreed;
    }

    public void setAgreed(Boolean agreed) {
        this.agreed = agreed;
        agreementDate = new Date(new java.util.Date().getTime());
    }

    public Date getAgreementDate() {
        return agreementDate;
    }

    public Boolean getCancalled() {
        return cancalled;
    }

    public void setCancalled(Boolean cancalled) {
        this.cancalled = cancalled;
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

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Respond() {
        agreed = false;
        cancalled = false;
        selected = false;
        priority = 0;
        creationDate = new Date(new java.util.Date().getTime());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Respond other = (Respond) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}

package com.bookshare.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Demand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String isbn;

    @JsonIgnore
    @ManyToOne
    private User user;
    
    @JsonIgnore
    @OneToOne
    private Respond selectedRespond;

    @JsonIgnore
    @OneToMany(mappedBy = "demand")
    @Column
    private List<Respond> pendingResponds;

    @Column(nullable = false)
    private Boolean cancalled;

    @Column(nullable = false)
    private final Date creationDate;

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public List<Respond> getResponds() {
        return pendingResponds;
    }

    public void setResponds(List<Respond> responds) {
        this.pendingResponds = responds;
    }

    public Boolean getCancalled() {
        return cancalled;
    }

    public void setCancalled(Boolean cancalled) {
        this.cancalled = cancalled;
    }

    public Demand() {
        cancalled = false;
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
        Demand other = (Demand) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}

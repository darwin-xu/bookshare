package com.bookshare.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Demand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @Column
    private Timestamp createdOn;

    @Column(nullable = false)
    private String isbn;

    @OneToMany(mappedBy = "demand")
    private List<Respond> responds;

    @OneToOne
    private Bookshelf bookshelf;

    @Column(nullable = false)
    private Boolean canceled;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public List<Respond> getResponds() {
        return responds;
    }

    public void setResponds(List<Respond> responds) {
        this.responds = responds;
    }

    public Bookshelf getBookshelf() {
        return bookshelf;
    }

    public void setBookshelf(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
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

    public Demand() {
    }

    public Demand(User user, String isbn) {
        this.user = user;
        this.isbn = isbn;
        this.createdOn = new Timestamp(System.currentTimeMillis());
        this.canceled = false;
    }

    public static Demand breakRecursiveRef(Demand demand) {
        demand.getUser().setBookshelfs(null);
        demand.getUser().setDemands(null);

        List<Respond> responds = demand.getResponds();
        for (Respond r : responds) {
            r.setBookshelf(null);
            r.setDemand(null);
        }

        return demand;
    }

    public static List<Demand> breakRecursiveRef(List<Demand> demands) {
        for (Demand demand : demands) {
            breakRecursiveRef(demand);
        }
        return demands;
    }

}

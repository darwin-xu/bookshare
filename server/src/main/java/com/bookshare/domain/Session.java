package com.bookshare.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private final String sessionID;

    @OneToOne
    private User user;

    @Column(nullable = false)
    private final Date creationDate;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sessionID == null) ? 0 : sessionID.hashCode());
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
        Session other = (Session) obj;
        if (sessionID == null) {
            if (other.sessionID != null)
                return false;
        } else if (!sessionID.equals(other.sessionID))
            return false;
        return true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Session() {
        sessionID = UUID.randomUUID().toString();
        creationDate = new Date(new java.util.Date().getTime());
    }

    public String getSessionID() {
        return sessionID;
    }

    public Date getCreationDate() {
        return creationDate;
    }

}

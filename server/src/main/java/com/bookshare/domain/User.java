package com.bookshare.domain;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bookshare.utility.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by kevinzhong on 23/12/2016.
 */
@Entity
public class User implements Serializable {

    private final static long serialVersionUID = 1L;

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static long validityLimit = 60 * 1000;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String username;

    @Column
    private String oldPassword;

    @Column
    private String password;

    @Column
    private String verifyCode;

    @JsonIgnore
    @Column
    private long verifyCodeValidity;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    @Column
    private List<Bookshelf> bookshelfs;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    @Column
    private List<Demand> demands;

    @Column(nullable = false)
    private Integer sharingIndex = 0;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public void setVerifyCodeValidity(long verifyCodeValidity) {
        this.verifyCodeValidity = verifyCodeValidity;
    }

    public long getVerifyCodeValidity() {
        return verifyCodeValidity;
    }

    public List<Bookshelf> getBookshelfs() {
        return bookshelfs;
    }

    public void setBookshelfs(List<Bookshelf> bookshelfs) {
        this.bookshelfs = bookshelfs;
    }

    public List<Demand> getDemands() {
        return demands;
    }

    public void setDemands(List<Demand> demands) {
        this.demands = demands;
    }

    public Integer getSharingIndex() {
        return sharingIndex;
    }

    public void setSharingIndex(Integer sharingIndex) {
        this.sharingIndex = sharingIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + "]";
    }

    public void generateVerifyCode() {
        // TODO: Change to RandomUtil.genDigitals(6); in real system.
        // verifyCode = RandomUtil.genDigitals(6);
        verifyCode = "112233";
        verifyCodeValidity = System.currentTimeMillis();
        logger.debug(
                "Username:" + username + " verifyCode:" + verifyCode + " verifyCodeValidity:" + verifyCodeValidity);
    }

    public boolean verify(User user) {
        long currentTimeMillis = System.currentTimeMillis();
        logger.debug("(this.verifyCode:" + verifyCode + " == user.verifyCode:" + user.getVerifyCode()
                + ") && (currentTimeMillis:" + currentTimeMillis + " - verifyCodeValidity:" + verifyCodeValidity
                + " < validityLimit:" + validityLimit + ")");
        if (StringUtil.equalsWithoutNull(verifyCode, user.getVerifyCode())
                && (currentTimeMillis - verifyCodeValidity < validityLimit)) {
            logger.debug("true");
            return true;
        } else {
            logger.debug("false");
            return false;
        }
    }

    public boolean authenticate(User user) {
        logger.debug("(this.username:" + username + " == user.username:" + user.username + ") && (user.oldPassword:"
                + user.oldPassword + " == password:" + password + " || user.password:" + user.password + " == password:"
                + password + ")");
        if (StringUtil.equalsWithoutNull(username, user.username)
                && (StringUtil.equalsWithoutNull(user.oldPassword, password)
                        || StringUtil.equalsWithoutNull(user.password, password))) {
            logger.debug("true");
            return true;
        } else {
            logger.debug("false");
            return false;
        }
    }

}

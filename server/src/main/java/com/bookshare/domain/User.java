package com.bookshare.domain;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;

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

    private static final long serialVersionUID = 1L;

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final long validityLimit = 60 * 1000;

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String username;

    private String oldPassword;

    private String password;

    private String verifyCode;

    @JsonIgnore
    private long verifyCodeValidty;

    @JsonIgnore
    private Session session;

    @OneToMany
    private List<Book> bookList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public void setVerifyCodeValidty(long verifyCodeValidty) {
        this.verifyCodeValidty = verifyCodeValidty;
    }

    public long getVerifyCodeValidty() {
        return verifyCodeValidty;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
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
        if (bookList == null) {
            if (other.bookList != null)
                return false;
        } else if (!bookList.equals(other.bookList))
            return false;
        if (oldPassword == null) {
            if (other.oldPassword != null)
                return false;
        } else if (!oldPassword.equals(other.oldPassword))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (session == null) {
            if (other.session != null)
                return false;
        } else if (!session.equals(other.session))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (verifyCode == null) {
            if (other.verifyCode != null)
                return false;
        } else if (!verifyCode.equals(other.verifyCode))
            return false;
        if (verifyCodeValidty != other.verifyCodeValidty)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bookList == null) ? 0 : bookList.hashCode());
        result = prime * result + ((oldPassword == null) ? 0 : oldPassword.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((session == null) ? 0 : session.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((verifyCode == null) ? 0 : verifyCode.hashCode());
        result = prime * result + (int) (verifyCodeValidty ^ (verifyCodeValidty >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", oldPassword=" + oldPassword + ", password=" + password
                + ", verifyCode=" + verifyCode + ", verifyCodeValidty=" + verifyCodeValidty + ", session=" + session
                + ", bookList=" + bookList + "]";
    }

    public void generateVerifyCode() {
        // TODO: Change to RandomUtil.genDigitals(6); in real system.
        // verifyCode = RandomUtil.genDigitals(6);
        verifyCode = "112233";
        verifyCodeValidty = System.currentTimeMillis();
        logger.debug("Username:" + username + " verifyCode:" + verifyCode + " verifyCodeValidty:" + verifyCodeValidty);
    }

    public boolean verify(User user) {
        long currentTimeMillis = System.currentTimeMillis();
        logger.debug("(this.verifyCode:" + verifyCode + " == user.verifyCode:" + user.getVerifyCode()
                + ") && (currentTimeMillis:" + currentTimeMillis + " - verifyCodeValidty:" + verifyCodeValidty
                + " < validityLimit:" + validityLimit + ")");
        if (StringUtil.equalsWithoutNull(verifyCode, user.getVerifyCode())
                && (currentTimeMillis - verifyCodeValidty < validityLimit)) {
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

}

package com.bookshare.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * Created by kevinzhong on 23/12/2016.
 */
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final long validityLimit = 60 * 1000;

    @Id
    @GeneratedValue
    @JsonIgnore
    private String id;

    private String username;

    private String password;

    @JsonProperty(access = Access.WRITE_ONLY)
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((verifyCode == null) ? 0 : verifyCode.hashCode());
        result = prime * result + (int) (verifyCodeValidty ^ (verifyCodeValidty >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "User{" + "id='" + id + '\'' + ", username='" + username + '\'' + ", password='" + password + '\''
                + ", bookList=" + bookList + '}';
    }

    public void generateVerifyCode() {
        // TODO: Change to RandomUtil.genDigitals(6); in real system.
        // verifyCode = RandomUtil.genDigitals(6);
        verifyCode = "112233";
        verifyCodeValidty = System.currentTimeMillis();
    }

    public boolean verify(User user) {
        if (verifyCode.equals(user.getVerifyCode()) && (System.currentTimeMillis() - verifyCodeValidty < validityLimit))
            return true;
        else
            return false;
    }

    public boolean authenticate(User user) {
        if (username.equals(user.username) && password.equals(user.password))
            return true;
        else
            return false;
    }

}

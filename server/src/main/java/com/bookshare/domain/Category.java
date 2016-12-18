
package com.bookshare.domain;



import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;


    @ManyToMany
    List<Book> bookList;


    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    public Long getId() {

        return id;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBookList() {
        return bookList;
    }
}

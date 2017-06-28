package com.bookshare.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @JsonIgnoreProperties
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String subtitle;

    @Column(nullable = false)
    private String isbn10;

    @Column(nullable = false)
    private String isbn13;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int pages;

    @Column
    private String publisher;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false, length = 10000)
    private String summary;

    @Column
    private String pubDate;

    @Column
    @JsonProperty("origin_title")
    private String originTitle;

    @Column
    private String binding;

    @Column
    private String translator;

    @Column
    @JsonProperty("images_medium")
    private String imageMedium;

    @Column
    @JsonProperty("images_large")
    private String imageLarge;

    @Column
    private String levelNum;

    public Book() {
    }

    public Book(String isbn13) {
        this.isbn13 = isbn13;
    }

    public Book(Long id, String title, String subtitle, String isbn10, String isbn13, String author, int pages,
            String publisher, String price) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.author = author;
        this.pages = pages;
        this.publisher = publisher;
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Book other = (Book) obj;
        if (isbn13 == null) {
            if (other.isbn13 != null)
                return false;
        } else if (!isbn13.equals(other.isbn13))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isbn13 == null) ? 0 : isbn13.hashCode());
        return result;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getIsbn10() {
        return this.isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getAuthor() {
        return author;
    }

    public int getPages() {
        return pages;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPrice() {
        return price;
    }

    public String getSummary() {
        return summary;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public String getBinding() {
        return binding;
    }

    public String getTranslator() {
        return translator;
    }

    public String getImageMedium() {
        return imageMedium;
    }

    public String getImageLarge() {
        return imageLarge;
    }

    public String getLevelNum() {
        return levelNum;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public void setImageMedium(String imageMedium) {
        this.imageMedium = imageMedium;
    }

    public void setImageLarge(String imageLarge) {
        this.imageLarge = imageLarge;
    }

    public void setLevelNum(String levelNum) {
        this.levelNum = levelNum;
    }

    @Override
    public String toString() {
        return "Book{" + "id=" + id + ", title='" + title + '\'' + ", subtitle='" + subtitle + '\'' + ", isbn10='"
                + isbn10 + '\'' + ", isbn13='" + isbn13 + '\'' + ", author='" + author + '\'' + ", pages=" + pages
                + ", publisher='" + publisher + '\'' + ", price=" + price + ", summary='" + summary + '\''
                + ", pubDate='" + pubDate + '\'' + ", originTitle='" + originTitle + '\'' + ", binding='" + binding
                + '\'' + ", translator='" + translator + '\'' + ", imageMedium='" + imageMedium + '\''
                + ", imageLarge='" + imageLarge + '\'' + ", levelNum='" + levelNum + '\'' + '}';
    }
}

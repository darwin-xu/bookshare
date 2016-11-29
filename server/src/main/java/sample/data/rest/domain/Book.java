
package sample.data.rest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Book implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    private String subtitle;

    @Column(nullable = false)
    private String isbn10;

    @Column(nullable = false)
    private String isbn13;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int pages;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private String pubDate;

    private String originTitle;

    private String binding;

    private String translator;

    private String imageMedium;

    private String imageLarge;

    private String levelNum;

    public Book() {
    }

    public Book(Long id, String title, String subtitle, String isbn10, String isbn13, String author, int pages, String publisher, float price) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (pages != book.pages) return false;
        if (Float.compare(book.price, price) != 0) return false;
        if (id != null ? !id.equals(book.id) : book.id != null) return false;
        if (title != null ? !title.equals(book.title) : book.title != null) return false;
        if (subtitle != null ? !subtitle.equals(book.subtitle) : book.subtitle != null) return false;
        if (isbn10 != null ? !isbn10.equals(book.isbn10) : book.isbn10 != null) return false;
        if (isbn13 != null ? !isbn13.equals(book.isbn13) : book.isbn13 != null) return false;
        if (author != null ? !author.equals(book.author) : book.author != null) return false;
        if (publisher != null ? !publisher.equals(book.publisher) : book.publisher != null) return false;
        if (summary != null ? !summary.equals(book.summary) : book.summary != null) return false;
        if (pubDate != null ? !pubDate.equals(book.pubDate) : book.pubDate != null) return false;
        if (originTitle != null ? !originTitle.equals(book.originTitle) : book.originTitle != null) return false;
        if (binding != null ? !binding.equals(book.binding) : book.binding != null) return false;
        if (translator != null ? !translator.equals(book.translator) : book.translator != null) return false;
        if (imageMedium != null ? !imageMedium.equals(book.imageMedium) : book.imageMedium != null) return false;
        if (imageLarge != null ? !imageLarge.equals(book.imageLarge) : book.imageLarge != null) return false;
        return levelNum != null ? levelNum.equals(book.levelNum) : book.levelNum == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (subtitle != null ? subtitle.hashCode() : 0);
        result = 31 * result + (isbn10 != null ? isbn10.hashCode() : 0);
        result = 31 * result + (isbn13 != null ? isbn13.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + pages;
        result = 31 * result + (publisher != null ? publisher.hashCode() : 0);
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (pubDate != null ? pubDate.hashCode() : 0);
        result = 31 * result + (originTitle != null ? originTitle.hashCode() : 0);
        result = 31 * result + (binding != null ? binding.hashCode() : 0);
        result = 31 * result + (translator != null ? translator.hashCode() : 0);
        result = 31 * result + (imageMedium != null ? imageMedium.hashCode() : 0);
        result = 31 * result + (imageLarge != null ? imageLarge.hashCode() : 0);
        result = 31 * result + (levelNum != null ? levelNum.hashCode() : 0);
        return result;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getIsbn10() {
        return isbn10;
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

    public float getPrice() {
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



    public void setId(Long id) {
        this.id = id;
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

    public void setPrice(float price) {
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
}

package com.bookshare.dto;

import com.bookshare.domain.Book;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ezhonke on 12/19/2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class BookDto {
    private String reason;

    @XmlElement(name = "result")
    private Book book;

    @XmlElement(name = "error_code")
    private int errorCode;

    public String getReason() {
        return reason;
    }

    public Book getBook() {
        return book;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookDto bookDto = (BookDto) o;

        if (errorCode != bookDto.errorCode) return false;
        if (reason != null ? !reason.equals(bookDto.reason) : bookDto.reason != null) return false;
        return book != null ? book.equals(bookDto.book) : bookDto.book == null;
    }

    @Override
    public int hashCode() {
        int result = reason != null ? reason.hashCode() : 0;
        result = 31 * result + (book != null ? book.hashCode() : 0);
        result = 31 * result + errorCode;
        return result;
    }

    @Override
    public String toString() {
        return "BookDto{" +
                "reason='" + reason + '\'' +
                ", book=" + book +
                ", errorCode=" + errorCode +
                '}';
    }
}

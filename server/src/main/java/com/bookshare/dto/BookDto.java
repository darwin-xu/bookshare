package com.bookshare.dto;

import com.bookshare.domain.Book;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ezhonke on 12/19/2016.
 */
public class BookDto {
    private String reason;

    @JsonProperty("result")
    private Book book;

    @JsonProperty("error_code")
    private int errorCode;


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getErrorCode() {
        return errorCode;
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
        if (!reason.equals(bookDto.reason)) return false;
        return book.equals(bookDto.book);
    }

    @Override
    public int hashCode() {
        int result = reason.hashCode();
        result = 31 * result + book.hashCode();
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

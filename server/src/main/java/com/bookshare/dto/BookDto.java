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
    private Book result;

    @XmlElement(name = "error_code")
    private int errorCode;


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Book getResult() {
        return result;
    }

    public void setResult(Book result) {
        this.result = result;
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
        return result.equals(bookDto.result);
    }

    @Override
    public int hashCode() {
        int result1 = reason.hashCode();
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + errorCode;
        return result1;
    }

    @Override
    public String toString() {
        return "BookDto{" +
                "reason='" + reason + '\'' +
                ", result=" + result +
                ", errorCode=" + errorCode +
                '}';
    }
}

package com.bookshare.dto;

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
    private Result result;

    @XmlElement(name = "error_code")
    private int errorCode;


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
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
        if (reason != null ? !reason.equals(bookDto.reason) : bookDto.reason != null) return false;
        return result != null ? result.equals(bookDto.result) : bookDto.result == null;
    }

    @Override
    public int hashCode() {
        int result = reason != null ? reason.hashCode() : 0;
        result = 31 * result + (this.result != null ? this.result.hashCode() : 0);
        result = 31 * result + errorCode;
        return result;
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

package com.bookshare.domain.app;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Sheet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String sheetName;

    @Column
    private String sectionName;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sectionName == null) ? 0 : sectionName.hashCode());
        result = prime * result + ((sheetName == null) ? 0 : sheetName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sheet other = (Sheet) obj;
        if (sectionName == null) {
            if (other.sectionName != null)
                return false;
        } else if (!sectionName.equals(other.sectionName))
            return false;
        if (sheetName == null) {
            if (other.sheetName != null)
                return false;
        } else if (!sheetName.equals(other.sheetName))
            return false;
        return true;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

}

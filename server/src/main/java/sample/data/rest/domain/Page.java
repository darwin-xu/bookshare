package sample.data.rest.domain;

/**
 * Created by ezhonke on 12/6/2016.
 */

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Page implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String pageName;

    @OneToMany
    private List<ColumnItem> columnItemList;


    public Long getId() {

        return id;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public List<ColumnItem> getColumnItemList() {
        return columnItemList;
    }

    public void setColumnItemList(List<ColumnItem> columnItemList) {
        this.columnItemList = columnItemList;
    }

    public Page() {
    }

    public Page(Long id, String pageName) {
        this.id = id;
        this.pageName = pageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        if (!id.equals(page.id)) return false;
        if (!pageName.equals(page.pageName)) return false;
        return columnItemList.equals(page.columnItemList);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + pageName.hashCode();
        result = 31 * result + columnItemList.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", pageName='" + pageName + '\'' +
                ", columnItemList=" + columnItemList +
                '}';
    }
}

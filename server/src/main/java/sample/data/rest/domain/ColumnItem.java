package sample.data.rest.domain;

/**
 * Created by ezhonke on 12/6/2016.
 */

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ColumnItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String columnName;



    public Long getId() {

        return id;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {

        return columnName;
    }

}

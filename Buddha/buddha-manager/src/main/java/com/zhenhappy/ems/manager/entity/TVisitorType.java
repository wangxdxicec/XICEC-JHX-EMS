package com.zhenhappy.ems.manager.entity;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by wangxd on 2016-08-11.
 */
@Entity
@Table(name = "t_visitor_type", schema = "dbo")
public class TVisitorType {
    private Integer id;
    private String typename;
    private String typevalue;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "typename")
    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    @Basic
    @Column(name = "typevalue")
    public String getTypevalue() {
        return typevalue;
    }

    public void setTypevalue(String typevalue) {
        this.typevalue = typevalue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TVisitorType that = (TVisitorType) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (typename != null ? !typename.equals(that.typename) : that.typename != null) return false;
        if (typevalue != null ? !typevalue.equals(that.typevalue) : that.typevalue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (typename != null ? typename.hashCode() : 0);
        result = 31 * result + (typevalue != null ? typevalue.hashCode() : 0);
        return result;
    }
}

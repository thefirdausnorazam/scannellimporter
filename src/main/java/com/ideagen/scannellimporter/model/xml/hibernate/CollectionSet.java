/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.model.xml.hibernate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author firdaus.norazam
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionSet {
    
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "access")
    private String access;
    
    @XmlAttribute(name = "table")
    private String table;
    
    @XmlAttribute(name = "inverse")
    private Boolean inverse;
    
    @XmlAttribute(name = "order-by")
    private String orderBy;
    
    @XmlAttribute(name = "outer-join")
    private Boolean outerJoin;
    
    @XmlAttribute(name = "lazy")
    private Boolean lazy;
    
    @XmlAttribute(name = "cascade")
    private String cascade;
    
    @XmlAttribute(name = "batch-size")
    private int batchSize;
    
    @XmlElement(name = "key")
    private Key key;
    
    @XmlElement(name = "element")
    private Element element;
    
    @XmlElement(name = "one-to-many")
    private OneToMany oneToMany;
    
    @XmlElement(name = "many-to-one")
    private ManyToOne manyToOne;
    
    @XmlElement(name = "many-to-many")
    private ManyToMany manyToMany;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Boolean getInverse() {
        return inverse;
    }

    public void setInverse(Boolean inverse) {
        this.inverse = inverse;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Boolean getOuterJoin() {
        return outerJoin;
    }

    public void setOuterJoin(Boolean outerJoin) {
        this.outerJoin = outerJoin;
    }

    public Boolean getLazy() {
        return lazy;
    }

    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

    public String getCascade() {
        return cascade;
    }

    public void setCascade(String cascade) {
        this.cascade = cascade;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public OneToMany getOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(OneToMany oneToMany) {
        this.oneToMany = oneToMany;
    }

    public ManyToOne getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(ManyToOne manyToOne) {
        this.manyToOne = manyToOne;
    }

    public ManyToMany getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(ManyToMany manyToMany) {
        this.manyToMany = manyToMany;
    }
}

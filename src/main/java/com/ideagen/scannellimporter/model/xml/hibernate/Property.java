/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.model.xml.hibernate;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author firdaus.norazam
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {
    
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "length")
    private int length;
    
    @XmlAttribute(name = "access")
    private String access;
    
    @XmlAttribute(name = "type")
    private String type;
    
    @XmlAttribute(name = "column")
    private String column;
    
    @XmlAttribute(name = "update")
    private Boolean update;
    
    @XmlAttribute(name = "not-null")
    private Boolean notNull;
    
    @XmlAttribute(name = "insert")
    private Boolean insert;
    
    @XmlElement(name = "column")
    private Column columnProperties;
    
    @XmlElement(name = "one-to-many")
    private List<OneToMany> oneToMany;
    
    @XmlElement(name = "many-to-one")
    private List<ManyToOne> manyToOne;
    
    @XmlElement(name = "many-to-many")
    private List<ManyToMany> manyToMany;
    
    @XmlElement(name = "property")
    private List<Property> properties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public Boolean getInsert() {
        return insert;
    }

    public void setInsert(Boolean insert) {
        this.insert = insert;
    }

    public Column getColumnProperties() {
        return columnProperties;
    }

    public void setColumnProperties(Column columnProperties) {
        this.columnProperties = columnProperties;
    }

    public List<OneToMany> getOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(List<OneToMany> oneToMany) {
        this.oneToMany = oneToMany;
    }

    public List<ManyToOne> getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(List<ManyToOne> manyToOne) {
        this.manyToOne = manyToOne;
    }

    public List<ManyToMany> getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(List<ManyToMany> manyToMany) {
        this.manyToMany = manyToMany;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
    
}

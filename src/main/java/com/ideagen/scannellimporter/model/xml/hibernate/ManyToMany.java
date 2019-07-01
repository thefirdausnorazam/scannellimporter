/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.model.xml.hibernate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author firdaus.norazam
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ManyToMany {
    
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "class")
    private String className;
    
    @XmlAttribute(name = "access")
    private String access;
    
    @XmlAttribute(name = "column")
    private String column;
    
    @XmlAttribute(name = "lazy")
    private Boolean lazy;
    
    @XmlAttribute(name = "update")
    private Boolean update;
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

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

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Boolean getLazy() {
        return lazy;
    }

    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }
}

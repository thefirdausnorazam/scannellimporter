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
public class Column {
    
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "not-null")
    private Boolean notNull;
    
    @XmlAttribute(name = "default")
    private String defaultValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}

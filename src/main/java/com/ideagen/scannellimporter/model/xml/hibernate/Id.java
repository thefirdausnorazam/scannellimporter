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
public class Id {
    
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "unsaved-value")
    private String unsavedValue;
    
    @XmlElement(name = "generator")
    private IdGenerator idGenerator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnsavedValue() {
        return unsavedValue;
    }

    public void setUnsavedValue(String unsavedValue) {
        this.unsavedValue = unsavedValue;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}

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
public class Bag {
    
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "access")
    private String access;
    
    @XmlAttribute(name = "cascade")
    private String cascade;
    
    @XmlAttribute(name = "inverse")
    private Boolean inverse;
    
    @XmlElement(name = "key")
    private Key key;
    
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

    public String getCascade() {
        return cascade;
    }

    public void setCascade(String cascade) {
        this.cascade = cascade;
    }

    public Boolean getInverse() {
        return inverse;
    }

    public void setInverse(Boolean inverse) {
        this.inverse = inverse;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
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

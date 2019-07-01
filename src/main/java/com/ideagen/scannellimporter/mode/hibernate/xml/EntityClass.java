/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.mode.hibernate.xml;

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
public class EntityClass {
    
    @XmlAttribute(name = "name")
    private String name;
    
    @XmlAttribute(name = "table")
    private String table;
    
    @XmlAttribute(name = "discriminator-value")
    private String discriminatorValue;
    
    @XmlAttribute(name = "lazy")
    private Boolean lazy;
    
    @XmlElement(name = "id")
    private Id id;
    
    @XmlElement(name = "discriminator")
    private Discriminator discriminator;
    
    @XmlElement(name = "version")
    private Version version;
    
    @XmlElement(name = "bag")
    private Bag bag;
    
    @XmlElement(name = "property")
    private List<Property> properties;
    
    @XmlElement(name = "one-to-many")
    private List<OneToMany> oneToMany;
    
    @XmlElement(name = "many-to-one")
    private List<ManyToOne> manyToOne;
    
    @XmlElement(name = "many-to-many")
    private List<ManyToMany> manyToMany;
    
    @XmlElement(name = "set")
    private List<CollectionSet> sets;
    
    @XmlElement(name = "list")
    private List<CollectionList> lists;
    
    @XmlElement(name = "subclass")
    private List<EntityClass> subClasses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getDiscriminatorValue() {
        return discriminatorValue;
    }

    public void setDiscriminatorValue(String discriminatorValue) {
        this.discriminatorValue = discriminatorValue;
    }

    public Boolean getLazy() {
        return lazy;
    }

    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Discriminator getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(Discriminator discriminator) {
        this.discriminator = discriminator;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Bag getBag() {
        return bag;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
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

    public List<CollectionSet> getSets() {
        return sets;
    }

    public void setSets(List<CollectionSet> sets) {
        this.sets = sets;
    }

    public List<CollectionList> getLists() {
        return lists;
    }

    public void setLists(List<CollectionList> lists) {
        this.lists = lists;
    }

    public List<EntityClass> getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(List<EntityClass> subClasses) {
        this.subClasses = subClasses;
    }
}

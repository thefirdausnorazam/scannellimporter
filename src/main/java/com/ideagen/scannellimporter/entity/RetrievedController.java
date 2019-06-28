/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 *
 * @author firdaus.norazam
 */
@Entity
@Table(name = "retrieved_controller")
public class RetrievedController implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "class", nullable = true, length = 2000)
    private String className;

    @Column(name = "bean_id", nullable = true, length = 2000)
    private String beanId;

    @Column(name = "abstract", nullable = true)
    private Boolean abstractBean;

    @Column(name = "bean_name", nullable = true, length = 2000)
    private String beanName;

    @Column(name = "parent", nullable = true, length = 2000)
    private String parent;

    @Column(name = "origin_file", nullable = true, length = 2000)
    private String originFile;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "retrieved_controller_properties",
            joinColumns = @JoinColumn(name = "retrieved_controller_id"))
    private List<RetrievedControllerProperties> properties = new ArrayList();

    public String listProperties(){
        return properties.stream()
                .map(prop -> "Name : " + prop.getName() + ", Ref : " + prop.getRef() + ", Value : " + prop.getValue())
                .collect(Collectors.joining(", "));
    }
    
    public void addProperty(RetrievedControllerProperties property) {
        this.properties.add(property);
    }

    public void removeProperty(RetrievedControllerProperties property) {
        this.properties.remove(property);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public Boolean getAbstractBean() {
        return abstractBean;
    }

    public void setAbstractBean(Boolean abstractBean) {
        this.abstractBean = abstractBean;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<RetrievedControllerProperties> getProperties() {
        return properties;
    }

    public void setProperties(List<RetrievedControllerProperties> properties) {
        this.properties = properties;
    }

    public String getOriginFile() {
        return originFile;
    }

    public void setOriginFile(String originFile) {
        this.originFile = originFile;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.model.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.springframework.lang.Nullable;

/**
 *
 * @author firdaus.norazam
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Bean {

    @Nullable
    @XmlAttribute(name = "id")
    private String beanId;

    @Nullable
    @XmlAttribute(name = "class")
    private String className;

    @Nullable
    @XmlAttribute(name = "name")
    private String name;

    @Nullable
    @XmlAttribute(name = "parent")
    private String parent;

    @Nullable
    @XmlAttribute(name = "abstract")
    private Boolean abstractBean;
    
    @Nullable
    @XmlElement(name = "property")
    private List<BeanProperty> beanProperties;

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Boolean getAbstractBean() {
        return abstractBean;
    }

    public void setAbstractBean(Boolean abstractBean) {
        this.abstractBean = abstractBean;
    }

    public List<BeanProperty> getBeanProperties() {
        return beanProperties;
    }

    public void setBeanProperties(List<BeanProperty> beanProperties) {
        this.beanProperties = beanProperties;
    }
}

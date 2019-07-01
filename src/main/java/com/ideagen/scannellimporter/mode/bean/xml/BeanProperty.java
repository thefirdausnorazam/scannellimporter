/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.mode.bean.xml;

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
public class BeanProperty {
    
    @Nullable
    @XmlAttribute(name = "name")
    private String name;
    
    @Nullable
    @XmlAttribute(name = "ref")
    private String ref;
    
    @Nullable
    @XmlAttribute(name = "value")
    private String value;
    
    @Nullable
    @XmlElement(name = "bean")
    private List<Bean> bean;
    
    @Nullable
    @XmlElement(name = "props")
    private Props props;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Bean> getBean() {
        return bean;
    }

    public void setBean(List<Bean> bean) {
        this.bean = bean;
    }

    public Props getProps() {
        return props;
    }

    public void setProps(Props props) {
        this.props = props;
    }
}

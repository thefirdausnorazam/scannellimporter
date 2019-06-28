/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.model.xml;

import java.util.List;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author firdaus.norazam
 */
@XmlRootElement(name = "beans")
@XmlAccessorType(XmlAccessType.FIELD)
public class ControllerServletBean {
    
    @OneToMany
    @XmlElement(name = "bean")
    private List<Bean> bean;

    public List<Bean> getBean() {
        return bean;
    }

    public void setBean(List<Bean> bean) {
        this.bean = bean;
    }
}

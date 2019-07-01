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
public class ListIndex {
    
    @XmlAttribute(name = "column")
    private String column;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}

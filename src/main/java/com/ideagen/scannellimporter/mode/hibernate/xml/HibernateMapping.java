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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author firdaus.norazam
 */
@XmlRootElement(name = "hibernate-mapping")
@XmlAccessorType(XmlAccessType.FIELD)
public class HibernateMapping {
    
    @XmlAttribute(name = "package")
    private String packageName;
    
    @XmlElement(name = "typedef")
    private List<TypeDefinition> typeDefinitions;
    
    @XmlElement(name = "class")
    private EntityClass entityClass;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    public void setTypeDefinitions(List<TypeDefinition> typeDefinitions) {
        this.typeDefinitions = typeDefinitions;
    }

    public EntityClass getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(EntityClass entityClass) {
        this.entityClass = entityClass;
    }
}

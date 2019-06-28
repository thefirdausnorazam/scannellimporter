/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author firdaus.norazam
 */
@Embeddable
public class RetrievedControllerProperties {
    
    @Column(name = "name", nullable = true, length = 2000)
    private String name;
    
    @Column(name = "value", nullable = true, length = 2000)
    private String value;
    
    @Column(name = "ref", nullable = true, length = 2000)
    private String ref;
    
    @Column(name = "mapping_key", nullable = true, length = 2000)
    private String mappingKey;
    
    @Column(name = "mapping_value", nullable = true, length = 2000)
    private String mappingValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getMappingKey() {
        return mappingKey;
    }

    public void setMappingKey(String mappingKey) {
        this.mappingKey = mappingKey;
    }

    public String getMappingValue() {
        return mappingValue;
    }

    public void setMappingValue(String mappingValue) {
        this.mappingValue = mappingValue;
    }
}

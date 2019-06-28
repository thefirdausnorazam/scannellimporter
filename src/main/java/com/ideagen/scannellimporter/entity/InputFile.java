/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.entity;

import com.ideagen.scannellimporter.constant.EntityType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Id;

/**
 *
 * @author firdaus.norazam
 */
@Entity
@Table(name = "input_file")
public class InputFile implements Serializable {
    
    @Id
    @GeneratedValue
    private int id;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "package_name", length = 200)
    private String packageName;
    
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private EntityType type; 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }
}

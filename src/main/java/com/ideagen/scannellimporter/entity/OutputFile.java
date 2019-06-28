/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.entity;

import com.ideagen.scannellimporter.constant.EntityType;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author firdaus.norazam
 */
@Entity
@Table(name = "output_file")
public class OutputFile implements Serializable {
    
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
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<OutputFile> dependencies;

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

    public List<OutputFile> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<OutputFile> dependencies) {
        this.dependencies = dependencies;
    }
    
    
}

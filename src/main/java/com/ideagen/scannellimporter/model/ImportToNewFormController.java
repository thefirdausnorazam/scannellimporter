/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author firdaus.norazam
 */
public class ImportToNewFormController {
    
    private String childBeanSetterLine = "";
    
    private List<BeanToInject> beanToInjects;
    
    private List<String> importLists = new ArrayList();
    
    private List<String> methodLines = new ArrayList();
    
    public String getChildBeanSetterLine() {
        return childBeanSetterLine;
    }

    public void setChildBeanSetterLine(String childBeanSetterLine) {
        this.childBeanSetterLine = childBeanSetterLine;
    }

    public void addImportList(String importLine){
        importLists.add(importLine);
    }
    
    public List<BeanToInject> getBeanToInjects() {
        return beanToInjects;
    }

    public void setBeanToInjects(List<BeanToInject> beanToInjects) {
        this.beanToInjects = beanToInjects;
    }

    public List<String> getImportLists() {
        return importLists;
    }

    public void setImportLists(List<String> importLists) {
        this.importLists = importLists;
    }

    public List<String> getMethodLines() {
        return methodLines;
    }

    public void setMethodLines(List<String> methodLines) {
        this.methodLines = methodLines;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.model;

/**
 * Bean to be injected in a class, look like 
 * @Autowired 
 * @Qualifier({QualifierValue}} {className} {beanName}
 * @author firdaus.norazam
 */
public class BeanToInject {

    private String qualifierValue;

    private String className;

    private String beanName;

    private String setterMethod;
    
    private String importLine;
    
    private boolean childBean = false;
    
    public String getQualifierValue() {
        return qualifierValue;
    }

    public void setQualifierValue(String qualifierValue) {
        this.qualifierValue = qualifierValue;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getSetterMethod() {
        return setterMethod;
    }

    public void setSetterMethod(String setterMethod) {
        this.setterMethod = setterMethod;
    }

    public String getImportLine() {
        return importLine;
    }

    public void setImportLine(String importLine) {
        this.importLine = importLine;
    }

    public boolean isChildBean() {
        return childBean;
    }

    public void setChildBean(boolean childBean) {
        this.childBean = childBean;
    }
}

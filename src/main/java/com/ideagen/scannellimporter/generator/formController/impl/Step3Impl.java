/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.formController.impl;

import com.ideagen.scannellimporter.generator.formController.Step3;
import com.ideagen.scannellimporter.model.BeanToInject;
import com.ideagen.scannellimporter.model.ImportToNewFormController;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component
public class Step3Impl implements Step3 {
    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public List<String> getImportList(ImportToNewFormController getAndPostMethodImport) {
        List<String> importList = new ArrayList();
        importList.add("import org.springframework.stereotype.Controller;");//<- For @Controller annotation
        importList.add("import org.springframework.web.bind.annotation.RequestMapping;");//<- For @RequestMapping annotation
        importList.add("import org.springframework.web.bind.annotation.ModelAttribute;");//<- for @ModelAttribute annotation
        importList.add("import org.springframework.web.servlet.ModelAndView;");//<- For ModelAndView
        importList.add("import org.springframework.web.bind.annotation.RequestMethod;");//<- For RequestMethod (GET/POST)
        importList.add("import org.springframework.validation.BindException;");//<- For formView method
        importList.add("import org.springframework.beans.factory.annotation.Autowired;");//<- For autowired method
        importList.add("import org.springframework.beans.factory.annotation.Qualifier;");//<- For qualifier method
        importList.add("import javax.servlet.http.HttpServletRequest;");//<- For formView method
        importList.add("import javax.servlet.http.HttpServletResponse;");//<- For post method

        importList.addAll(getAndPostMethodImport.getImportLists());
        
        List<BeanToInject> beansToInject = getAndPostMethodImport.getBeanToInjects();

        //constructor imports
        if (beansToInject != null && !beansToInject.isEmpty()) {
            beansToInject.forEach(bean -> {
                importList.add(bean.getImportLine());
            });
        }

        //remove duplicate imports
        Set<String> importSet = new LinkedHashSet();
        importSet.addAll(importList);
        
        return importSet
                .stream()
                .collect(Collectors.toList());
    }
}

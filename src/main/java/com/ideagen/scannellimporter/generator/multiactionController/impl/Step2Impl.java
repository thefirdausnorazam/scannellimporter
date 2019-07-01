/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.multiactionController.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.generator.multiactionController.Step2;
import com.ideagen.scannellimporter.model.BeanToInject;
import com.ideagen.scannellimporter.util.service.ControllerGeneratorUtilityService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component("multiactionControllerGeneratorStep2")
public class Step2Impl implements Step2 {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ControllerGeneratorUtilityService controllerGeneratorUtilityService;

    @Override
    public List<String> addContstructorAndInjectBeans(RetrievedController retrievedController,
            File originalControllerFile,
            List<String> modifiedController) throws ServiceException {
        try {
            LOGGER.info("Adding constructor to {}", originalControllerFile.getName());

            String className = originalControllerFile.getName().replace(".java", "");

            List<BeanToInject> beansToInject = new ArrayList();

            modifiedController.stream()
                    .forEach(line -> {
                        boolean check = false;
                        final String[] lines = line.trim().split(" ");
                        //if length is 2 then annotation might have not been defined
                        //if length is 3 then we check
                        if (lines.length == 3) {
                            //if the first word is access modifier
                            if (lines[0].equals("private")
                                    || lines[0].equals("public")
                                    || lines[0].equals("protected")) {
                                check = true;
                            }
                        } else if (lines.length == 2) {
                            if (!lines[0].equals("return")) {
                                check = true;
                            }
                        }

                        //check if bean needs to be injected
                        if (check) {
                            final String lastWord = lines[lines.length - 1].replace(";", "");
                            retrievedController.getProperties().forEach(prop -> {
                                if (prop.getName().equals(lastWord)) {
                                    BeanToInject beanToInject = new BeanToInject();
                                    beanToInject.setBeanName(prop.getName());
                                    beanToInject.setQualifierValue(prop.getRef());
                                    //seconds last word should be the bean class
                                    //like; private Bean bean
                                    beanToInject.setClassName(lines[lines.length - 2]);
                                    //TODO: try to actually find the setter lol
                                    //for now we assume getter and setter is using standard sintax
                                    beanToInject.setSetterMethod("set" + StringUtils.capitalize(beanToInject.getBeanName()));
                                    //get import
                                    beanToInject.setImportLine(controllerGeneratorUtilityService
                                            .findImportLine(originalControllerFile, beanToInject.getClassName()));
                                    beansToInject.add(beanToInject);
                                }
                            });

                        }
                    });

            final StringJoiner constructorParameters = new StringJoiner(",\n");
            final StringJoiner setterLines = new StringJoiner("\n");

            beansToInject.stream()
                    .forEach(bean -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append("\t\t\t").append("@Qualifier");
                        sb.append("(");
                        sb.append("\"").append(bean.getQualifierValue()).append("\"");
                        sb.append(")");
                        sb.append(" ").append(bean.getClassName());
                        sb.append(" ").append(bean.getBeanName());

                        constructorParameters.add(sb.toString());

                        sb = new StringBuilder();
                        sb.append("\t\t");
                        sb.append("this.").append(bean.getBeanName()).append(" ");
                        sb.append("=").append(" ").append(bean.getBeanName()).append(";");

                        setterLines.add(sb.toString());
                    });

            List<String> constructorLines = new ArrayList();
            constructorLines.add("");
            constructorLines.add("\t@Autowired");
            constructorLines.add("\tpublic " + className + " (");
            constructorLines.add(constructorParameters.toString());
            constructorLines.add("\t\t\t) {");
            constructorLines.add(setterLines.toString());
            constructorLines.add("\t}");

            boolean classStarted = false;

            for (int i = 0; i < modifiedController.size(); i++) {
                String line = modifiedController.get(i);

                if (line.contains("public class")) {
                    classStarted = true;
                }

                if (classStarted) {
                    if (line.contains("{")) {
                        modifiedController.addAll(i + 1, constructorLines);
                        break;
                    }
                }
            }

            return modifiedController;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}

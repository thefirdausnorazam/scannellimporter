/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.formController.impl;

import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.entity.RetrievedControllerProperties;
import com.ideagen.scannellimporter.generator.formController.Step4;
import com.ideagen.scannellimporter.model.ImportToNewFormController;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component("formControllerGeneratorStep4")
public class Step4Impl implements Step4 {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<String> injectBeanInConstructor(ImportToNewFormController getAndPostMethodImport,
            RetrievedController retrievedController, String className) {
        final List<String> constructor = new ArrayList();
        if (!getAndPostMethodImport.getBeanToInjects().isEmpty()) {
            constructor.add("\t@Autowired");
            constructor.add("\tprivate " + className + " (");

            final StringJoiner sj = new StringJoiner(",\n");
            List<String> setters = new ArrayList();

            getAndPostMethodImport.getBeanToInjects().forEach(bean -> {
                StringBuilder sb = new StringBuilder("\t\t\t");
                sb.append("@Qualifier");
                sb.append("(").append("\"").append(bean.getQualifierValue()).append("\"").append(")");
                sb.append(" ").append(bean.getClassName()).append(" ").append(bean.getBeanName());

                sj.add(sb.toString());

                //if not child bean then add setter
                //from super class
                if (!bean.isChildBean()) {
                    sb = new StringBuilder("\t\t");
                    sb.append(bean.getSetterMethod());
                    sb.append("(").append(bean.getBeanName()).append(")").append(";");
                    setters.add(sb.toString());
                }
            });

            String formView = retrievedController
                    .getProperties()
                    .stream()
                    .filter(prop -> prop.getName().equals("formView"))
                    .findAny()
                    .map(RetrievedControllerProperties::getValue)
                    .orElse(null);

            String successView = retrievedController
                    .getProperties()
                    .stream()
                    .filter(prop -> prop.getName().equals("successView"))
                    .findAny()
                    .map(RetrievedControllerProperties::getValue)
                    .orElse(null);

            constructor.add(sj.toString());
            constructor.add("\t\t\t) {");
            constructor.add("\t\tsuper();");
            constructor.addAll(setters);
            constructor.add(getAndPostMethodImport.getChildBeanSetterLine());
            if (formView != null) {
                constructor.add("\t\tsetFormView(\"" + formView + "\");");
            }
            if (successView != null) {
                constructor.add("\t\tsetSuccessView(\"" + successView + "\");");
            }
            constructor.add("\t}");
        }
        return constructor;
    }
}

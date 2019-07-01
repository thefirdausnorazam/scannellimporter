/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.multiactionController.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.generator.multiactionController.Step1;
import com.ideagen.scannellimporter.util.service.ControllerGeneratorUtilityService;
import com.ideagen.scannellimporter.util.service.FileUtilityService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component("multiactionControllerGeneratorStep1")
public class Step1Impl implements Step1 {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ControllerGeneratorUtilityService controllerGeneratorUtilityService;

    @Autowired
    private FileUtilityService fileUtilityService;

    @Override
    public List<String> addControllerAnnotation(RetrievedController retrievedController,
            Path originalFilePath)
            throws ServiceException {
        try {

            LOGGER.info("Adding annotations to {}", originalFilePath.toString());

            List<String> fileLines = Files.readAllLines(originalFilePath);

            List<String> modifiedControllerLines = new ArrayList();

            //retrieve original xml file
            File originalXmlFile = new File(retrievedController.getOriginFile());
            StringBuilder sb = new StringBuilder();
            sb.append("@RequestMapping").append("(").append("value = ").append("\"").append("/");
            sb.append(originalXmlFile.getName().replace("-servlet.xml", ""));
            sb.append("\"").append(")");

            //add @Controller annotation
            //add @RequestMapping annotation
            for (String fileLine : fileLines) {

                if (fileLine.contains("public")
                        && fileLine.contains("class")
                        && fileLine.contains(controllerGeneratorUtilityService.getClassName(retrievedController))) {
                    modifiedControllerLines.add("import org.springframework.stereotype.Controller;");
                    modifiedControllerLines.add("import org.springframework.web.bind.annotation.RequestMapping;");
                    modifiedControllerLines.add("import org.springframework.beans.factory.annotation.Autowired;");
                    modifiedControllerLines.add("import org.springframework.beans.factory.annotation.Qualifier;");

                    modifiedControllerLines.add("");//<- pretty spacing

                    modifiedControllerLines.add("@Controller");//@Controller annotation
//                    modifiedControllerLines.add(sb.toString());//<- @RequestMapping annotation; TODO: check how servlet is handled
                }

                modifiedControllerLines.add(fileLine);
            }

            return modifiedControllerLines;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}

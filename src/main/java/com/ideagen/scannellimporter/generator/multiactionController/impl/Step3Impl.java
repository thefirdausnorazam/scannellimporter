/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.multiactionController.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.entity.RetrievedControllerProperties;
import com.ideagen.scannellimporter.generator.multiactionController.Step3;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component("multiactionControllerGeneratorStep3")
public class Step3Impl implements Step3 {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<String> addRequestMappingForEachMethod(RetrievedController retrievedController,
            List<String> controllerFileLines) throws ServiceException {
        try {
            LOGGER.info("Adding request mapping for each method [className : {}]", retrievedController.getClassName());

            List<String> modifiedController = new ArrayList();

            return addRequestMapping(retrievedController, controllerFileLines, modifiedController);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private List<String> addRequestMapping(RetrievedController retrievedController,
            List<String> controllerFileLines, List<String> modifiedController) {

        for (int i = 0; i < controllerFileLines.size(); i++) {
            String line = controllerFileLines.get(i);

            if (line.contains("public") && line.contains(line)) {
                try {
                    String methodNameLine = line.substring(0, line.indexOf("(")).trim();

//                    LOGGER.info("methodNameLine = {}", methodNameLine);
                    String[] methodNameLines = methodNameLine.split(" ");
                    String methodName = methodNameLines[methodNameLines.length - 1];

                    String mappingKey = retrievedController.getProperties()
                            .stream()
                            .filter(prop -> prop.getName().equals("methodNameResolver")
                            && prop.getMappingValue().equals(methodName))
                            .findAny()
                            .map(RetrievedControllerProperties::getMappingKey)
                            .orElse(null);

                    if (mappingKey != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("\t").append("@RequestMapping");
                        sb.append("(");
                        sb.append("\"").append(mappingKey).append("\"");
                        sb.append(")");

                        modifiedController.add(sb.toString());
                        modifiedController.add(line);

                        return addRequestMapping(retrievedController,
                                controllerFileLines.subList(i + 1, controllerFileLines.size()),
                                modifiedController);
                    }
                } catch (Exception e) {
//                    LOGGER.warn(e.getMessage());
                }
            }

            modifiedController.add(line);
        }

        return modifiedController;
    }
}

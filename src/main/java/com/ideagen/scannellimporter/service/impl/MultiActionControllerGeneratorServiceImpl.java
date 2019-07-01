/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.service.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.generator.multiactionController.Step1;
import com.ideagen.scannellimporter.generator.multiactionController.Step2;
import com.ideagen.scannellimporter.generator.multiactionController.Step3;
import com.ideagen.scannellimporter.model.ImportCommand;
import com.ideagen.scannellimporter.service.MultiActionControllerGeneratorService;
import com.ideagen.scannellimporter.util.service.ControllerGeneratorUtilityService;
import com.ideagen.scannellimporter.util.service.FileUtilityService;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component
public class MultiActionControllerGeneratorServiceImpl implements MultiActionControllerGeneratorService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("multiactionControllerGeneratorStep1")
    private Step1 step1;
    
    @Autowired
    @Qualifier("multiactionControllerGeneratorStep2")
    private Step2 step2;
    
    @Autowired
    @Qualifier("multiactionControllerGeneratorStep3")
    private Step3 step3;

    @Autowired
    private ControllerGeneratorUtilityService controllerGeneratorUtilityService;

    @Autowired
    private FileUtilityService fileUtilityService;

    @Override
    public void generate(RetrievedController retrievedController, ImportCommand importCommand)
            throws ServiceException {
        try {
            Path originalFilePath
                    = fileUtilityService.findPathFromClass(retrievedController, importCommand.getInputPath());

            if (originalFilePath == null) {
                throw new ServiceException(String.format("Failed to find original controller file from class "
                        + "[inputPath : %s] [class : %s]", importCommand.getInputPath(), retrievedController.getClassName()));
            }

            Path outputPath
                    = Paths.get(importCommand.getOutputPath()
                            + "/"
                            + retrievedController.getClassName().replaceAll("\\.", "/")
                            + ".java");

            //STEP 1
            //add @Controller annotation and @RequestMapping annotation
            List<String> controllerFileLines = step1.addControllerAnnotation(retrievedController, originalFilePath);

            //STEP 2 
            //add Constructor
            controllerFileLines = step2.addContstructorAndInjectBeans(
                    retrievedController, originalFilePath.toFile(), controllerFileLines);
            
            //STEP 3
            //add @RequestMapping method at each method
            controllerFileLines = step3.addRequestMappingForEachMethod(retrievedController, controllerFileLines);
            
            LOGGER.info("Writing modified controller class to {}", outputPath);
            File outputFile = outputPath.toFile();

            //create directory
            outputFile.getParentFile().mkdirs();

            Files.write(outputPath, controllerFileLines, Charset.defaultCharset());
            
            LOGGER.info("End...");
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}

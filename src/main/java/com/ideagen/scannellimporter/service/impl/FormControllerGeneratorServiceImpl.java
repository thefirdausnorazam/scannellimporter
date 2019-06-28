/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.service.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.generator.formController.Step1;
import com.ideagen.scannellimporter.generator.formController.Step2;
import com.ideagen.scannellimporter.generator.formController.Step3;
import com.ideagen.scannellimporter.generator.formController.Step4;
import com.ideagen.scannellimporter.model.ImportToNewFormController;
import com.ideagen.scannellimporter.util.service.ControllerGeneratorUtilityService;
import com.ideagen.scannellimporter.util.service.FileUtilityService;
import com.ideagen.scannellimporter.service.FormControllerGeneratorService;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component
public class FormControllerGeneratorServiceImpl implements FormControllerGeneratorService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileUtilityService fileUtilityService;

    @Autowired
    private ControllerGeneratorUtilityService controllerGeneratorUtilityService;

    @Autowired
    private Step1 step1;

    @Autowired
    private Step2 step2;

    @Autowired
    private Step3 step3;

    @Autowired
    private Step4 step4;

    @Override
    public void generate(RetrievedController retrievedController, String inputPath, String outputPath)
            throws ServiceException {
        try {
            String originalClassName = controllerGeneratorUtilityService.getClassName(retrievedController);
            String newClassName = originalClassName + "Impl";
            String originalController = originalClassName + ".java";

            LOGGER.info("Creating form controller : {}", newClassName);

            LOGGER.info("Looking for original controller file : {}", originalController);
            File originalControllerFile = fileUtilityService.searchForFile(inputPath, originalController);

            LOGGER.info("Writing modified old controller");

            final String outputFolderPathString = retrievedController.getClassName()
                    .replaceAll("\\.", "/")
                    .replace(originalClassName, "");

            StringBuilder oldFilePath = new StringBuilder(outputPath);
            oldFilePath.append("/");
            oldFilePath.append(outputFolderPathString.substring(0, outputFolderPathString.length() - 1));

            //create directory
            LOGGER.info("Creating directories " + oldFilePath.toString());
            new File(oldFilePath.toString()).mkdirs();
            oldFilePath.append("/").append(originalClassName);
            Path modifedOldClassFilePath = Paths.get(oldFilePath.toString() + ".java");

            //Step 1
            //create modified original controller
            //and get back any bean in the parent controller that was not overridden in original controller
            ImportToNewFormController importFromOriginalController
                    = step1.modifyAndCopyOriginalControllerAndReturnBean(
                            retrievedController,
                            originalControllerFile,
                            modifedOldClassFilePath,
                            inputPath);

            LOGGER.info("Reading content from : " + originalControllerFile.getAbsolutePath());
            List<String> fileContent = Files.lines(originalControllerFile.toPath())
                    .collect(Collectors.toList());

            /**
             * Get request mapping mapping from original xml file name
             */
            File originalXmlFile = new File(retrievedController.getOriginFile());
            StringBuilder sb = new StringBuilder();
            sb.append("@RequestMapping").append("(").append("value = ").append("\"").append("/");
            sb.append(originalXmlFile.getName().replace("-servlet.xml", ""));
            sb.append("\"").append(")");

            LOGGER.info("Building new controller class");

            //add original package name and imports
            List<String> newFileContent = new ArrayList();
            newFileContent.add(controllerGeneratorUtilityService.getPackageForClass(fileContent));

            //Step 2 get Get and Post method handling
            //build get and post methods and get back stuff
            ImportToNewFormController importFromGetAndPostMethod = step2.getGetAndPostMethod(
                    retrievedController, originalControllerFile);
            
            //combine all imports
            importFromGetAndPostMethod.setBeanToInjects(importFromOriginalController.getBeanToInjects());
            importFromGetAndPostMethod.setChildBeanSetterLine(importFromOriginalController.getChildBeanSetterLine());
            
            //Step 3 get all imports
            //add filtered imports
            newFileContent.addAll(step3.getImportList(importFromGetAndPostMethod));

            newFileContent.add(" ");//<- Space to make things look nice
            newFileContent.add("@Controller");
//            newFileContent.add(sb.toString());//<- Controller request mapping; TODO: check how servlet is handled

            //class starts here
            newFileContent.add("public class " + newClassName + " extends " + originalClassName + " {");
            newFileContent.add(" ");//<- Space to make things look nice

            //Step 4 inject bean at constructor
            //bean injections in constructor  
            newFileContent.addAll(step4.injectBeanInConstructor(importFromGetAndPostMethod, 
                    retrievedController, newClassName));
            //insert form view method
            newFileContent.addAll(importFromGetAndPostMethod.getMethodLines());
            //class ends here
            newFileContent.add("}");

            //build output path for new controller
            sb = new StringBuilder(outputPath);
            sb.append("/");
            sb.append(outputFolderPathString.substring(0, outputFolderPathString.length() - 1));

            //create directory
            LOGGER.info("Creating directories " + sb.toString());
            new File(sb.toString()).mkdirs();

            sb.append("/").append(originalClassName);
            Path newClassFilePath = Paths.get(sb.toString() + "Impl.java");

            LOGGER.info("Writing new controller class");
            Files.write(newClassFilePath, newFileContent, Charset.defaultCharset());

            LOGGER.info("End...");
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}

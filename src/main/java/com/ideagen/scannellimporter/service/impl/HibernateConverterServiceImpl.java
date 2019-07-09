/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.service.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.generator.hibernateEntity.Step1;
import com.ideagen.scannellimporter.model.ImportCommand;
import com.ideagen.scannellimporter.model.xml.hibernate.HibernateMapping;
import com.ideagen.scannellimporter.service.HibernateConverterService;
import com.ideagen.scannellimporter.util.service.FileUtilityService;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

/**
 * @author firdaus.norazam
 */
@Component
public class HibernateConverterServiceImpl implements HibernateConverterService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileUtilityService fileUtilityService;

    @Autowired
    private Jaxb2Marshaller marshaller;

    @Autowired
    @Qualifier("hibernateGeneratorStep1")
    private Step1 step1;

    @Override
    public void convert(ImportCommand importCommand) throws ServiceException {
        try {
            File originalXmlHibernateFile = fileUtilityService.searchForFile(importCommand);

            if (originalXmlHibernateFile == null) {
                LOGGER.warn("Failed to find original xml file [inputPath = {}] [fileName = {}]",
                        importCommand.getInputPath(), importCommand.getFileName());
                return;
            }

            HibernateMapping hibernateMapping
                    = (HibernateMapping) marshaller.unmarshal(new StreamSource(originalXmlHibernateFile));

            Path classPath = fileUtilityService.findPathFromClass(hibernateMapping, importCommand.getInputPath());

            if (classPath == null) {
                throw new ServiceException(String.format("Failed to find original entity file " +
                        "[inputPath = %s]", importCommand.getInputPath()));
            }

            LOGGER.info("Adding @Entity and @Table annotation to entity...");
            List<String> entityClassLines = step1.addEntityAnnotation(classPath, hibernateMapping);

            //Build path to generate file
            Path outputPath = getOutputPath(hibernateMapping, importCommand);
            outputPath.toFile().getParentFile().mkdirs();
            LOGGER.info("Writing modified entity class class to {}", outputPath.toString());
            Files.write(outputPath, entityClassLines, Charset.defaultCharset());

            LOGGER.info("End");
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private Path getOutputPath(HibernateMapping hibernateMapping, ImportCommand importCommand){
        String pathString = hibernateMapping.getPackageName() + "." +hibernateMapping.getEntityClass().getName();

        pathString = importCommand.getOutputPath()
                + "/"
                +  pathString.replaceAll("\\.", "/") + ".java";

        return Paths.get(pathString);
    }
}

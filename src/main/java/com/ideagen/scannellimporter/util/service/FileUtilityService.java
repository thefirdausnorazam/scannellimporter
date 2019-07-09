/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.util.service;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.model.ImportCommand;
import com.ideagen.scannellimporter.model.xml.hibernate.HibernateMapping;
import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author firdaus.norazam
 */
public interface FileUtilityService {

    File searchForFile(String inputPath, String fileName) throws ServiceException;
    
    File searchForFile(ImportCommand importCommand)throws ServiceException;

    Path findPathFromImportLine(String importLine) throws ServiceException;

    File findFileFromImportLine(String inputLine, String importLine) throws ServiceException;
    
    Path findPathFromClass(RetrievedController retrievedController, String inputPath) throws ServiceException;
    
    Path findPathFromClass(HibernateMapping hibernateMapping, String inputPath) throws ServiceException;
}

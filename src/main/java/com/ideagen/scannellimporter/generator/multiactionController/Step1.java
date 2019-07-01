/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.multiactionController;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.model.ImportCommand;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author firdaus.norazam
 */
public interface Step1 {

    List<String> addControllerAnnotation(RetrievedController retrievedController, Path originalFilePath) throws ServiceException;
}

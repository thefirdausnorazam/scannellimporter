/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.formController;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.model.ImportToNewFormController;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author firdaus.norazam
 */
public interface Step1 {

    ImportToNewFormController modifyAndCopyOriginalControllerAndReturnBean(RetrievedController retrievedController,
            File originalFile, Path outputPath, String inputPath) throws IOException, ServiceException;
}

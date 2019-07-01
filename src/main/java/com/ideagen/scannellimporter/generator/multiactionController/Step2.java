/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.multiactionController;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import java.io.File;
import java.util.List;

/**
 *
 * @author firdaus.norazam
 */
public interface Step2 {
    
    List<String> addContstructorAndInjectBeans(RetrievedController retrievedController,
            File originalControllerFile,
            List<String> modifiedController) throws ServiceException;
}

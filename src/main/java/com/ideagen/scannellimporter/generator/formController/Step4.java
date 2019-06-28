/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.formController;

import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.model.ImportToNewFormController;
import java.util.List;

/**
 *
 * @author firdaus.norazam
 */
public interface Step4 {
    
    List<String> injectBeanInConstructor(ImportToNewFormController getAndPostMethodImport, 
            RetrievedController retrievedController,String className);
}

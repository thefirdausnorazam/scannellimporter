/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.multiactionController;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import java.util.List;

/**
 *
 * @author firdaus.norazam
 */
public interface Step3 {
    
    List<String> addRequestMappingForEachMethod(RetrievedController retrievedController,
            List<String> controllerFileLines) throws ServiceException;
}

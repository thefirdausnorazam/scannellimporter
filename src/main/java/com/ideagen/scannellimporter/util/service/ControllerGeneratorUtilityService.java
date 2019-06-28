/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.util.service;

import com.ideagen.scannellimporter.constant.FormPostMethodHandler;
import com.ideagen.scannellimporter.entity.RetrievedController;
import java.io.File;
import java.util.List;

/**
 *
 * @author firdaus.norazam
 */
public interface ControllerGeneratorUtilityService {

    List<String> injectBean(RetrievedController retrievedController, String inputPath);

    List<String> getPackageAndImportLists(List<String> fileContent);

    List<String> getImportLists(List<String> fileContent);

    String getPackageName(RetrievedController retrievedController);

    String getClassName(RetrievedController retrievedController);

    String getPackageForClass(List<String> fileContent);

    String findImportLine(File file, String className);

    String getClassInMethod(String line, String objectParameterName);
    
    FormPostMethodHandler identifyPostMethodHandler(List<String> originalController);
    
    String getSuperClassClassName(File file);
    
    File findParentClassFile(File childClass, String inputPath);
}

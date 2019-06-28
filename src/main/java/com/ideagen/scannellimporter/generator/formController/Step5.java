/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.formController;

import com.ideagen.scannellimporter.ServiceException;
import java.io.File;
import java.util.List;

/**
 *
 * @author firdaus.norazam
 */
public interface Step5 {

    public List<String> getInitBinderMethod(File originalControlller) throws ServiceException;
}

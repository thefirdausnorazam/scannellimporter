/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.formController.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.generator.formController.Step5;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component
public class Step5Impl implements Step5 {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<String> getInitBinderMethod(File originalControlller) throws ServiceException {
        try {
            return null;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}

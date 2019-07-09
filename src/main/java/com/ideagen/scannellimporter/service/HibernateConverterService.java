/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.service;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.model.ImportCommand;

/**
 *
 * @author firdaus.norazam
 */
public interface HibernateConverterService {

    void convert(ImportCommand importCommand) throws ServiceException;
}

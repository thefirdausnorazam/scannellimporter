/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.service;

import com.ideagen.scannellimporter.ServiceException;

/**
 *
 * @author firdaus.norazam
 */
public interface ImportControllerService {

    void readAll(String inputFilePath) throws ServiceException;

    void readSpecific(String inputFilePath, String filename) throws ServiceException;
}

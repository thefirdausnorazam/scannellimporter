/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.controller;

import com.ideagen.scannellimporter.model.ImportCommand;
import com.ideagen.scannellimporter.service.ControllerGeneratorService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.ideagen.scannellimporter.service.ImportControllerService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author firdaus.norazam
 */
@RestController
public class ImporterController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ImportControllerService importControllerService;
    
    @Autowired
    private ControllerGeneratorService controllerGeneratorService;

    @PostMapping(value = "findAllController",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String findAllController(@RequestBody ImportCommand importCommand) {
        try {
            LOGGER.info("\nIncoming import command : "
                    + "\n[input path = {}] "
                    + "\n[output path = {}] "
                    + "\n[start time = {}]",
                    importCommand.getInputPath(), importCommand.getOutputPath(), LocalDateTime.now());

            importControllerService.readAll(importCommand.getInputPath());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            return "Failed";
        }

        return "Ok";
    }

    @PostMapping(value = "findController",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String findController(@RequestBody ImportCommand importCommand) {
        try {
            LOGGER.info("\nIncoming import command : "
                    + "\n[fileName = {}]"
                    + "\n[input path = {}] "
                    + "\n[output path = {}] "
                    + "\n[start time = {}]",
                    importCommand.getFileName(), importCommand.getInputPath(),
                    importCommand.getOutputPath(), LocalDateTime.now());

            importControllerService.readSpecific(importCommand.getInputPath(), importCommand.getFileName());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            return "Failed";
        }

        return "Ok";
    }
    
    @PostMapping(value = "createController/{retrievedControllerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String createSingleController(
            @RequestBody ImportCommand importCommand,
            @PathVariable(value = "retrievedControllerId", required = true) int retrievedControllerId){
        
        controllerGeneratorService.generate(importCommand, retrievedControllerId);
        
        return "Ok";
    }
    
    @PostMapping(value = "createController",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String createAllControllerFromRetrievedList(
            @RequestBody ImportCommand importCommand){
        
        controllerGeneratorService.generate(importCommand);
        
        return "Ok";
    }
}

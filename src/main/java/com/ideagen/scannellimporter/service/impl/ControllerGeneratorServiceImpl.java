/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.service.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.model.ImportCommand;
import com.ideagen.scannellimporter.repository.RetrievedControllerRepository;
import com.ideagen.scannellimporter.service.ControllerGeneratorService;
import com.ideagen.scannellimporter.service.FormControllerGeneratorService;
import com.ideagen.scannellimporter.service.MultiActionControllerGeneratorService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component
public class ControllerGeneratorServiceImpl implements ControllerGeneratorService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RetrievedControllerRepository retrievedControllerRepository;

    @Autowired
    private MultiActionControllerGeneratorService multiActionControllerGeneratorService;

    @Autowired
    private FormControllerGeneratorService formControllerGeneratorService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(16);

    private final String MULTI_ACTION_CONTROLLER_CLASS_NAME = "MultiActionController";

    @Override
    public void generate(ImportCommand importCommand, int retrievedControllerId) {
        try {
            RetrievedController retrievedController
                    = retrievedControllerRepository
                            .findById(retrievedControllerId)
                            .orElse(null);

            if (retrievedController == null) {
                throw new ServiceException("Failed to retrieve retrievedController [id=" + retrievedControllerId + "]");
            }

            if (retrievedController.getClassName().contains(MULTI_ACTION_CONTROLLER_CLASS_NAME)) {
                multiActionControllerGeneratorService.generate(retrievedController, importCommand);
                return;
            }

            formControllerGeneratorService.generate(
                    retrievedController,
                    importCommand.getInputPath(),
                    importCommand.getOutputPath());
        } catch (ServiceException e) {
            LOGGER.error("Error reading input files : ", e);
        }
    }

    @Override
    public void generate(ImportCommand importCommand) {
        List<RetrievedController> formControllers
                = retrievedControllerRepository
                        .findByParent("baseFormController")
                        .orElse(null);

        List<RetrievedController> multiActionController
                = retrievedControllerRepository
                .findByClassNameContaining(MULTI_ACTION_CONTROLLER_CLASS_NAME)
                .orElse(null);
        
        List<Callable<Boolean>> callables = new ArrayList();

        if (formControllers != null) {
            callables.addAll(formControllers.stream()
                    .map((retrievedController) -> createFormControllerCallable(importCommand, retrievedController))
                    .collect(Collectors.toList()));

        }
        
        if(multiActionController != null){
            callables.addAll(multiActionController.stream()
                    .map((retrievedController) -> createMultiActionControllerCallable(importCommand, retrievedController))
                    .collect(Collectors.toList()));
        }

        List<Future<Boolean>> futures;
        try {
            Instant start = Instant.now();

            futures = executorService.invokeAll(callables);
            int success = 0;
            int fail = 0;
            for (Future<Boolean> future : futures) {
                if (future.get()) {
                    success++;
                } else {
                    fail++;
                }
            }

            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            LOGGER.info("----------------------------------------------------------------------------------------");
            LOGGER.info("Generate form controller process summary: Duration : {} ms, Success: {}, Fail: {}",
                    timeElapsed, success, fail);
            LOGGER.info("----------------------------------------------------------------------------------------");
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    private Callable<Boolean> createFormControllerCallable(
            ImportCommand importCommand, RetrievedController retrievedController) {
        return (Callable<Boolean>) () -> {
            try {
                formControllerGeneratorService.generate(
                        retrievedController,
                        importCommand.getInputPath(),
                        importCommand.getOutputPath() + "/formController/");
            } catch (ServiceException e) {
                LOGGER.error("Error generating controller file : ", e);
                return false;
            }
            return true;
        };
    }

    private Callable<Boolean> createMultiActionControllerCallable(
            ImportCommand importCommand, RetrievedController retrievedController) {
        return (Callable<Boolean>) () -> {
            try {
                ImportCommand newImportCommand = new ImportCommand();
                newImportCommand.setFileName(importCommand.getFileName());
                newImportCommand.setInputPath(importCommand.getInputPath());
                newImportCommand.setOutputPath(importCommand.getOutputPath() + "/multiactioncontroller/");
                LOGGER.info("Output path : {}", newImportCommand.getOutputPath());
                multiActionControllerGeneratorService.generate(retrievedController, newImportCommand);
            } catch (ServiceException e) {
                LOGGER.error("Error generating controller file : ", e);
                return false;
            }
            return true;
        };
    }
}

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
import java.time.Duration;
import java.time.Instant;
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
    private FormControllerGeneratorService formControllerGeneratorService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(6);

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
        List<RetrievedController> retrievedControllers
                = retrievedControllerRepository
                        .findByParent("baseFormController")
                        .orElse(null);

        if (retrievedControllers != null) {
            List<Callable<Boolean>> callables = retrievedControllers.stream()
                    .map((retrievedController) -> createCallable(importCommand, retrievedController))
                    .collect(Collectors.toList());

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
                LOGGER.info("---------------------------------------------------------------------");
                LOGGER.info("Generate form controller process summary: Duration : {} ms, Success: {}, Fail: {}", 
                        timeElapsed, success, fail);
                LOGGER.info("---------------------------------------------------------------------");
            } catch (ExecutionException | InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }

        }
    }

    private Callable<Boolean> createCallable(ImportCommand importCommand, RetrievedController retrievedController) {
        return (Callable<Boolean>) () -> {
            try {
                formControllerGeneratorService.generate(
                        retrievedController,
                        importCommand.getInputPath(),
                        importCommand.getOutputPath());
            } catch (ServiceException e) {
                LOGGER.error("Error generating controller file : ", e);
                return false;
            }
            return true;
        };
    }
}

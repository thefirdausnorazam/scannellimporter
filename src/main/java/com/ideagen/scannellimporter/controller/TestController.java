/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.controller;

import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.model.ImportCommand;
import com.ideagen.scannellimporter.repository.RetrievedControllerRepository;
import com.ideagen.scannellimporter.service.MultiActionControllerGeneratorService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author firdaus.norazam
 */
@Controller
@RequestMapping("/test")
public class TestController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MultiActionControllerGeneratorService multiActionControllerGeneratorService;

    @Autowired
    private RetrievedControllerRepository retrievedControllerRepository;

    @RequestMapping(value = {"/pepega", "monkagiga"})
    @ResponseBody
    public String testMultipleMapping() {
        return "You have reached multiple mapping controller";
    }

    @RequestMapping(value = "/testObjectAsRequestBody",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String formView(@RequestBody ImportCommand importCommand,
            HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("\nIncoming /testObjectAsRequestBody request : \n{}", importCommand.toString());

        return importCommand.toString();
    }

    @PostMapping(value = "multiaction/testStep1/{retrievedControllerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity testMultiactionStep1(
            @PathVariable int retrievedControllerId,
            @RequestBody ImportCommand importCommand) {
        LOGGER.info("Incoming testMultiactionStep1 : \nRetrieved Controller Id : {}\nImport Command : {}",
                retrievedControllerId, importCommand.toString());

        try {
            RetrievedController retrievedController
                    = retrievedControllerRepository.findById(retrievedControllerId)
                            .orElse(null);

            if (retrievedController != null) {
                multiActionControllerGeneratorService.generate(retrievedController, importCommand);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("Ok");
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, WebDataBinder binder)
            throws Exception {
        onInitBinder(request, (ServletRequestDataBinder) binder);
    }

    private void onInitBinder(HttpServletRequest request, ServletRequestDataBinder binder)
            throws Exception {

    }
}

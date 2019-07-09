/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.controller;

import com.ideagen.scannellimporter.model.ImportCommand;
import com.ideagen.scannellimporter.model.xml.hibernate.HibernateMapping;
import com.ideagen.scannellimporter.service.HibernateConverterService;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author firdaus.norazam
 */
@RestController
public class HibernateConverterController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Jaxb2Marshaller marshaller;

    @Autowired
    private HibernateConverterService hibernateConverterService;

    @PostMapping(value = "convertHibernateMappingToJson",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity convertFromXmlToJson(@RequestBody String hibernateMappingText) {

        LOGGER.info("Incoming convertHibernateMappingToJson request with XML body : \n"
                + "--------------------------------XML Start----------------------------------------"
                + "\n{}\n"
                + "--------------------------------XML End------------------------------------------",
                hibernateMappingText);

        try {
            return ResponseEntity.ok((HibernateMapping) marshaller
                    .unmarshal(new StreamSource(new StringReader(hibernateMappingText))));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "createHibernateEntity",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity createSingleHibernateEntity(
            @RequestBody ImportCommand importCommand) {

        try {
            hibernateConverterService.convert(importCommand);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("Ok");
    }
}

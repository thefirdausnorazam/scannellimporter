/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.controller;

import com.ideagen.scannellimporter.model.ImportCommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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

    @RequestMapping(value = {"/pepega","monkagiga"})
    @ResponseBody
    public String testMultipleMapping(){
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

    @InitBinder
    protected void initBinder(HttpServletRequest request, WebDataBinder binder)
            throws Exception {
        onInitBinder(request, (ServletRequestDataBinder) binder);
    }

    private void onInitBinder(HttpServletRequest request, ServletRequestDataBinder binder)
            throws Exception {

    }
}

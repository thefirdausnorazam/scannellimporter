/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.generator.formController.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.constant.FormPostMethodHandler;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.entity.RetrievedControllerProperties;
import com.ideagen.scannellimporter.generator.formController.Step2;
import com.ideagen.scannellimporter.model.ImportToNewFormController;
import com.ideagen.scannellimporter.util.service.ControllerGeneratorUtilityService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
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
public class Step2Impl implements Step2 {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ControllerGeneratorUtilityService controllerGeneratorUtilityService;

    @Override
    public ImportToNewFormController getGetAndPostMethod(RetrievedController retrievedController,
            File originalControllerFile)
            throws ServiceException, IOException {
        List<String> methodLine = new ArrayList();

        final String requestMappingValue = getRequestMappingValue(retrievedController);

        if (retrievedController.getBeanName() == null) {
            throw new ServiceException("Unable to determine uri mapping due to bean_name being null...");
        }

        String viewModel = retrievedController.getProperties()
                .stream()
                .filter(prop -> prop.getName() != null)
                .filter(prop -> prop.getValue() != null)
                .filter(prop -> prop.getName().equals("formView"))
                .findAny()
                .map(RetrievedControllerProperties::getValue)
                .orElse(null);

        if (viewModel == null) {
            throw new ServiceException("Unable to determine view model...");
        }

        //1st line
        //@RequestMapping(value = "{uri mapping}", method = RequestMethod.GET)
        methodLine.add("\t@RequestMapping(value = "
                + requestMappingValue
                + ", method = RequestMethod.GET)");

        //2nd line
        //private ModelAndView formView(HttpServletRequest request) throws Exception{
        methodLine.add("\tprivate ModelAndView formView(HttpServletRequest request) throws Exception {");

        //3rd line
        //ModelAndView model = new ModelAndView("{jsp file name}")
        methodLine.add("\t\tModelAndView model = new ModelAndView(\""
                + viewModel
                + "\");");

        methodLine.add(" ");//<- space to make things cute

        if (isFormBackingObjectMethodExist(originalControllerFile)) {
            //if formBackingObject is in the original controller then we add this in
            methodLine.add("\t\tObject command = formBackingObject(request);");
            methodLine.add(" ");//<- space to make things cute

            //then we add BindException to that object
            methodLine.add("\t\tBindException errors = new BindException(command, \"command\");");
            methodLine.add(" ");//<- space to make things cute

            //if isOnBindAndValidateExist is in the original controller then we add this is
            if (isOnBindAndValidateExist(originalControllerFile)) {
                methodLine.add("\t\tonBindAndValidate(request, command, errors);");
                methodLine.add(" ");//<- space to make things cute
            }

            methodLine.add("\t\tmodel.addObject(\"command\", command);");
            methodLine.add("\t\tmodel.addAllObjects(referenceData(request, command, errors));");
            methodLine.add(" ");//<- space to make things cute
        }

        methodLine.add("\t\treturn model;");
        methodLine.add("\t}");

        String postModelAttributeClass = insertFormPostMethodAndReturnModelAttributeClass(
                retrievedController, originalControllerFile, methodLine, requestMappingValue);

        String postModelAttributeClassImport = controllerGeneratorUtilityService.findImportLine(
                originalControllerFile,
                postModelAttributeClass);

        if (postModelAttributeClassImport == null) {
            LOGGER.warn("Unable to find post method @ModelAttribute class [className : {}] from {}",
                    postModelAttributeClass, originalControllerFile.getAbsoluteFile());
        }

        ImportToNewFormController importToNewFormController = new ImportToNewFormController();
        importToNewFormController.setMethodLines(methodLine);
        if (postModelAttributeClassImport != null) {
            importToNewFormController.addImportList(postModelAttributeClassImport);
        }
        return importToNewFormController;
    }

    private String getModelAttributeClass(List<String> originalController,
            FormPostMethodHandler formPostMethodHandler) throws IOException {

        String objectParameterName = null;

        if (formPostMethodHandler == FormPostMethodHandler.ON_SUBMIT) {
            boolean currentConstructorLine = false;
            for (String line : originalController) {
                if (line.contains("ModelAndView")
                        && line.contains("onSubmit")
                        && line.contains("HttpServletRequest")) {
                    currentConstructorLine = true;
                }

                if (objectParameterName != null
                        && !currentConstructorLine) {
                    if (line.contains(objectParameterName)) {
                        return controllerGeneratorUtilityService.getClassInMethod(line, objectParameterName);
                    }
                }

                if (currentConstructorLine) {
                    if (line.contains("Object")) {
                        String[] lines = line.split(" ");
                        for (int i = 0; i < lines.length; i++) {
                            if (lines[i].contains("Object")) {
                                objectParameterName = lines[i + 1].replaceAll("[^a-zA-Z0-9]", "");
                                break;
                            }
                        }
                    }
                }

                if (line.contains("{")) {
                    currentConstructorLine = false;
                }
            }

        }

        if (formPostMethodHandler == FormPostMethodHandler.APPLY_COMMAND) {
            boolean currentConstructorLine = false;
            for (String line : originalController) {
                if (line.contains("AbstractEntity")
                        && line.contains("applyCommand")) {
                    currentConstructorLine = true;
                }

                if (objectParameterName != null
                        && !currentConstructorLine) {
                    if (line.contains(objectParameterName)) {
                        return controllerGeneratorUtilityService.getClassInMethod(line, objectParameterName);
                    }
                }

                if (currentConstructorLine) {
                    if (line.contains("Object")) {
                        String[] lines = line.split(" ");
                        for (int i = 0; i < lines.length; i++) {
                            if (lines[i].contains("Object")) {
                                objectParameterName = lines[i + 1].replaceAll("[^a-zA-Z0-9]", "");
                                break;
                            }
                        }
                    }
                }

                if (line.contains("{")) {
                    currentConstructorLine = false;
                }
            }
        }

        return null;
    }

    private String insertFormPostMethodAndReturnModelAttributeClass(
            RetrievedController retrievedController,
            File originalControllerFile,
            List<String> methodLine,
            final String requestMappingValue)
            throws IOException {

        List<String> originalController = Files.lines(originalControllerFile.toPath())
                .collect(Collectors.toList());

        //get modelattribute class
        FormPostMethodHandler formPostMethodHandler = controllerGeneratorUtilityService.identifyPostMethodHandler(
                originalController);
        LOGGER.info("Form Post Method Handler indentified : {}", formPostMethodHandler.name());
        String modelAttributeClass = getModelAttributeClass(originalController, formPostMethodHandler);

        //1st line
        //@RequestMapping(value = "{uri mapping}", method = RequestMethod.GET)
        methodLine.add("\t@RequestMapping(value = "
                + requestMappingValue
                + ", method = RequestMethod.POST)");

        //2nd line
        //private ModelAndView formSubmit(
        methodLine.add("\tprivate ModelAndView formSubmit(");
        methodLine.add("\t\t\tHttpServletRequest request,");
        methodLine.add("\t\t\tHttpServletResponse response,");
        methodLine.add("\t\t\t@ModelAttribute " + modelAttributeClass + " command) throws Exception {");
        methodLine.add("");
        methodLine.add("\t\tBindException errors = new BindException(command, \"command\");");
        methodLine.add("");
        //if isOnBindAndValidateExist is in the original controller then we add this is
        if (isOnBindAndValidateExist(originalControllerFile)) {
            methodLine.add("\t\tonBindAndValidate(request, command, errors);");
            methodLine.add(" ");//<- space to make things cute
        }
        methodLine.add("\t\treturn onSubmit(request, response, command, errors);");

//        if (formPostMethodHandler == FormPostMethodHandler.ON_SUBMIT) {
//            methodLine.add("\t\treturn onSubmit(request, response, command, errors);");
//        } else if (formPostMethodHandler == FormPostMethodHandler.APPLY_COMMAND) {
//            String successView = retrievedController
//                    .getProperties()
//                    .stream()
//                    .filter(prop -> prop.getName().equals("successView"))
//                    .findAny()
//                    .map(RetrievedControllerProperties::getValue)
//                    .orElse("");
//            methodLine.add("\t\tapplyCommand(command);");
//            methodLine.add("\t\treturn new ModelAndView(\"" + successView + "\");");
//        }
        methodLine.add("\t}");

        return modelAttributeClass;
    }

    private boolean isFormBackingObjectMethodExist(File originalControllerFile) throws IOException {
        List<String> originalController = Files.lines(originalControllerFile.toPath())
                .collect(Collectors.toList());

        return originalController.stream()
                .anyMatch(line -> line.contains("Object")
                && line.contains("formBackingObject")
                && line.contains("HttpServletRequest"));
    }

    private boolean isOnBindAndValidateExist(File originalControllerFile) throws IOException {
        List<String> originalController = Files.lines(originalControllerFile.toPath())
                .collect(Collectors.toList());

        return originalController.stream()
                .anyMatch(line -> line.contains("void")
                && line.contains("onBindAndValidate")
                && line.contains("HttpServletRequest"));
    }

    private String getRequestMappingValue(RetrievedController retrievedController) {
        String[] values = Optional.ofNullable(retrievedController.getBeanName())
                .map(t -> t.split(" "))
                .orElse(null);

        if (values != null) {
            final StringJoiner sj = new StringJoiner(",");

            Arrays.asList(values).forEach(value -> {
                sj.add("\"" + value + "\"");
            });

            return "{" + sj.toString() + "}";
        } else {
            return null;
        }
    }
}

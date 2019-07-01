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
import com.ideagen.scannellimporter.generator.formController.Step1;
import com.ideagen.scannellimporter.model.BeanToInject;
import com.ideagen.scannellimporter.model.ImportToNewFormController;
import com.ideagen.scannellimporter.repository.RetrievedControllerRepository;
import com.ideagen.scannellimporter.util.service.ControllerGeneratorUtilityService;
import com.ideagen.scannellimporter.util.service.FileUtilityService;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component("formControllerGeneratorStep1")
public class Step1Impl implements Step1 {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final String CONSTRUCTOR_PLACEHOLDER = "/** constructors here **/";

    @Autowired
    private RetrievedControllerRepository retrievedControllerRepository;

    @Autowired
    private ControllerGeneratorUtilityService controllerGeneratorUtilityService;

    @Autowired
    private FileUtilityService fileUtilityService;

    @Override
    public ImportToNewFormController modifyAndCopyOriginalControllerAndReturnBean(
            RetrievedController retrievedController,
            File originalFile, Path outputPath, String inputPath)
            throws IOException, ServiceException {
        List<String> modifiedController = new ArrayList();
        List<String> originalController = Files.lines(originalFile.toPath())
                .collect(Collectors.toList());

        LOGGER.info("Adding @Autowired and @Qualifier annotation to old controller file...");
        RetrievedController parentControllerBean = addRequiredAnnotationAtOldController(
                retrievedController, null, modifiedController, originalController);

        LOGGER.info("Parent controller found [id = {}] [properties = {}]",
                parentControllerBean.getId(), parentControllerBean.listProperties());

        LOGGER.info("Building constructor for old controller...");
        StringBuilder childBeanSetterMethod = new StringBuilder("");
        modifiedController = insertConstructorAndInjectBeans(retrievedController,
                modifiedController, childBeanSetterMethod);

        //rewriting onsubmit method for appy command method
        FormPostMethodHandler formPostMethodHandler = controllerGeneratorUtilityService.identifyPostMethodHandler(
                originalController);
        if (formPostMethodHandler == FormPostMethodHandler.APPLY_COMMAND) {
            modifiedController = rewriteOnSubmitMethodForApplyCommand(originalFile, modifiedController, inputPath);
        }

        LOGGER.info("Writing old controller file {}", outputPath.toUri().toString());
        Files.write(outputPath, modifiedController, Charset.defaultCharset());

        List<BeanToInject> beansToInject = new ArrayList();
        beansToInject.addAll(Optional.ofNullable(getParentBeanToInject(parentControllerBean, originalFile, inputPath))
                .orElse(new ArrayList()));
        beansToInject.addAll(Optional.ofNullable(getChildBeansToInject(originalFile, retrievedController))
                .orElse(new ArrayList()));

        ImportToNewFormController importToNewFormController = new ImportToNewFormController();
        importToNewFormController.setChildBeanSetterLine(childBeanSetterMethod.toString());
        importToNewFormController.setBeanToInjects(beansToInject);

        return importToNewFormController;
    }

    private List<BeanToInject> getChildBeansToInject(File originalFile, RetrievedController retrievedController) {
        List<BeanToInject> beansToInject = new ArrayList();

        //mapping value = class
        //name = field name
        //ref = qualifier value
        retrievedController.getProperties().forEach(prop -> {
            if (prop.getName() != null && prop.getRef() != null && prop.getMappingValue() != null) {
                BeanToInject bean = new BeanToInject();
                bean.setChildBean(true);
                bean.setBeanName(prop.getName());
                bean.setClassName(prop.getMappingValue());
                bean.setQualifierValue(prop.getRef());
                bean.setImportLine(controllerGeneratorUtilityService.findImportLine(originalFile, prop.getMappingValue()));
                beansToInject.add(bean);
            }
        });

        return beansToInject;
    }

    private List<String> insertConstructorAndInjectBeans(
            RetrievedController retrievedController,
            List<String> modifiedController,
            StringBuilder childBeanSetterMethod) {
        final List<String> setters = new ArrayList();
        final StringJoiner sj = new StringJoiner(",\n");
        final StringJoiner sj2 = new StringJoiner(",\n");
        //mapping value = class
        //name = field name
        //ref = qualifier value
        retrievedController.getProperties().forEach(prop -> {
            if (prop.getName() != null && prop.getRef() != null && prop.getMappingValue() != null) {
                //this is for the parameter passing 
                sj.add("\t\t\t" + prop.getMappingValue() + " " + prop.getName());
                setters.add("\t\t" + "this." + prop.getName() + " = " + prop.getName() + ";");
                //this is for the bean setters
                sj2.add("\t\t\t\t" + prop.getName());
            }
        });

        List<String> constructorLines = new ArrayList();
        constructorLines.add("");
        constructorLines.add("\tpublic void setBeans (");
        constructorLines.add(sj.toString());
        constructorLines.add("\t\t\t) {");
        constructorLines.addAll(setters);
        constructorLines.add("\t}");

        if (!setters.isEmpty()) {
            for (int i = 0; i < modifiedController.size(); i++) {
                if (modifiedController.get(i).contains(CONSTRUCTOR_PLACEHOLDER)) {
                    modifiedController.addAll(i, constructorLines);
                    break;
                }
            }

            childBeanSetterMethod.append("\t\tsetBeans(");
            childBeanSetterMethod.append("\n");
            childBeanSetterMethod.append(sj2.toString());
            childBeanSetterMethod.append("\n");
            childBeanSetterMethod.append("\t\t\t\t);");
        }

        return modifiedController;
    }

    private RetrievedController addRequiredAnnotationAtOldController(
            RetrievedController retrievedController,
            RetrievedController parentControllerBean,
            List<String> modifiedController,
            List<String> originalController) throws IOException {

        final String formView = retrievedController
                .getProperties()
                .stream()
                .filter(prop -> prop.getName().equals("formView"))
                .findAny()
                .map(RetrievedControllerProperties::getValue)
                .orElse(null);
        final String successView = retrievedController
                .getProperties()
                .stream()
                .filter(prop -> prop.getName().equals("successView"))
                .findAny()
                .map(RetrievedControllerProperties::getValue)
                .orElse(null);

        //search the parent controller
        if (parentControllerBean == null) {
            LOGGER.info("Finding parent bean [parent = {}] [origin file ={}]",
                    retrievedController.getParent(), retrievedController.getOriginFile());

            parentControllerBean = retrievedControllerRepository
                    .findByBeanIdAndOriginFile(retrievedController.getParent(),
                            retrievedController.getOriginFile())
                    .orElse(null);

            if (parentControllerBean == null) {
                parentControllerBean = retrievedControllerRepository
                        .findByBeanNameAndOriginFile(retrievedController.getParent(),
                                retrievedController.getOriginFile())
                        .orElse(null);
            }
        }

        boolean classStart = false;

        for (int i = 0; i < originalController.size(); i++) {
            String currentLine = originalController.get(i);

            //check if 1st line
            //if it is then add it in then add imports for @Autowired and @Qualifier annotation
            if (currentLine.contains("package")
                    && currentLine.contains(controllerGeneratorUtilityService.getPackageName(retrievedController))) {
                modifiedController.add(currentLine);
                modifiedController.add("import org.springframework.beans.factory.annotation.Autowired;");
                modifiedController.add("import org.springframework.beans.factory.annotation.Qualifier;");
                modifiedController.add("import org.springframework.web.bind.annotation.InitBinder;");
                return addRequiredAnnotationAtOldController(
                        retrievedController,
                        parentControllerBean,
                        modifiedController,
                        originalController.subList(i + 1, originalController.size()));
            }

            //check if we need to add @Autowired and @Qualifier
            boolean addAnnotation = false;
            String[] lines = currentLine.trim().split(" ");

            //if length is 2 then annotation might have not been defined
            //if length is 3 then we check
            if (lines.length == 3) {
                //if the first word is access modifier
                if (lines[0].equals("private")
                        || lines[0].equals("public")
                        || lines[0].equals("protected")) {
                    addAnnotation = true;
                }
            } else if (lines.length == 2) {
                if (!lines[0].equals("return")) {
                    addAnnotation = true;
                }
            }

            //check if need annotation
            if (addAnnotation) {
                //check if the last word equals the bean name
                String secondLastWord = lines[lines.length - 2];
                String lastWord = lines[lines.length - 1].replace(";", "");

                RetrievedControllerProperties beanProperty = retrievedController
                        .getProperties()
                        .stream()
                        .filter(prop -> prop.getName().equals(lastWord))
                        .findAny()
                        .orElse(null);

                if (beanProperty != null) {
                    //we put the class in mapping value
                    //temporarily
                    beanProperty.setMappingValue(secondLastWord);
                    retrievedController.setProperties(retrievedController
                            .getProperties()
                            .stream()
                            .filter(prop -> !prop.getName().equals(beanProperty.getName()))
                            .collect(Collectors.toList()));
                    retrievedController.addProperty(beanProperty);

                    modifiedController.add(currentLine);

                    //if parent controller was identified,
                    //then we remove any bean that was override in the child class
                    if (parentControllerBean != null) {
                        parentControllerBean.setProperties(parentControllerBean
                                .getProperties()
                                .stream()
                                .filter(prop -> !prop.getName().equals(beanProperty.getName()))
                                .collect(Collectors.toList()));
                    }

                    return addRequiredAnnotationAtOldController(
                            retrievedController,
                            parentControllerBean,
                            modifiedController,
                            originalController.subList(i + 1, originalController.size()));
                }
            }

            if (currentLine.contains("void")
                    && currentLine.contains("onInitBinder")
                    && currentLine.contains("HttpServletRequest")) {

                modifiedController.add("@InitBinder");
                modifiedController.add(currentLine);

                return addRequiredAnnotationAtOldController(
                        retrievedController,
                        parentControllerBean,
                        modifiedController,
                        originalController.subList(i + 1, originalController.size()));
            }

            if (currentLine.contains("public class")) {
                classStart = true;
            }

            if (classStart) {
                if (currentLine.contains("{")) {
                    modifiedController.add(currentLine);
                    modifiedController.add(CONSTRUCTOR_PLACEHOLDER);
                    return addRequiredAnnotationAtOldController(
                            retrievedController,
                            parentControllerBean,
                            modifiedController,
                            originalController.subList(i + 1, originalController.size()));
                }
            }

            String toAdd = currentLine;

            if (formView != null) {
                toAdd = toAdd.replaceAll("getFormView\\(\\)", "\"" + formView + "\"");
            }

            if (successView != null) {
                toAdd = toAdd.replaceAll("getSuccessView\\(\\)", "\"" + successView + "\"");
            }
            modifiedController.add(toAdd);
        }

        return parentControllerBean;
    }

    private List<BeanToInject> getParentBeanToInject(
            final RetrievedController retrievedParentController,
            final File originalChildControllerFile,
            final String inputPath) throws IOException, ServiceException {
        if (retrievedParentController == null) {
            return null;
        }

        if (retrievedParentController.getProperties() == null
                || retrievedParentController.getProperties().isEmpty()) {
            return null;
        }

        File originalParentFile = controllerGeneratorUtilityService.findParentClassFile(originalChildControllerFile, inputPath);

        if (originalParentFile == null) {
            LOGGER.warn("Failed to find parent class for class {}",
                    originalChildControllerFile.getName());
            return null;
        }

        final List<String> originalParentController = Files.lines(originalParentFile.toPath())
                .collect(Collectors.toList());

        final List<BeanToInject> beans = new ArrayList();

        originalParentController.forEach(line -> {
            boolean check = false;
            final String[] lines = line.trim().split(" ");
            //if length is 2 then annotation might have not been defined
            //if length is 3 then we check
            if (lines.length == 3) {
                //if the first word is access modifier
                if (lines[0].equals("private")
                        || lines[0].equals("public")
                        || lines[0].equals("protected")) {
                    check = true;
                }
            } else if (lines.length == 2) {
                if (!lines[0].equals("return")) {
                    check = true;
                }
            }

            //check if bean needs to be injected
            if (check) {
                final String lastWord = lines[lines.length - 1].replace(";", "");
                retrievedParentController.getProperties().forEach(prop -> {
                    if (prop.getName().equals(lastWord)) {
                        BeanToInject beanToInject = new BeanToInject();
                        beanToInject.setBeanName(prop.getName());
                        beanToInject.setQualifierValue(prop.getRef());
                        //seconds last word should be the bean class
                        //like; private Bean bean
                        beanToInject.setClassName(lines[lines.length - 2]);
                        //TODO: try to actually find the setter lol
                        //for now we assume getter and setter is using standard sintax
                        beanToInject.setSetterMethod("set" + StringUtils.capitalize(beanToInject.getBeanName()));
                        //get import
                        beanToInject.setImportLine(controllerGeneratorUtilityService
                                .findImportLine(originalParentFile, beanToInject.getClassName()));
                        beans.add(beanToInject);
                    }
                });

            }
        });

        return beans;
    }

    private List<String> rewriteOnSubmitMethodForApplyCommand(
            File originalFile, List<String> modifiedController, String inputPath) throws IOException {

        File parentClassFile = controllerGeneratorUtilityService.findParentClassFile(originalFile, inputPath);

        if (parentClassFile == null) {
            return modifiedController;
        }

        List<String> parentClassLines = Files.lines(parentClassFile.toPath())
                .collect(Collectors.toList());

        List<String> parentImportLines = controllerGeneratorUtilityService.getImportLists(parentClassLines);

        int bracketCount = 0;
        boolean onSubmitMethodStart = false;
        List<String> onSubmitMethodLines = new ArrayList();

        for (String line : parentClassLines) {

            if (line.contains("ModelAndView")
                    && line.contains("onSubmit")
                    && !line.contains("*")) {
                onSubmitMethodStart = true;
            }
            if (onSubmitMethodStart) {
                onSubmitMethodLines.add(line);

                if (line.contains("{")) {
                    bracketCount++;
                }

                if (line.contains("}")) {
                    bracketCount--;
                    if (bracketCount == 0) {
                        break;
                    }
                }
            }
        }

        boolean importsInserted = false;

        for (int i = 0; i < modifiedController.size(); i++) {
            if (modifiedController.get(i).startsWith("package") && !importsInserted) {
                importsInserted = true;
                modifiedController.addAll(i + 1, parentImportLines);
            }
            if (modifiedController.get(i).contains(CONSTRUCTOR_PLACEHOLDER)) {
                modifiedController.addAll(i, onSubmitMethodLines);
                break;
            }
        }

        return modifiedController;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.util.service.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.constant.FormPostMethodHandler;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.entity.RetrievedControllerProperties;
import com.ideagen.scannellimporter.util.service.ControllerGeneratorUtilityService;
import com.ideagen.scannellimporter.util.service.FileUtilityService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
public class ControllerGeneratorUtilityServiceImpl implements ControllerGeneratorUtilityService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final String IMPORT_KEYWORD = "import";

    @Autowired
    private FileUtilityService fileUtilityService;

    @Override
    public List<String> injectBean(RetrievedController retrievedController, String inputPath) {
        List<String> beans = new ArrayList();

        for (RetrievedControllerProperties prop : retrievedController.getProperties()) {
            if (prop.getName() != null && prop.getRef() != null) {
                String beanClass = Character
                        .toUpperCase(prop.getName().charAt(0)) + prop.getName().substring(1);

                String beanClassName = beanClass + ".java";

                try {
                    File beanClassFile = fileUtilityService.searchForFile(inputPath, beanClassName);

                    if (beanClassFile == null) {
                        LOGGER.warn("Unable to find " + beanClassName + ", assuming class exists...");
                    }
                } catch (ServiceException e) {
                    LOGGER.warn("Error searching for " + beanClassName + ", assuming class exists : ", e);
                }

                beans.add("@Autowired");
                beans.add("@Qualifier(\"" + prop.getRef() + "\")");
                beans.add("private " + beanClass + " " + prop.getName() + ";");
                beans.add("");//<- spacing for prettyness
            }
        }

        return beans;
    }

    @Override
    public List<String> getPackageAndImportLists(List<String> fileContent) {
        List<String> packageAndImportList = new ArrayList();

        for (String content : fileContent) {

            if (content.contains("package") || content.contains("import")) {
                packageAndImportList.add(content);
            }

            if (content.contains("public class")) {
                break;
            }
        }

        return packageAndImportList;
    }

    @Override
    public List<String> getImportLists(List<String> fileContent) {
        List<String> packageAndImportList = new ArrayList();

        for (String content : fileContent) {

            if (content.contains("import")) {
                packageAndImportList.add(content);
            }

            if (content.contains("public class")) {
                break;
            }
        }

        return packageAndImportList;
    }

    @Override
    public String getPackageForClass(List<String> fileContent) {
        for (String content : fileContent) {

            if (content.contains("package") || content.contains("import")) {
                return content;
            }
        }

        return null;
    }

    @Override
    public String getPackageName(RetrievedController retrievedController) {
        if (retrievedController.getClassName() == null) {
            return null;
        }

        String[] names = retrievedController.getClassName().split("\\.");

        StringJoiner sj = new StringJoiner(".");

        for (int i = 0; i < names.length - 1; i++) {
            sj.add(names[i]);
        }

        return sj.toString();
    }

    @Override
    public String findImportLine(File file, String className) {
        try {
            LOGGER.info("Finding import line [file : {}] [className : {}]", file.getAbsoluteFile(), className);

            final List<String> originalParentController = Files.lines(file.toPath())
                    .collect(Collectors.toList());

            for (String line : originalParentController) {
                if (line.startsWith(IMPORT_KEYWORD)) {
                    String[] lines = line.replace(IMPORT_KEYWORD, "").trim().split(" ");
                    if (lines[lines.length - 1].contains(className)) {
                        return line;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public String getClassInMethod(String line, String objectParameterName) {
        String[] lines = line.split(" ");
        for (int i = 0; i < lines.length; i++) {
            String currentLine = lines[i];
            if (currentLine.contains(objectParameterName)) {
                final String bracketwithParam = ")" + objectParameterName;
                final String lineToSearch;
                if (currentLine.contains(bracketwithParam)) {
                    //assumes ()object
                    //remove the object parameter name
                    lineToSearch = currentLine.replace(objectParameterName, "");
                } else {
                    lineToSearch = lines[i - 1];
                }
                LOGGER.info("Finding parameter object class [name : {}] [line : {}]",
                        objectParameterName, lineToSearch);

                return getBetweenBrackets(lineToSearch);
            }
        }

        return null;
    }

    private String getBetweenBrackets(String theLine) {
        String[] characters = theLine.split("");

        boolean bracketEndFound = false;

        StringBuilder sb = new StringBuilder("");

        for (int i = characters.length - 1; i >= 0; i--) {
            if (bracketEndFound) {
                if (characters[i].equals("(")) {
                    break;
                }
            }

            if (bracketEndFound) {
                sb.insert(0, characters[i]);
            }

            if (characters[i].equals(")")) {
                bracketEndFound = true;
            }
        }

        return sb.toString();
    }

    @Override
    public String getClassName(RetrievedController retrievedController) {
        String[] packageNames = retrievedController.getClassName().split("\\.");

        return packageNames[packageNames.length - 1];
    }

    @Override
    public FormPostMethodHandler identifyPostMethodHandler(List<String> originalController) {
        for (String line : originalController) {
            if (line.contains("AbstractEntity")
                    && line.contains("applyCommand")
                    && line.contains("Object")) {
                return FormPostMethodHandler.APPLY_COMMAND;
            }
        }

        for (String line : originalController) {
            if (line.contains("ModelAndView")
                    && line.contains("onSubmit")
                    && line.contains("HttpServletRequest")) {
                return FormPostMethodHandler.ON_SUBMIT;
            }
        }

        return FormPostMethodHandler.NOT_KNOWN;
    }

    @Override
    public String getSuperClassClassName(File file) {
        String parentClass = null;

        try {
            List<String> fileContent = Files.lines(file.toPath())
                    .collect(Collectors.toList());
            String controllerClass = file.getName().toString().replace(".java", "");

            boolean constructorFound = false;

            for (String line : fileContent) {
                if (line.contains("public") && line.contains("class")) {
                    constructorFound = true;
                }

                if (constructorFound) {
                    if (line.contains("extends")) {
                        try {
                            parentClass = line.trim()
                                    .replace("public", "")
                                    .replace("class", "")
                                    .replace(controllerClass, "")
                                    .replace("extends", "")
                                    .replace("{", "")
                                    .trim();
                            break;
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return parentClass;
    }

    @Override
    public File findParentClassFile(File childClassFile, String inputPath) {
        LOGGER.info("Finding parent class for class : " + childClassFile.getName());

        File parentFile = null;

        try {
            String parentClassName = getSuperClassClassName(childClassFile);
            String originalParentFileName = parentClassName + ".java";

            if (parentClassName != null) {
                String parentClassImportLine = findImportLine(childClassFile, parentClassName);

                //if no import line then its in the same package
                //else we get from the import line
                if (parentClassImportLine == null) {
                    Path pathToFindParentController = childClassFile.getParentFile().toPath();
                    parentFile = fileUtilityService.searchForFile(pathToFindParentController.toString(),
                            originalParentFileName);
                } else {
                    parentFile = fileUtilityService.findFileFromImportLine(inputPath, parentClassImportLine);
                }

                if (parentFile == null) {
                    LOGGER.warn("Failed to find parent class file named {} in {}",
                            originalParentFileName, inputPath);
                    return null;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return parentFile;
    }
}

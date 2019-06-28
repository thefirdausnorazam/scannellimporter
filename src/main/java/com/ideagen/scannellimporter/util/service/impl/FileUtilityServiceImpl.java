/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.util.service.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.util.service.FileUtilityService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author firdaus.norazam
 */
@Component
public class FileUtilityServiceImpl implements FileUtilityService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public File searchForFile(String inputPath, String fileName) throws ServiceException {
        LOGGER.info("Searching for file [fileName = {}] [inputPath = {}]",
                fileName, inputPath);

        try {
            try ( Stream<Path> paths = Files.walk(Paths.get(inputPath))) {
                return paths
                        .filter(Files::isRegularFile)
                        .filter(t -> t.toFile().getName().equals(fileName))
                        .findAny()
                        .map(Path::toFile)
                        .orElse(null);
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Path getPathFromImportLine(String importLine) throws ServiceException {
        try {
            importLine = importLine.replace("import", "").trim();
            List<String> importLines = Arrays.asList(importLine.split("\\."));
            return Paths.get(importLines.stream()
                    .filter(line -> !line.contains(";"))
                    .collect(Collectors.joining("/")));
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public File findFileFromImportLine(String inputLine, String importLine) throws ServiceException {
        LOGGER.info("Searching file from import line [inputPath = {}] [importLine = {}]",
                inputLine, importLine);

        try {
            importLine = importLine.replace("import", "").trim();

            List<String> importLines = Arrays.asList(importLine.split("\\."));

            String packageLine = "package "
                    + importLines.stream()
                            .filter(line -> !line.contains(";"))
                            .collect(Collectors.joining("."))
                    + ";";

            String className = importLines
                    .stream()
                    .filter(t -> t.contains(";"))
                    .findAny()
                    .orElse("").replace(";", ".java");

            try ( Stream<Path> paths = Files.walk(Paths.get(inputLine))) {
                List<File> files = paths
                        .filter(Files::isRegularFile)
                        .filter(t -> t.toFile().getName().equals(className))
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                for (File file : files) {
                    final List<String> fileLines = Files.lines(file.toPath()).collect(Collectors.toList());

                    for (String line : fileLines) {
                        if (line.contains(packageLine)) {
                            return file;
                        }
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}

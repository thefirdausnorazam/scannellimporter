package com.ideagen.scannellimporter.generator.hibernateEntity.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.generator.hibernateEntity.Step1;
import com.ideagen.scannellimporter.model.xml.hibernate.EntityClass;
import com.ideagen.scannellimporter.model.xml.hibernate.HibernateMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("hibernateGeneratorStep1")
public class Step1Impl implements Step1 {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<String> addEntityAnnotation(Path originalFilePath, HibernateMapping hibernateMapping)
            throws ServiceException {
        try {
            List<String> originalFileContents = Files.readAllLines(originalFilePath);

            List<String> modifiedFileContents = new ArrayList<>();

            String fileName = originalFilePath.getFileName().toString().replaceAll(".java", "");

            String tableName = Optional.ofNullable(hibernateMapping.getEntityClass())
                    .map(EntityClass::getTable)
                    .orElse(null);

            originalFileContents.forEach(line -> {
                if (line.contains("public")
                        && line.contains("class")
                        && line.contains(fileName)) {
                    modifiedFileContents.add("import javax.persistence.Entity;");
                    modifiedFileContents.add("");
                    modifiedFileContents.add("@Entity");
                    if (tableName != null) {
                        modifiedFileContents.add("@Table(name = \"" + tableName + "\")");
                    }
                }
                modifiedFileContents.add(line);
            });

            return modifiedFileContents;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}

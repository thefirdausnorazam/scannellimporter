package com.ideagen.scannellimporter.generator.hibernateEntity;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.model.xml.hibernate.HibernateMapping;

import java.nio.file.Path;
import java.util.List;

public interface Step1 {

    List<String> addEntityAnnotation(Path originalFilePath, HibernateMapping hibernateMapping)
            throws ServiceException;
}

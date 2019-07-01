/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.service.impl;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.entity.RetrievedControllerProperties;
import com.ideagen.scannellimporter.model.xml.bean.Bean;
import com.ideagen.scannellimporter.model.xml.bean.BeanProperty;
import com.ideagen.scannellimporter.model.xml.bean.ControllerServletBean;
import com.ideagen.scannellimporter.model.xml.bean.Prop;
import com.ideagen.scannellimporter.model.xml.bean.Props;
import com.ideagen.scannellimporter.repository.RetrievedControllerRepository;
import com.ideagen.scannellimporter.util.service.FileUtilityService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import com.ideagen.scannellimporter.service.ImportControllerService;

/**
 *
 * @author firdaus.norazam
 */
@Component
public class ImportControllerServiceImpl implements ImportControllerService {

    @Autowired
    private Jaxb2Marshaller marshaller;

    @Autowired
    private RetrievedControllerRepository retrievedControllerRepository;

    @Autowired
    private FileUtilityService fileUtilityService;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final String CONTROLLER_SERVLET = "-servlet.xml";

    @Override
    public void readAll(String inputFilePath) throws ServiceException {
        try {
            try ( Stream<Path> pathStream = Files.walk(Paths.get(inputFilePath))) {
                Set<Path> paths = pathStream
                        .filter(Files::isRegularFile)
                        .filter(t -> t.toFile().getName().contains(CONTROLLER_SERVLET))
                        .collect(Collectors.toSet());

                paths.forEach(filepath -> {
                    getController(filepath.toFile());
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error reading input files : ", e);
        }
    }

    @Override
    public void readSpecific(String inputFilePath, String fileName) throws ServiceException {
        try {
            File specifiedFile = fileUtilityService.searchForFile(inputFilePath, fileName);

            if (specifiedFile != null) {
                getController(specifiedFile);
            }
        } catch (Exception e) {
            LOGGER.error("Error reading input files : ", e);
        }
    }

    private static final String MULTIACTION_BEAN_CLASS = "org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver";

    private void getController(File file) {
        LOGGER.info("Retrieving controller beans from [filepath = {}] [fileName = {}]",
                file.getAbsolutePath(), file.getName());
        ControllerServletBean controllerServletBean
                = (ControllerServletBean) marshaller.unmarshal(new StreamSource(file));
        if (controllerServletBean != null) {
            controllerServletBean.getBean().forEach((bean) -> {
                //check if bean id or bean name or class name contains controller
                boolean process = Optional.ofNullable(bean.getBeanId())
                        .map(beanId -> beanId.toLowerCase().contains("controller"))
                        .orElse(false)
                        || Optional.ofNullable(bean.getName())
                                .map(beanName -> beanName.toLowerCase().contains("controller"))
                                .orElse(false)
                        || Optional.ofNullable(bean.getClassName())
                                .map(t -> t.toLowerCase().contains("controller"))
                                .orElse(false);
                
                if (process) {
                    RetrievedController retrievedController = new RetrievedController();
                    retrievedController.setBeanId(bean.getBeanId());
                    retrievedController.setAbstractBean(bean.getAbstractBean());
                    retrievedController.setClassName(bean.getClassName());
                    retrievedController.setBeanName(bean.getName());
                    retrievedController.setParent(bean.getParent());
                    retrievedController.setOriginFile(file.getAbsolutePath());

                    if (bean.getBeanProperties() != null) {
                        for (BeanProperty prop : bean.getBeanProperties()) {
                            if (prop.getName().equals("methodNameResolver")) {
                                try {
                                    Bean methodNameResolverBean = prop.getBean().stream()
                                            .filter(t -> t.getClassName().equals(MULTIACTION_BEAN_CLASS))
                                            .findAny()
                                            .orElse(null);

                                    if (methodNameResolverBean != null) {
                                        BeanProperty mappingsProperty = methodNameResolverBean.getBeanProperties()
                                                .stream()
                                                .filter(t -> t.getName().equals("mappings"))
                                                .findAny()
                                                .orElse(null);

                                        if (mappingsProperty != null) {
                                            Props props = mappingsProperty.getProps();

                                            if (props != null) {
                                                for (Prop p : props.getProps()) {
                                                    RetrievedControllerProperties property = new RetrievedControllerProperties();
                                                    property.setName(prop.getName());
                                                    property.setMappingKey(p.getKey());
                                                    property.setMappingValue(p.getValue());
                                                    retrievedController.addProperty(property);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            } else {
                                RetrievedControllerProperties property = new RetrievedControllerProperties();
                                property.setName(prop.getName());
                                property.setRef(prop.getRef());
                                property.setValue(prop.getValue());
                                retrievedController.addProperty(property);
                            }

                        }
                    }

                    retrievedControllerRepository.save(retrievedController);
                }
            });
        }
    }

    /**
     * final String filePath; if(fileEntry.getName().contains(HIBERNATE_XML)){
     * filePath = HIBERNATE_XML; }else{ filePath =
     * FilenameUtils.getExtension(fileEntry.getName()); }
     *
     * if (!files.containsKey(filePath)) { files.put(filePath, 1); } else {
     * files.put(filePath, files.get(filePath) + 1); }
     *
     */
}

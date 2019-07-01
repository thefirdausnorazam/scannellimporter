/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.configuration;

import com.ideagen.scannellimporter.model.xml.bean.ControllerServletBean;
import com.ideagen.scannellimporter.model.xml.hibernate.HibernateMapping;
import java.util.HashMap;
import javax.xml.bind.Marshaller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 *
 * @author firdaus.norazam
 */
@Configuration
public class JaxbConfiguration {

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setSupportDtd(true);
        
        marshaller.setClassesToBeBound(new Class[]{
            ControllerServletBean.class,
            HibernateMapping.class
        });

        marshaller.setMarshallerProperties(new HashMap<String, Object>() {
            {
                put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            }
        });

        return marshaller;
    }
    
}

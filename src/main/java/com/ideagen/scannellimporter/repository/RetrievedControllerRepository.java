/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.repository;

import com.ideagen.scannellimporter.entity.RetrievedController;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author firdaus.norazam
 */
public interface RetrievedControllerRepository extends JpaRepository<RetrievedController, Integer> {

    Optional<List<RetrievedController>> findByParent(String parent);

    Optional<RetrievedController> findByBeanIdAndOriginFile(String beanId, String originFile);

    Optional<RetrievedController> findByBeanNameAndOriginFile(String beanName, String originFile);

    Optional<List<RetrievedController>> findByClassNameContaining(String className);
}

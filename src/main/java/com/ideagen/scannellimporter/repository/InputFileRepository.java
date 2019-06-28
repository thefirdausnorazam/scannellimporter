/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.repository;

import com.ideagen.scannellimporter.entity.InputFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author firdaus.norazam
 */
public interface InputFileRepository extends JpaRepository<InputFile, Long> {
    
}

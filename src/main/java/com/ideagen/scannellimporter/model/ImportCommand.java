/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ideagen.scannellimporter.model;

/**
 *
 * @author firdaus.norazam
 */
public class ImportCommand {
    
    private String inputPath;
    
    private String outputPath;
    
    private String fileName;

    @Override
    public String toString(){
        return "ImportCommand : [inputPath = " + this.inputPath + "] "
                + "[outputPath = " + this.outputPath + "] "
                + "[fileName = " + this.fileName + "] ";
    }
    
    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

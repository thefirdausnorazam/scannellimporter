package com.ideagen.scannellimporter.service;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.model.ImportCommand;
/**
*
* @author john.goh
*/
public interface AttachmentControllerGeneratorService {
	void reset();
	void generate(RetrievedController retrievedController, String inputPath, String outputPath)throws ServiceException;
}

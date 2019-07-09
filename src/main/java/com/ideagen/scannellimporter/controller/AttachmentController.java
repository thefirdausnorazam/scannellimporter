package com.ideagen.scannellimporter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.model.ImportCommand;
import com.ideagen.scannellimporter.repository.RetrievedControllerRepository;
import com.ideagen.scannellimporter.service.AttachmentControllerGeneratorService;

@Controller
public class AttachmentController {
	
	@Autowired
	AttachmentControllerGeneratorService attachmentController;
	
	@Autowired
    private RetrievedControllerRepository retrievedControllerRepository;

	@PostMapping(value = "createSingleAttachmentController/{retrievedControllerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
	public String createSingleAttachmentController(
            @RequestBody ImportCommand importCommand,
            @PathVariable(value = "retrievedControllerId", required = true) int retrievedControllerId){
		RetrievedController retrievedController
        = retrievedControllerRepository
                .findById(retrievedControllerId)
                .orElse(null);
		System.out.println("createSingleAttachmentController:");
		try {
			attachmentController.reset();
			attachmentController.generate(retrievedController, importCommand.getInputPath(), importCommand.getOutputPath());
		} catch(ServiceException ex) {
			ex.printStackTrace();
		}
		
		return "OK";
	}
	
	@PostMapping(value = "createAllAttachmentController",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
	public String createAllAttachmentController(
            @RequestBody ImportCommand importCommand){
		List<RetrievedController> allRetrievedControllers
        = retrievedControllerRepository
        .findByClassNameContaining("Attachment")
        .orElse(null);
		allRetrievedControllers.forEach(controller->{
			try {
				attachmentController.reset();
				attachmentController.generate(controller, importCommand.getInputPath(), importCommand.getOutputPath());
			} catch(ServiceException ex) {
				ex.printStackTrace();
			}
		});
		
		return "OK";
	}
}

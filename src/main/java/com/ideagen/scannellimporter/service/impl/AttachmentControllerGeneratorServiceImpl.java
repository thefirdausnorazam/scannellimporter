package com.ideagen.scannellimporter.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ideagen.scannellimporter.ServiceException;
import com.ideagen.scannellimporter.entity.RetrievedController;
import com.ideagen.scannellimporter.entity.RetrievedControllerProperties;
import com.ideagen.scannellimporter.service.AttachmentControllerGeneratorService;
import com.ideagen.scannellimporter.service.ControllerGeneratorService;
import com.ideagen.scannellimporter.util.service.ControllerGeneratorUtilityService;
import com.ideagen.scannellimporter.util.service.FileUtilityService;

@Component
public class AttachmentControllerGeneratorServiceImpl implements AttachmentControllerGeneratorService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private Boolean buildConstructor=true;

    @Autowired
    private FileUtilityService fileUtilityService;

    @Autowired
    private ControllerGeneratorUtilityService controllerGeneratorUtilityService;
    
    @Override
    public void reset() {
    	buildConstructor=true;
    }
	
	@Override
	public void generate(RetrievedController retrievedController, String inputPath, String outputPath)
			throws ServiceException {
		String originalClassName = controllerGeneratorUtilityService.getClassName(retrievedController);
		String newClassName = originalClassName;
		String originalController = originalClassName + ".java";
		
		LOGGER.info("Creating form controller : {}", newClassName);
		
		LOGGER.info("Looking for original controller file : {}", originalController);
		File originalControllerFile = fileUtilityService.searchForFile(inputPath, originalController);
		
		LOGGER.info("Writing modified old controller");
		
		final String outputFolderPathString = retrievedController.getClassName()
		         .replaceAll("\\.", "/")
		     .replace(originalClassName, "");
		
		StringBuilder oldFilePath = new StringBuilder(outputPath);
		oldFilePath.append("/");
		oldFilePath.append(outputFolderPathString.substring(0, outputFolderPathString.length() - 1));
		
		 //create directory
		LOGGER.info("Creating directories " + oldFilePath.toString());
		new File(oldFilePath.toString()).mkdirs();
		oldFilePath.append("/").append(originalClassName);
		
		LOGGER.info("Reading content from : " + originalControllerFile.getAbsolutePath());
		List<String> fileContent;
		try {
			fileContent = Files.lines(originalControllerFile.toPath(),StandardCharsets.UTF_8)
                    .collect(Collectors.toList());
			
			//add original package name and imports
			List<String> newFileContent = new ArrayList();
			fileContent.forEach(line->{
				if(line.contains("package")) {
					newFileContent.add(line);
					newFileContent.add("import org.springframework.stereotype.Controller;");
					newFileContent.add("import org.springframework.beans.factory.annotation.Autowired;");
					newFileContent.add("import org.springframework.beans.factory.annotation.Qualifier;");
					newFileContent.add("import org.springframework.web.bind.annotation.ModelAttribute;");
					newFileContent.add("import org.springframework.web.bind.annotation.RequestMapping;");
					newFileContent.add("import org.springframework.web.bind.annotation.RequestMethod;");
					newFileContent.add("import org.springframework.validation.Validator;");
					newFileContent.add("import org.springframework.web.servlet.ModelAndView;");
					newFileContent.add("import org.springframework.validation.BindException;");
					newFileContent.add("import javax.servlet.http.HttpServletRequest;");
					newFileContent.add("import javax.servlet.http.HttpServletResponse;");

					newFileContent.add("");
					newFileContent.add("import com.scannellsolutions.modules.system.domain.AttachmentType;");
					newFileContent.add("import java.util.Enumeration;");
					
				} else if(line.contains("import")) {
					newFileContent.add(line);
				} else if(line.contains("public class")) {
					newFileContent.add("@Controller");
					newFileContent.add(line);
					if(line.contains("extends")||line.contains("implements")) {
						newFileContent.addAll(autowiredPrivateProperties(retrievedController));
						newFileContent.addAll(controllerConstructor(retrievedController));
					}
				} else if(line.contains("extends")||line.contains("implements")) {
					newFileContent.add(line);
					newFileContent.addAll(autowiredPrivateProperties(retrievedController));
					newFileContent.addAll(controllerConstructor(retrievedController));
				} else if(line.contains("private")&&!(line.contains("(")||line.contains(")"))){
					newFileContent.add("\t@Autowired");
					newFileContent.add(line);
				} else {
					newFileContent.add(line);
				}
				
			});
			
			newFileContent.addAll(newFileContent.lastIndexOf("}"),formViewSubmit(retrievedController));
            Path newClassFilePath = Paths.get(oldFilePath + ".java");

            LOGGER.info("Writing new controller class");
            Files.write(newClassFilePath, newFileContent, Charset.defaultCharset());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private List<String> controllerConstructor(RetrievedController retrievedController){
		if(buildConstructor==false) return new ArrayList();
		String originalClassName = controllerGeneratorUtilityService.getClassName(retrievedController);
		List<String> content = new ArrayList();
		
		final String formView = retrievedController
                .getProperties()
                .stream()
                .filter(prop -> prop.getName().equals("formView"))
                .findAny()
                .map(RetrievedControllerProperties::getValue)
                .orElse(null);
        final String successView = retrievedController
                .getProperties()
                .stream()
                .filter(prop -> prop.getName().equals("successView"))
                .findAny()
                .map(RetrievedControllerProperties::getValue)
                .orElse(null);
		final String validator = retrievedController
                .getProperties()
                .stream()
                .filter(prop -> prop.getName().equals("validator"))
                .findAny()
                .map(RetrievedControllerProperties::getRef)
                .orElse(null);
		final String service = retrievedController
                .getProperties()
                .stream()
                .filter(prop -> prop.getName().contains("Service")||prop.getName().contains("service"))
                .findAny()
                .map(RetrievedControllerProperties::getName)
                .orElse(null);
		final String serviceRef = retrievedController
                .getProperties()
                .stream()
                .filter(prop -> prop.getName().contains("Service"))
                .findAny()
                .map(RetrievedControllerProperties::getRef)
                .orElse(null);
		
		content.add("");
		content.add("\t@Autowired");
		String function="\tpublic "+originalClassName+"(";
		if(validator!=null) {
			function+="@Qualifier(\""+validator+"\") Validator validator,\n";
		}
		if(service!=null) {
			function+="\t\t\t@Qualifier(\""+serviceRef+"\") "+service.substring(0, 1).toUpperCase()+service.substring(1)+" "+service+"\n";
		}
		function+="\t) {";
		content.add(function);
		content.add("\t\tsuper();");
		content.add("\t\tsetFormView(\""+formView+"\");");
		content.add("\t\tsetSuccessView(\""+successView+"\");");
		content.add("\t\tsetValidator(validator);");
		content.add("\t\tset"+service.substring(0, 1).toUpperCase()+service.substring(1)+"("+service+");");
		content.add("\t}");
		content.add("");
		buildConstructor=false;
		return content;
	}
	private List<String> autowiredPrivateProperties(RetrievedController retrievedController){
		List<String> content = new ArrayList();
		retrievedController.getProperties().forEach(prop->{
			if(!prop.getName().contains("formView")&&!prop.getName().contains("successView")&&!prop.getName().contains("validator")) {
				String beanClass = Character.toUpperCase(prop.getName().charAt(0)) + prop.getName().substring(1);

				content.add("");
				content.add("\t@Autowired");
				content.add("\t@Qualifier(\"" + prop.getRef() + "\")");
				content.add("\tprivate " + beanClass + " " + prop.getName() + ";");
				content.add("");
			}
		});
		return content;
	}
	private List<String> formViewSubmit(RetrievedController retrievedController) {
		List<String> content = new ArrayList();
		final String formView = retrievedController
                .getProperties()
                .stream()
                .filter(prop -> prop.getName().equals("formView"))
                .findAny()
                .map(RetrievedControllerProperties::getValue)
                .orElse(null);
        final String successView = retrievedController
                .getProperties()
                .stream()
                .filter(prop -> prop.getName().equals("successView"))
                .findAny()
                .map(RetrievedControllerProperties::getValue)
                .orElse(null);
        content.add(" ");
        content.add("\t@RequestMapping(value = {\""+retrievedController.getBeanName()+"\"}, method = RequestMethod.GET)");
        content.add("\tprotected final ModelAndView showForm(HttpServletRequest request, HttpServletResponse response) throws Exception {");
        content.add(" ");
        content.add("\t\tObject command = formBackingObject(request);");
        content.add("\t\tBindException errors = new BindException(command, \"command\");");
        content.add("\t\treturn showForm(request, response, errors, null);");
        content.add("\t}");
        content.add(" ");
        
        content.add("\t@RequestMapping(value = {\""+retrievedController.getBeanName()+"\"}, method = RequestMethod.POST)");
        content.add("\tprivate ModelAndView formSubmit(");
        content.add("\t\tHttpServletRequest request,");
        content.add("\t\tHttpServletResponse response,");
        content.add("\t\t@ModelAttribute Attachment command) throws Exception {");
        content.add(" ");
        content.add("\t\tcommand = (Attachment) formBackingObject(request);");
        content.add("\t\tcommand.setType(request.getParameter(\"type\"));");
        content.add("\t\tcommand.setName(request.getParameter(\"name\"));");
        content.add("\t\tcommand.setDescription(request.getParameter(\"description\"));");
        content.add("\t\tBindException errors = new BindException(command, \"command\");");
        content.add(" ");
        content.add("\t\tonBindAndValidate(request, command, errors);");
        content.add(" "); 
        content.add("\t\treturn onSubmit(request, response, command, errors);");
        content.add("\t}");
		return content;
	}
	
	

}

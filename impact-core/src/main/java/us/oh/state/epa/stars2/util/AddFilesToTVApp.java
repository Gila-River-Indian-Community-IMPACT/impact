package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

public class AddFilesToTVApp {
	private static final String eacFilePath = "C:\\work\\EAC.doc";
	private static final String processFlowFilePath = "C:\\work\\ProcessFlow.pdf";
	private File eacFile;
	private File processFlowFile;
	
	public boolean init() {
		boolean ok = true;
		eacFile = new File(eacFilePath);
		processFlowFile = new File(processFlowFilePath);
		ok = (eacFile.canRead() && processFlowFile.canRead());
		return ok;
	}
	
	public void addFiles(int applicationId) {
		try {
			ApplicationService appBO = ServiceFactory.getInstance().getApplicationService();
			Application app = appBO.retrieveApplicationWithAllEUs(applicationId);
			System.out.println("Processing application " + app.getApplicationNumber());
			for (ApplicationEU appEU : app.getIncludedEus()) {
				System.out.println("Processing EU " + appEU.getFpEU().getEpaEmuId() + "...");
				boolean hasEAC = false;
				boolean hasProcessFlow = false;
				for (ApplicationDocumentRef docRef : appEU.getEuDocuments()) {
					/*if (ApplicationDocumentTypeDef.EAC.equals(docRef.getApplicationDocumentTypeCD())) {
						hasEAC = true;
					}*/
					if (ApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM.equals(docRef.getApplicationDocumentTypeCD())) {
						hasProcessFlow = true;
					}
				}
				if (!hasEAC) {
					addEACAttachment(app, appEU, appBO);
				}
				if (!hasProcessFlow) {
					addProcessFlowAttachment(app, appEU, appBO);
				}
				
			}
			System.out.println("Done");
		} catch (ServiceFactoryException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addEACAttachment(Application app, ApplicationEU appEU, ApplicationService appBO) throws FileNotFoundException, RemoteException, RemoteException {
//		UploadedFileInfo fileInfo = new UploadedFileInfo(eacFile.getName(), eacFile);
//		ApplicationDocumentRef applicationDoc = new ApplicationDocumentRef();
//		applicationDoc.setApplicationDocumentTypeCD(ApplicationDocumentTypeDef.EAC);
//		applicationDoc.setEacFormTypeCD("0");
//		applicationDoc.setApplicationEUId(appEU.getApplicationEuId());
//		applicationDoc.setApplicationId(appEU.getApplicationId());
//		applicationDoc.setDescription("TEST EAC");
//		
//		applicationDoc = appBO.uploadApplicationDocument(app, applicationDoc, fileInfo, null, 1);
//		System.out.println("Added EAC");
	}

	private void addProcessFlowAttachment(Application app, ApplicationEU appEU, ApplicationService appBO) throws FileNotFoundException, RemoteException, RemoteException {
//		UploadedFileInfo fileInfo = new UploadedFileInfo(processFlowFile.getName(), processFlowFile);
//		ApplicationDocumentRef applicationDoc = new ApplicationDocumentRef();
//		applicationDoc.setApplicationDocumentTypeCD(ApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM);
//		applicationDoc.setApplicationEUId(appEU.getApplicationEuId());
//		applicationDoc.setApplicationId(appEU.getApplicationId());
//		applicationDoc.setDescription("TEST PROCESS FLOW");
//		
//		applicationDoc = appBO.uploadApplicationDocument(app, applicationDoc, fileInfo, null, 1);
//		System.out.println("Added Process Flow");
	}
	
	public static void main (String[] args) {
		AddFilesToTVApp add = new AddFilesToTVApp();
		if (add.init()) {
			add.addFiles(45700);
		} else {
			System.err.println("ERROR initializing class");
		}
	}
}

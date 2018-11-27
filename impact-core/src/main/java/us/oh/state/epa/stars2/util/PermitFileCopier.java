package us.oh.state.epa.stars2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;

@Component
public class PermitFileCopier {
	private File outputDir;
	private File permitListFile;
	
	@Resource private PermitDAO readOnlyPermitDAO;
	
	public boolean init() {
		boolean ok = false;
		outputDir = new File("C:\\work\\paul\\permitFiles");
		if (!outputDir.exists() || !outputDir.isDirectory()) {
			System.err.println("Unable to read directory " + outputDir.getAbsolutePath());
		} else {
			permitListFile = new File("C:\\work\\paul\\PermitNumbers.csv");
			if (!permitListFile.exists() || !permitListFile.canRead()) {
				System.err.println("Unable to read file " + permitListFile.getAbsolutePath());
			} else {
				ok = true;
			}
		}
		return ok;
	}
	
	public void copyPermitFiles() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(permitListFile));
		String line = null;
//		PermitDAO permitDAO = (PermitDAO)DAOFactory.getDAO("PermitDAO");
		
		while ((line = br.readLine()) != null) {
			String permitNbr = line;
			Permit permit = readOnlyPermitDAO.retrievePermit(permitNbr);
			for (PermitDocument permitDoc : permit.getDocuments()) {
				if ("F".equals(permitDoc.getIssuanceStageFlag()) && "D".equals(permitDoc.getPermitDocTypeCD())) {
					try {
						String relativePath = permitDoc.getPath();
						File permitFile = new File(DocumentUtil.getFileStoreRootPath(), relativePath);
						File destFile = new File(outputDir, permitNbr + "." + permitDoc.getExtension());
						DocumentUtil.copyFile(permitFile, destFile);
					} catch (Exception e) {
						System.err.println("Exception copying file for permit: " + permitNbr);
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static final void main(String[] args) {
		PermitFileCopier pfc = new PermitFileCopier();
		if (pfc.init()) {
			try {
				pfc.copyPermitFiles();
			} catch (IOException e) {
				System.err.println("Exception in copyPermitFiles: ");
				e.printStackTrace();
			}
		}
	}
}

package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.aspose.words.Cell;
import com.aspose.words.CellMerge;
import com.aspose.words.ControlChar;
import com.aspose.words.Document;
import com.aspose.words.Font;
import com.aspose.words.License;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.PreferredWidth;
import com.aspose.words.Row;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.Table;

import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.wy.state.deq.impact.def.FileExtensionDef;

public class DocumentUtil {

	private static String FILE_STORE_ROOT_PATH = "";
	private static String FILE_STORE_PROTOCOL = "";
	private static String FILE_STORE_HOST = "";
	private static String FILE_STORE_PORT = "";
	private static String FILE_STORE_URL_PATH = "";

	private static final int BUFFER_SIZE = 8192;

	private static Logger logger = Logger.getLogger(DocumentUtil.class);
	
	public static Font boldFont;
	public static Font normalFont;
	
	public static final double CELL1_PER_WIDTH = 82;
	public static final double CELL2_PER_WIDTH = 18;
	

	static {
		FILE_STORE_ROOT_PATH = getNodeValue("app.fileStore.rootPath");
		FILE_STORE_PROTOCOL = getNodeValue("app.fileStore.protocol");
		FILE_STORE_HOST = getNodeValue("app.fileStore.host");
		FILE_STORE_PORT = getNodeValue("app.fileStore.port");
		FILE_STORE_URL_PATH = getNodeValue("app.fileStore.urlPath");
	
		try {
			boldFont = new Run(new Document()).getFont();
			normalFont = new Run(new Document()).getFont();
			
			boldFont.setBold(true);
			boldFont.setSize(12);
			boldFont.setName("Times New Roman");
	
			normalFont.setBold(false);
			normalFont.setSize(12);
			normalFont.setName("Times New Roman");
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static String getNodeValue(String node) {
		// TODO should use ConfigManager or Config???
		// ConfigManager cfgMgr = ConfigManagerFactory.configManager();

		String value = null;
		String jndiName = Config.findNode(node).getAsString("jndiName");
		try {
			// TODO potential optimization opportunity?
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			value = (String) envContext.lookup(jndiName);
			logger.debug("node value = " + value);
		} catch (NamingException ne) {
			throw new RuntimeException(ne);
		}
		return value;
	}

	public static String getFileStoreRootPath() {
		return FILE_STORE_ROOT_PATH;
	}

	public static String getFileStoreBaseURL() {
		String baseURL = FILE_STORE_PROTOCOL + "://" + FILE_STORE_HOST;
		if (FILE_STORE_PORT != null && FILE_STORE_PORT.length() > 0) {
			baseURL += ":" + FILE_STORE_PORT;
		}
		baseURL += FILE_STORE_URL_PATH;
		return baseURL;
	}

	public static String getFileExtension(String path) {
		int pos = path.lastIndexOf(".");
		if (pos >= 0) {
			return path.substring(pos + 1);
		}
		return "";
	}

	public static String getFileName(String path) {
		int index = path.replaceAll("\\\\", "/").lastIndexOf("/");

		return index >= 0 ? path.substring(index + 1) : path;
	}

	public static String getParentURL(String url) {
		int pos = url.lastIndexOf('/');
		if (pos >= 0) {
			return url.substring(0, pos);
		}

		return url;
	}

	public static Map<String, String> getDirList(String path)
			throws IOException {

		if (path == null || path.length() < 1) {
			throw new IOException("Path parameter is missing.");
		}

		String urlPath = path;
		if (urlPath.startsWith("/") || urlPath.startsWith("\\")) {
			urlPath = "/" + urlPath.substring(1);
		}

		String fullPath = FILE_STORE_ROOT_PATH + path;
		File dir = new File(fullPath);

		TreeMap<String, String> tmpDocs = new TreeMap<String, String>();

		if (dir.isDirectory()) {

			if (urlPath.length() > 1 && !urlPath.endsWith("/")) {
				urlPath = urlPath + "/";
			}

			for (File dirEntry : dir.listFiles()) {

				if (dirEntry.isDirectory()) {
					tmpDocs.putAll(getChildDirList(dirEntry, urlPath));
				}

				else {
					tmpDocs.put(dirEntry.getName(), getFileStoreBaseURL()
							+ urlPath + dirEntry.getName());
				}

			} // END: for (File dirEntry : dir.listFiles())

		} else {
			tmpDocs.put(dir.getName(),
					getFileStoreBaseURL() + urlPath + dir.getName());
		}

		return tmpDocs;
	}

	private static Map<String, String> getChildDirList(File dir, String parent)
			throws IOException {

		if (dir == null) {
			throw new IOException("Path parameter is missing.");
		}
		if (parent == null || parent.length() < 1) {
			throw new IOException("Parent parameter is missing.");
		}

		TreeMap<String, String> tmpDocs = new TreeMap<String, String>();

		for (File dirEntry : dir.listFiles()) {
			if (dirEntry.isDirectory()) {
				tmpDocs.putAll(getChildDirList(dirEntry, parent + dir.getName()
						+ "/"));
			}

			else {
				tmpDocs.put(dirEntry.getName(), getFileStoreBaseURL() + parent
						+ dir.getName() + "/" + dirEntry.getName());
			}

		} // END: for (File dirEntry : dir.listFiles())

		return tmpDocs;
	}

	public static void mkDir(String path) throws IOException {

		if (path == null || path.length() < 1) {
			throw new IOException("Path parameter is missing.");
		}
		String fullPath = FILE_STORE_ROOT_PATH + path;
		File newDir = new File(fullPath);
		if (newDir.exists() && newDir.isDirectory()) {
			return;
		}

		boolean success = false;
		try {
			success = newDir.mkdirs();
		} catch (Exception e) {
			IOException ioe = new IOException("Unable to create directory "
					+ fullPath + ". " + e.getMessage());
			ioe.initCause(e);
			throw ioe;
		}
		if (!success || !newDir.exists()) {
			String error = "Unable to create directory " + fullPath + ". ";
			if (newDir.exists() && newDir.isDirectory()) {
				error += "Directory already exists.";
			} else if (newDir.exists() && newDir.isFile()) {
				error += "Path already exists and is a file.";
			}
			IOException ioe = new IOException(error);
			throw ioe;
		}
	}

	public static void mkDirs(String path) throws IOException {

		if (path == null || path.length() < 1) {
			throw new IOException("Path parameter is missing.");
		}
		String fullPath = FILE_STORE_ROOT_PATH + path;
		File newDir = new File(fullPath);

		boolean noOp = false;
		if (newDir.exists()) {
			if (newDir.isFile()) {
				String error = "Unable to create directory " + fullPath + ". "
						+ "Path already exists and is a file.";
				IOException ioe = new IOException(error);
				throw ioe;
			}

			noOp = true;
		}

		if (!noOp) {
			boolean success = false;
			try {
				success = newDir.mkdirs();
			} catch (Exception e) {
				IOException ioe = new IOException("Unable to create directory "
						+ fullPath + ". " + e.getMessage());
				ioe.initCause(e);
				throw ioe;
			}
			if (!success) {
				String error = "Unable to create directory " + fullPath + ". ";
				IOException ioe = new IOException(error);
				throw ioe;
			}
		}
	}

	public static void rmDir(String path) throws IOException {

		if (path == null || path.length() < 1) {
			throw new IOException("Path parameter is missing.");
		}
		String fullPath = FILE_STORE_ROOT_PATH + path;
		File dir = new File(fullPath);
		if (dir == null || !dir.exists()) {
			return;
		}

		if (!dir.isDirectory()) {
			throw new IOException(fullPath + " is not a directory.");
		}

		for (File dirEntry : dir.listFiles()) {

			String newPath = dirEntry.getAbsolutePath().substring(
					FILE_STORE_ROOT_PATH.length());

			if (dirEntry.isDirectory()) {
				rmDir(newPath);
			} else {
				removeDocument(newPath);
			}
		}

		boolean success = false;
		try {
			success = dir.delete();
		} catch (Exception e) {
			IOException ioe = new IOException("Unable to delete directory "
					+ fullPath + ". " + e.getMessage());
			ioe.initCause(e);
			throw ioe;
		}
		if (!success) {
			throw new IOException("Unable to delete directory " + fullPath
					+ ". ");
		}
	}

	/**
	 * Pass something like "Facilities/old_facility_id" and
	 * "Facilities/new_facility_id".
	 */
	public static void renameDir(String oldPath, String newPath)
			throws IOException {

		if (oldPath == null || oldPath.length() < 1) {
			throw new IOException("oldPath parameter is missing.");
		}
		if (newPath == null || newPath.length() < 1) {
			throw new IOException("newPath parameter is missing.");
		}

		String oldFullPath = FILE_STORE_ROOT_PATH + oldPath;
		String newFullPath = FILE_STORE_ROOT_PATH + newPath;

		File oldFile = new File(oldFullPath);
		File newFile = new File(newFullPath);
		boolean success = oldFile.renameTo(newFile);
		if (!success) {
			throw new IOException("Rename operation from " + oldPath + "to "
					+ newPath + "failed.");
		}

	}

	public static void copyDocument(String origDocumentPath,
			String newDocumentPath) throws IOException {

		if (origDocumentPath == null || origDocumentPath.length() < 1
				|| newDocumentPath == null || newDocumentPath.length() < 1) {
			throw new IOException(
					"Original or new document path parameter is missing.");
		}
		String origFullPath = FILE_STORE_ROOT_PATH + origDocumentPath;
		String newFullPath = FILE_STORE_ROOT_PATH + newDocumentPath;
		File origFile = new File(origFullPath);
		File newFile = new File(newFullPath);

		if (!newFile.createNewFile()) {
			throw new IOException("Unable to create new document.");
		}

		copyFile(origFile, newFile);
	}
	
	public static void copyAttachmentDocument(String origDocumentPath,
			String newDocumentPath) throws IOException {

		if (origDocumentPath == null || origDocumentPath.length() < 1
				|| newDocumentPath == null || newDocumentPath.length() < 1) {
			throw new IOException(
					"Original or new document path parameter is missing.");
		}
		//String origFullPath = origDocumentPath;
		//String newFullDirectory = FILE_STORE_ROOT_PATH + newDocumentDirectory;
		//String newFilePath = newFullDirectory+origFullPath.substring(origFullPath.lastIndexOf("."));
		origDocumentPath = FILE_STORE_ROOT_PATH + origDocumentPath;
		newDocumentPath = FILE_STORE_ROOT_PATH + newDocumentPath;
		File origFile = new File(origDocumentPath);
		File newFile = new File(newDocumentPath);

		if (!newFile.createNewFile()) {
			throw new IOException("Unable to create new document.");
		}

		copyFile(origFile, newFile);
	}


	public static void copyFile(File origFile, File newFile) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(origFile);
			fos = new FileOutputStream(newFile);

			byte[] buffer = new byte[BUFFER_SIZE];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (fos != null) {
				fos.flush();
				fos.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
	}

	public static void removeDocument(String documentPath) throws IOException {

		if (documentPath == null || documentPath.length() < 1) {
			throw new IOException("Document path parameter is missing.");
		}
		String fullPath = FILE_STORE_ROOT_PATH + documentPath;
		File doc = new File(fullPath);
		if (!doc.exists()) {
			throw new IOException(fullPath + " does not exist.");
		}
		if (!doc.delete()) {
			throw new IOException("Unable to delete file " + fullPath);
		}

	}

	public static void moveDocument(String origDocumentPath,
			String newDocumentPath) throws IOException {

		copyDocument(origDocumentPath, newDocumentPath);
		removeDocument(origDocumentPath);

	}

	public static String generateAsposeDocument(String templatePath,
			DocumentGenerationBean data, String documentPath)
			throws IOException, DocumentGenerationException {
		String docPath = null;

		String documentTemplateFullPath = FILE_STORE_ROOT_PATH + templatePath;
		String documentFullPath = FILE_STORE_ROOT_PATH + documentPath;

		File tmpFile = new File(documentTemplateFullPath);
		if (tmpFile == null || !tmpFile.exists()) {
			String msg = new String ("Template file " + documentTemplateFullPath + " does not exist.");
			DisplayUtil.displayError(msg);
			msg = new String ("Please upload template file and try again.");
			DisplayUtil.displayError(msg);
			IOException ioe = new IOException(
					msg);
			throw ioe;
		}
		
			try{
					//Set License
					License license = new License();
					license.setLicense("../classes/Aspose.Words.lic");
					
					Document modifiedDoc=null;
					Document modifiedDoc1=null;
					
					
					Document doc = new Document(documentTemplateFullPath);
					Set s = data.getProperties().keySet();
					Iterator iter = s.iterator();
					
						while (iter.hasNext()) {
							  String propName = (String)iter.next();
							  String dataValue=data.getProperties().get(propName);
							 //System.out.println("propName: "+propName+" dataValue: "+dataValue);
							  if(dataValue != null){	
								  if(dataValue.length() > 0){
									  doc.getRange().replace(Pattern.compile("\\<"+propName+"\\>"), dataValue);		
								  }
							  }							  
						}
						
                        int numOfSections = doc.getSections().getCount();
						for(int i=0; i<numOfSections; i++){
							Section section = doc.getSections().get(i);
							String secText = section.getText();
							if(secText.contains("###")){
								modifiedDoc=replaceEuData(doc, data, i, "fpEUs", documentFullPath);
							}
						}
						
						if(modifiedDoc==null)
							modifiedDoc=doc;
	                    int modifiedNumOfSections = modifiedDoc.getSections().getCount();
						for(int j=0; j<modifiedNumOfSections; j++){
								Section modifiedDocSection = modifiedDoc.getSections().get(j);
								String modifiedDocSecText = modifiedDocSection.getText();
								if(modifiedDocSecText.contains("&&&")){
									modifiedDoc=replaceEuData(modifiedDoc, data, j, "eus", documentFullPath);
									modifiedNumOfSections = modifiedDoc.getSections().getCount();
								}
						}
						
						if(modifiedDoc1==null)
							modifiedDoc1=modifiedDoc;
	                    int modifiedNumOfSections1 = modifiedDoc1.getSections().getCount();
						for(int k=0; k<modifiedNumOfSections1; k++){
								Section modifiedDocSection = modifiedDoc1.getSections().get(k);
								String modifiedDocSecText = modifiedDocSection.getText();
								if(modifiedDocSecText.contains("@@@")){
									modifiedDoc1=replaceEuData(modifiedDoc1, data, k, "poll", documentFullPath);
									modifiedNumOfSections1 = modifiedDoc1.getSections().getCount();
								}
						}


			       
			        
				    System.out.println("documentFullPath: "+documentFullPath);
				    if(modifiedDoc1 != null){				    	
				    	modifiedDoc1 = removeSectionBreaks(modifiedDoc1);				    	
				    	 Pattern regex1 = Pattern.compile("<", Pattern.CASE_INSENSITIVE);
				    	 Pattern regex2 = Pattern.compile(">", Pattern.CASE_INSENSITIVE);				    	 
				    	 modifiedDoc1.getRange().replace(regex1,  new ReplaceEvaluatorFindAndHighlight(), false);
				    	 modifiedDoc1.getRange().replace(regex2,  new ReplaceEvaluatorFindAndHighlight(), false);
				    	//modifiedDoc1 = removeSectionBreaks(modifiedDoc1);
				    	modifiedDoc1.save(documentFullPath);				    	
				    }else if(doc != null){				    	
				    	//doc = removePageBreaks(doc);
				    	//doc = removeSectionBreaks(doc);
				    	doc.save(documentFullPath);					    	
				    }
				    
				    docPath = DocumentUtil.getFileStoreBaseURL() + documentPath;
				    //docPath.replace('\\', '/');
				    //Desktop desktop = Desktop.getDesktop();  
				    //File f = new File(docPath);
				    //desktop.open(f); 				    
				    //FacesContext context = FacesContext.getCurrentInstance();
				    //HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
				    //response.sendRedirect(docPath);
				}catch(Exception e){
					logger.error("An exception occured while generating document from the template " + documentTemplateFullPath);
					throw new DocumentGenerationException(e.getMessage(), e);
				}catch(Error err) {
					logger.error("An error occured while generating document from the template " + documentTemplateFullPath);
					throw new DocumentGenerationException(err.getMessage(), err);
				}
			
			return docPath.replace('\\', '/');
	}
	
	
	public static Document replaceEuData(Document tempDoc, DocumentGenerationBean data, int i,String childName, String documentFullPath){
		
		try{
			List<DocumentGenerationBean>  fpeuList= data.getChildCollections().get(childName);
		    Iterator<DocumentGenerationBean> dFpEuGen=  fpeuList.iterator();
		    Set setFpEu=null;
		    DocumentGenerationBean dBean=null;
		    Section cloneSection=null;

		    while (dFpEuGen.hasNext()) {
		    				  
		    						dBean = dFpEuGen.next();
		    						setFpEu = dBean.getProperties().keySet();
		    						Iterator iterFpEu = setFpEu.iterator();
		    						cloneSection = tempDoc.getSections().get(i).deepClone();
		    						while (iterFpEu.hasNext()) {
		    							  String propName = (String)iterFpEu.next();
		    							  String propValue=dBean.getProperties().get(propName);
		    							  if(propValue != null){
		    								  cloneSection.getRange().replace(Pattern.compile("###"), "");
		    								  cloneSection.getRange().replace(Pattern.compile("&&&"), "");
		    								  cloneSection.getRange().replace(Pattern.compile("@@@"), "");
		    								  cloneSection.getRange().replace(Pattern.compile("\\<"+propName+"\\>"), propValue);
		    							  }
		    							 
		    						}
		    						tempDoc.getSections().insert(i++, cloneSection);
		    }
		    
		    int numOfSections = tempDoc.getSections().getCount();
			for(int k=0; k<numOfSections; k++){
				Section section = tempDoc.getSections().get(k);
				String secText = section.getText();
				if(secText.contains("###") && childName.contains("fpEUs")){
					tempDoc.getSections().removeAt(k);
					return tempDoc;
				} else if(secText.contains("&&&") && childName.contains("eus")){
					tempDoc.getSections().removeAt(k);
					return tempDoc;
				} else if(secText.contains("@@@") && childName.contains("poll")){
					tempDoc.getSections().removeAt(k);
					return tempDoc;
				}
			}
		
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	    return tempDoc;
	}
	
	
	public static Document removeSectionBreaks(Document doc) throws Exception{
		//System.out.println("doc.getSections().getCount(): "+doc.getSections().getCount());
		for (int i = doc.getSections().getCount() - 2; i >= 0; i--)
	    {
			//System.out.println("doc.getSections().get(i): "+doc.getSections().get(i).getText());
			// Copy the content of the current section to the beginning of the last section.
	        doc.getLastSection().prependContent(doc.getSections().get(i));
	        // Remove the copied section.
	        doc.getSections().get(i).remove();
	    }
		
		doc.updatePageLayout();
		 
		Node[] tables = doc.getChildNodes(NodeType.TABLE, true).toArray();
		ArrayList emptyParagaphs = new ArrayList();
		 
		for(int i= 0; i < tables.length - 1 ; i++)
		{
		       Node currentNode = tables[i];
		       Node nextTable = tables[i + 1];
		      
		       while (currentNode!= null && currentNode != nextTable) {
		              Node node = currentNode.getNextSibling();
		              //System.out.println(node.toString(SaveFormat.TEXT));
		              if(null != node) {
			              if(node.getNodeType() == NodeType.PARAGRAPH && node.toString(SaveFormat.TEXT).trim().equals(""))
			              {
			                     emptyParagaphs.add(node);
			              }
		              }
		             
		              currentNode = node;
		       }
		}
		 
		for(Node node :  (Iterable<Node>)emptyParagaphs)
		       node.remove();
		
		return doc;
	}
	
	public static Document removePageBreaks(Document doc) throws Exception
	{
	    // Retrieve all paragraphs in the document.
	    NodeCollection paragraphs = doc.getChildNodes(NodeType.PARAGRAPH, true);

	    // Iterate through all paragraphs
	    for (Paragraph para : (Iterable<Paragraph>) paragraphs)
	    {
	        // If the paragraph has a page break before set then clear it.
	        if (para.getParagraphFormat().getPageBreakBefore())
	            para.getParagraphFormat().setPageBreakBefore(false);

	        // Check all runs in the paragraph for page breaks and remove them.
	        for (Run run : (Iterable<Run>) para.getRuns())
	        {
	            if (run.getText().contains(ControlChar.PAGE_BREAK))
	                run.setText(run.getText().replace(ControlChar.PAGE_BREAK, ""));
	        }

	    }
	    return doc;

	}
	
	
	public static void generateDocument(String templatePath,
			DocumentGenerationBean data, String documentPath)
			throws IOException, DocumentGenerationException {

		if (templatePath == null || templatePath.length() < 1) {
			throw new IOException("Template path parameter is missing.");
		}

		if (documentPath == null || documentPath.length() < 1) {
			throw new IOException("Document path parameter is missing.");
		}

		if (data == null) {
			throw new IOException("Data parameter is missing.");
		}

		FileInputStream fis = null;
		ZipInputStream zis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		try {
			String compiledTemplatePath = templatePath.substring(0,
					templatePath.lastIndexOf(".docx")) + ".cmt";

			File templateFile = new File(FILE_STORE_ROOT_PATH + templatePath);
			File compiledTemplateFile = new File(FILE_STORE_ROOT_PATH
					+ compiledTemplatePath);
			if (!compiledTemplateFile.exists()
					|| templateFile.lastModified() > compiledTemplateFile
							.lastModified()) {
				compileTemplate(templatePath, compiledTemplatePath);
			}

			fis = new FileInputStream(compiledTemplateFile);
			zis = new ZipInputStream(fis);

			String documentFullPath = FILE_STORE_ROOT_PATH + documentPath;

			File documentFile = new File(documentFullPath);

			// create all non exists folders else you will hit
			// FileNotFoundException
			new File(documentFile.getParent()).mkdirs();

			fos = new FileOutputStream(documentFile);
			zos = new ZipOutputStream(fos);

			zos.setLevel(Deflater.BEST_COMPRESSION);

			DocumentGenerationUtil dgu = new DocumentGenerationUtil();
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {

				ZipEntry newEntry = new ZipEntry(ze.getName());
				zos.putNextEntry(newEntry);

				if (ze.getName().endsWith(".xml")) {

					byte[] buf = new byte[8192];
					StringBuffer subIntoThis = new StringBuffer();
					int i = 0;
					while ((i = zis.read(buf, 0, 8192)) > 0) {
						String inStr = new String(buf, 0, i, "UTF-8");
						subIntoThis.append(inStr);
					}
					dgu.generate(subIntoThis.toString(), zos, data);

				} else {
					int i = -1;
					while ((i = zis.read()) != -1) {
						zos.write(i);
					}
				}
				zos.flush();
				zos.closeEntry();
			}
		} catch (DocumentGenerationException dge) {
			throw dge;
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (zos != null) {
				zos.flush();
				zos.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (zis != null) {
				zis.close();
			}
			if (fis != null) {
				fis.close();
			}
		}

	}

	public static void compileTemplate(String templatePath, String documentPath)
			throws IOException, DocumentGenerationException {

		if (templatePath == null || templatePath.length() < 1) {
			throw new IOException("Template path parameter is missing.");
		}

		if (documentPath == null || documentPath.length() < 1) {
			throw new IOException("Document path parameter is missing.");
		}

		FileInputStream fis = null;
		ZipInputStream zis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		try {

			String templateFullPath = FILE_STORE_ROOT_PATH + templatePath;
			File templateFile = new File(templateFullPath);
			fis = new FileInputStream(templateFile);
			zis = new ZipInputStream(fis);

			String documentFullPath = FILE_STORE_ROOT_PATH + documentPath;
			File documentFile = new File(documentFullPath);
			fos = new FileOutputStream(documentFile);
			zos = new ZipOutputStream(fos);

			zos.setLevel(Deflater.BEST_COMPRESSION);

			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {

				ZipEntry newEntry = new ZipEntry(ze.getName());
				zos.putNextEntry(newEntry);

				if (ze.getName().endsWith(".xml")) {
					WordParser wp = new WordParser();
					wp.parseWordDoc(zis, zos, false, null);
				} else {
					int i = -1;
					while ((i = zis.read()) != -1) {
						zos.write(i);
					}
				}
				zos.flush();
				zos.closeEntry();
			}
		} catch (DocumentGenerationException dge) {
			throw dge;
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (zos != null) {
				zos.flush();
				zos.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (zis != null) {
				zis.close();
			}
			if (fis != null) {
				fis.close();
			}
		}

	}

	public static void createDocument(String documentPath, InputStream in)
			throws IOException {

		if (documentPath == null || documentPath.length() < 1) {
			throw new IOException("Document path parameter is missing.");
		}

		if (in == null) {
			throw new IOException("InputStream parameter is missing.");
		}

		String fullPath = FILE_STORE_ROOT_PATH + documentPath;
		File newFile = new File(fullPath);

		makeSureDirExist(newFile.getParentFile());

		if (!newFile.createNewFile()) {
			throw new IOException("Unable to create new document "
					+ documentPath + ". File already exists.");
		}

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(newFile);

			byte[] buffer = new byte[BUFFER_SIZE];
			int len;
			while ((len = in.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (fos != null) {
				fos.flush();
				fos.close();
			}
		}

	}
	
	
	
	public static void createTemplate(String fileName, InputStream in)
			throws IOException {

		if (fileName == null || fileName.length() < 1) {
			throw new IOException("Document path parameter is missing.");
		}
		
		if (!fileName.endsWith(".DOCX") && !fileName.endsWith(".docx")) {
			throw new IOException("Only MICROSOFT DOCX files are acceptable.");
		}

		if (in == null) {
			throw new IOException("InputStream parameter is missing.");
		}

		String fullPath = FILE_STORE_ROOT_PATH + "/Templates/" + fileName;
		File newFile = new File(fullPath);

		makeSureDirExist(newFile.getParentFile());

		if (!newFile.createNewFile()) {	
			if(newFile.delete())
			{
				newFile.createNewFile();
			}
			//throw new IOException("Unable to create new document "
			//		+ fileName + ". File already exists.");
		}

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(newFile);

			byte[] buffer = new byte[BUFFER_SIZE];
			int len;
			while ((len = in.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (fos != null) {
				fos.flush();
				fos.close();
			}
		}		
		
	}

	private static void makeSureDirExist(File parentDir) {
		if (!parentDir.exists())
			parentDir.mkdirs();
	}

	public static InputStream getDocumentAsStream(String documentPath)
			throws IOException {

		if (documentPath == null || documentPath.length() < 1) {
			throw new IOException("Document path parameter is missing.");
		}

		String fullPath = FILE_STORE_ROOT_PATH + documentPath;
		File docFile = new File(fullPath);

		makeSureDirExist(docFile.getParentFile());
		
		FileInputStream fis = new FileInputStream(docFile);

		return fis;

	}

	public static OutputStream createDocumentStream(String documentPath)
			throws IOException {

		if (documentPath == null || documentPath.length() < 1) {
			throw new IOException("Document path parameter is missing.");
		}

		String fullPath = FILE_STORE_ROOT_PATH + documentPath;
		File docFile = new File(fullPath);

		makeSureDirExist(docFile.getParentFile());

		FileOutputStream fos = new FileOutputStream(docFile);

		return fos;

	}

	public static boolean canRead(String documentPath) throws IOException {

		if (documentPath == null || documentPath.length() < 1) {
			throw new IOException("Document path parameter is missing.");
		}

		String fullPath = FILE_STORE_ROOT_PATH + documentPath;
		File docFile = new File(fullPath);

		return docFile.canRead();

	}

	/*
	 * Creates a zip file from a directory or a file.
	 * 
	 * @param directoryToZip Name of the directory to zip relative to Stars2
	 * attachments root directory path.
	 * 
	 * @param zipFilePath File name and path of the new zip file relative to
	 * Stars2 attachments root directory path..
	 * 
	 * @param rootDirName Name of the root directory in the zip file. A null
	 * root directory name is acceptable,
	 */
	public static void createZipFile(String directoryToZip, String zipFilePath,
			String rootDirName) throws IOException {

		if (directoryToZip == null || directoryToZip.length() < 1) {
			throw new IOException("Directory to zip parameter is missing.");
		}

		if (zipFilePath == null || zipFilePath.length() < 1) {
			throw new IOException("Zip output file name parameter is missing.");
		}

		String rootName = rootDirName;
		if (rootName == null) {
			rootName = "";
		} else if (rootName.startsWith("/") || rootName.startsWith("\\")) {
			rootName = rootName.substring(1);
		}

		if (rootName.length() > 1 && !rootName.endsWith("/")) {
			rootName = rootName + "/";
		}

		String fullDirPath = FILE_STORE_ROOT_PATH + directoryToZip;
		String fullZipPath = FILE_STORE_ROOT_PATH + zipFilePath;

		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		try {

			File zipFile = new File(fullZipPath);
			zipFile.createNewFile();

			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);

			zos.setLevel(Deflater.BEST_COMPRESSION);

			File zipThisDir = new File(fullDirPath);

			addFileToZipFile(zos, zipThisDir, rootName);
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (zos != null) {
				zos.flush();
				zos.close();
			}
			if (fos != null) {
				fos.close();
			}
		}

	}

	private static void addFileToZipFile(ZipOutputStream zos, File thingToAdd,
			String parentName) throws IOException {
		if (thingToAdd.isDirectory()) {
			for (File dirEntry : thingToAdd.listFiles()) {

				if (dirEntry.isDirectory()) {
					addFileToZipFile(zos, dirEntry,
							parentName + dirEntry.getName() + "/");
				}

				else {
					zipOneFile(zos, dirEntry, parentName);
				} // END: else

			} // END: for (File dirEntry : thingToAdd.listFiles())
		} else {
			zipOneFile(zos, thingToAdd, parentName);
		}

	} // END: private static void addFileToZipFile(...)

	static void zipOneFile(ZipOutputStream zos, File file, String parentName)
			throws IOException {
		FileInputStream fis = null;
		try {

			ZipEntry ze = new ZipEntry(parentName + file.getName());
			zos.putNextEntry(ze);
			fis = new FileInputStream(file);

			int i = -1;
			while ((i = fis.read()) != -1) {
				zos.write(i);
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (zos != null) {
				zos.flush();
				zos.closeEntry();
			}
			if (fis != null) {
				fis.close();
			}
		}
	}

	public static void cleanTmpDirs(Integer daysOld) {

		if (daysOld == null) {
			throw new IllegalArgumentException(
					"cleanTmpDirs(daysOld) daysOld is null.");
		}

		try {

			String tmpPath = FILE_STORE_ROOT_PATH + File.separator + "tmp";
			File tmpDir = new File(tmpPath);
			for (File userTmp : tmpDir.listFiles()) {

				if (userTmp.isDirectory()) {
					cleanTmpDir(userTmp, daysOld);
				} else {
					removeDocument(File.separator + "tmp" + File.separator
							+ userTmp.getName());
				}

			}
		} catch (Exception e) {
			logger.error(
					"Exception caught while trying to clean tmp directories: "
							+ e.getMessage(), e);
		}

	}

	private static void cleanTmpDir(File dirToClean, Integer daysOld) {

		long saveIfAfter = Calendar.getInstance().getTimeInMillis()
				- (daysOld.longValue() * 24 * 3600 * 1000);

		try {

			for (File dirEntry : dirToClean.listFiles()) {

				if (dirEntry.isDirectory()) {
					cleanTmpDir(dirEntry, daysOld);
					if (dirEntry.listFiles() == null
							|| dirEntry.listFiles().length == 0) {
						String basePath = dirEntry.getAbsolutePath().substring(
								FILE_STORE_ROOT_PATH.length());
						rmDir(basePath);
					}
				} else if (dirEntry.isFile()
						&& dirEntry.lastModified() < saveIfAfter) {
					String basePath = dirEntry.getAbsolutePath().substring(
							FILE_STORE_ROOT_PATH.length());
					removeDocument(basePath);
				}

			}
		} catch (Exception e) {
			logger.error("Exception caught while trying to clean tmp directories: "
					+ e.getMessage());
		}

	}
	
	public static void mergeCellsHorizontally(Cell c1, Cell c2) {
		c1.getCellFormat().setVerticalMerge(CellMerge.FIRST);
		c2.getChildNodes();
		for(Node n : (Iterable<Node>) c2.getChildNodes()) {
			c1.appendChild(n);
		}
		c2.getCellFormat().setVerticalMerge(CellMerge.PREVIOUS);
	}
	
	public static Run createNewRun(Document doc, String text, Font font) {
		Run aRun = new Run(doc, text);
		try {
			aRun.getFont().setName(font.getName());
			aRun.getFont().setBold(font.getBold());
			aRun.getFont().setSize(font.getSize());
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return aRun;
	}
	
	public static Cell createNewCell(Document doc, String text, Font font, int alignment, double percentWidth) {
		Cell aCell = new Cell(doc);
		Run aRun = createNewRun(doc, text, font);
		
		aCell.appendChild(new Paragraph(doc));
		aCell.getFirstParagraph().appendChild(aRun);
		aCell.getFirstParagraph().getParagraphFormat().setAlignment(alignment);
		aCell.getCellFormat().setPreferredWidth(PreferredWidth.fromPercent(percentWidth));
		
		return aCell;
	}
	
	public static Row createNewTwoCellRow(Document doc, Cell cell1, Cell cell2, boolean mergeCells) {
		Row aRow = new Row(doc);
		
		if(mergeCells) {
			mergeCellsHorizontally(cell1, cell2);
			aRow.appendChild(cell1);
		} else {
			aRow.appendChild(cell1);
			aRow.appendChild(cell2);
		}
		
		return aRow;
	}
	
	/**
	 * Returns the table at the given index
	 * 
	 * @param doc
	 * @param index
	 * 
	 * @return Table at the given index if exists otherwise null
	 */
	public static Table getTable(Document doc, int index) {
		return (null != doc) ? (Table)doc.getChild(NodeType.TABLE, index, true) : null;
	}
	
	public static Document getDocument(String documentPath) {
		String documentFullPath = FILE_STORE_ROOT_PATH + documentPath;
		Document doc = null;
    	
    	try{
			doc = new Document(documentFullPath);
    	}catch(Exception e) {	
			logger.error(e.getMessage(), e);
		} 
    	
    	return doc;
	}
	
	public static boolean saveDocument(Document doc, String documentPath) {
		boolean ret  = false;
		String documentFullPath = FILE_STORE_ROOT_PATH + documentPath;
		try{
			doc.save(documentFullPath);
			ret = true;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return ret;
	}
	
	public static Document mergeDocuments(Document srcDoc, Document destDoc, int importFormatMode) {
		try {
			// append destination document to the source document using the input format
			srcDoc.appendDocument(destDoc, importFormatMode);
		} catch(Exception e) {
			DisplayUtil.displayError("Error occured while merging documents. " + e.getMessage());
			logger.error(e.getMessage(), e);
		}
		
		return srcDoc;
	}
	
	/**
	 * Creates a new row using the supplied array of cell as columns
	 * @param doc
	 * @param cell
	 * @return Row
	 * @throws none
	 */
	public static Row createRow(Document doc, Cell[] cell) {
		Row aRow = new Row(doc);
		for(Cell acell : cell) {
			aRow.appendChild(acell);
		}
		return aRow;
	}
	
	public static boolean isFileExists(final String path) {
		boolean exists = false;

		if (!Utility.isNullOrEmpty(path)) {

			File file = new File(getFileStoreRootPath() + path);

			exists = file.exists();
		}

		return exists;
	}
	
	public static boolean isValidFileExtension(String fileName){
		if (fileName == null || fileName.length() == 0 || getFileExtension(fileName).length() == 0){
			return false;
		}
		boolean ret = false;
		String fileExtension = getFileExtension(fileName);
		List<SelectItem> items = FileExtensionDef.getData().getItems().getCurrentItems();
		for (SelectItem i : items){
			if (fileExtension.equalsIgnoreCase(i.getLabel())){
				ret = true;
			}
		}
		return ret;
	}
	
	public static boolean isValidFileExtension(UploadedFile file){
		if (file == null || file.getFilename() == null){
			return false;
		} 
		boolean ret = false;
		List<SelectItem> items = FileExtensionDef.getData().getItems().getCurrentItems();
		String fileExtension = getFileExtension(file.getFilename());
		
		for (SelectItem i : items){
			if (fileExtension.equalsIgnoreCase(i.getLabel())){
				ret = true;
			}
		}
		return ret;
	}
	
	public static String invalidFileExtensionMessage(String fileLabel){
		String ret;
		if (fileLabel == null){
			ret = "The uploaded file extension is invalid. The uploaded file extension must be one of the following: ";
		} else {
			ret = "The uploaded " + fileLabel + " file extension is invalid. The uploaded file extension must be one of the following: ";
		}
		List<SelectItem> items = FileExtensionDef.getData().getItems().getCurrentItems();
		for (SelectItem i : items){
			ret = ret.concat(i.getLabel().toLowerCase() + ", ");
		}
		ret = ret.substring(0, ret.length()-2);
		return ret.concat(".");
	}
	

}

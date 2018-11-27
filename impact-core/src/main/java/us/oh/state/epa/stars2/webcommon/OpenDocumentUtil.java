package us.oh.state.epa.stars2.webcommon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.util.Pair;

public class OpenDocumentUtil {

    /*
     * Size of buffer used when downloading document
     */
    private static final int BUFFER_SIZE = 4096;
    
    public static void downloadDocument(String path) throws IOException {
    	downloadDocument(path, null);
    }
    
    public static void downloadDocument(String path, String contentDisposition) throws IOException {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response
            = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                
        response.setContentType(getDocumentMimeType(path));
        
        if (!Utility.isNullOrEmpty(contentDisposition)) {
        	response.setHeader("Content-disposition", contentDisposition);
        }
        
        OutputStream os = response.getOutputStream();
        writeFileStream(os, path);
        
        facesContext.responseComplete();
    }

	private static void writeFileStream(OutputStream os, String path)
			throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        InputStream fis = DocumentUtil.getDocumentAsStream(path);
        
        while ((len = fis.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
        fis.close();
        os.flush();
	}

    public static String getDocumentMimeType(String path) {
        String extension = DocumentUtil.getFileExtension(path);

        if (extension.equalsIgnoreCase("htm")) {
            return "text/html";
        } else if (extension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        } else if (extension.equalsIgnoreCase("cdr")) {
            return "image/cdr";
        } else if (extension.equalsIgnoreCase("cgm")) {
            return "image/cgm";
        } else if (extension.equalsIgnoreCase("css")) {
            return "text/css";
        } else if (extension.equalsIgnoreCase("dbf")) {
            return "application/dbase";
        } else if (extension.equalsIgnoreCase("doc")) {
            return "application/msword";
        } else if (extension.equalsIgnoreCase("docm")) {
            return "application/vnd.ms-word.document.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (extension.equalsIgnoreCase("dotm")) {
            return "application/vnd.ms-word.template.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("dotx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.template";
        } else if (extension.equalsIgnoreCase("dwf")) {
            return "model/vnd.dwf";
        } else if (extension.equalsIgnoreCase("dwg")) {
            return "image/x-dwg";
        } else if (extension.equalsIgnoreCase("dxf")) {
            return "image/x-dwg";
        } else if (extension.equalsIgnoreCase("eml")) {
            return "message/rfc822";
        } else if (extension.equalsIgnoreCase("eps")) {
            return "application/postscript";
        } else if (extension.equalsIgnoreCase("frm")) {
            return "application/x-wpwin";
        } else if (extension.equalsIgnoreCase("html")) {
            return "text/html";
        } else if (extension.equalsIgnoreCase("ico")) {
            return "image/x-icon";
        } else if (extension.equalsIgnoreCase("mdb")) {
            return "application/vnd.ms-access";
        } else if (extension.equalsIgnoreCase("mif")) {
            return "application/x-mif";
        } else if (extension.equalsIgnoreCase("mp3")) {
            return "audio/mpeg3";
        } else if (extension.equalsIgnoreCase("mpp")) {
            return "application/vnd.ms-project";
        } else if (extension.equalsIgnoreCase("pcl")) {
            return "application/x-pcl";
        } else if (extension.equalsIgnoreCase("pct")) {
            return "image/x-pict";
        } else if (extension.equalsIgnoreCase("pcx")) {
            return "image/x-pcx";
        } else if (extension.equalsIgnoreCase("pic")) {
            return "image/pict";
        } else if (extension.equalsIgnoreCase("pict")) {
            return "image/pict";
        } else if (extension.equalsIgnoreCase("png")) {
            return "image/png";
        } else if (extension.equalsIgnoreCase("potm")) {
            return "application/vnd.ms-powerpoint.template.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("potx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.template";
        } else if (extension.equalsIgnoreCase("ppam")) {
            return "application/vnd.ms-powerpoint.addin.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("ppsm")) {
            return "application/vnd.ms-powerpoint.slideshow.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("ppsx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.slideshow";
        } else if (extension.equalsIgnoreCase("ppt")) {
            return "application/ms-powerpoint";
        } else if (extension.equalsIgnoreCase("pptm")) {
            return "application/vnd.ms-powerpoint.presentation.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("pptx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else if (extension.equalsIgnoreCase("pre")) {
            return "application/x-freelance";
        } else if (extension.equalsIgnoreCase("prz")) {
            return "application/x-freelance";
        } else if (extension.equalsIgnoreCase("rtf")) {
            return "application/rtf";
        } else if (extension.equalsIgnoreCase("svf")) {
            return "image/x-dwg";
        } else if (extension.equalsIgnoreCase("tcw")) {
            return "application/x-tcw";
        } else if (extension.equalsIgnoreCase("tif")) {
            return "image/tiff";
        } else if (extension.equalsIgnoreCase("tiff")) {
            return "image/tiff";
        } else if (extension.equalsIgnoreCase("txt")) {
            return "application/octet-stream";
        } else if (extension.equalsIgnoreCase("vsd")) {
            return "application/x-visio";
        } else if (extension.equalsIgnoreCase("vst")) {
            return "application/x-visio";
        } else if (extension.equalsIgnoreCase("vsw")) {
            return "application/x-visio";
        } else if (extension.equalsIgnoreCase("w60")) {
            return "application/wordperfect6.0";
        } else if (extension.equalsIgnoreCase("w61")) {
            return "application/wordperfect6.1";
        } else if (extension.equalsIgnoreCase("w6w")) {
            return "application/msword";
        } else if (extension.equalsIgnoreCase("wb1")) {
            return "application/x-quattropro";
        } else if (extension.equalsIgnoreCase("wb2")) {
            return "application/x-quattropro";
        } else if (extension.equalsIgnoreCase("wb3")) {
            return "application/x-quattropro";
        } else if (extension.equalsIgnoreCase("wk1")) {
            return "application/x-123";
        } else if (extension.equalsIgnoreCase("wk2")) {
            return "application/x-123";
        } else if (extension.equalsIgnoreCase("wk3")) {
            return "application/x-123";
        } else if (extension.equalsIgnoreCase("wk4")) {
            return "application/x-123";
        } else if (extension.equalsIgnoreCase("wmf")) {
            return "windows/metafile";
        } else if (extension.equalsIgnoreCase("wp")) {
            return "application/wordperfect";
        } else if (extension.equalsIgnoreCase("wp5")) {
            return "application/wordperfect";
        } else if (extension.equalsIgnoreCase("wp6")) {
            return "application/wordperfect";
        } else if (extension.equalsIgnoreCase("wpd")) {
            return "application/x-wpwin";
        } else if (extension.equalsIgnoreCase("wpg")) {
            return "image/wpg";
        } else if (extension.equalsIgnoreCase("wq1")) {
            return "application/x-lotus";
        } else if (extension.equalsIgnoreCase("wri")) {
            return "application/mswrite";
        } else if (extension.equalsIgnoreCase("xlam")) {
            return "application/vnd.ms-excel.addin.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("xls")) {
            return "application/ms-excel";
        } else if (extension.equalsIgnoreCase("xlsb")) {
            return "application/vnd.ms-excel.sheet.binary.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("xlsm")) {
            return "application/vnd.ms-excel.sheet.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (extension.equalsIgnoreCase("xltm")) {
            return "application/vnd.ms-excel.template.macroEnabled.12";
        } else if (extension.equalsIgnoreCase("xltx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.template";
        } else if (extension.equalsIgnoreCase("xwd")) {
            return "image/x-xwindowdump";
        } else if(extension.equalsIgnoreCase("pdf")){
        	return "application/pdf";
        } else if(extension.equalsIgnoreCase("zip")){
        	return "application/x-compressed";
        } else {
            return "text/plain";
        }
    }

    public static String printDocuments(List<Document> docs, int userID)
            throws ServiceFactoryException, RemoteException {
        List<Pair<String, String>> printAttribute = new ArrayList<Pair<String, String>>();

        for (Document doc : docs) {
            String extension = DocumentUtil.getFileExtension(doc.getPath());
            String docURL = DocumentUtil.getFileStoreBaseURL() + doc.getPath();
            printAttribute.add(new Pair<String, String>(extension, docURL));
        }

        return "printDocs";
    }
}

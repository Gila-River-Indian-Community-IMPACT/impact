package us.oh.state.epa.stars2.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.HttpOutputStream;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;

import org.apache.log4j.Logger;

public class WebDAVOperation {
    private static Logger logger = Logger.getLogger(WebDAVOperation.class);
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private HTTPResponse davResponse;
    private int documentID;
    private String filePath;
    private int tokenID;
    private int userID;
    private List<NVPair> requestHeaders;
    private boolean hasDAVLockToken;
    private int statusCode;

    public WebDAVOperation(HttpServletRequest request,
            HttpServletResponse response) {
        httpRequest = request;
        httpResponse = response;

        documentID = -1;
        tokenID = -1;
        userID = -1;
        filePath = null;

        String servletPath = httpRequest.getPathInfo();
        if (servletPath == null) {
            servletPath = "/";
        }
        /*
         * int pos;
         * 
         * if ((pos = servletPath.indexOf('?')) >= 0) { String paramsStr =
         * httpRequest.getRequestURL().substring(pos + 1); }
         */
        filePath = servletPath;

        hasDAVLockToken = false;
        requestHeaders = new ArrayList<NVPair>();
        Enumeration<?> hdrNames = httpRequest.getHeaderNames();
        while (hdrNames.hasMoreElements()) {
            String hdrName = (String) hdrNames.nextElement();
            String hdrValue = httpRequest.getHeader(hdrName);
            if (hdrName.equalsIgnoreCase("Host")) {
                continue;
            }
            if (hdrName.equalsIgnoreCase("If")
                    && hdrValue.indexOf("opaquelocktoken") >= 0) {
                hasDAVLockToken = true;
            }
            requestHeaders.add(new NVPair(hdrName, hdrValue));
        }
    }

    public final String getDAVMethod() {
        return httpRequest.getMethod();
    }

    public final int getDocumentID() {
        return documentID;
    }

    public final int getTokenID() {
        return tokenID;
    }

    public final int getUserID() {
        return userID;
    }

    public final String getFilePath() {
        return filePath;
    }

    public final boolean getHasDAVLockToken() {
        return hasDAVLockToken;
    }

    public final int getStatus() {
        return statusCode;
    }

    /*
     * private void parseURLParameters(String paramStr) { StringTokenizer st =
     * new StringTokenizer(paramStr, ","); while (st.hasMoreTokens()) { String
     * nvPairStr = st.nextToken(); int pos = nvPairStr.indexOf("="); if (pos <
     * 0) { continue; } String paramName = nvPairStr.substring(0, pos); String
     * paramValue = nvPairStr.substring(pos + 1); if
     * (paramName.equals("tokenID")) { tokenID = Integer.parseInt(paramValue); }
     * else if (paramName.equals("documentID")) { documentID =
     * Integer.parseInt(paramValue); } else if (paramName.equals("userID")) {
     * userID = Integer.parseInt(paramValue); } } }
     */
    public final void sendRequestToWebDAV(HTTPConnection conn, String davFilePath)
            throws IOException, ModuleException {
        HttpOutputStream out = new HttpOutputStream();
        davResponse = conn.ExtensionMethod(httpRequest.getMethod(),
                davFilePath, out, requestHeaders.toArray(new NVPair[0]));

        byte[] buf = new byte[4096];
        InputStream in = httpRequest.getInputStream();
        int len;
        while ((len = in.read(buf, 0, buf.length)) >= 0) {
            out.write(buf, 0, len);
        }
        out.close();

        statusCode = davResponse.getStatusCode();
    }

    public final void sendResponseToClient() throws IOException, ModuleException {
        httpResponse.setStatus(statusCode);
        Enumeration<?> hdrNames = davResponse.listHeaders();
        while (hdrNames.hasMoreElements()) {
            String hdrName = (String) hdrNames.nextElement();
            String hdrValue = davResponse.getHeader(hdrName);
            logger.debug("Name = " + hdrName + ", value = " + hdrValue);
            httpResponse.addHeader(hdrName, hdrValue);
        }

        OutputStream out = httpResponse.getOutputStream();
        InputStream in = davResponse.getInputStream();
        int len;
        byte[] buf = new byte[4096];
        while ((len = in.read(buf, 0, buf.length)) >= 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.flush();
    }
}


package us.oh.state.epa.stars2.servlet;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.OpenDocumentUtil;

@SuppressWarnings("serial")
public class DocumentServlet extends HttpServlet {
    private String epaPortal;
    
    /** Size of buffer used when downloading document. */
    private static final int BUFFER_SIZE = 8192;

    private static Logger logger = Logger.getLogger(DocumentServlet.class);

    public DocumentServlet() {
        epaPortal = Config.findNode("app.defaultEPAPortal").getText();
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, java.io.IOException {
        
        HttpSession tempSession = req.getSession(false);
        
        if (tempSession != null) {
            MyTasks myTask = (MyTasks) tempSession.getAttribute("myTasks");

            if ((myTask != null) && (myTask.getFacility() != null) && 
            		(req.getRequestURL().toString().contains(myTask.getFacility().getFacilityId()))) {
                logger.debug("DocumentServlet request URL " + req.getRequestURL());

                String contextPath = req.getContextPath();
                String pathInfo = req.getPathInfo();

                logger.debug("User request for contextPath = " + contextPath + ", pathInfo = " + pathInfo);

                resp.setContentType(OpenDocumentUtil.getDocumentMimeType(pathInfo));

                OutputStream os = resp.getOutputStream();
                InputStream fis = DocumentUtil.getDocumentAsStream(pathInfo);

                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
                fis.close();
                os.flush();
                resp.flushBuffer();
            } else {
                if (myTask == null) {
                    logger.error("MyTask is null, can't retrieve document");
                } else if (myTask.getFacility() == null) {
                    logger.error("Facility is null, can't retrieve document");                 
                } else if (!(req.getRequestURL().toString().contains(myTask.getFacility().getFacilityId()))) { 
                    logger.error("URL: " + req.getRequestURL().toString() + " doesn't contain FacilityId: " + myTask.getFacility().getFacilityId() + ", can't retrieve document");
                }
                logger.error("redirecting to '" + epaPortal + "'");
                resp.sendRedirect(epaPortal);                
            }
        } else {
            String pathInfoMsg = " - pathInfo not available: req is null";
            if (req != null) {
                pathInfoMsg = " - pathInfo = " + req.getPathInfo();
            }
            logger.error("Session is null, can't retrieve requested document " + pathInfoMsg + ". Redirecting to portal.");
            resp.sendRedirect(epaPortal);
        }
    }

    public void doPost (HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, java.io.IOException {

        doPost(req, resp);
    }

}

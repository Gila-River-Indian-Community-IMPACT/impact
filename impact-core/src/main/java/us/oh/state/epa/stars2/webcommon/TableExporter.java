package us.oh.state.epa.stars2.webcommon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import oracle.adf.view.faces.component.core.data.CoreColumn;
import oracle.adf.view.faces.component.core.data.CoreTable;
import oracle.adf.view.faces.model.CollectionModel;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class TableExporter implements java.io.Serializable {
    private transient Logger logger;
    private boolean timeStampFlag = false;
    private ExportType exportType;

    public TableExporter() {
        logger = Logger.getLogger(TableExporter.class);        
    }
    
    private static enum ExportType {
        HTML, EXCEL, HTML_HEAD
    }

    private String headerText;
    
    private void addChildComponents(List<UIComponent> childComponents, CoreColumn column) {
        /*
         * Does the column have child nodes?
         */
        if (column.getChildCount() > 0) {
            /*
             * Yes, process the children. See if any of them are columns
             */
            int subColumnCount = 0;
            Iterator<?> it = column.getChildren().iterator();
            while (it.hasNext()) {
                UIComponent comp = (UIComponent)it.next();
                if (comp instanceof CoreColumn && comp.isRendered()) {
                    subColumnCount++;
                    addChildComponents(childComponents, (CoreColumn)comp);
                } else if (comp instanceof CoreTable) {
                    subColumnCount++;
                	childComponents.add(comp);
                }
            }
            if (subColumnCount == 0) {
            	childComponents.add(column);
            }
        }
    }

    /*
     * Table export engine
     */
    private void exportTable(ActionEvent actionEvent, ExportType exportType)
            throws IOException {
        /*
         * Find the ADF table that contains the component that fired the event
         */
        UIComponent parent;

        parent = actionEvent.getComponent();

        while ((parent != null) && !(parent instanceof CoreTable)) {
            parent = parent.getParent();
        }

        if (parent != null) {
            CoreTable table = (CoreTable) parent;
            Collection<Object> data = getDataCollection(table.getValue());

            /*
             * Start export
             */
            if (data != null) {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext
                        .getExternalContext().getResponse();
                PrintStream os = new PrintStream(response.getOutputStream());

                switch (exportType) {
                case EXCEL:
                    response.setContentType("application/ms-excel");
                    response.setHeader("Content-Disposition", "attachment; filename="+FacesUtil.getCurrentPage()+".xls");
                    break;
                case HTML:
                case HTML_HEAD:
                    response.setContentType("text/html");
                    os.println("<html><body>");
                    break;
                default:
                    break;
                }
                if (exportType == TableExporter.ExportType.HTML) {
                    os.println("</body></html>");
                }
                this.exportType = exportType;
                addTable(table, data, facesContext, os);
                os.close();

                /*
                 * Short-circuit the JSF lifecycle
                 */
                facesContext.responseComplete();
            }
        }
        return;
    }
    
    private void addTable(CoreTable table, Collection<Object> data, FacesContext facesContext, PrintStream os) {
        os.println("<table border=1>");
        List<UIComponent> columns = addTableColumns(table, os);
        addTableData(table, columns, data, facesContext, os);
        os.println("</table>");
    }
    
    private List<UIComponent> addTableColumns(CoreTable table, PrintStream os) {
        /*
         * Export the headers
         */
        os.print("<thead>");
        // TODO figure this out
        if (exportType == TableExporter.ExportType.HTML_HEAD) {
            Date now = new Date();
            SimpleDateFormat dForm = new SimpleDateFormat();
            // should be ok if colspan value is bigger than number of columns
            String hdrText = "<tr><th colspan=\""
                    + (table.getChildren().size() + 1) + "\">" + headerText
                    + "</th><th align=\"right\">" + dForm.format(now)
                    + "</th></tr>";
            os.println(hdrText);
        }
        os.print("<tr>");

        Iterator<?> rootTableChildren = table.getChildren().iterator();
        List<UIComponent> columns = new ArrayList<UIComponent>();
        while (rootTableChildren.hasNext()) {
        	UIComponent comp = (UIComponent)rootTableChildren.next();
        	if (comp instanceof CoreColumn && comp.isRendered()) {
        		CoreColumn column = (CoreColumn) comp;
        		List<UIComponent> childComponents = new ArrayList<UIComponent>();
        		addChildComponents(childComponents, column);
        		if ( childComponents.size() > 0) {
        			for (UIComponent childComp : childComponents) {
        				if (childComp instanceof CoreColumn) {
	        				addColumn((CoreColumn)childComp, os);
	        				columns.add(childComp);
        				} else if (childComp instanceof CoreTable) {
        					UIComponent parent = childComp.getParent();
        					while (!(parent instanceof CoreColumn)) {
        						parent = parent.getParent();
        					}
        					if (parent instanceof CoreColumn) {
        						addColumn((CoreColumn)parent, os);
        					}
        					columns.add(childComp);
        				}
        			}
        		} else {
        			addColumn(column, os);
        			columns.add(column);
        		}
        	} else if (comp instanceof CoreTable) {
        		columns.add((UIComponent)comp);
        	}
        }
        os.println("</tr></thead>");
        return columns;
    }
    
    @SuppressWarnings("unchecked")
	private void addTableData(CoreTable table, List<UIComponent> columns, Collection<Object> data, FacesContext facesContext, PrintStream os) {

        /*
         * Export the data. For each row, add a variable pointing to the
         * row data to the request context, then retrieve the value of
         * the exportable component in each column.
         */
        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
        Iterator<Object> it = data.iterator();
        os.println("<tbody>");
        while (it.hasNext()) {
            Object obj = it.next();
            requestMap.put(table.getVar(), obj);

            os.print("<tr>");
            for (UIComponent column : columns) {
            	if (column instanceof CoreColumn) {
	                os.print("<td>");
	                /*
	                 * Excel and HTML output looks better with something in
	                 * each cell (no empty cells)
	                 */
	                String pText = new String(FacesUtil
	                        .getComponentText(column, timeStampFlag));
	                if(pText == null || pText.trim().length() == 0){
	                	pText = "&nbsp;" + pText;
	                } else {
	                    // replace '<' and '>' since they cause problems
	                    // with exporting to Excel
	                    pText = pText.replaceAll("<", "&lt;");
	                    pText = pText.replaceAll(">", "&gt;");
	                }
	                os.print(pText);
	                os.print("</td>");
            	} else if (column instanceof CoreTable) {
            		os.println();
            		os.print("<td>");
            		CoreTable childTable = (CoreTable)column;
                    Collection<Object> childData = getDataCollection(childTable.getValue());
                    addTable(childTable, childData, facesContext, os);
                    os.println("</td>");
            	}
            }
            os.println("</tr>");
        }

        /*
         * Remove the variable from the request context
         */
        requestMap.remove(table.getVar());

        /*
         * Finish off the export
         */
        os.println("</tbody>");
    }
        
    private void addColumn(CoreColumn column, PrintStream os) {
        UIComponent myParent;
        StringBuffer ancestorHeader = new StringBuffer();
        for (myParent = column.getParent(); myParent != null; myParent = myParent
                .getParent()) {

            if (myParent instanceof CoreColumn) {
                String hText = ((CoreColumn) myParent)
                        .getHeaderText();
                if (hText == null) {
                    UIComponent headerFacet = myParent
                            .getFacet("header");
                    if (headerFacet != null) {
                        hText = FacesUtil
                                .getComponentText(headerFacet, timeStampFlag);
                    }
                }
                if (hText != null) {
                    String insText = hText + " "; // Need space
                    // for
                    // readability
                    ancestorHeader.insert(0, insText);
                }

            }
        }

        String headerText = column.getHeaderText();
        if (headerText == null) {
            UIComponent headerFacet = column.getFacet("header");
            if (headerFacet != null) {
                headerText = FacesUtil
                        .getComponentText(headerFacet, timeStampFlag);
            }
        }
        os.print("<th>" + ancestorHeader + headerText + "</th>");
    }
    
    @SuppressWarnings("unchecked")
	private Collection<Object> getDataCollection(Object dataObj) {
    	Collection<Object> data = null;
        if (dataObj instanceof Object[]) {
            Object[] dataObjArray = (Object[]) dataObj;
            data = new ArrayList<Object>(dataObjArray.length);
            for (Object obj : dataObjArray) {
                data.add(obj);
            }
        } else if (dataObj instanceof Collection<?>) {
            data = (Collection<Object>) dataObj;
        } else if (dataObj instanceof CollectionModel) {
            dataObj = ((CollectionModel) dataObj).getWrappedData();
            data = getDataCollection(dataObj);
        } else {
            logger.error("Unknown type in requested for export");
        }
        return data;
    }

    /*
     * Handles an exception
     */
    private void handleException(Exception ex) {
        HttpServletResponse response = (HttpServletResponse) FacesContext
                .getCurrentInstance().getExternalContext().getResponse();
        response.setStatus(500);
        FacesContext.getCurrentInstance().responseComplete();

        return;
    }

    /*
     * Exports a table to an HTML page
     */
    public final void printTable(ActionEvent actionEvent) {
        try {
            exportTable(actionEvent, TableExporter.ExportType.HTML);
        } catch (IOException ex) {
            handleException(ex);
        }

        return;
    }

    public final void removeCloseValidationDialogSession(ActionEvent actionEvent) {
    	FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
    }
    
    /*
     * Exports a table to an Excel spreadsheet
     */
    public final void excelTable(ActionEvent actionEvent) {
        try {
            exportTable(actionEvent, TableExporter.ExportType.EXCEL);
        } catch (IOException ex) {
            handleException(ex);
        }

        return;
    }

    /*
     * Exports a table as a AQD manifest file
     */
    public final void printHeaderTable(ActionEvent actionEvent) {
        try {
            exportTable(actionEvent, TableExporter.ExportType.HTML_HEAD);
        } catch (IOException ex) {
            handleException(ex);
        }

        return;
    }

    /*
     * Returns the script to use for the onclick() handler
     */
    public final String getOnClickScript() {
    	timeStampFlag = false;
        return "document.forms[0].target = '_blank'; setTimeout('document.forms[0].target = \"_self\"', 0); return true;";
    }
    
    /*
     * Returns the script to use for the onclick() handler
     */
    public final String getOnClickScriptTS() {
    	timeStampFlag = true;
        return "document.forms[0].target = '_blank'; setTimeout('document.forms[0].target = \"_self\"', 0); return true;";
    }

    public final String getHeaderText() {
        return headerText;
    }

    public final void setHeaderText(String headerText) {
        this.headerText = headerText;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}

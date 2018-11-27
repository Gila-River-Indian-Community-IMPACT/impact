<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Printable Application Documents">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="false">
      <af:messages />
      <af:panelForm>

        <af:objectSpacer width="100%" height="15" />

            <af:commandButton text="Download/Print PDF of Compliance Report"  
                      	    disabled="#{complianceReport.editMode}" 
         					action="#{complianceReport.viewComplianceReportAsPDF}"  /> 

        <af:objectSpacer width="100%" height="20" />

        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Cancel" immediate="true">
              <af:returnActionListener />
            </af:commandButton>
          </af:panelButtonBar>
        </f:facet>

      </af:panelForm>
    </af:form>
  </af:document>
</f:view>


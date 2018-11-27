<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Printable EIS XML Documents">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />
      <af:panelForm>

        <af:objectSpacer width="100%" height="15" />

        <af:outputText value="#{usEpaEisReport.year} EIS XML Documents" />

         <af:objectSpacer height="10"/> 
         
        <af:goLink id="facilityDocumentLink"
          text="Facility Inventory XML"
          destination="#{usEpaEisReport.facilityXmlDocURL}"
          targetFrame="_blank" />
         
		<af:objectSpacer height="5"/>
		 
        <af:goLink id="eiDocumentLink"
          text="Emissions Inventory XML"
          destination="#{usEpaEisReport.EIXmlDocURL}"
          targetFrame="_blank" /> 
          
        <af:objectSpacer height="10"/>
          
        <af:outputText
          value="Select a link above to download the XML document." />

        <af:objectSpacer width="100%" height="20" />

        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Close" immediate="true">
              <af:returnActionListener />
            </af:commandButton>
          </af:panelButtonBar>
        </f:facet>

      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

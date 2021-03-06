<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Printable Facility Documents">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />
      <af:panelForm>

        <af:objectSpacer width="100%" height="15" />

        <af:outputText value="Facility Detail Documents" />

        <af:table id="pribtableDocumentsTable"
          value="#{facilityProfile.facProfDocuments}"
          bandingInterval="1" width="100%" banding="row"
          var="printableDocument">
          <af:column sortable="true" formatType="text"
            headerText="Document Description">
            <af:goLink id="documentLink"
              text="#{printableDocument.description}"
              destination="#{printableDocument.docURL}"
              targetFrame="_blank" />
          </af:column>
        </af:table>
        <af:outputText
          value="Select a link in the above table to download a 
        document to print." />

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

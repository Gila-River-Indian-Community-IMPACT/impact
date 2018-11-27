<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Printable Application Documents">
    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
    <af:form usesUpload="true">
      <af:messages />
      <af:panelForm>

        <af:objectSpacer width="100%" height="15" />

        <af:outputText value="Application Documents" />

        <af:table id="printableDocumentsTable"
          value="#{applicationDetail.printableDocumentList}"
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
        
        <af:objectSpacer width="100%" height="15" />
        
        <af:outputText value="Application Documents - Referenced EUs Only" 
        	rendered="#{applicationDetail.application.applicationTypeCD == 'PTIO'}"/>
        
        <af:table id="printableDocumentAppRefEUs"
          value="#{applicationDetail.printableDocumentAppRefEUs}"
          bandingInterval="1" width="100%" banding="row"
          var="printableDocumentAppRefEUs"
          rendered="#{applicationDetail.application.applicationTypeCD == 'PTIO'}">
          <af:column sortable="true" formatType="text"
            headerText="Document Description">
            <af:goLink id="documentLink"
              text="#{printableDocumentAppRefEUs.description}"
              destination="#{printableDocumentAppRefEUs.docURL}"
              targetFrame="_blank" />
          </af:column>
        </af:table>        
        
        <af:objectSpacer width="100%" height="15" />
        
        <af:outputText value="Attachments that are part of the Application:" 
          rendered="#{not empty applicationDetail.printableAttachmentList}"/>
        
        <af:table id="printableAttachmentsTable"
          value="#{applicationDetail.printableAttachmentList}"
          bandingInterval="1" width="100%" banding="row"
          var="printableAttachment" rendered="#{not empty applicationDetail.printableAttachmentList}">
          <af:column sortable="true" formatType="text"
            headerText="Document Description">
            <af:goLink id="documentLink"
              text="#{printableAttachment.description}"
              destination="#{printableAttachment.docURL}"
              targetFrame="_blank" />
          </af:column>
        </af:table>
        <af:outputText
          value="Select a link in the above table to download a 
        document to print." />

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


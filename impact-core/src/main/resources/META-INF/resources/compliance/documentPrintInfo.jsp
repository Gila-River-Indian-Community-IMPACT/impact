<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Printable Compliance Report Documents">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			

				<af:objectSpacer width="100%" height="15" />

				<af:outputText value="Compliance Report Documents" />

				<af:table id="pribtableDocumentsTable"
          value="#{complianceReport.printableDocumentList}"
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

				<af:outputText
					value="Attachments that are part of the Compliance Report"
					rendered="#{complianceReport.reportAttachments != null && !complianceReport.publicApp}" />

				<af:table id="pribtableAttachmentsTable"
					rendered="#{complianceReport.reportAttachments != null && !complianceReport.publicApp}"
					value="#{complianceReport.reportAttachments}" bandingInterval="1"
					width="100%" banding="row" var="printableDocument">
					<af:column sortable="true" formatType="text"
						headerText="Document Description">
						<af:goLink id="documentLink"
							text="#{printableDocument.description}"
							destination="#{printableDocument.docURL}" targetFrame="_blank" />
					</af:column>
				</af:table>
				<af:outputText
					value="Select a link in an above table to view or download a 
        document to print." rendered="#{!complianceReport.publicApp}"/>
        		<af:outputText
					value="Select a link in the above table to view or download a 
        document to print." rendered="#{complianceReport.publicApp}"/>

				<af:objectSpacer width="100%" height="20" />

			
			<afh:rowLayout halign="center">
				<af:panelButtonBar>
					<af:commandButton text="Close" immediate="true">
						<af:returnActionListener />
					</af:commandButton>
				</af:panelButtonBar>
			</afh:rowLayout>

		</af:form>
	</af:document>
</f:view>


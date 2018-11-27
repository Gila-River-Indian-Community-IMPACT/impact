<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:table value="#{attachments.attachmentList}" bandingInterval="1"
		width="98%" banding="row" var="report">

		<af:column sortable="true" sortProperty="documentID" noWrap="true"
			formatType="text" headerText="Attachment ID">
			<af:commandLink action="#{attachments.startEditAttachment}"
				useWindow="true" windowWidth="500" windowHeight="300"
				text="#{report.documentID}"
				rendered="#{attachments.updatePermitted}"
				shortDesc="Click to Delete the attachment or to Edit the attachment description">
				<t:updateActionListener property="#{attachments.document}"
					value="#{report}" />
				<t:updateActionListener property="#{attachments.locked}"
					value="false" />
			</af:commandLink>
			<af:outputText value="#{report.documentID}"
				rendered="#{!attachments.updatePermitted}" />
		</af:column>

		<af:column sortable="true" sortProperty="attachmentType"
			rendered="#{attachments.hasDocType}" formatType="text"
			headerText="Attachment Type">
			<af:goLink inlineStyle="clear:none" targetFrame="_blank"
				destination="#{report.docURL}"
				text="#{attachments.attachmentTypesDef.itemDesc[report.docTypeCd]}"
				shortDesc="Download document (.#{report.extension})" />
		</af:column>

		<af:column sortable="true" rendered="#{attachments.hasDocType}"
			sortProperty="description" formatType="text" headerText="Description">
			<af:outputText inlineStyle="clear:none" value="#{report.description}" />
		</af:column>

		<af:column sortable="true" rendered="#{!attachments.hasDocType}"
			sortProperty="description" formatType="text" headerText="Description">
			<af:goLink inlineStyle="clear:none" targetFrame="_blank"
				destination="#{report.docURL}" text="#{report.description}"
				shortDesc="Download document (#{report.extension})" />
		</af:column>

<%-- 		<af:column headerText="Trade Secret Document"
			rendered="#{attachments.tradeSecretSupported}" formatType="text">
			<af:goLink inlineStyle="clear:none" targetFrame="_blank"
				rendered="#{not empty report.tradeSecretDocId}"
				destination="#{report.tradeSecretDocURL}" text="Download"
				shortDesc="Download trade secret document" />
			<af:outputText rendered="#{empty report.tradeSecretDocId}"
				value="None Provided" />
		</af:column>

		<af:column headerText="Trade Secret Justification"
			rendered="#{attachments.tradeSecretSupported}" formatType="text">
			<af:outputText value="#{report.tradeSecretJustification}"
				truncateAt="50" 
				shortDesc="#{report.tradeSecretJustification}"/>
		</af:column> --%>

		<af:column sortable="true" sortProperty="lastModifiedBy"
			formatType="text" headerText="Uploaded By"
			rendered="#{attachments.internalApp}">
			<af:selectOneChoice readOnly="true" value="#{report.lastModifiedBy}">
				<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
			</af:selectOneChoice>
		</af:column>

		<af:column sortable="true" sortProperty="uploadDate" formatType="text"
			headerText="Upload Date"
			rendered="#{attachments.internalApp}">
			<af:selectInputDate readOnly="true" value="#{report.uploadDate}" />
		</af:column>

		<f:facet name="footer">
			<afh:rowLayout halign="center">
				<af:panelButtonBar>
					<af:commandButton text="Add" useWindow="true" windowWidth="800"
						windowHeight="300" 
						rendered="#{attachments.internalApp && !attachments.readOnlyUser}"
						action="#{attachments.startNewAttachment}"
						disabled="#{attachments.readOnlyUser}" />
					<af:commandButton actionListener="#{tableExporter.printTable}"
						onclick="#{tableExporter.onClickScript}" text="Printable view" />
					<af:commandButton actionListener="#{tableExporter.excelTable}"
						onclick="#{tableExporter.onClickScript}" text="Export to excel" />
				</af:panelButtonBar>
			</afh:rowLayout>
		</f:facet>
	</af:table>
	<af:outputText inlineStyle="font-size:75%;color:#666"
		rendered="#{attachments.updatePermitted}"
		value="To Delete the attachment, or to Edit attachment description, click in the Attachment ID column." />
</af:panelGroup>
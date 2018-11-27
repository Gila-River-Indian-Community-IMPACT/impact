<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="Attachments" disclosed="true">
		<af:outputText inlineStyle="font-size:75%;color:#666"
			value="#{attachments.subtitle}"></af:outputText>
		<af:table value="#{attachments.attachmentList}" bandingInterval="1"
			width="100%" banding="row" var="report">

			<af:column headerText="ID" inlineStyle="vertical-align: middle;"
				width="40">
				<af:outputText id="attachmentId" value="#{report.documentID}" />
			</af:column>

			<af:column sortable="true" sortProperty="attachmentType"
				rendered="#{attachments.hasDocType}" formatType="text"
				headerText="Attachment Type">
				<af:goLink inlineStyle="clear:none" targetFrame="_blank"
					destination="#{report.docURL}"
					disabled="#{relocation.editable || delegation.editable || applicationDetail.editMode}"
					text="#{attachments.attachmentTypesDef.itemDesc[report.docTypeCd]}"
					shortDesc="Download document (.#{report.extension})" />
			</af:column>

			<af:column sortable="true" rendered="#{attachments.hasDocType}"
				sortProperty="description" formatType="text"
				headerText="Description">
				<af:outputText inlineStyle="clear:none"
					value="#{report.description}" />
			</af:column>

			<af:column sortable="true" rendered="#{!attachments.hasDocType}"
				sortProperty="description" formatType="text"
				headerText="Description">
				<af:goLink inlineStyle="clear:none" targetFrame="_blank"
					destination="#{report.docURL}" text="#{report.description}"
					disabled="#{relocation.editable || delegation.editable || applicationDetail.editMode}"
					shortDesc="Download document (#{report.extension})" />
			</af:column>

			<f:facet name="footer">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton actionListener="#{tableExporter.printTable}"
							onclick="#{tableExporter.onClickScript}" text="Printable view" />
						<af:commandButton actionListener="#{tableExporter.excelTable}"
							onclick="#{tableExporter.onClickScript}" text="Export to excel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>
		</af:table>

	</af:showDetailHeader>

</af:panelGroup>
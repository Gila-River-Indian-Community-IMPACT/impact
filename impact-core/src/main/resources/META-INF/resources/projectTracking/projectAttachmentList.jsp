<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:showDetailHeader text="Attachments" disclosed="true">
	<afh:rowLayout halign="center" width="98%">
		<af:table width="98%" bandingInterval="1" banding="row"
			value="#{projectTrackingDetail.project.attachments}" var="attachment">
			
			<af:column width="8%" headerText="Attachment ID" sortable="true"
				sortProperty="documentID" formatType="text" noWrap="true">
				<af:commandLink useWindow="true" windowWidth="600"
					rendered="#{!projectTrackingDetail.readOnlyUser}"
					windowHeight="300" text="#{attachment.attachment.documentID}"
					shortDesc="Click to Delete the attachment or to Edit the attachment description"
					action="#{projectTrackingDetail.startToEditAttachment}">
					<t:updateActionListener
						property="#{projectTrackingDetail.projectAttachment}"
						value="#{attachment}" />
				</af:commandLink>
				<af:outputText inlineStyle="clear:none"
					rendered="#{projectTrackingDetail.readOnlyUser}"
					value="#{attachment.attachment.documentID}" />
			</af:column>

			<af:column width="15%" headerText="Attachment Type" sortable="true"
				sortProperty="docTypeCd" formatType="text">
				<af:goLink inlineStyle="clear:none" targetFrame="_blank"
					destination="#{attachment.attachment.docURL}"
					text="#{attachments.attachmentTypesDef.itemDesc[attachment.attachment.docTypeCd]}"
					shortDesc="Download document (.#{attachment.attachment.extension})" />
			</af:column>

			<af:column width="20%" headerText="Description" sortable="true"
				sortProperty="description" formatType="text">
				<af:outputText inlineStyle="clear:none"
					value="#{attachment.attachment.description}" />
			</af:column>

			<af:column width="10%" headerText="Tracking Event ID" sortable="true"
				sortProperty="trackingEventId" formatType="text">
				<af:commandLink useWindow="true" windowWidth="900"
					windowHeight="700" inlineStyle="padding-left:5px;"
					action="#{projectTrackingDetail.loadTrackingEvent}">
					<af:selectOneChoice readOnly="true"
						value="#{attachment.trackingEventId}" >
						<f:selectItems
							value="#{projectTrackingDetail.projectTrackingEventDefs}" />
					</af:selectOneChoice>
					<t:updateActionListener value="#{attachment.trackingEventId}"
						property="#{projectTrackingDetail.trackingEventId}" />
				</af:commandLink>
			</af:column>

			<af:column width="12%" headerText="Uploaded By" sortable="true"
				sortProperty="lastModifiedBy" formatType="text">
				<af:selectOneChoice readOnly="true"
					value="#{attachment.attachment.lastModifiedBy}">
					<f:selectItems value="#{infraDefs.allUsersDef.allItems}" />
				</af:selectOneChoice>
			</af:column>

			<af:column width="8%" headerText="Upload Date" sortable="true"
				 sortProperty="uploadDate" formatType="text">
				<af:selectInputDate readOnly="true"
					value="#{attachment.attachment.uploadDate}" />
			</af:column>

			<f:facet name="footer">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Add" useWindow="true" windowHeight="300"
							disabled="#{projectTrackingDetail.readOnlyUser}"
							windowWidth="600"
							action="#{projectTrackingDetail.startToAddAttachment}" />
						<af:commandButton actionListener="#{tableExporter.printTable}"
							onclick="#{tableExporter.onClickScript}" text="Printable view" />
						<af:commandButton actionListener="#{tableExporter.excelTable}"
							onclick="#{tableExporter.onClickScript}" text="Export to excel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>
		</af:table>
	</afh:rowLayout>
	<af:outputText inlineStyle="font-size:75%;color:#666"
		rendered="#{attachments.updatePermitted}"
		value="To Delete the attachment, or to Edit attachment description, click in the Attachment ID column." />
</af:showDetailHeader>


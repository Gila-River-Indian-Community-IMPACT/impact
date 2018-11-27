<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:showDetailHeader text="Tracking Events" disclosed="true">
	<afh:rowLayout halign="center" width="98%">
		<af:table id="trackingEvents" width="98%"
			value="#{projectTrackingDetail.project.trackingEvents}" var="event"
			bandingInterval="1" banding="row">

			<af:column id="eventNbr" headerText="Event ID" width="5%"
				formatType="text" sortable="true"
				sortProperty="eventNbr">
				<af:commandLink useWindow="true" windowWidth="900"
					windowHeight="700" inlineStyle="padding-left:5px;"
					action="#{projectTrackingDetail.startToViewTrackingEvent}">
					<af:inputText readOnly="true" valign="middle"
						value="#{event.eventNbr}" />
					<t:updateActionListener value="#{event}"
						property="#{projectTrackingDetail.trackingEvent}" />
				</af:commandLink>
			</af:column>

			<af:column id="eventDescription" headerText="Event Description"
				noWrap="false" width="15%" formatType="text" sortable="true"
				sortProperty="eventDescription">
				<af:outputText truncateAt="50" value="#{event.eventDescription}"
					shortDesc="#{event.eventDescription}" />
			</af:column>

			<af:column id="eventDate" headerText="Event Date" formatType="text"
				width="5%" sortable="true" sortProperty="eventDate">
				<af:selectInputDate readOnly="true" value="#{event.eventDate}">
					<af:convertDateTime pattern="MM/dd/yyyy" />
				</af:selectInputDate>
			</af:column>

			<af:column id="eventTypeCd" headerText="Event Type" formatType="text"
				width="10%" sortable="true" sortProperty="eventTypeDesc">
				<af:selectOneChoice readOnly="true" value="#{event.eventTypeCd}">
					<f:selectItems
						value="#{projectTrackingDetail.projectTrackingEventTypeDefs.items[
						empty event.eventTypeCd ? '' : event.eventTypeCd]}" />
				</af:selectOneChoice>
			</af:column>

			<af:column id="eventStatus" headerText="Event Status" noWrap="false"
				width="15%" formatType="text" sortable="true"
				sortProperty="eventStatus">
				<af:outputText truncateAt="50" value="#{event.eventStatus}" 
					shortDesc="#{event.eventStatus}" />
			</af:column>

			<af:column id="issuesToAddress" headerText="Issues To Address"
				noWrap="false" width="15%" formatType="text" sortable="true"
				sortProperty="issuesToAddress">
				<af:outputText truncateAt="50" value="#{event.issuesToAddress}" 
					shortDesc="#{event.issuesToAddress}" />
			</af:column>

			<af:column id="responseDueDate" headerText="Response Due Date"
				width="5%" formatType="text" sortable="true"
				sortProperty="responseDueDate">
				<af:selectInputDate readOnly="true" value="#{event.responseDueDate}">
					<af:convertDateTime pattern="MM/dd/yyyy" />
				</af:selectInputDate>
			</af:column>

			<af:column id="attachmentIds" headerText="Attachment ID(s)"
				formatType="text"
				noWrap="false" width="15%" sortable="false">
				<af:iterator value="#{event.associatedAttachmentsInfo}" var="attachment">
					<af:outputText value="#{attachment.documentId}" />
					<af:goLink
						targetFrame="_blank"
						destination="#{attachment.docURL}"
						text="(Download)" />
					<af:outputText value=" " />	
				</af:iterator>
			</af:column>

			<af:column id="addedByUserId" headerText="Added By" formatType="text"
				width="10%" sortable="true" sortProperty="addedByUserId">
				<af:selectOneChoice readOnly="true" value="#{event.addedByUserId}">
					<f:selectItems value="#{infraDefs.allActiveUsersDef.allItems}" />
				</af:selectOneChoice>
			</af:column>

			<f:facet name="footer">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Add Event" useWindow="true"
							disabled="#{projectTrackingDetail.readOnlyUser}"
							windowWidth="900" windowHeight="700"
							inlineStyle="padding-left:5px;"
							action="#{projectTrackingDetail.startToAddTrackingEvent}" />
						<af:commandButton actionListener="#{tableExporter.printTable}"
							onclick="#{tableExporter.onClickScript}" text="Printable view" />
						<af:commandButton actionListener="#{tableExporter.excelTable}"
							onclick="#{tableExporter.onClickScript}" text="Export to excel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>

		</af:table>
	</afh:rowLayout>
</af:showDetailHeader>

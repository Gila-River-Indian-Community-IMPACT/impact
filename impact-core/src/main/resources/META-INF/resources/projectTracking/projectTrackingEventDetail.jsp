<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Project Tracking Event Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
		<af:page id="projectTrackingEventDetail" var="foo" value="#{menuModel.model}">
			<af:panelHeader text="Project Tracking Event Detail" />
				
				<f:facet name="messages">
					<af:messages />
				</f:facet>

				<af:panelForm rows="1" maxColumns="1" labelWidth="250px">
				<af:inputText id="eventNbr" 
					label="Event ID: " readOnly="true"
					rendered="#{!projectTrackingDetail.trackingEvent.newObject}"
					value="#{projectTrackingDetail.trackingEvent.eventNbr}" />
					
				<af:inputText id="eventDescription" 
					label="Event Description: "	showRequired="true" 
					readOnly="#{!projectTrackingDetail.trackingEventEditMode}"
					maximumLength="1000" rows="5" columns="100"
					value="#{projectTrackingDetail.trackingEvent.eventDescription}" />

				<af:selectInputDate id="eventDate" 
					label="Event Date: " showRequired="true" 
					readOnly="#{!projectTrackingDetail.trackingEventEditMode}"
					value="#{projectTrackingDetail.trackingEvent.eventDate}">
					<af:convertDateTime pattern="MM/dd/yyyy" />
					<af:validateDateTimeRange maximum="#{infraDefs.currentDate}" />
				</af:selectInputDate>

				<af:selectOneChoice id="eventTypeCd" 
					label="Event Type: " showRequired="true" 
					readOnly="#{!projectTrackingDetail.trackingEventEditMode}"
					unselectedLabel=""
					value="#{projectTrackingDetail.trackingEvent.eventTypeCd}">
					<f:selectItems
						value="#{projectTrackingDetail.projectTrackingEventTypeDefs.items[
									empty projectTrackingDetail.trackingEvent.eventTypeCd 
									? '' 
									: projectTrackingDetail.trackingEvent.eventTypeCd]}" />
				</af:selectOneChoice>

				<af:inputText id="eventStatus" 
					label="Event Status: "
					readOnly="#{!projectTrackingDetail.trackingEventEditMode}"
					maximumLength="1000" rows="5" columns="100"
					value="#{projectTrackingDetail.trackingEvent.eventStatus}" />

				<af:inputText id="issuesToAddress" 
					label="Issues To Address: "
					readOnly="#{!projectTrackingDetail.trackingEventEditMode}"
					maximumLength="1000" rows="5" columns="100"
					value="#{projectTrackingDetail.trackingEvent.issuesToAddress}" />

				<af:selectInputDate id="responseDueDate" 
					label="Response Due Date: "
					readOnly="#{!projectTrackingDetail.trackingEventEditMode}"
					value="#{projectTrackingDetail.trackingEvent.responseDueDate}">
					<af:convertDateTime pattern="MM/dd/yyyy" />
				</af:selectInputDate>
			</af:panelForm>
			
			<afh:rowLayout halign="center">
				<af:switcher
					facetName="#{projectTrackingDetail.trackingEventEditMode ? 'edit' : 'readOnly'}"
					defaultFacet="readOnly">
					<f:facet name="readOnly">
						<af:panelButtonBar>
							<af:commandButton id="editBtn" text="Edit"
								disabled="#{projectTrackingDetail.readOnlyUser}"
								action="#{projectTrackingDetail.enterTrackingEventEditMode}" />
								<af:commandButton id="deleteBtn" text="Delete" useWindow="true"
									windowWidth="#{confirmWindow.width}"
									windowHeight="#{confirmWindow.height}"
									returnListener="#{projectTrackingDetail.deleteProjectTrackingEvent}"
									rendered="#{infraDefs.stars2Admin}"
									disabled="#{!projectTrackingDetail.allowedToDeleteTrackingEvent}"
									shortDesc="#{projectTrackingDetail.allowedToDeleteTrackingEvent 
													? '' : 'Tracking event is associated with one or more attachments' }"
									action="#{confirmWindow.confirm}">
									<t:updateActionListener property="#{confirmWindow.type}"
										value="#{confirmWindow.yesNo}" />
									<t:updateActionListener property="#{confirmWindow.message}"
										value="Click Yes to confirm the deletion of the project tracking event" />
								</af:commandButton>
								<af:commandButton id="closeBtn" text="Close"
								action="#{projectTrackingDetail.closeTrackingEventDialog}" />
						</af:panelButtonBar>
					</f:facet>
					<f:facet name="edit">
						<af:panelButtonBar>
							<af:commandButton id="saveBtn" text="Save" 
								action="#{projectTrackingDetail.saveProjectTrackingEvent}"/>
							<af:commandButton id="cancelBtn" text="Cancel"
								immediate="true"
								action="#{projectTrackingDetail.closeTrackingEventDialog}" />
						</af:panelButtonBar>
					</f:facet>
				</af:switcher>

			</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
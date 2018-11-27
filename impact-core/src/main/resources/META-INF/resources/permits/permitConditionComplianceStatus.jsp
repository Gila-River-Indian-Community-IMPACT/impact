<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="complianceStatusEventDetailBody" title="Compliance Status Event Detail">
		<f:verbatim>
     		<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
   		</f:verbatim>
    
		<%@ include file="../util/validate.js"%>
		
		<af:form>
			<af:messages />
			<af:panelHeader text="Compliance Status Event Detail" size="0" />
			<af:panelForm rows="9" maxColumns="1" labelWidth="150"
				partialTriggers="CSeventTypeCd " >

				<afh:rowLayout halign="left">
					<af:inputText maximumLength="30" label="Compliance Status ID : "
						rendered="#{!permitConditionDetail.editComplianceStatus}" readOnly="true"
						id="complianceStatusId" columns="30"
						value="#{permitConditionDetail.modifyComplianceStatusEvent.cStatusId}">
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:selectInputDate label="Event Date : "
						readOnly="#{!permitConditionDetail.editComplianceStatus}" id="eventDateId"
						showRequired="#{permitConditionDetail.editComplianceStatus}"
						value="#{permitConditionDetail.modifyComplianceStatusEvent.eventDate}">
						<af:validateDateTimeRange minimum="1900-01-01"
							maximum="#{permitConditionDetail.maxDate}" />
					</af:selectInputDate>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:selectOneChoice label="Event Type : " autoSubmit="true"
						readOnly="#{!permitConditionDetail.editComplianceStatus}"
						id="CSeventTypeCd" showRequired="#{permitConditionDetail.editComplianceStatus}"
						value="#{permitConditionDetail.modifyComplianceStatusEvent.eventTypeCd}"
						valueChangeListener="#{permitConditionDetail.eventTypeCdChanged }"
						unselectedLabel="">
						<f:selectItems id="complianceTrackingEventTypes" 
							value="#{permitReference.complianceTrackingEventTypeDefs.items[0]}" />
					</af:selectOneChoice>

					<af:selectOneChoice label="Reference : "
						value="#{permitConditionDetail.modifyComplianceStatusEvent.complianceReportReference}"
						rendered="#{permitConditionDetail.complianceReportEventType}"
						id="compliance_report_reference" unselectedLabel="" showRequired="true"
						readOnly="#{!permitConditionDetail.editComplianceStatus}" >
						<f:selectItems value="#{permitConditionDetail.complianceEventReferenceIdsForFacility}" />
					</af:selectOneChoice>
					<af:selectOneChoice label="Reference : "
						value="#{permitConditionDetail.modifyComplianceStatusEvent.inspectionReference}"
						rendered="#{permitConditionDetail.inspectionEventType}"
						id="inspection_reference" unselectedLabel="" showRequired="true"
						readOnly="#{!permitConditionDetail.editComplianceStatus}" >
						<f:selectItems value="#{permitConditionDetail.complianceEventReferenceIdsForFacility}" />
					</af:selectOneChoice>
					<af:selectOneChoice label="Reference : "
						value="#{permitConditionDetail.modifyComplianceStatusEvent.stackTestReference}"
						rendered="#{permitConditionDetail.stackTestEventType}"
						id="stack_reference" unselectedLabel="" showRequired="true"
						readOnly="#{!permitConditionDetail.editComplianceStatus}">
						<f:selectItems value="#{permitConditionDetail.complianceEventReferenceIdsForFacility}" />
					</af:selectOneChoice>

					<af:panelHorizontal valign="top" halign="left"
						inlineStyle="padding-left:10px;font-size:8pt;color:#cc0000;">
						<af:outputText id="referenceWarning"
							rendered="#{permitConditionDetail.complianceEventReferenceApplicable && permitConditionDetail.complianceEventReferenceIdsForFacilitySize == 0}"
							value="#{permitConditionDetail.referenceWarning }">
						</af:outputText>
					</af:panelHorizontal>
				</afh:rowLayout>
				
				<afh:rowLayout halign="left">
					<af:inputText maximumLength="500" label="Status : "
						readOnly="#{!permitConditionDetail.editComplianceStatus}"
						id="status"
						value="#{permitConditionDetail.modifyComplianceStatusEvent.status}" 
						columns="50" rows="10">
					</af:inputText>
				</afh:rowLayout>
				
				<af:objectSpacer height="20" />
				
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							rendered="#{!permitConditionDetail.editComplianceStatus 
										&& !permitConditionDetail.fromFacilityPermitConditionSearch
										&& !permitConditionDetail.readOnlyUser}"
							action="#{permitConditionDetail.editComplianceStatusEvent}" />
						<af:commandButton id="deleteBtn" text="Delete" 
							useWindow="true" 
							windowWidth="#{confirmWindow.width}" windowHeight="#{confirmWindow.height}"
							rendered="#{!permitConditionDetail.editComplianceStatus 
										&& !permitConditionDetail.fromFacilityPermitConditionSearch
										&& !permitConditionDetail.readOnlyUser}"
							returnListener="#{permitConditionDetail.deleteComplianceStatusEvent}"
							action="#{confirmWindow.confirm}">
							<t:updateActionListener property="#{confirmWindow.type}"
								value="#{confirmWindow.yesNo}" />
							<t:updateActionListener property="#{confirmWindow.message}"
								value="Click Yes to confirm the deletion of the compliance status event." />
						</af:commandButton>
						<af:commandButton text="Close" immediate="true"
							rendered="#{!permitConditionDetail.editComplianceStatus}"
							action="#{permitConditionDetail.closeComplianceStatusEventDialog}" />
						<af:commandButton text="Save" 
							rendered="#{permitConditionDetail.editComplianceStatus}"
							action="#{permitConditionDetail.saveComplianceStatusEvent}" />
						<af:commandButton text="Cancel" immediate="true"
							rendered="#{permitConditionDetail.editComplianceStatus}"
							action="#{permitConditionDetail.cancelComplianceStatusDialog}" />	
					</af:panelButtonBar>
				</afh:rowLayout>
				
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
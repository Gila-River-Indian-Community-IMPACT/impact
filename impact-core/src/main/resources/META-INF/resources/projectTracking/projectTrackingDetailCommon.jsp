<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2"
	labelWidth="250px" width="98%" >
	<af:inputText id="projectName"
		showRequired="true" 
		label="Project Name: " 
		readOnly="#{!projectTrackingDetail.editMode}"
		columns="100" maximumLength="250"
		value="#{projectTrackingDetail.project.projectName}" />
</af:panelForm>

<af:panelForm rows="1" maxColumns="2"
	labelWidth="250px" width="98%" >
	<af:selectOneChoice id="projectStateCd"
		showRequired="true" 
		label="Project Status: " 
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.projectStateCd}" >
		<f:selectItems
			value="#{projectTrackingReference.projectStateDefs.items[
						(empty projectTrackingDetail.project.projectStateCd
							? '' : projectTrackingDetail.project.projectStateCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm  labelWidth="247px" width="98%">
	<h:panelGrid columns="2" width="98%">
		<afh:cellFormat valign="top">
			<af:selectManyListbox id="divisionCds" label="Divisions: "
				readOnly="#{!projectTrackingDetail.editMode}"
				value="#{projectTrackingDetail.project.projectDivisionCds}">
				<f:selectItems
					value="#{projectTrackingReference.divisionDefs.items[
							(empty projectTrackingDetail.project.projectDivisionCds
									? '' : projectTrackingDetail.project.projectDivisionCds)]}" />
			</af:selectManyListbox>
		</afh:cellFormat>
		<afh:cellFormat width="25%" valign="top">
			<af:table id="projectLeadUserIds" width="100%"
				value="#{projectTrackingDetail.projectLeadsWrapper}"
				binding="#{projectTrackingDetail.projectLeadsWrapper.table}"
				var="projLead" bandingInterval="1" banding="row">
				<f:facet name="selection">
					<af:tableSelectMany shortDesc="Select" 
						rendered="#{projectTrackingDetail.editMode}" />
				</f:facet>
				<af:column sortable="false" formatType="text"
					headerText="Project Lead(s)">
					<af:selectOneChoice unselectedLabel="" value="#{projLead.value}"
						readOnly="#{!projectTrackingDetail.editMode}">
						<f:selectItems
							value="#{empty projLead.value 
										? projectTrackingDetail.projectLeadUserDefs.items[0] 
										: infraDefs.allUsersDef.items[projLead.value]}" />
					</af:selectOneChoice>
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton id="addBtn"
								disabled="#{projectTrackingDetail.readOnlyUser}"
								rendered="#{projectTrackingDetail.editMode}" text="Add"
								action="#{projectTrackingDetail.project.addProjectLead}" />
							<af:commandButton id="delBtn"
								disabled="#{projectTrackingDetail.readOnlyUser}"
								rendered="#{projectTrackingDetail.editMode}"
								text="Delete Selected"
								action="#{projectTrackingDetail.deleteProjectLeads}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</afh:cellFormat>
	</h:panelGrid>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2"
	labelWidth="250px" width="98%" >
	<af:inputText id="extAgencyWebsiteUri"
		rendered="#{projectTrackingDetail.editMode}"
		label="Link to External Agency Web Site Docs: "
		readOnly="#{!projectTrackingDetail.editMode}"
		columns="50" maximumLength="200"
		value="#{projectTrackingDetail.project.extAgencyWebsiteUri}" />
	<af:panelLabelAndMessage for="extAgencyWebsiteLink" 
		label="Link to External Agency Web Site Docs:"
		rendered="#{!projectTrackingDetail.editMode}" 
		tip="Opens in a new browser window.">
		<af:goLink id="extAgencyWebsiteLink"  
			targetFrame="blank" 
			text="#{projectTrackingDetail.project.extAgencyWebsiteUri}"
			destination="#{projectTrackingDetail.project.extAgencyWebsiteUri}"/>
	</af:panelLabelAndMessage>
	<af:panelLabelAndMessage for="extAgencyWebsiteLink" 
		label="DEQ Staff Directory:"
		rendered="true" 
		tip="Opens in a new browser window.">
		<af:goLink id="deqStaffDirectoryLink"  
			targetFrame="blank" 
			text="Click Here"
			destination="http://deq.wyoming.gov/staff-directory"/>
	</af:panelLabelAndMessage>
</af:panelForm>	

<af:panelForm rows="2" maxColumns="1"
	labelWidth="250px" width="98%" >
	<af:inputText id="projectDescription" 
		showRequired="true"
		label="Project Description: " 
		rows="5" columns="100" maximumLength="1000"
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.projectDescription}" />
	<af:inputText id="projectSummary" 
		label="Project Summary: " 
		rows="5" columns="100" maximumLength="1000"
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.projectSummary}" />
</af:panelForm>

			

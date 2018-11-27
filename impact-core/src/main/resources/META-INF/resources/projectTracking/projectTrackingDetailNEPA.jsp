<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:objectSpacer height="10" />

<af:panelForm rows="1" maxColumns="2"
	labelWidth="250px" width="98%" >
	<af:selectOneChoice id="categoryCd"
		label="NEPA Category: " 
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.categoryCd}" >
		<f:selectItems
			value="#{projectTrackingReference.NEPACategoryTypeDefs.items[
						(empty projectTrackingDetail.project.categoryCd
							? '' : projectTrackingDetail.project.categoryCd)]}" />
	</af:selectOneChoice>
	<af:selectManyListbox id="levelCds"
		label="NEPA Levels: "
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.levelCds}" >
		<f:selectItems
			value="#{projectTrackingReference.NEPALevelTypeDefs.items[
						(empty projectTrackingDetail.project.levelCds
								? '' : projectTrackingDetail.project.levelCds)]}" />
	</af:selectManyListbox>
	<af:selectOneChoice  id="projectStageCd" 
		label="Project Stage: " 
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.projectStageCd}" >
		<f:selectItems
			value="#{projectTrackingReference.projectStageDefs.items[
						(empty projectTrackingDetail.project.projectStageCd
								? '' : projectTrackingDetail.project.projectStageCd)]}" />
	</af:selectOneChoice>
	<af:selectOneChoice id="modelingRequired"
		label="Modeling Required: "
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.modelingRequired}" >
		<f:selectItem itemValue="N" itemLabel="No" />
		<f:selectItem itemValue="Y" itemLabel="Yes" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="4"
	labelWidth="250px" width="98%"
	partialTriggers="leadAgencyCds" >	
	<af:selectManyListbox id="leadAgencyCds"
		autoSubmit="true"
		label="Lead Agencies: "
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.leadAgencyCds}" >
		<f:selectItems
			value="#{projectTrackingReference.agencyDefs.items[
						(empty projectTrackingDetail.project.leadAgencyCds
								? '0' : projectTrackingDetail.project.leadAgencyCds)]}" />
	</af:selectManyListbox>
	
	<af:selectManyListbox id="BLMFieldOfficeCds"
		label="BLM Field Offices: "
		rendered="#{projectTrackingDetail.displayBLMFieldOfficeList}"
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.BLMFieldOfficeCds}" >
		<f:selectItems
			value="#{projectTrackingReference.BLMFieldOfficeDefs.items[
						(empty projectTrackingDetail.project.BLMFieldOfficeCds
								? '' : projectTrackingDetail.project.BLMFieldOfficeCds)]}" />
	</af:selectManyListbox>
	
	<af:selectManyListbox id="nationalForestCds"
		label="USFS National Forests: "
		rendered="#{projectTrackingDetail.displayNationalForestList}"
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.nationalForestCds}" >
		<f:selectItems
			value="#{projectTrackingReference.nationalForestDefs.items[
						(empty projectTrackingDetail.project.nationalForestCds
								? '' : projectTrackingDetail.project.nationalForestCds)]}" />
	</af:selectManyListbox>
	
	<af:selectManyListbox id="nationalParkCds"
		label="National Park Service Managed Areas: "
		rendered="#{projectTrackingDetail.displayNationalParkList}"
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.nationalParkCds}" >
		<f:selectItems
			value="#{projectTrackingReference.nationalParkDefs.items[
						(empty projectTrackingDetail.project.nationalParkCds
								? '' : projectTrackingDetail.project.nationalParkCds)]}" />
	</af:selectManyListbox>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2"
	labelWidth="250px" width="98%" >	
	<af:inputText  id="extAgencyContact" 
		label="External Agency Contact: "
		columns="30" maximumLength="80" 
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.extAgencyContact}" />
	<af:inputText  id="extAgencyContactPhone" 
		label="Phone Number: "
		columns="15" maximumLength="13" 
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.extAgencyContactPhone}" 
		converter="#{infraDefs.phoneNumberConverter}" />
</af:panelForm>

<af:panelForm rows="1" maxColumns="2"
	labelWidth="250px" width="98%" >
	<af:selectOneChoice id="shapeId"
		unselectedLabel=""
		label="Project Area (Map): " 
		rendered="#{projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.strShapeId}" >
		<f:selectItems value="#{projectTrackingReference.shapeDefs.items[
			(empty projectTrackingDetail.project.strShapeId 
				? '' : projectTrackingDetail.project.strShapeId)]}" />
	</af:selectOneChoice>
	<af:panelLabelAndMessage for="shapeId" 
		label="Project Area (Map): "
		rendered="#{!projectTrackingDetail.editMode}" > 
		<af:commandLink text="#{projectTrackingDetail.shapeLabel}" 
			action="#{projectTrackingDetail.displayProjectAreaOnMap}" >
		</af:commandLink>	
	</af:panelLabelAndMessage>		
</af:panelForm>



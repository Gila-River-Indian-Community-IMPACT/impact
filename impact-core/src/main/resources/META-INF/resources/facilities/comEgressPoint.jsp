<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:panelGroup layout="vertical"
	rendered="#{facilityProfile.renderComponent == 'egressPoints'}">
	<af:panelHeader text="Release Point Information" size="0" />
	<af:panelForm rows="1" maxColumns="1" labelWidth="185" width="600">
		<af:inputText label="AQD ID:" id="releasePointId" columns="12"
			maximumLength="12" readOnly="true"
			value="#{facilityProfile.egressPoint.releasePointId}" />
		<af:selectOneChoice label="Release Point Type:" id="egressPointTypeCd"
			value="#{facilityProfile.egressPoint.egressPointTypeCd}"
			valueChangeListener="#{facilityProfile.egressPoint.refresh}"
			unselectedLabel=" " readOnly="#{! facilityProfile.editable}"
			showRequired="true" autoSubmit="true">
			<f:selectItems
				value="#{facilityReference.egrPntTypeDefs.items[(empty facilityProfile.egressPoint.egressPointTypeCd ? '' : facilityProfile.egressPoint.egressPointTypeCd)]}" />
		</af:selectOneChoice>
		<af:inputText label="AQD Description:" id="egpDapcDesc" rows="4"
			value="#{facilityProfile.egressPoint.dapcDesc}" columns="80"
			showRequired="#{facilityProfile.dapcUser}" maximumLength="240"
			readOnly="#{! facilityProfile.editable}"
			disabled="#{! facilityProfile.dapcUser}" />
		<af:inputText label="AQD WISE Release Point ID:"
			id="aqdWiseEgressPointId" columns="12" maximumLength="12"
			value="#{facilityProfile.egressPoint.aqdWiseEgressPointId}"
			readOnly="#{! facilityProfile.editable}"
			rendered="#{infraDefs.hidden && (infraDefs.stars2Admin || facilityProfile.allowedToEditWiseViewId)}" />
		<af:inputText label="Company Release Point ID:" id="egressPointId" columns="12"
			maximumLength="12" showRequired="true"
			value="#{facilityProfile.egressPoint.egressPointId}"
			readOnly="#{! facilityProfile.editable}" />
		<af:inputText label="Company Release Point Description:" id="egrPntegulatedUserDsc"
			value="#{facilityProfile.egressPoint.regulatedUserDsc}" columns="80"
			showRequired="true" rows="4" maximumLength="240"
			readOnly="#{! facilityProfile.editable}" />
		<af:selectOneChoice label="Operating status:" showRequired="true"
			value="#{facilityProfile.egressPoint.operatingStatusCd}"
			id="egOperatingStatus" readOnly="#{! facilityProfile.editable}">
			<f:selectItems
				value="#{facilityReference.egOperatingStatusDefs.items[(empty facilityProfile.egressPoint.operatingStatusCd ? '' : facilityProfile.egressPoint.operatingStatusCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="2" maxColumns="2" labelWidth="185" width="600"
		partialTriggers="egressPointTypeCd">
		<af:inputText label="Release Point Latitude:" id="facLatitudeDec"
			showRequired="#{facilityProfile.egressPoint.egressPointTypeCd != 'AVL'}"
			value="#{facilityProfile.egressPoint.latitudeDeg}" columns="15"
			maximumLength="10" readOnly="#{! facilityProfile.editable}"
			onchange="if(document.getElementById('facilityProfile:comFacProfile:egressPoint:facLatitudeDec').value < 0){ document.getElementById('facilityProfile:comFacProfile:egressPoint:facLatitudeDec').value = -document.getElementById('facilityProfile:comFacProfile:egressPoint:facLatitudeDec').value;}"
			shortDesc="The value must be between #{infraDefs.minLatitude}  ~ #{infraDefs.maxLatitude}.">
			<af:convertNumber pattern=".00000" />
		</af:inputText>

		<af:inputText label="Release Point Longitude:" id="facLongitudeDec"
			showRequired="#{facilityProfile.egressPoint.egressPointTypeCd != 'AVL'}"
			styleClass="longitude" inlineStyle="font-size:10pt;"
			value="#{facilityProfile.egressPoint.longitudeDeg}" columns="15"
			maximumLength="11" readOnly="#{! facilityProfile.editable}"
			onchange="if(document.getElementById('facilityProfile:comFacProfile:egressPoint:facLongitudeDec').value > 0){ document.getElementById('facilityProfile:comFacProfile:egressPoint:facLongitudeDec').value = -document.getElementById('facilityProfile:comFacProfile:egressPoint:facLongitudeDec').value;}"
			shortDesc="The value must be between #{infraDefs.minLongitude}  ~ #{infraDefs.maxLongitude}.">
			<af:convertNumber pattern=".00000" />
		</af:inputText>
		
		<af:inputText value="" readOnly="true" inlineStyle="display:none;" />
		<af:inputText value="" readOnly="true" inlineStyle="display:none;" />
		<af:inputText value="" readOnly="true" inlineStyle="display:none;" 
			rendered="#{(empty facilityProfile.egressPoint.googleMapsURL)}" />
		<af:goLink text="Show On Map" targetFrame="_new"
			destination="#{facilityProfile.egressPoint.googleMapsURL}"
			rendered="#{!(empty facilityProfile.egressPoint.googleMapsURL)}"
			shortDesc="Clicking this will open Google Maps in a separate tab or window." inlineStyle="margin-left:100px;" />

		<af:inputText label="Facility Latitude:" id="facilityLatitude"
			readOnly="true" value="#{facilityProfile.facility.phyAddr.latitude}">
			<af:convertNumber pattern=".00000" />
		</af:inputText>

		<af:inputText label="Facility Longitude:" id="facilityLongitude"
			readOnly="true" value="#{facilityProfile.facility.phyAddr.longitude}">
			<af:convertNumber pattern=".00000" />
		</af:inputText>
							
		<f:verbatim><%@ include
				file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/wording-filter.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/facility-detail-location.js"%></f:verbatim>
	</af:panelForm>

	<af:showDetailHeader text="Release Point Type Specific Information"
		disclosed="true" partialTriggers="egressPointTypeCd">
		<af:panelForm rows="1" maxColumns="2" labelWidth="170" width="600"
			rendered="#{facilityProfile.egressPoint.egressPointTypeCd == 'VER' || facilityProfile.egressPoint.egressPointTypeCd == 'HOR'}">
			<af:inputText label="Base Elevation (ft):" id="baseElevation"
				tip="Feet above sea level"
				value="#{facilityProfile.egressPoint.baseElevation}" columns="10"
				showRequired="true" maximumLength="10"
				readOnly="#{! facilityProfile.editable}">
				<af:convertNumber pattern=".00" />
			</af:inputText>
		</af:panelForm>
		<af:panelForm rows="2" maxColumns="2" labelWidth="170" width="600"
			rendered="#{facilityProfile.egressPoint.egressPointTypeCd == 'AVL' }">
			<af:inputText label="Release Height (ft):" id="areaReleaseHeight"
				value="#{facilityProfile.egressPoint.releaseHeight}" columns="10"
				maximumLength="10" readOnly="#{! facilityProfile.editable}">
				<af:convertNumber pattern=".00" />
			</af:inputText>
		</af:panelForm>
		<af:panelForm rows="1" maxColumns="2" labelWidth="170" width="600"
			rendered="#{facilityProfile.egressPoint.egressPointTypeCd == 'VER' || facilityProfile.egressPoint.egressPointTypeCd == 'HOR' }">
			<af:inputText label="Stack Height (ft):" id="areaReleaseHeight"
				tip="Feet above base elevation"
				value="#{facilityProfile.egressPoint.releaseHeight}" columns="12"
				showRequired="true" maximumLength="12"
				readOnly="#{! facilityProfile.editable}">
				<af:convertNumber pattern=".00" />
			</af:inputText>
			<af:inputText label="Stack Diameter (ft):" id="diameter"
				value="#{facilityProfile.egressPoint.diameter}" columns="12"
				showRequired="true" maximumLength="13"
				readOnly="#{! facilityProfile.editable}" autoSubmit="true"
				valueChangeListener="#{facilityProfile.egressPoint.diameterValueChanged}" >
				<f:converter converterId="us.oh.state.epa.stars2.webcommon.converter.BigDecimalConverter" /> 
			</af:inputText>
		</af:panelForm>
		<af:panelForm rows="2" maxColumns="2" labelWidth="30%" width="600"
			rendered="#{facilityProfile.egressPoint.egressPointTypeCd == 'VER' || facilityProfile.egressPoint.egressPointTypeCd == 'HOR' }"
			partialTriggers="exitGasVelocity diameter">
			<af:inputText label="Exit Gas Velocity (ft/s):" id="exitGasVelocity"
				value="#{facilityProfile.egressPoint.exitGasVelocity}" columns="12"
				showRequired="true" maximumLength="13"
				readOnly="#{! facilityProfile.editable}" autoSubmit="true"
				valueChangeListener="#{facilityProfile.egressPoint.exitGasVelocityValueChanged}" >
				<f:converter converterId="us.oh.state.epa.stars2.webcommon.converter.BigDecimalConverter" /> 
			</af:inputText>
			<af:objectSpacer height="10" /> <%-- for painting the fields in a specific order --%>
			<af:inputText label="Exit Gas Flow Rate (acfm):" id="exitGasFlowAvg"
				value="#{facilityProfile.egressPoint.exitGasFlowAvg}" columns="12"
				showRequired="true" maximumLength="13"
				tip="Flow rate is calculated by IMPACT: 3.1415927*Velocity*60*(Diameter/2)^2"
				readOnly="true" >
				<f:converter converterId="us.oh.state.epa.stars2.webcommon.converter.BigDecimalConverter" /> 
			</af:inputText>
			<af:inputText label="Exit Gas Temp (F):" id="exitGasTempAvg"
				value="#{facilityProfile.egressPoint.exitGasTempAvg}" columns="12"
				showRequired="true" maximumLength="13"
				readOnly="#{! facilityProfile.editable}">
				<f:converter converterId="us.oh.state.epa.stars2.webcommon.converter.BigDecimalConverter" /> 
			</af:inputText>
			<af:objectSpacer height="10" /> <%-- for painting the fields in a specific order --%>
		</af:panelForm>
	</af:showDetailHeader>

	<%/*<af:panelHeader text="CEM Data" size="1" rendered="true" />
	<af:panelForm rendered="true">
		<af:table value="#{facilityProfile.egressPoint.cems}"
			bandingInterval="1" banding="row" var="cem" id="egressPointCemTab"
			binding="#{facilityProfile.egressPointCemTable}" width="98%">
			<f:facet name="selection">
				<af:tableSelectMany rendered="#{facilityProfile.editable}" />
			</f:facet>
			<af:column sortProperty="cemDsc" sortable="true" id="cemDsc"
				formatType="text" headerText="Description">
				<af:inputText value="#{cem.cemDsc}" columns="40" maximumLength="40"
					readOnly="#{! facilityProfile.editable}" />
			</af:column>
			<af:column formatType="text" headerText="Pollutant Monitored">
				<af:column sortProperty="h2sFlag" sortable="true" id="h2sFlag"
					formatType="icon" headerText="H2S">
					<af:selectBooleanCheckbox value="#{cem.h2sFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="so2Flag" sortable="true" id="so2Flag"
					formatType="icon" headerText="SO2">
					<af:selectBooleanCheckbox value="#{cem.so2Flag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="noxFlag" sortable="true" id="noxFlag"
					formatType="icon" headerText="NOX">
					<af:selectBooleanCheckbox value="#{cem.noxFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="coFlag" sortable="true" id="coFlag"
					formatType="icon" headerText="CO">
					<af:selectBooleanCheckbox value="#{cem.coFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="thcFlag" sortable="true" id="thcFlag"
					formatType="icon" headerText="THC">
					<af:selectBooleanCheckbox value="#{cem.thcFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="hclFlag" sortable="true" id="hclFlag"
					formatType="icon" headerText="HCL">
					<af:selectBooleanCheckbox value="#{cem.hclFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="hflFlag" sortable="true" id="hflFlag"
					formatType="icon" headerText="HFL">
					<af:selectBooleanCheckbox value="#{cem.hflFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="o1Flag" sortable="true" id="o1Flag"
					formatType="icon" headerText="O">
					<af:selectBooleanCheckbox value="#{cem.o1Flag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="trsFlag" sortable="true" id="trsFlag"
					formatType="icon" headerText="TRS">
					<af:selectBooleanCheckbox value="#{cem.trsFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="co2Flag" sortable="true" id="co2Flag"
					formatType="icon" headerText="CO2">
					<af:selectBooleanCheckbox value="#{cem.co2Flag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="flowFlag" sortable="true" id="flowFlag"
					formatType="icon" headerText="FLOW">
					<af:selectBooleanCheckbox value="#{cem.flowFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="opacityFlag" sortable="true"
					id="opacityFlag" formatType="icon" headerText="OPACITY">
					<af:selectBooleanCheckbox value="#{cem.opacityFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="pmFlag" sortable="true" id="pmFlag"
					formatType="icon" headerText="PM">
					<af:selectBooleanCheckbox value="#{cem.pmFlag}"
						readOnly="#{! facilityProfile.editable}" />
				</af:column>
			</af:column>
			<f:facet name="footer">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Add CEM"
							action="#{facilityProfile.addEgressPointCem}"
							rendered="#{facilityProfile.editable}">
						</af:commandButton>
						<af:commandButton text="Delete Selected CEMs"
							rendered="#{facilityProfile.editable}"
							action="#{facilityProfile.deleteEgressPointCems}">
						</af:commandButton>
						<af:commandButton actionListener="#{tableExporter.printTable}"
							onclick="#{tableExporter.onClickScript}" text="Printable view" />
						<af:commandButton actionListener="#{tableExporter.excelTable}"
							onclick="#{tableExporter.onClickScript}" text="Export to excel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>
		</af:table>
	</af:panelForm>*/%>
	<af:objectSpacer height="20" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar rendered="#{!facilityProfile.readOnlyUser}">
			<af:commandButton text="Edit"
				action="#{facilityProfile.editEgressPoint}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="releasePointEditBtn" />
			<af:commandButton text="Delete"
				action="#{facilityProfile.confirmRemoveOp}" partialSubmit="true"
				useWindow="true" windowWidth="600" windowHeight="150"
				returnListener="#{facilityProfile.confirmReturned}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.deleteEgrPntDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="releasePointDeleteBtn" />
			<af:objectSpacer width="20" />
			<af:commandButton text="Create Cloned Release Point"
				action="#{facilityProfile.cloneEgressPoint}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="releasePointCreateClonedRP" />
			<af:commandButton text="Save"
				action="#{facilityProfile.saveEgressPoint}"
				rendered="#{facilityProfile.editable}"
				id="releasePointSaveBtn" />
			<af:commandButton text="Cancel"
				action="#{facilityProfile.cancelEgressPoint}"
				rendered="#{facilityProfile.editable}" immediate="true"
				id="releasePointCancelBtn" />
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>

<af:panelGroup layout="vertical"
	rendered="#{facilityProfile.renderComponent == 'addEgressPoint' }">
	<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
		<afh:rowLayout halign="left">
			<af:outputFormatted
				rendered="#{(facilityProfile.selectedTreeNode.type == 'emissionProcesses')}"
				inlineStyle="color: orange; font-weight: bold;"
				value="<b>Caution: You are attempting to associate a release point directly to a process. If there is control equipment for the process, please associate the control equipment to the process and then associate the release point to the control equipment.</b>" />
		</afh:rowLayout>
	</afh:tableLayout>
	<af:objectSpacer height="10" />
	<af:showDetailHeader text="Select Release Point to Associate"
		disclosed="true">
		<af:panelForm>
			<af:selectOneChoice label="AQD Release Point ID:"
				value="#{facilityProfile.egrPntId}" required="true"
				id="aqdReleasePointIDAssoc">
				<f:selectItems value="#{facilityProfile.egressPointsList}" />
			</af:selectOneChoice>
			<afh:rowLayout halign="center">
				<af:objectSpacer height="20" />
				<af:panelButtonBar>
					<af:commandButton text="Save"
						action="#{facilityProfile.saveAddEgressPoint}"
						id="associateRPSaveBtn" />
					<af:commandButton text="Cancel"
						action="#{facilityProfile.cancelAddEgressPoint}" immediate="true"
						id="associateRPCancelBtn" />
				</af:panelButtonBar>
			</afh:rowLayout>
		</af:panelForm>
	</af:showDetailHeader>
</af:panelGroup>

<af:panelGroup layout="vertical"
	rendered="#{facilityProfile.renderComponent == 'removeEgressPoint' }">
	<af:objectSpacer height="10" />
	<af:showDetailHeader text="Select Release Point to Disassociate"
		disclosed="true">
		<af:panelForm>
			<af:selectOneChoice label="AQD Release Point ID:"
				value="#{facilityProfile.egrPntId}" required="true"
				id="aqdReleasePointIDDis" >
				<f:selectItems value="#{facilityProfile.egressPointsRemoveList}" />
			</af:selectOneChoice>
			<afh:rowLayout halign="center">
				<af:objectSpacer height="20" />
				<af:panelButtonBar>
					<af:commandButton text="Save"
						action="#{facilityProfile.saveRemoveEgressPoint}"
						id="disassociateRPSaveBtn" />
					<af:commandButton text="Cancel"
						action="#{facilityProfile.cancelAddEgressPoint}" immediate="true"
						id="disassociateRPCancelBtn" />
				</af:panelButtonBar>
			</afh:rowLayout>
		</af:panelForm>
	</af:showDetailHeader>
</af:panelGroup>
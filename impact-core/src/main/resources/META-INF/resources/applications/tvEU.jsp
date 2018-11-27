<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical">

	<af:panelHeader text="Non-insignificant Emissions Unit">
		<%
			/* EU general info begin */
		%>
		<af:panelForm labelWidth="200">
			<af:inputText id="EUIdText" label="AQD EU ID :"
				value="#{applicationDetail.selectedEU.fpEU.epaEmuId}"
				readOnly="true" />
			<af:inputText id="EUDescriptionText" label="AQD EU description :"
				value="#{applicationDetail.selectedEU.fpEU.euDesc}" readOnly="true" 
				columns="80" rows="4"/>
			<af:inputText id="CompanyEUIdText" label="Company EU ID :"
				value="#{applicationDetail.selectedEU.fpEU.companyId}"
				readOnly="true" />
			<af:inputText id="CompanyEUDescText" label="Company EU Description :"
				rows="4" columns="80" maximumLength="240"
				value="#{applicationDetail.selectedEU.fpEU.regulatedUserDsc}"
				readOnly="true" />
		</af:panelForm>
	</af:panelHeader>
	<%
		/* EU general info end */
	%>
	<%
		/**************************************************************************/
			/* Item 1: Maximum Allowable Operating Schedule                                      */
			/**************************************************************************/
	%>
	<af:showDetailHeader disclosed="true" text="Maximum Allowable Operating Schedule">
		<af:panelForm partialTriggers="OpSchedTSCheckBox" labelWidth="100">
			<af:outputText id="NormOpSchedOutText"
				value="Provide the maximum allowable operating schedule for this emissions unit"
				inlineStyle="font-size: 13px; font-weight: bold;" />
			<af:inputText id="opSchedHrsDayText" label="Hours/day :"
				value="#{applicationDetail.selectedScenario.opSchedHrsDay}"
				readOnly="#{! applicationDetail.editMode}">
				<f:validateLongRange minimum="0" maximum="24" />
			</af:inputText>
			<af:inputText id="opSchedHrsYrText" label="Hours/year :"
				value="#{applicationDetail.selectedScenario.opSchedHrsYr}"
				readOnly="#{! applicationDetail.editMode}">
				<f:validateLongRange minimum="0" maximum="8760" />
			</af:inputText>
			<af:panelForm labelWidth="600">
				<af:selectOneChoice id="OpSchedTSCheckBox"
					label="Is there an Alternate Operating Scenario (AOS) authorized for this emission unit that is not included in an AOS for multiple emission units, already attached to this application? :"
					value="#{applicationDetail.selectedScenario.opAosAutherized}"
					readOnly="#{!applicationDetail.editMode}"
					valueChangeListener="#{applicationDetail.refreshAttachments}"
					autoSubmit="true">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
			</af:panelForm>
			<af:outputText inlineStyle="font-size: 12px;  font-style: italic;"
				value="* The attachment must include a list of each emissions unit affected by the scenario, 
				the SIC code(s) for processes and products associated with the AOS, as well as the requirements 
				that apply during the AOS." />
		</af:panelForm>
	</af:showDetailHeader>

	<%
		/**************************************************************************/
			/* Item 2: PTE Tables                                                     */
			/**************************************************************************/
	%>
	<f:subview id="tvEuPTE">
		<jsp:include page="tvEuPTE.jsp" />
	</f:subview>

	
	<%
		/**************************************************************************/
			/* Item 6: Emission Unit - Specific Requirements                   */
			/**************************************************************************/
	%>
	<f:subview id="tvEuSpecificRequirements">
		<jsp:include page="tvEuRequirements.jsp" />
	</f:subview>	

</af:panelGroup>


<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical">
	<af:panelHeader text="Emissions Unit">
		<%
			/* EU general info begin */
		%>
		<af:panelForm labelWidth="200">
			<af:inputText id="EUIdText" label="AQD EU ID :"
				value="#{applicationDetail.selectedEU.fpEU.epaEmuId}"
				readOnly="true" />
			<af:inputText id="EUDescriptionText" label="AQD EU description :"
				value="#{applicationDetail.selectedEU.fpEU.euDesc}" readOnly="true" columns="80" rows="4"/>
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
		/* Installation or Modification Schedule begin */
	%>
	<af:showDetailHeader id="schedulePanel" disclosed="true"
		partialTriggers="euPurposesCheckbox"
		text="Source Installation or Modification Schedule">
		<af:panelGroup id="scheduleChoicesPanel" layout="vertical">
			<af:outputLabel for="modSchedPanel"
				value="Select reason(s) for this emissions unit being included in this 
      application (must be completed regardless of date of installation or modification):" />
			<af:objectSpacer height="10" />
			<af:panelGroup id="modSchedPanel" layout="vertical">
				<af:selectOneRadio id="euPurposesCheckbox" autoSubmit="true"
					disabled="#{empty applicationDetail.selectedEU.ptioEUPurposeCD && !applicationDetail.editMode}"
					readOnly="#{not empty applicationDetail.selectedEU.ptioEUPurposeCD && !applicationDetail.editMode}"
					value="#{applicationDetail.selectedEU.ptioEUPurposeCD}" layout="vertical">
					<f:selectItems
						value="#{applicationReference.ptioEUPurposeDefs.items[applicationDetail.selectedEU.ptioEUPurposeCDs]}" />
				</af:selectOneRadio>

				<af:panelForm id="datesForm">
					<af:switcher defaultFacet="noAfterPermitBox"
						facetName="#{applicationDetail.euPurposeCDsRequireAfterPermitFlag ? 'afterPermitBox' : 'noAfterPermitBox'}">
						<f:facet name="noAfterPermitBox">
							<af:selectInputDate id="workStartOnlyDate"
								label="#{applicationDetail.euPurposeCDWorkStartLabel} "
								rendered="#{applicationDetail.euPurposeCDsRequireWorkDate}"
								readOnly="#{! applicationDetail.editMode}"
								value="#{applicationDetail.selectedEU.workStartDate}">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</f:facet>
						<f:facet name="afterPermitBox">
							<af:panelGroup id="workStartGroupPanel" layout="horizontal">
								<af:outputLabel for="workStartPanel"
									value="#{applicationDetail.euPurposeCDWorkStartLabel}"
									rendered="#{applicationDetail.euPurposeCDsRequireWorkDate}" />
								<af:objectSpacer width="10" />
								<af:panelGroup id="workStartPanel" layout="horizontal"
									partialTriggers="workStartDate workStartAfterPermitIssuedBox">
									<af:selectInputDate id="workStartDate"
										rendered="#{applicationDetail.euPurposeCDsRequireWorkDate && (applicationDetail.editMode || !applicationDetail.selectedEU.workStartAfterPermit)}"
										readOnly="#{! applicationDetail.editMode}"
										value="#{applicationDetail.selectedEU.workStartDate}"
										autoSubmit="true">
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>
									<af:objectSpacer width="10" />
									<af:selectBooleanCheckbox id="workStartAfterPermitIssuedBox"
										label="#{applicationDetail.editMode ? ' OR after' : ' After' } permit has been issued : "
										rendered="#{applicationDetail.euPurposeCDsRequireAfterPermitFlag && (applicationDetail.editMode || applicationDetail.selectedEU.workStartDate == null)}"
										readOnly="#{! applicationDetail.editMode}"
										value="#{applicationDetail.selectedEU.workStartAfterPermit}"
										autoSubmit="true" />
								</af:panelGroup>
							</af:panelGroup>
						</f:facet>
					</af:switcher>

				</af:panelForm>

				<af:outputLabel for="reconExplPanel" value="Please explain : "
					rendered="#{applicationDetail.euPurposeCDsContainReconstruction}" />
				<af:panelGroup id="reconExplPanel" layout="vertical">
					<af:inputText id="reconExplText" columns="120" rows="4"
						maximumLength="1000"
						rendered="#{applicationDetail.euPurposeCDsContainReconstruction}"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.reconstructionDesc}" />
				</af:panelGroup>
				<af:outputLabel for="reconExplPanel" value="Please explain : "
					rendered="#{applicationDetail.euPurposeCDsContainOther}" />
				<af:panelGroup id="otherReasonPanel" layout="vertical">
					<af:inputText id="otherReasonText" columns="120" rows="4"
						rendered="#{applicationDetail.euPurposeCDsContainOther}"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.euModificationDesc}" />
				</af:panelGroup>

				<af:objectSpacer height="2" />

			</af:panelGroup>
		</af:panelGroup>
	</af:showDetailHeader>
	<%
		/* Installation or Modification Schedule begin  end */
	%>
	<af:showDetailHeader disclosed="true"
		text="Emission Unit Type Specific Information">
		<af:panelForm rendered="#{applicationDetail.selectedEU != null}">
			<af:switcher
				facetName="#{applicationDetail.selectedEU.fpEU.emissionUnitTypeCd}"
				defaultFacet="default">
				<f:subview id="nsrAppEmissionUnitTypeDefault">
					<f:facet name="default" />
				</f:subview>
				<f:facet name="ABS">
					<f:subview id="nsrAppEmissionUnitTypeABS">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeABS.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="ACB">
					<f:subview id="nsrAppEmissionUnitTypeACB">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeACB.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																																
				<f:facet name="AMN">
					<f:subview id="nsrAppEmissionUnitTypeAMN">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeAMN.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="APT">
					<f:subview id="nsrAppEmissionUnitTypeAPT">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeAPT.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="BAK">
					<f:subview id="nsrAppEmissionUnitTypeBAK">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeBAK.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="BGM">
					<f:subview id="nsrAppEmissionUnitTypeBGM">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeBGM.jsp" />
					</f:subview>
				</f:facet>	
				<f:facet name="BOL">
					<f:subview id="nsrAppEmissionUnitTypeBOL">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeBOL.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="BVC">
					<f:subview id="nsrAppEmissionUnitTypeBVC">
						<af:outputText id="euTypeLabel"
							value="Emission Unit Type : Blow-down, Venting, Well Completion"
							inlineStyle="font-size: 13px; font-weight: bold;" />
						<af:objectSpacer height="15" />
					</f:subview>
				</f:facet>
				<f:facet name="CCU">
					<f:subview id="nsrAppEmissionUnitTypeCCU">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeCCU.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="CKD">
					<f:subview id="nsrAppEmissionUnitTypeCKD">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeCKD.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="CMX">
					<f:subview id="nsrAppEmissionUnitTypeCMX">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeCMX.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="COT">
					<f:subview id="nsrAppEmissionUnitTypeCOT">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeCOT.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="CSH">
					<f:subview id="nsrAppEmissionUnitTypeCSH">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeCSH.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="CTW">
					<f:subview id="nsrAppEmissionUnitTypeCTW">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeCTW.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="DHY">
					<f:subview id="nsrAppEmissionUnitTypeDHY">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeDHY.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="DIS">
					<f:subview id="nsrAppEmissionUnitTypeDIS">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeDIS.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="DRY">
					<f:subview id="nsrAppEmissionUnitTypeDRY">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeDRY.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="EGU">
					<f:subview id="nsrAppEmissionUnitTypeEGU">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeEGU.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="ENG">
					<f:subview id="nsrAppEmissionUnitTypeENG">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeENG.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="FLR">
					<f:subview id="nsrAppEmissionUnitTypeFLR">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeFLR.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="GIN">
					<f:subview id="nsrAppEmissionUnitTypeGIN">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeGIN.jsp" />
					</f:subview>
				</f:facet>						
				<f:facet name="GRI">
					<f:subview id="nsrAppEmissionUnitTypeGRI">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeGRI.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="FAT">
					<f:subview id="nsrAppEmissionUnitTypeFAT">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeFAT.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="FUG">
					<f:subview id="nsrAppEmissionUnitTypeFUG">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeFUG.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="HET">
					<f:subview id="nsrAppEmissionUnitTypeHET">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeHET.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="HMA">
					<f:subview id="nsrAppEmissionUnitTypeHMA">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeHMA.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="INC">
					<f:subview id="nsrAppEmissionUnitTypeINC">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeINC.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="LUD">
					<f:subview id="nsrAppEmissionUnitTypeLUD">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeLUD.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="MAC">
					<f:subview id="nsrAppEmissionUnitTypeMAC">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeMAC.jsp" />
					</f:subview>					
				</f:facet>
				<f:facet name="MAT">
					<f:subview id="nsrAppEmissionUnitTypeMAT">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeMAT.jsp" />
					</f:subview>					
				</f:facet>
				<f:facet name="MET">
					<f:subview id="nsrAppEmissionUnitTypeMET">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeMET.jsp" />
					</f:subview>					
				</f:facet>
				<f:facet name="MIL">
					<f:subview id="nsrAppEmissionUnitTypeMIL">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeMIL.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="MIX">
					<f:subview id="nsrAppEmissionUnitTypeMIX">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeMIX.jsp" />
					</f:subview>					
				</f:facet>						
				<f:facet name="MLD">
					<f:subview id="nsrAppEmissionUnitTypeMLD">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeMLD.jsp" />
					</f:subview>					
				</f:facet>
				<f:facet name="OEP">
					<f:subview id="nsrAppEmissionUnitTypeOEP">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeOEP.jsp" />
					</f:subview>													
				</f:facet>
				<f:facet name="ORD">
					<f:subview id="nsrAppEmissionUnitTypeORD">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeORD.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="OZG">
					<f:subview id="nsrAppEmissionUnitTypeOZG">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeOZG.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="PAM">
					<f:subview id="nsrAppEmissionUnitTypePAM">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypePAM.jsp" />
					</f:subview>					
				</f:facet>
				<f:facet name="PEL">
					<f:subview id="nsrAppEmissionUnitTypePEL">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypePEL.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="PNE">
					<f:subview id="nsrAppEmissionUnitTypePNE">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypePNE.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="PRN">
					<f:subview id="nsrAppEmissionUnitTypePRN">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypePRN.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="REM">
					<f:subview id="nsrAppEmissionUnitTypeREM">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeREM.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="RES">
					<f:subview id="nsrAppEmissionUnitTypeRES">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeRES.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="SEB">
					<f:subview id="nsrAppEmissionUnitTypeSEB">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeSEB.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="SEP">
					<f:subview id="nsrAppEmissionUnitTypeSEP">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeSEP.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="SEM">
					<f:subview id="nsrAppEmissionUnitTypeSEM">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeSEM.jsp" />
					</f:subview>					
				</f:facet>
				<f:facet name="SRU">
					<f:subview id="nsrAppEmissionUnitTypeSRU">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeSRU.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="SVC">
					<f:subview id="nsrAppEmissionUnitTypeSVC">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeSVC.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="STZ">
					<f:subview id="nsrAppEmissionUnitTypeSTZ">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeSTZ.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="SVU">
					<f:subview id="nsrAppEmissionUnitTypeSVU">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeSVU.jsp" />
					</f:subview>													
				</f:facet>																																																																																																																												
				<f:facet name="TAR">
					<f:subview id="nsrAppEmissionUnitTypeTAR">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeTAR.jsp" />
					</f:subview>					
				</f:facet>
				<f:facet name="TGT">
					<f:subview id="nsrAppEmissionUnitTypeTGT">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeTGT.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="TIM">
					<f:subview id="nsrAppEmissionUnitTypeTIM">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeTIM.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="TKO">
					<f:subview id="nsrAppEmissionUnitTypeTKO">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeTKO.jsp" />
					</f:subview>													
				</f:facet>
				<f:facet name="TNK">
					<f:subview id="nsrAppEmissionUnitTypeTNK">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeTNK.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="VNT">
					<f:subview id="nsrAppEmissionUnitTypeVNT">
						<jsp:include flush="true" page="nsrAppEmissionUnitTypeVNT.jsp" />
					</f:subview>
				</f:facet>
				<f:facet name="WEL">
					<f:subview id="nsrAppEmissionUnitTypeWEL">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeWEL.jsp" />
					</f:subview>					
				</f:facet>
				<f:facet name="WWE">
					<f:subview id="nsrAppEmissionUnitTypeWWE">
						<jsp:include flush="true" page="ptioAppEmissionUnitTypeWWE.jsp" />
					</f:subview>					
				</f:facet>				
																																																																																																																																
			</af:switcher>
		</af:panelForm>
	</af:showDetailHeader>
	<af:showDetailHeader disclosed="true"
		text="Potential Operating Schedule">
		<af:panelForm>
			<af:outputText id="PotenSchedOutText"
				value="Provide the operating schedule for this emissions unit"
				inlineStyle="font-size: 13px; font-weight: bold;" />
			<af:inputText id="opSchedHrsDayText" label="Hours/day :"
				value="#{applicationDetail.selectedEU.opSchedHrsDay}"
				readOnly="#{! applicationDetail.editMode}">
				<f:validateLongRange minimum="0" maximum="24" />
			</af:inputText>
			<af:inputText id="opSchedHrsYrText" label="Hours/year :"
				value="#{applicationDetail.selectedEU.opSchedHrsYr}"
				readOnly="#{! applicationDetail.editMode}">
				<f:validateLongRange minimum="0" maximum="8760" />
			</af:inputText>
		</af:panelForm>
	</af:showDetailHeader>

	<%
		/* Emissions Info begin */
	%>
	<af:showDetailHeader disclosed="true" text="Emissions Information">
		<f:subview id="ptioEUEmissionsInstructions">
			<jsp:include page="ptioEUEmissionsInstructions.jsp" />
		</f:subview>
		<af:outputText value="Criteria Pollutants : "
			inlineStyle="font-size: 13px;font-weight:bold" />
		<af:table emptyText=" " var="capEmission" bandingInterval="1"
			banding="row" value="#{applicationDetail.selectedEU.capEmissions}" width="98%"
			id="capTable">
			<af:column formatType="text" headerText="Pollutant" id="capPollutant">
				<af:selectOneChoice id="pollutantCd" readOnly="true"
					value="#{capEmission.pollutantCd}">
					<f:selectItems value="#{applicationDetail.capPollutantDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column headerText="Pre-Controlled Potential Emissions (tons/yr)"
				formatType="number" id="capPCPEtons_yr">
				<af:switcher defaultFacet="capPollutant"
					facetName="#{capEmission.hapCapPollutant}">
					<f:facet name="capPollutant">
						<af:inputText readOnly="#{! applicationDetail.editMode}" id="preControlledPotentialEmissions"
							value="#{capEmission.preCtlPotentialEmissions}" columns="10"
							label="Pre-Controlled Potential Emissions (tons/yr)">
							<mu:convertSigDigNumber
								pattern="#{applicationDetail.capEmissionsValueFormat}" />
						</af:inputText>
					</f:facet>
					<f:facet name="hapPollutant">
						<af:inputText readOnly="#{! applicationDetail.editMode}" id="preControlledPotentialEmissions"
							value="#{capEmission.preCtlPotentialEmissions}" columns="10"
							label="Pre-Controlled Potential Emissions (tons/yr)">
							<mu:convertSigDigNumber
								pattern="#{applicationDetail.hapEmissionsValueFormat}" />
						</af:inputText>
					</f:facet>
				</af:switcher>
			</af:column>
			<af:column headerText="Efficiency Standards" formatType="number"
				id="capPTE">
				<af:column id="capCalcPTEText" headerText="Potential to Emit (PTE)*"
					shortDesc="Sum of values from each EU included in the application"
					formatType="number">
					<af:switcher defaultFacet="capPollutant"
						facetName="#{capEmission.hapCapPollutant}">
						<f:facet name="capPollutant">
							<af:inputText readOnly="#{! applicationDetail.editMode}"
								id="potentialToEmit" value="#{capEmission.potentialToEmit}"
								columns="10" label="Potential to Emit (PTE)*">
								<mu:convertSigDigNumber
									pattern="#{applicationDetail.capLbHrEmissionsValueFormat}" />
							</af:inputText>
						</f:facet>
						<f:facet name="hapPollutant">
							<af:inputText readOnly="#{! applicationDetail.editMode}"
								id="potentialToEmit" value="#{capEmission.potentialToEmit}"
								columns="10" label="Potential to Emit (PTE)*">
								<mu:convertSigDigNumber
									pattern="#{applicationDetail.hapEmissionsValueFormat}" />
							</af:inputText>
						</f:facet>
					</af:switcher>
				</af:column>

				<af:column headerText="Units*" id="capUnits">
					<af:switcher defaultFacet="capPollutant"
						facetName="#{capEmission.hapCapPollutant}">
						<f:facet name="capPollutant">
							<af:selectOneChoice id="unitsCd" label="Units:"
								readOnly="#{! applicationDetail.editMode}"
								value="#{capEmission.unitCd}" showRequired="true" unselectedLabel="">
								<f:selectItems
									value="#{applicationDetail.paeuPteUnitsDefs.items[(empty capEmission.unitCd ? '' : capEmission.unitCd)]}" />
							</af:selectOneChoice>
						</f:facet>
						<f:facet name="hapPollutant">
							<af:selectOneChoice id="unitsCd" label="Units:"
								readOnly="#{! applicationDetail.editMode}"
								value="#{capEmission.unitCd}" showRequired="true" unselectedLabel="">
								<f:selectItems
									value="#{applicationDetail.paeuPteUnitsDefs.items[(empty capEmission.unitCd ? '' : capEmission.unitCd)]}" />
							</af:selectOneChoice>
						</f:facet>
					</af:switcher>
				</af:column>
			</af:column>
			<af:column headerText="Potential to Emit (PTE) (lbs/hr)*"
				formatType="number" id="capPTE_hr">
				<af:switcher defaultFacet="capPollutant"
					facetName="#{capEmission.hapCapPollutant}">
					<f:facet name="capPollutant">
						<af:inputText readOnly="#{! applicationDetail.editMode}"
							id="potentialToEmitLbHr"
							value="#{capEmission.potentialToEmitLbHr}" columns="10"
							label="Potential to Emit (PTE) (lbs/hr)*">
							<mu:convertSigDigNumber
								pattern="#{applicationDetail.capLbHrEmissionsValueFormat}" />
						</af:inputText>
					</f:facet>
					<f:facet name="hapPollutant">
						<af:inputText readOnly="#{! applicationDetail.editMode}"
							id="potentialToEmitLbHr"
							value="#{capEmission.potentialToEmitLbHr}" columns="10"
							label="Potential to Emit (PTE) (lbs/hr)*">
							<mu:convertSigDigNumber
								pattern="#{applicationDetail.hapEmissionsValueFormat}" />
						</af:inputText>
					</f:facet>
				</af:switcher>
			</af:column>
			<af:column headerText="Potential to Emit (PTE) (tons/yr)*"
				formatType="number" id="capPTEton_yr">
				<af:switcher defaultFacet="capPollutant"
					facetName="#{capEmission.hapCapPollutant}">
					<f:facet name="capPollutant">
						<af:inputText readOnly="#{! applicationDetail.editMode}"
							id="potentialToEmitTonYr"
							value="#{capEmission.potentialToEmitTonYr}" columns="10"
							label="Potential to Emit (PTE) (tons/yr)*">
							<mu:convertSigDigNumber
								pattern="#{applicationDetail.capEmissionsValueFormat}" />
						</af:inputText>
					</f:facet>
					<f:facet name="hapPollutant">
						<af:inputText readOnly="#{! applicationDetail.editMode}"
							value="#{capEmission.potentialToEmitTonYr}" columns="10"
							id="potentialToEmitTonYr"
							label="Potential to Emit (PTE) (tons/yr)*">
							<mu:convertSigDigNumber
								pattern="#{applicationDetail.hapEmissionsValueFormat}" />
						</af:inputText>
					</f:facet>
				</af:switcher>
			</af:column>
			<af:column headerText="Basis for Determination*" formatType="number"
				id="capBFD">
				<af:switcher defaultFacet="capPollutant"
					facetName="#{capEmission.hapCapPollutant}">
					<f:facet name="capPollutant">
						<af:selectOneChoice id="AppBasisForDeterminationDropdown" autoSubmit="true"
							readOnly="#{! applicationDetail.editMode}"
							label="Basis for Determination*" valueChangeListener="#{applicationDetail.refreshAttachments}" unselectedLabel=" "
							value="#{capEmission.pteDeterminationBasisCd}">
							<f:selectItems
								value="#{applicationDetail.paeuPteDeterminationBasisDefs}" />
						</af:selectOneChoice>
					</f:facet>
					<f:facet name="hapPollutant">
						<af:selectOneChoice id="AppBasisForDeterminationDropdown" autoSubmit="true"
							readOnly="#{! applicationDetail.editMode}"
							label="Basis for Determination*" valueChangeListener="#{applicationDetail.refreshAttachments}" unselectedLabel=" "
							inlineStyle="color: #000000;"
							value="#{capEmission.pteDeterminationBasisCd}">
							<f:selectItems
								value="#{applicationDetail.paeuPteDeterminationBasisDefs}" />
						</af:selectOneChoice>
					</f:facet>
				</af:switcher>
			</af:column>
		</af:table>

		<af:objectSpacer height="10" />
		<af:outputText
			value="Hazardous Air Pollutants (HAPs) and Toxic Air Contaminants: "
			inlineStyle="font-size: 13px;font-weight:bold" />
		<af:table emptyText=" " var="hapTacEmissions" bandingInterval="1"
			banding="row" value="#{applicationDetail.hapTacEmissions}"
			width="98%" id="hapsTable">
			<f:facet name="selection">
				<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
			</f:facet>
			<af:column sortable="true" sortProperty="pollutantCd"
				formatType="text" headerText="Pollutant" id="hapPollutant">
				<af:selectOneChoice id="pollutantCd" readOnly="true"
					value="#{hapTacEmissions.pollutantCd}">
					<f:selectItems value="#{applicationDetail.hapTacPollutantDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column headerText="Pollutant Category" sortable="true"
				sortProperty="pollutantCategory" formatType="text"
				id="hapPollutantCategory">
				<af:inputText readOnly="true"
					value="#{hapTacEmissions.pollutantCategory}" />
			</af:column>
			<af:column headerText="Pre-Controlled Potential Emissions (tons/yr)"
				sortable="true" sortProperty="preCtlPotentialEmissions"
				formatType="number" id="hapEBC">
				<af:inputText readOnly="#{! applicationDetail.editMode}"
					value="#{hapTacEmissions.preCtlPotentialEmissions}" columns="10" 
					id="preCtlPotentialEmissions" label="Pre-Controlled Potential Emissions (tons/yr)">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.hapEmissionsValueFormat}" />
				</af:inputText>
			</af:column>
			<af:column headerText="Efficiency Standards" formatType="number"
				id="hapPTE">
				<af:column headerText="Potential to Emit (PTE)*" sortable="true"
					sortProperty="potentialToEmit" formatType="number"
					id="hapCalcPTEText">
					<af:inputText readOnly="#{! applicationDetail.editMode}"
						value="#{hapTacEmissions.potentialToEmit}" columns="10" id="potentialToEmit" label="Potential to Emit (PTE)*">
						<mu:convertSigDigNumber
							pattern="#{applicationDetail.hapEmissionsValueFormat}" />
					</af:inputText>
				</af:column>
				<af:column headerText="Units*" id="hapUnits">
					<af:selectOneChoice id="unitsCd" label="Units:" readOnly="true"
						value="#{hapTacEmissions.unitCd}" showRequired="true">
						<f:selectItems
							value="#{applicationDetail.paeuPteUnitsDefs.items[(empty hapTacEmissions.unitCd ? '' : hapTacEmissions.unitCd)]}" />
					</af:selectOneChoice>
				</af:column>
			</af:column>
			<af:column headerText="Potential to Emit (PTE) (lbs/hr)*"
				sortable="true" sortProperty="potentialToEmitLbHr"
				formatType="number" id="hapPTE_hr">
				<af:inputText readOnly="#{! applicationDetail.editMode}"
					value="#{hapTacEmissions.potentialToEmitLbHr}" columns="10" id="potentialToEmitLbHr"  label="Potential to Emit (PTE) (lbs/hr)*">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.hapEmissionsValueFormat}" />
				</af:inputText>
			</af:column>
			<af:column headerText="Potential to Emit (PTE) (tons/yr)*"
				sortable="true" sortProperty="potentialToEmitTonYr" formatType="number" id="hapPTE_yr">
				<af:inputText readOnly="#{! applicationDetail.editMode}"
					value="#{hapTacEmissions.potentialToEmitTonYr}" columns="10" id="potentialToEmitTonYr" label="Potential to Emit (PTE) (tons/yr)*">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.hapEmissionsValueFormat}" />
				</af:inputText>
			</af:column>
			<af:column headerText="Basis for Determination*" id="hapBFD">
				<af:selectOneChoice id="AppBasisForDeterminationDropdown" valueChangeListener="#{applicationDetail.refreshAttachments}" autoSubmit="true"
					readOnly="true" label="Basis for Determination*"
					unselectedLabel=" " inlineStyle="color: #000000;"
					value="#{hapTacEmissions.pteDeterminationBasisCd}">
					<f:selectItems
						value="#{applicationDetail.paeuPteDeterminationBasisDefs}" />
				</af:selectOneChoice>
			</af:column>
			<f:facet name="footer">
				<af:panelButtonBar>
					<af:commandButton text="Add" id="addHapTacPollutant"
						useWindow="true" windowWidth="500" windowHeight="300"
						rendered="#{applicationDetail.editMode}"
						returnListener="#{applicationDetail.emissionsDialogDone}"
						action="#{applicationDetail.startAddEmissionsInfo}">
						<t:updateActionListener
							property="#{applicationDetail.pollutantType}"
							value="#{applicationDetail.HAPTAC}" />
					</af:commandButton>
					<af:commandButton text="Delete Selected HAPs"
						rendered="#{applicationDetail.editMode}"
						actionListener="#{applicationDetail.initActionTable}"
						action="#{applicationDetail.deleteEmissions}">
					</af:commandButton>
				</af:panelButtonBar>
			</f:facet>
		</af:table>

		<af:objectSpacer height="10" />
		<af:outputText value="Greenhouse Gases (GHGs): "
			inlineStyle="font-size: 13px;font-weight:bold" />
		<af:table emptyText=" " var="ghgEmissions" bandingInterval="1"
			banding="row" value="#{applicationDetail.ghgEmissions}" width="98%"
			id="ghgTable">
			<f:facet name="selection">
				<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
			</f:facet>
			<af:column sortable="true" sortProperty="pollutantCd"
				formatType="text" headerText="Pollutant" id="ghgPollutant">
				<af:selectOneChoice id="pollutantCd" readOnly="true"
					value="#{ghgEmissions.pollutantCd}">
					<f:selectItems value="#{applicationDetail.ghgPollutantDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column headerText="Pollutant Category" sortable="true"
				sortProperty="pollutantCategory" formatType="text"
				id="ghgPollutantCategory">
				<af:inputText readOnly="true"
					value="#{ghgEmissions.pollutantCategory}" />
			</af:column>
			<af:column headerText="Pre-Controlled Potential Emissions (tons/yr)"
				sortable="true" sortProperty="preCtlPotentialEmissions" formatType="number" id="ghgPCPEtons_yr">
				<af:inputText readOnly="#{! applicationDetail.editMode}"
					value="#{ghgEmissions.preCtlPotentialEmissions}" columns="10" id="preCtlPotentialEmissions"
					 label="Pre-Controlled Potential Emissions (tons/yr)">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.ghgEmissionsValueFormat}" />
				</af:inputText>
			</af:column>
			<af:column headerText="Efficiency Standards" formatType="number"
				id="capPTE">
				<af:column headerText="Potential to Emit (PTE)*" sortable="true"
					sortProperty="potentialToEmit" formatType="number"
					id="ghgPTE">
					<af:inputText readOnly="#{! applicationDetail.editMode}"
						value="#{ghgEmissions.potentialToEmit}" columns="10" id="potentialToEmit" label="Potential to Emit (PTE)*">
						<mu:convertSigDigNumber
							pattern="#{applicationDetail.ghgEmissionsValueFormat}" />
					</af:inputText>
				</af:column>
				<af:column headerText="Units*">
					<af:selectOneChoice id="unitCd" label="Units:" readOnly="true"
						value="#{ghgEmissions.unitCd}" showRequired="true">
						<f:selectItems
							value="#{applicationDetail.paeuPteUnitsDefs.items[(empty ghgEmissions.unitCd ? '' : ghgEmissions.unitCd)]}" />
					</af:selectOneChoice>
				</af:column>
			</af:column>
			<af:column headerText="Potential to Emit (PTE) (lbs/hr)*"
				sortable="true" sortProperty="potentialToEmitLbHr" formatType="number"
				id="ghgPTE_hr">
				<af:inputText readOnly="#{! applicationDetail.editMode}"
					value="#{ghgEmissions.potentialToEmitLbHr}" columns="10" id="potentialToEmitLbHr"  label="Potential to Emit (PTE) (lbs/hr)*">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.ghgEmissionsValueFormat}" />
				</af:inputText>
			</af:column>
			<af:column headerText="Potential to Emit (PTE) (tons/yr)*"
				sortable="true" sortProperty="potentialToEmitTonYr" formatType="number" id="ghgPTE_yr"  >
				<af:inputText readOnly="#{! applicationDetail.editMode}"
					value="#{ghgEmissions.potentialToEmitTonYr}" columns="10" id="potentialToEmitTonYr"  label="Potential to Emit (PTE) (tons/yr)*">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.ghgEmissionsValueFormat}" />
				</af:inputText>
			</af:column>
			<af:column headerText="Basis for Determination*" id="ghgBFD">
				<af:selectOneChoice id="AppBasisForDeterminationDropdown" valueChangeListener="#{applicationDetail.refreshAttachments}" 
					readOnly="true" autoSubmit="true" label="Basis for Determination*"
					unselectedLabel=" " inlineStyle="color: #000000;"
					value="#{ghgEmissions.pteDeterminationBasisCd}">
					<f:selectItems
						value="#{applicationDetail.paeuPteDeterminationBasisDefs}" />
				</af:selectOneChoice>
			</af:column>
			<f:facet name="footer">
				<af:panelButtonBar>
					<af:commandButton text="Add" id="addGhgPollutant" useWindow="true"
						windowWidth="500" windowHeight="300"
						rendered="#{applicationDetail.editMode}"
						returnListener="#{applicationDetail.emissionsDialogDone}"
						action="#{applicationDetail.startAddEmissionsInfo}">
						<t:updateActionListener
							property="#{applicationDetail.pollutantType}"
							value="#{applicationDetail.GHG}" />
					</af:commandButton>
					<af:commandButton text="Delete Selected GHGs"
						rendered="#{applicationDetail.editMode}"
						actionListener="#{applicationDetail.initActionTable}"
						action="#{applicationDetail.deleteEmissions}">
					</af:commandButton>
				</af:panelButtonBar>
			</f:facet>
		</af:table>

		<af:outputText inlineStyle="font-size:75%;color:#666"
			value="* Provide your calculations as an attachment and explain how all process
		variables and emissions factors were selected. Note the emission factor(s)
		employed and document origin. Example: AP-42, Table 4.4-3 (8/97); stack test,
		Method 5, 4/96; mass balance based on MSDS; etc." />
		<af:objectSpacer height="1" />
		<af:outputText inlineStyle="font-size:75%;color:#666"
			value="** AQD Calculated - See 'Help' for more information." />
	</af:showDetailHeader>
	<%
		/* Emissions Info end */
	%>

	<%
		/* BACT begin */
	%>
	<af:showDetailHeader disclosed="true"
		text="Best Available Control Technology (BACT)"
		partialTriggers="bactAnalysisCompleted">

		<af:selectOneChoice id="isPsdBactAnalysis"
			label="Is this unit subject to PSD BACT? :"
			readOnly="#{! applicationDetail.editMode}" unselectedLabel=" "
			value="#{applicationDetail.selectedEU.psdBACTFlag}"
			rendered="#{applicationDetail.application.psdSubjectToReg}"
			autoSubmit="true">
			<af:selectItem label="Yes" value="Y" />
			<af:selectItem label="No" value="N" />
		</af:selectOneChoice>

		<af:selectOneChoice id="bactAnalysisCompleted"
			label="Was a BACT Analysis completed for this unit? :"
			valueChangeListener="#{applicationDetail.refreshAttachments}" 
			readOnly="#{! applicationDetail.editMode}" unselectedLabel=" "
			value="#{applicationDetail.selectedEU.bactFlag}" autoSubmit="true">
			<af:selectItem label="Yes" value="Y" />
			<af:selectItem label="No" value="N" />
		</af:selectOneChoice>

		<af:panelForm
			rendered="#{applicationDetail.selectedEU.bactAnalysisCompleted}">
			<af:table value="#{applicationDetail.bactPollutantWrapper}"
				bandingInterval="1" banding="row" var="pollutant"
				binding="#{applicationDetail.bactPollutantWrapper.table}"
				width="98%">
				<af:column sortProperty="bactPollutantCd" sortable="true"
					id="bactPollutantCd" formatType="text" headerText="Pollutant">
					<af:commandLink
						text="#{facilityReference.pollutantDescs[(empty pollutant.pollutantCd ? '' : pollutant.pollutantCd)]}"
						useWindow="true" windowWidth="760" windowHeight="300"
						returnListener="#{applicationDetail.dialogDone}"
						action="#{applicationDetail.startToEditBACTEmission}">
						<t:updateActionListener
							property="#{applicationDetail.selectedBACTEmission}"
							value="#{pollutant}" />
					</af:commandLink>
				</af:column>
				<af:column sortProperty="bact" sortable="true" formatType="text"
					headerText="Proposed BACT">
					<af:outputText value="#{pollutant.bact}" />
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Add Pollutant" useWindow="true"
								windowWidth="760" windowHeight="300"
								returnListener="#{applicationDetail.dialogDone}"
								disabled="#{!applicationDetail.euEditAllowed}"
								action="#{applicationDetail.startToAddBACTEmission}" />
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</af:panelForm>
	</af:showDetailHeader>
	<%
		/* BACT end */
	%>

	<%
		/* LAER begin */
	%>
	<af:showDetailHeader disclosed="true"
		text="Lowest Achievable Emission Rate (LAER)"
		partialTriggers="laerAnalysisCompleted">

		<af:selectOneChoice id="isNsrLaerAnalysis"
			label="Is this unit subject to LAER? :"
			readOnly="#{! applicationDetail.editMode}" unselectedLabel=" "
			value="#{applicationDetail.selectedEU.nsrLAERFlag}"
			rendered="#{applicationDetail.application.nsrSubjectToReg}"
			autoSubmit="true">
			<af:selectItem label="Yes" value="Y" />
			<af:selectItem label="No" value="N" />
		</af:selectOneChoice>

		<af:selectOneChoice id="laerAnalysisCompleted"
			label="Was a LAER Analysis completed for this unit? :"
			valueChangeListener="#{applicationDetail.refreshAttachments}" 
			readOnly="#{! applicationDetail.editMode}" unselectedLabel=" "
			value="#{applicationDetail.selectedEU.laerFlag}" autoSubmit="true">
			<af:selectItem label="Yes" value="Y" />
			<af:selectItem label="No" value="N" />
		</af:selectOneChoice>

		<af:panelForm
			rendered="#{applicationDetail.selectedEU.laerAnalysisCompleted}">
			<af:table value="#{applicationDetail.laerPollutantWrapper}"
				bandingInterval="1" banding="row" var="pollutant"
				binding="#{applicationDetail.laerPollutantWrapper.table}"
				width="98%">
				<af:column sortProperty="laerPollutantCd" sortable="true"
					id="laerPollutantCd" formatType="text" headerText="Pollutant">
					<af:commandLink
						text="#{facilityReference.pollutantDescs[(empty pollutant.pollutantCd ? '' : pollutant.pollutantCd)]}"
						useWindow="true" windowWidth="760" windowHeight="300"
						returnListener="#{applicationDetail.dialogDone}"
						action="#{applicationDetail.startToEditLAEREmission}">
						<t:updateActionListener
							property="#{applicationDetail.selectedLAEREmission}"
							value="#{pollutant}" />
					</af:commandLink>
				</af:column>
				<af:column sortProperty="laer" sortable="true" formatType="text"
					headerText="Proposed LAER">
					<af:outputText value="#{pollutant.laer}" />
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Add Pollutant" useWindow="true"
								windowWidth="760" windowHeight="300"
								returnListener="#{applicationDetail.dialogDone}"
								disabled="#{!applicationDetail.euEditAllowed}"
								action="#{applicationDetail.startToAddLAEREmission}" />
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</af:panelForm>
	</af:showDetailHeader>
	<%
		/* LAER end */
	%>

	<%
		/* Federal rules begin */
	%>
	<af:showDetailHeader id="FederalRulesShowDetail" disclosed="true"
		text="Federal and State Rule Applicability">
		<af:panelGroup id="fedRulesPanel"
			partialTriggers="NSPSChoice NESHAPChoice MACTChoice"
			layout="vertical">
			<af:panelForm id="fedRulesForm">
				<af:selectOneChoice id="NSPSChoice" autoSubmit="true"
					label="New Source Performance Standards (NSPS) :"
					readOnly="#{! applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.selectedEU.nspsApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.federalRuleAppl1Defs.items[(empty applicationDetail.selectedEU.nspsApplicableFlag? '': applicationDetail.selectedEU.nspsApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText id="dummyNSPSText"
					label="New Source Performance Standards are listed under 40 CFR"
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:inputText id="dummyNSPSText2"
					label="60 - Standards of Performance for New Stationary Sources."
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<%
					/* NSPS Subparts table */
				%>
				<af:table value="#{applicationDetail.nspsSubparts}"
					bandingInterval="1" banding="row" var="nspsSubpart"
					rendered="#{applicationDetail.displayNSPSSubparts}">
					<f:facet name="selection">
						<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
					</f:facet>
					<af:column sortProperty="value" sortable="true" formatType="text"
						headerText="NSPS Subpart">
						<af:selectOneChoice value="#{nspsSubpart.value}"
							readOnly="#{! applicationDetail.editMode}">
							<f:selectItems value="#{applicationReference.nspsSubpartDefs}" />
						</af:selectOneChoice>
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton text="Add Subpart" id="addNSPS"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.addActionTableRow}">
									<t:updateActionListener
										property="#{applicationDetail.actionTableNewObject}"
										value="#{applicationDetail.newStars2Object}" />
								</af:commandButton>
								<af:commandButton text="Delete Selected Subparts"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.deleteActionTableRows}">
								</af:commandButton>
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>
				<af:selectOneChoice id="NESHAPChoice" autoSubmit="true"
					label="National Emission Standards for Hazardous Air Pollutants (NESHAP Part 61) :"
					readOnly="#{! applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.selectedEU.neshapApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.federalRuleAppl1Defs.items[(empty applicationDetail.selectedEU.neshapApplicableFlag? '': applicationDetail.selectedEU.neshapApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText id="dummyNESHAPText1"
					label="National Emissions Standards for Hazardous Air Pollutants (NESHAP Part 61) are listed under 40"
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic; text-align: left;" />
				<af:inputText id="dummyNESHAPText2"
					label="CFR 61. (These include asbestos, benzene, beryllium, mercury, and vinyl chloride)."
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic; text-align: left;" />
				<%
					/* NESHAP Subparts table */
				%>
				<af:table value="#{applicationDetail.neshapSubparts}"
					bandingInterval="1" banding="row" var="neshapSubpart"
					rendered="#{applicationDetail.displayNESHAPSubparts}">
					<f:facet name="selection">
						<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
					</f:facet>
					<af:column sortProperty="value" sortable="true" formatType="text"
						headerText="Part 61 NESHAP Subpart">
						<af:selectOneChoice value="#{neshapSubpart.value}"
							readOnly="#{! applicationDetail.editMode}">
							<f:selectItems value="#{applicationReference.neshapSubpartDefs}" />
						</af:selectOneChoice>
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton text="Add Subpart" id="addNESHAP"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.addActionTableRow}">
									<t:updateActionListener
										property="#{applicationDetail.actionTableNewObject}"
										value="#{applicationDetail.newStars2Object}" />
								</af:commandButton>
								<af:commandButton text="Delete Selected Subparts"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.deleteActionTableRows}">
								</af:commandButton>
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>
				<af:selectOneChoice id="MACTChoice" autoSubmit="true"
					label="National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) :"
					readOnly="#{! applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.selectedEU.mactApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.federalRuleAppl1Defs.items[(empty applicationDetail.selectedEU.mactApplicableFlag? '': applicationDetail.selectedEU.mactApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText id="dummyMACTText"
					label="National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) standards"
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:inputText id="dummyMACTText2"
					label="are listed under 40 CFR 63." readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />

				<%
					/* MACT Subparts table */
				%>
				<af:table value="#{applicationDetail.mactSubparts}"
					bandingInterval="1" banding="row" var="mactSubpart"
					rendered="#{applicationDetail.displayMACTSubparts}">
					<f:facet name="selection">
						<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
					</f:facet>
					<af:column sortProperty="value" formatType="text"
						headerText="Part 63 NESHAP Subpart">
						<af:selectOneChoice value="#{mactSubpart.value}"
							readOnly="#{! applicationDetail.editMode}">
							<f:selectItems value="#{applicationReference.mactSubpartDefs}" />
						</af:selectOneChoice>
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton text="Add Subpart" id="addMACT"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.addActionTableRow}">
									<t:updateActionListener
										property="#{applicationDetail.actionTableNewObject}"
										value="#{applicationDetail.newStars2Object}" />
								</af:commandButton>
								<af:commandButton text="Delete Selected Subparts"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.deleteActionTableRows}">
								</af:commandButton>
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>
				<af:selectOneChoice id="PSDChoice"
					label="Prevention of Significant Deterioration (PSD) :"
					readOnly="#{! applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.selectedEU.psdApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.nsrEuFederalRuleAppDefs.items[(empty applicationDetail.selectedEU.psdApplicableFlag? '': applicationDetail.selectedEU.psdApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText id="dummyPSDTText" label="These rules are found under WAQSR Chapter 6, Section 4." readOnly="true"
					secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:selectOneChoice id="NSRChoice"
					label="Non-Attainment New Source Review :"
					readOnly="#{! applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.selectedEU.nsrApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.nsrEuFederalRuleAppDefs.items[(empty applicationDetail.selectedEU.nsrApplicableFlag? '': applicationDetail.selectedEU.nsrApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText id="dummyNSRText"
					label="These rules are found under WAQSR Chapter 6, Section 13." readOnly="true"
					secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:panelForm rendered="#{applicationDetail.fedRulesExemption}"
					partialTriggers="NSPSChoice NESHAPChoice MACTChoice">
					<af:outputLabel for="exemptionExplPanel"
						value="Please explain why you checked &quot;Subject, but exempt&quot; 
          in this question for one or more federal rules. Identify each 
          exemption and whether the entire facility and/or the specific air 
          contaminant sources included in this permit application is exempted. 
          Attach an additional page if necessary." />
					<af:panelGroup id="exemptionExplPanel">
						<af:inputText id="exemptionExplText"
							readOnly="#{! applicationDetail.editMode}"
							value="#{applicationDetail.selectedEU.federalRuleApplicabilityExplanation}"
							columns="120" rows="4" maximumLength="1000" />
					</af:panelGroup>
				</af:panelForm>
			</af:panelForm>
		</af:panelGroup>
	</af:showDetailHeader>
	<%
		/* Federal rules end */
	%>

</af:panelGroup>

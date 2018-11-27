<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup layout="vertical"
	rendered="#{facilityProfile.renderComponent == 'emissionUnits'}">
	<af:panelHeader text="Emissions Unit Information" size="0" />
	<af:panelForm rows="1" maxColumns="2">
		<af:inputText label="AQD ID:"
			value="#{facilityProfile.emissionUnit.epaEmuId}" columns="9"
			maximumLength="9" readOnly="true" />
		<af:panelForm rows="1">
			<af:outputFormatted value="&nbsp;" />
		</af:panelForm>
		<af:commandLink text="View Emission Unit change log"
			inlineStyle="margin-left:10px;padding:5px;"
			action="#{facilityProfile.displayEUTypeChangeLog}"
			rendered="#{facilityProfile.emissionUnit.corrEpaEmuId != null && !facilityProfile.editable && facilityProfile.dapcUser}" />
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2">
		<af:selectOneChoice label="Emission Unit Type:" id="emissionUnitType"
			value="#{facilityProfile.emissionUnit.emissionUnitTypeCd}"
			readOnly="#{!facilityProfile.allowedToChangeEUType}"
			showRequired="true" autoSubmit="true">
			<f:selectItems
				value="#{facilityReference.emissionUnitTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitTypeCd ? '' : facilityProfile.emissionUnit.emissionUnitTypeCd)]}" />
		</af:selectOneChoice>
		<af:panelForm rows="1">
			<af:outputFormatted value="&nbsp;" />
		</af:panelForm>
		<af:commandLink
			text="#{facilityProfile.publicApp ? 'Emission Unit Type Descriptions' : 'Help me select the Emission Unit Type'}"
			useWindow="true" blocking="false" windowWidth="800"
			windowHeight="600" inlineStyle="margin-left:10px;padding:5px;"
			returnListener="#{facilityProfile.dialogDone}"
			action="#{facilityProfile.displayEUTypeHelpInfo}" />
	</af:panelForm>
	<af:panelForm maxColumns="3" width="600">
		<af:inputText label="AQD Description:" id="euDesc" columns="80"
			rows="4" maximumLength="1000"
			value="#{facilityProfile.emissionUnit.euDesc}"
			readOnly="#{! facilityProfile.editable}"
			disabled="#{! facilityProfile.dapcUser}"
			showRequired="#{facilityProfile.dapcUser}" />
		<af:inputText label="AQD WISE Emission Unit ID:"
			value="#{facilityProfile.emissionUnit.wiseViewId}" id="wiseViewId"
			columns="12" maximumLength="12"
			readOnly="#{! facilityProfile.editable}"
			rendered="#{ infraDefs.hidden && (infraDefs.stars2Admin || facilityProfile.allowedToEditWiseViewId)}" />
		<af:inputText label="Company Equipment ID:"
			value="#{facilityProfile.emissionUnit.companyId}" id="companyId"
			columns="12" maximumLength="12"
			readOnly="#{! facilityProfile.editable}" showRequired="true" />
		<af:inputText label="Company Equipment Description:" rows="4"
			value="#{facilityProfile.emissionUnit.regulatedUserDsc}"
			id="regulatedUserDsc" columns="80" maximumLength="240"
			readOnly="#{! facilityProfile.editable}" showRequired="true" />
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="3" width="600"
		partialTriggers="euOperatingStatus">
		<af:selectOneChoice label="Operating Status:"
			inlineStyle="width:250px;display:block;"
			value="#{facilityProfile.emissionUnit.operatingStatusCd}"
			showRequired="true" id="euOperatingStatus"
			readOnly="#{! facilityProfile.euOperStatusUpdatable}"
			autoSubmit="true">
			<f:selectItems
				value="#{facilityProfile.euOperatingStatusDefs.items[(empty facilityProfile.emissionUnit.operatingStatusCd ? '' : facilityProfile.emissionUnit.operatingStatusCd)]}" />
		</af:selectOneChoice>
		<af:selectInputDate label="Shutdown Date:"
			value="#{facilityProfile.emissionUnit.euShutdownDate}"
			id="euShutdownDate" showRequired="true"
			readOnly="#{! facilityProfile.editable}"
			rendered="#{facilityProfile.emissionUnit.operatingStatusCd == 'sd'}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
		<af:selectInputDate label="Shutdown Notification Date:"
			value="#{facilityProfile.emissionUnit.euShutdownNotificationDate}"
			id="euShutdownNotificationDate" showRequired="true"
			readOnly="#{! facilityProfile.editable}"
			rendered="#{facilityProfile.emissionUnit.operatingStatusCd == 'sd' && facilityProfile.facility.permitClassCd != null && facilityProfile.facility.permitClassCd == 'tv'}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
	</af:panelForm>
	<af:panelForm rows="3" maxColumns="1" width="600"
		partialTriggers="euOperatingStatus emissionUnitType">
		<af:selectInputDate label="Initial Construction Commencement Date:"
			value="#{facilityProfile.emissionUnit.euInitInstallDate}"
			id="euInitInstallDate" readOnly="#{! facilityProfile.editable}"
			showRequired="#{facilityProfile.emissionUnit.emissionsUnitDatesRequired}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
		<af:selectInputDate label="Initial Operation Commencement Date:"
			value="#{facilityProfile.emissionUnit.euInitStartupDate}"
			id="euInitStartupDate" readOnly="#{! facilityProfile.editable}"
			showRequired="#{facilityProfile.emissionUnit.emissionsUnitDatesRequired}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
		<af:selectInputDate
			label="Most Recent Construction/Modification Commencement Date:"
			value="#{facilityProfile.emissionUnit.euInstallDate}"
			id="euInstallDate" readOnly="#{! facilityProfile.editable}">
			<af:validateDateTimeRange maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
		<af:selectInputDate label="Most Recent Operation Commencement Date:"
			value="#{facilityProfile.emissionUnit.euStartupDate}"
			id="euStartupDate" readOnly="#{! facilityProfile.editable}">
			<af:validateDateTimeRange maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
	</af:panelForm>
	<af:panelForm rendered="#{facilityProfile.displayUncertainArea}">
		<af:panelHeader text="Permitting Classification and Status" size="1" />
		<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600">
			<af:selectOneChoice label=" Permit Status:" unselectedLabel=" "
				rendered="#{facilityProfile.stars2Admin}" id="euPtioStatusCd"
				value="#{facilityProfile.emissionUnit.ptioStatusCd}"
				readOnly="#{facilityProfile.demReadOnlyAttr || !facilityProfile.stars2Admin}">
				<f:selectItems
					value="#{infraDefs.ptioRegulatoryStatuses.items[(empty facilityProfile.emissionUnit.ptioStatusCd ? '' : facilityProfile.emissionUnit.ptioStatusCd)]}" />
			</af:selectOneChoice>
			<af:selectOneChoice label="NSR Status:" unselectedLabel=" "
				rendered="#{facilityProfile.stars2Admin}"
				value="#{facilityProfile.emissionUnit.ptiStatusCd}"
				id="euPtiStatusCd"
				readOnly="#{facilityProfile.demReadOnlyAttr || !facilityProfile.stars2Admin}">
				<f:selectItems
					value="#{infraDefs.ptiRegulatoryStatuses.items[(empty facilityProfile.emissionUnit.ptiStatusCd ? '' : facilityProfile.emissionUnit.ptiStatusCd)]}" />
			</af:selectOneChoice>
			<af:selectOneChoice label="Title V EU Classification:"
				unselectedLabel=" " id="euTvClassCd"
				value="#{facilityProfile.emissionUnit.tvClassCd}"
				readOnly="#{facilityProfile.dapcReadOnlyAdminAttr}">
				<f:selectItems
					value="#{infraDefs.tvClassifications.items[(empty facilityProfile.emissionUnit.tvClassCd ? '' : facilityProfile.emissionUnit.tvClassCd)]}" />
			</af:selectOneChoice>
			<af:selectOneChoice label="Exemption Status:" unselectedLabel=" "
				value="#{facilityProfile.emissionUnit.exemptStatusCd}"
				id="euExemptStatusCd" readOnly="#{! facilityProfile.editable}">
				<f:selectItems
					value="#{facilityReference.exemptStatusDefs.items[(empty facilityProfile.emissionUnit.exemptStatusCd ? '' : facilityProfile.emissionUnit.exemptStatusCd)]}" />
			</af:selectOneChoice>
		</af:panelForm>
		<af:objectSpacer height="20" />
		<afh:rowLayout halign="center">
			<af:commandLink text="Permit History"
				action="#{facilityProfile.getAllEuPermitDialog}" useWindow="true"
				windowWidth="1000" windowHeight="800" />
		</afh:rowLayout>
		<af:showDetailHeader text="EIS Information" disclosed="true"
			id="euEisInfo">
			<af:panelForm rows="1" maxColumns="1" labelWidth="300" width="600"
				partialTriggers="designCapacityCd">
				<af:selectOneChoice
					label="Boiler/Turbine/Generator Design Capacity:"
					value="#{facilityProfile.emissionUnit.designCapacityCd}"
					id="designCapacityCd" readOnly="true">
					<mu:selectItems value="#{facilityReference.designCapacityDefs}" />
				</af:selectOneChoice>
			</af:panelForm>
			<af:panelForm rows="1" maxColumns="2" width="600"
				partialTriggers="designCapacityCd">
				<af:inputText label="Design Capacity:" id="designCapacityUnitsVal"
					value="#{facilityProfile.emissionUnit.designCapacityUnitsVal}"
					readOnly="#{! facilityProfile.editable}"
					showRequired="#{facilityProfile.emissionUnit.operatingStatusCd != 'sd'}"
					columns="10" maximumLength="10"
					rendered="#{facilityProfile.emissionUnit.designCapacityCd != null && facilityProfile.emissionUnit.designCapacityCd != 'na'}">
					<mu:convertSigDigNumber pattern="##,##0.##" />
				</af:inputText>
				<af:selectOneChoice label="Design Capacity Units:"
					id="designCapacityUnitsCd"
					value="#{facilityProfile.emissionUnit.designCapacityUnitsCd}"
					readOnly="#{! facilityProfile.editable}"
					showRequired="#{facilityProfile.emissionUnit.operatingStatusCd != 'sd'}"
					rendered="#{facilityProfile.emissionUnit.designCapacityCd != null && facilityProfile.emissionUnit.designCapacityCd != 'na'}">
					<mu:selectItems
						value="#{facilityReference.designCapacityUnitsDefs}" />
				</af:selectOneChoice>
			</af:panelForm>
			<af:panelForm rows="1" maxColumns="2" width="600">
				<af:inputText label="ORIS Boiler ID:" id="orisBoilerId"
					value="#{facilityProfile.emissionUnit.orisBoilerId}" columns="20"
					maximumLength="20" readOnly="#{! facilityProfile.editable}" />
			</af:panelForm>
		</af:showDetailHeader>
	</af:panelForm>
	<af:showDetailHeader text="Emission Unit Type Specific Information"
		disclosed="true">
		<af:panelForm partialTriggers="emissionUnitType">
			<af:panelGroup layout="vertical"
				rendered="#{empty facilityProfile.emissionUnit.emissionUnitTypeCd ? false : true}">
				<af:objectSpacer height="10" />
				<f:subview id="emissionUnitTypeABS"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'ABS'}">
				<jsp:include flush="true" page="emissionUnitTypeABS.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeACB"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'ACB'}">
				<jsp:include flush="true" page="emissionUnitTypeACB.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeAMN"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'AMN'}">
					<jsp:include flush="true" page="emissionUnitTypeAMN.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeAPT"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'APT'}">
					<jsp:include flush="true" page="emissionUnitTypeAPT.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeBAK"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'BAK'}">
				<jsp:include flush="true" page="emissionUnitTypeBAK.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeBGM"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'BGM'}">
				<jsp:include flush="true" page="emissionUnitTypeBGM.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeBOL"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'BOL'}">
					<jsp:include flush="true" page="emissionUnitTypeBOL.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeBVC"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'BVC'}">
					<jsp:include flush="true" page="emissionUnitTypeBVC.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeCCU"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'CCU'}">
					<jsp:include flush="true" page="emissionUnitTypeCCU.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeCKD"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'CKD'}">
					<jsp:include flush="true" page="emissionUnitTypeCKD.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeCMX"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'CMX'}">
					<jsp:include flush="true" page="emissionUnitTypeCMX.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeCOT"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'COT'}">
					<jsp:include flush="true" page="emissionUnitTypeCOT.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeCSH"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'CSH'}">
					<jsp:include flush="true" page="emissionUnitTypeCSH.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeCTW"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'CTW'}">
					<jsp:include flush="true" page="emissionUnitTypeCTW.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeDHY"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'DHY'}">
					<jsp:include flush="true" page="emissionUnitTypeDHY.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeDIS"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'DIS'}">
				<jsp:include flush="true" page="emissionUnitTypeDIS.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeDRY"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'DRY'}">
				<jsp:include flush="true" page="emissionUnitTypeDRY.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeEGU"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'EGU'}">
					<jsp:include flush="true" page="emissionUnitTypeEGU.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeENG"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'ENG'}">
					<jsp:include flush="true" page="emissionUnitTypeENG.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeFAT"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'FAT'}">
				<jsp:include flush="true" page="emissionUnitTypeFAT.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeFLR"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'FLR'}">
					<jsp:include flush="true" page="emissionUnitTypeFLR.jsp" />
				</f:subview>
				<%-- FUG --%>
				<f:subview id="emissionUnitTypeFUG"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'FUG'}">
					<jsp:include flush="true" page="emissionUnitTypeFUG.jsp" />
				</f:subview>				
				
				<f:subview id="emissionUnitTypeGIN"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'GIN'}">
				<jsp:include flush="true" page="emissionUnitTypeGIN.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeGRI"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'GRI'}">
				<jsp:include flush="true" page="emissionUnitTypeGRI.jsp" />
				</f:subview>															
				<f:subview id="emissionUnitTypeHET"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'HET'}">
					<jsp:include flush="true" page="emissionUnitTypeHET.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeHMA"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'HMA'}">
					<jsp:include flush="true" page="emissionUnitTypeHMA.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeINC"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'INC'}">
					<jsp:include flush="true" page="emissionUnitTypeINC.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeLUD"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'LUD'}">
					<jsp:include flush="true" page="emissionUnitTypeLUD.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeMAC"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'MAC'}">
				<jsp:include flush="true" page="emissionUnitTypeMAC.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeMAT"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'MAT'}">
				<jsp:include flush="true" page="emissionUnitTypeMAT.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeMET"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'MET'}">
				<jsp:include flush="true" page="emissionUnitTypeMET.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeMIL"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'MIL'}">
				<jsp:include flush="true" page="emissionUnitTypeMIL.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeMIX"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'MIX'}">
				<jsp:include flush="true" page="emissionUnitTypeMIX.jsp" />
				</f:subview>															
				<f:subview id="emissionUnitTypeMLD"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'MLD'}">
				<jsp:include flush="true" page="emissionUnitTypeMLD.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeOEP"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'OEP'}">
				<jsp:include flush="true" page="emissionUnitTypeOEP.jsp" />
				</f:subview>																																																																																																																																																																												
				<f:subview id="emissionUnitTypeORD"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'ORD'}">
				<jsp:include flush="true" page="emissionUnitTypeORD.jsp" />
				</f:subview>																																																																																																																																																																												
				<f:subview id="emissionUnitTypeOZG"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'OZG'}">
				<jsp:include flush="true" page="emissionUnitTypeOZG.jsp" />
				</f:subview>	
				<f:subview id="emissionUnitTypePAM"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'PAM'}">
				<jsp:include flush="true" page="emissionUnitTypePAM.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypePEL"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'PEL'}">
				<jsp:include flush="true" page="emissionUnitTypePEL.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypePNE"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'PNE'}">
					<jsp:include flush="true" page="emissionUnitTypePNE.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypePRN"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'PRN'}">
				<jsp:include flush="true" page="emissionUnitTypePRN.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeREM"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'REM'}">
				<jsp:include flush="true" page="emissionUnitTypeREM.jsp" />
				</f:subview>																																																																																																																																																																												
				<f:subview id="emissionUnitTypeRES"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'RES'}">
				<jsp:include flush="true" page="emissionUnitTypeRES.jsp" />
				</f:subview>																																																																																																																																																																												
				<f:subview id="emissionUnitTypeSEB"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'SEB'}">
					<jsp:include flush="true" page="emissionUnitTypeSEB.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeSEM"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'SEM'}">
				<jsp:include flush="true" page="emissionUnitTypeSEM.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeSEP"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'SEP'}">
					<jsp:include flush="true" page="emissionUnitTypeSEP.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeSRU"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'SRU'}">
					<jsp:include flush="true" page="emissionUnitTypeSRU.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeSTZ"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'STZ'}">
				<jsp:include flush="true" page="emissionUnitTypeSTZ.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeSVC"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'SVC'}">
				<jsp:include flush="true" page="emissionUnitTypeSVC.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeSVU"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'SVU'}">
				<jsp:include flush="true" page="emissionUnitTypeSVU.jsp" />
				</f:subview>																																																																																																																																																																												
				<f:subview id="emissionUnitTypeTAR"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'TAR'}">
				<jsp:include flush="true" page="emissionUnitTypeTAR.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeTGT"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'TGT'}">
					<jsp:include flush="true" page="emissionUnitTypeTGT.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeTIM"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'TIM'}">
					<jsp:include flush="true" page="emissionUnitTypeTIM.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeTKO"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'TKO'}">
				<jsp:include flush="true" page="emissionUnitTypeTKO.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeTNK"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'TNK'}">
					<jsp:include flush="true" page="emissionUnitTypeTNK.jsp" />
				</f:subview>
				<%-- VNT --%>
				<f:subview id="emissionUnitTypeWEL"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'WEL'}">
				<jsp:include flush="true" page="emissionUnitTypeWEL.jsp" />
				</f:subview>
				<f:subview id="emissionUnitTypeWWE"
					rendered="#{facilityProfile.emissionUnit.emissionUnitTypeCd == 'WWE'}">
				<jsp:include flush="true" page="emissionUnitTypeWWE.jsp" />
				</f:subview>
				
				<af:objectSpacer height="10" />
			</af:panelGroup>
		</af:panelForm>
	</af:showDetailHeader>
	<af:showDetailHeader text="Permitted Emissions" disclosed="true"
		id="allowEmission">
		<af:outputText inlineStyle="font-size:75%;color:#666"
			rendered="#{!applicationDetail.internalApp}"
			value="This table is populated by AQD staff based on established/permitted emission limits. It is shown here for informational purposes only." />
		<af:panelForm>
			<af:table value="#{facilityProfile.euEmissionsWrapper}"
				bandingInterval="1" banding="row" var="emission"
				binding="#{facilityProfile.euEmissionsWrapper.table}" width="98%"
				id="permittedEmissions">
				<%-- 				<f:facet name="selection"> --%>
				<%-- 					<af:tableSelectMany --%>
				<%-- 						rendered="#{facilityProfile.potentialEmissionsEditable}" /> --%>
				<%-- 				</f:facet> --%>
				<af:column sortProperty="c01" sortable="true" id="pollutantCd"
					formatType="text" headerText="Pollutant">
					<af:commandLink
						text="#{facilityReference.euPollutantDescs[(empty emission.pollutantCd ? '' : emission.pollutantCd)]}"
						disabled="#{!facilityProfile.dapcUser}"
						action="#{facilityProfile.startToEditPTE}" actionListener="#{facilityProfile.savePermittedEmissions}" useWindow="true"
						windowWidth="760" windowHeight="300"
						returnListener="#{facilityProfile.dialogDone}" />
				</af:column>
				<af:column formatType="icon" headerText="Potential Emissions">
					<af:column formatType="text" headerText="Lbs/Hour">
						<af:inputText value="#{emission.potentialEmissionsLbsHour}"
							id="potentialEmissionsLbsHour" columns="20" maximumLength="20"
							readOnly="true">
							<af:convertNumber type='number' locale="en-US" maxFractionDigits="6"/>
						</af:inputText>
					</af:column>
					<af:column formatType="text" headerText="Tons/Year">
						<af:inputText value="#{emission.potentialEmissionsTonsYear}"
							id="potentialEmissionsTonsYear" columns="20" maximumLength="20"
							readOnly="true">
							<af:convertNumber type='number' locale="en-US" maxFractionDigits="6"/>
						</af:inputText>
					</af:column>
				</af:column>
				<af:column formatType="icon" headerText="Allowable Emissions">
					<af:column formatType="text" headerText="Lbs/Hour">
						<af:inputText value="#{emission.allowableEmissionsLbsHour}"
							id="allowableEmissionsLbsHour" columns="20" maximumLength="20"
							readOnly="true">
							<af:convertNumber type='number' locale="en-US" maxFractionDigits="6" />
						</af:inputText>
					</af:column>
					<af:column formatType="text" headerText="Tons/Year">
						<af:inputText value="#{emission.allowableEmissionsTonsYear}"
							id="allowableEmissionsTonsYear" columns="20" maximumLength="20"
							readOnly="true">
							<af:convertNumber type='number' locale="en-US" maxFractionDigits="6" />
						</af:inputText>
					</af:column>
				</af:column>
				<af:column sortProperty="comment" sortable="true" formatType="text"
					headerText="Comments">
					<af:inputText value="#{emission.comment}" columns="40"
						maximumLength="100" readOnly="true" />
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Add Emissions"
								action="#{facilityProfile.startToAddPTE}"
								actionListener="#{facilityProfile.savePermittedEmissions}"
								rendered="#{facilityProfile.dapcUser}" useWindow="true"
								windowWidth="760" windowHeight="300"
								disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
								returnListener="#{facilityProfile.dialogDone}"
								id="euAddEmissions">
							</af:commandButton>
							<%-- 							<af:commandButton text="Delete Selected Pollutants" --%>
							<%-- 								rendered="#{facilityProfile.potentialEmissionsEditable}" --%>
							<%-- 								action="#{facilityProfile.deleteEuEmissions}"> --%>
							<%-- 							</af:commandButton> --%>
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
	<af:objectSpacer height="20" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar partialTriggers="euOperatingStatus"
			onmouseover="triggerEmissionsUnitUpdate();">
			<af:commandButton text="Edit"
				action="#{facilityProfile.editEmissionUnit}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="editEUBtn" />
			<af:commandButton text="Create Cloned Emissions Unit"
				action="#{facilityProfile.cloneEmissionUnit}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.addToEuDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="createClonedEU" />
			<af:commandButton text="Remove Emission Unit" id="removeEUBtn"
				action="dialog:confirmRemoveEU" partialSubmit="true"
				useWindow="true" windowWidth="600" windowHeight="300"
				returnListener="#{facilityProfile.confirmRemoveEUReturned}"
				rendered="#{facilityProfile.allowEuRemoval}" />
			<af:commandButton text="Delete Emission Unit" id="deleteEUBtn"
				action="dialog:confirmDeleteEU"
				useWindow="true" windowWidth="600" windowHeight="300"
				rendered="#{!facilityProfile.editable && facilityProfile.allowEuDelete}" />	
			<af:commandButton text="Save" id="saveNormal"
				action="#{facilityProfile.saveEmissionUnit}"
				rendered="#{facilityProfile.normalEuShutDown}" />
			<af:commandButton text="Save" id="saveConfirm"
				action="dialog:confirmShutdownEuOp" partialSubmit="true"
				useWindow="true" windowWidth="600" windowHeight="150"
				returnListener="#{facilityProfile.confirmReturned}"
				rendered="#{facilityProfile.confirmEuShutDown}" />
			<af:commandButton text="Cancel"
				action="#{facilityProfile.cancelEmissionUnit}"
				rendered="#{facilityProfile.editable}" immediate="true"
				id="euCancelBtn" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Create Emissions Process"
				action="#{facilityProfile.addEmissionProcess}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.addToEuDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="euCreateEmissionsProcess" />
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>
<f:verbatim>
	<script type="text/javascript">
				function triggerEmissionsUnitUpdate(){
					var activeId = document.activeElement.id;
					if(activeId != null){
			    		input = document.getElementById(activeId);
			    		input.blur();
			    		input.focus();
					} 
				}
			</script>
</f:verbatim>
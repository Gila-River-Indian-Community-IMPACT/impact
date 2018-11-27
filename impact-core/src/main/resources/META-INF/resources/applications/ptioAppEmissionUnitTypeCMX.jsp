<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Concrete Batch/Cement Mixer"
	inlineStyle="font-size: 13px; font-weight: bold;"/>
<af:objectSpacer height="15"/>
<af:panelForm width="600" labelWidth="150" maxColumns="2">

	<af:outputText value="Material Used : "
			inlineStyle="font-size: 13px;font-weight:bold" />
	<af:table emptyText=" " var="materialUsed" bandingInterval="1"
		banding="row" value="#{applicationDetail.materialUsed}" width="98%"
		id="materialUsedTable">
		<af:column formatType="text" headerText="Material Used"
			id="materialUsed">
			<af:selectOneChoice id="materialUsedCd" readOnly="true"
				value="#{materialUsed.materialUsedCd}">
				<f:selectItems value="#{applicationDetail.materialUsedDefs}" />
			</af:selectOneChoice>
		</af:column>
		<af:column headerText="Amount*" formatType="number">
			<af:inputText readOnly="#{! applicationDetail.editMode}" id="materialAmount"
					value="#{materialUsed.materialAmount}" columns="10">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.cmxEmissionsMaterialUsedValueFormat}"
						nonNumericAllowed="false"/>
			</af:inputText>

		</af:column>
		<af:column headerText="Units*" id="materialUsedUnits">

			
				<af:selectOneChoice id="UnitCd" 
					readOnly="#{! applicationDetail.editMode}"
					value="#{materialUsed.unitCd}" showRequired="true">
					<f:selectItems
						value="#{applicationReference.appEUProductionRateDefs.items[(empty materialUsed.unitCd ? '' : materialUsed.unitCd)]}" />
				</af:selectOneChoice>
			

		</af:column>

	</af:table>
	<af:objectSpacer height="20" />
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="maxAnnualProductionRate" label="Maximum Annual Production Rate :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.maxAnnualProductionRate}"
			showRequired="true" />

		<af:selectOneChoice id="maxAnnualProductionRateUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.maxAnnualProductionRateUnitsCd}"
			showRequired="true">
			<f:selectItems value="#{applicationReference.appEUProductionRateDefs.items[(empty applicationDetail.selectedEU.euType.maxAnnualProductionRateUnitsCd ? '' : applicationDetail.selectedEU.euType.maxAnnualProductionRateUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	    <af:inputText id="avgHourlyProductionRate" label="Average Hourly Production Rate :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.avgHourlyProductionRate}"
			showRequired="true" />
		<af:selectOneChoice id="avgHourlyProductionRateUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.avgHourlyProductionRateUnitsCd}"
			showRequired="true">
			<f:selectItems value="#{applicationReference.appEUProductionHourRateDefs.items[(empty applicationDetail.selectedEU.euType.avgHourlyProductionRateUnitsCd ? '' : applicationDetail.selectedEU.euType.avgHourlyProductionRateUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

</af:panelForm>

<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600">
	<af:inputText id="nameAndTypeOfMaterial"
		label="Name and Type of Material:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.nameAndTypeOfMaterial}"
		showRequired="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="substrateBlasted  substrateBlastedOther">
	<af:selectOneChoice id="substrateBlasted"
		label="Substrate being Blasted:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.substrateBlasted}"
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.substrateBlastedListener}"
		showRequired="true" autoSubmit="true">
		<f:selectItems
			value="#{facilityReference.absSubstrateBlastedTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.substrateBlasted ? '' : facilityProfile.emissionUnit.emissionUnitType.substrateBlasted)]}" />
	</af:selectOneChoice>
	<af:inputText id="substrateBlastedOther"
		label="Substrate being Blasted (Other):"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.substrateBlasted == 'other'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.substrateBlastedOther}"
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.substrateBlastedListener}"
		autoSubmit="true"
		showRequired="true" columns="12" maximumLength="80">
	</af:inputText>	
</af:panelForm>

<af:panelForm rows="1" maxColumns="4" labelWidth="150" width="600"
	partialTriggers="substrateRemoved concentrationOfLeadPct substrateRemovedOther">
	<af:selectOneChoice id="substrateRemoved"
		label="Substrate being Removed:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.substrateRemoved}"
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.substrateRemovedListener}"
		showRequired="true" autoSubmit="true">
		<f:selectItems
			value="#{facilityReference.absSubstrateRemovedTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.substrateRemoved ? '' : facilityProfile.emissionUnit.emissionUnitType.substrateRemoved)]}" />
	</af:selectOneChoice>
	<af:selectOneChoice id="concentrationOfLeadPct"
		label="Percentage Concentration Of Lead in Paint:"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.substrateRemoved == 'leadedpaint'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.concentrationOfLeadPct}"
		showRequired="#{facilityProfile.emissionUnit.emissionUnitType.substrateRemoved == 'leadedpaint'}"
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.substrateRemovedListener}" 
		partialTriggers="substrateRemoved" autoSubmit="true">
		<f:selectItems
			value="#{facilityReference.absLeadConcentrationPctTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.concentrationOfLeadPct ? '' : facilityProfile.emissionUnit.emissionUnitType.concentrationOfLeadPct)]}" />
	</af:selectOneChoice>
	<af:inputText id="substrateRemovedOther"
		label="Substrate being Removed (Other):"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.substrateRemoved == 'other'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.substrateRemovedOther}"
		autoSubmit="true"
		showRequired="true" columns="12" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
	<af:inputText id="maxAnnualUsageAbs"
		label="Maximum Annual Usage (tons/yr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxAnnualUsageAbs}"
		showRequired="true" columns="12" maximumLength="9">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="blastMediaCARBCertifiedFlag"
		label="Are Blast Media CARB Certified?:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.blastMediaCARBCertifiedFlag}"
		showRequired="true">
		<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxNumberOfTimesBlastMediaReclaimedForReuse"
		label="Max number of times Blast Media is reclaimed for reuse? (times/yr):"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxNumberOfTimesBlastMediaReclaimedForReuse}"
		showRequired="true" columns="12" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="applicationMethod" label="Application Method:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.applicationMethod}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.absApplicationMethodTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.applicationMethod ? '' : facilityProfile.emissionUnit.emissionUnitType.applicationMethod)]}" />
	</af:selectOneChoice>
</af:panelForm>

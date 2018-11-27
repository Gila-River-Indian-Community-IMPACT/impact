<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="namePlateRating">
	<af:inputText id="namePlateRating" label="Name Plate Rating:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.namePlateRating}"
		columns="12" maximumLength="12" autoSubmit="true">
	</af:inputText>

	<af:selectOneChoice id="namePlateRatingUnitsCd" label="Units:"
		unselectedLabel=" " readOnly="#{! facilityProfile.editable}"
		showRequired="#{!facilityProfile.emissionUnit.emissionUnitType.namePlateRatingEmptyOrZero}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitCd}">
		<f:selectItems
			value="#{facilityReference.namePlateRatingUnitsDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.unitCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="siteRating">
	<af:inputText id="siteRating" label="Site Rating:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.siteRating}"
		columns="12" maximumLength="12" autoSubmit="true">
	</af:inputText>

	<af:selectOneChoice id="siteRatingUnitsCd" label="Units:"
		unselectedLabel=" " readOnly="#{! facilityProfile.editable}"
		showRequired="#{!facilityProfile.emissionUnit.emissionUnitType.siteRatingEmptyOrZero}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.siteRatingUnitCd}">
		<f:selectItems
			value="#{facilityReference.siteRatingUnitsDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.siteRatingUnitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.siteRatingUnitCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="primaryFuelType" label="Primary Fuel Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.primaryFuelType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.primaryFuelTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.primaryFuelType ? '' : facilityProfile.emissionUnit.emissionUnitType.primaryFuelType)]}" />
	</af:selectOneChoice>

	<af:selectOneChoice id="secondaryFuelType" label="Secondary Fuel Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.secondaryFuelType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.secondaryFuelTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.secondaryFuelType ? '' : facilityProfile.emissionUnit.emissionUnitType.secondaryFuelType)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="modelNameAndNumber" label="Model Name and Number:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.modelNameNumber}"
		showRequired="true" columns="50" maximumLength="50">
	</af:inputText>

	<af:selectOneChoice id="engine" label="Engine:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.engineType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.engineTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.engineType ? '' : facilityProfile.emissionUnit.emissionUnitType.engineType)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="typeOfUse" label="Type of Use:"  unselectedLabel=" "
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.typeOfUse}"
		showRequired="false">
		<f:selectItems
			value="#{facilityReference.engTypeOfUseDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.typeOfUse ? '' : facilityProfile.emissionUnit.emissionUnitType.typeOfUse)]}" />
	</af:selectOneChoice>
</af:panelForm>

<%@ include file="comEmissionUnitSerialTrackingTableENG.jsp"%>


<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="MaterialProduced" label="Material Produced:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.producedMaterialType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.materialProducedDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.producedMaterialType ? '' : facilityProfile.emissionUnit.emissionUnitType.producedMaterialType)]}" />
	</af:selectOneChoice>

</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maximumThroughput" label="Maximum Throughput:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxThroughput}"
		showRequired="true" columns="20" maximumLength="12">
	</af:inputText>
	<af:selectOneChoice id="Units" label="Units:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitCd}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.aptMaxThroughputUnitsDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.unitCd)]}" />

	</af:selectOneChoice>
</af:panelForm>
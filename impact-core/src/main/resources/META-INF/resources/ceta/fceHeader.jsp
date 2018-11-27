<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="100%">
	<af:panelForm rows="2" maxColumns="3">
		<af:inputText label="Facility ID:" readOnly="True"
			value="#{fceDetail.facility.facilityId}" />
		<af:inputText label="Facility Name:" readOnly="true"
			value="#{fceDetail.facility.name}" />
		<af:selectOneChoice label="Facility Class:"
			value="#{fceDetail.facility.permitClassCd}" readOnly="true">
			<f:selectItems
				value="#{facilityReference.permitClassDefs.items[(empty fceDetail.facility.permitClassCd ? '' : fceDetail.facility.permitClassCd)]}" />
		</af:selectOneChoice>
		<af:inputText label="Facility Type:" readOnly="True"
			value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty fceDetail.facility.facilityTypeCd ? '' : fceDetail.facility.facilityTypeCd)]}" />
		<af:inputText label="Inspection ID:" readOnly="true"
			value="#{fceDetail.fce.inspId}" />
		<af:selectOneChoice label="Inspection Report State:"
			value="#{fceDetail.fce.inspectionReportStateCd}" readOnly="true">
			<f:selectItems
				value="#{compEvalDefs.fceInspectionReportStateDef.items[(empty fceDetail.fce.inspectionReportStateCd ? '' : fceDetail.fce.inspectionReportStateCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
</af:panelBox>
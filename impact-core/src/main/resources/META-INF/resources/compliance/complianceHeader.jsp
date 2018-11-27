<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:panelBox background="light" width="100%">

	<af:panelForm rows="2" maxColumns="3">
		<af:inputText label="Facility ID:" readOnly="True"
			value="#{complianceReport.facility.facilityId}" />
		<af:inputText label="Facility Name:" readOnly="True"
			value="#{complianceReport.facility.name}" />
		<af:selectOneChoice label="Facility Class:"
			value="#{complianceReport.facility.permitClassCd}" readOnly="true">
			<f:selectItems
				value="#{facilityReference.permitClassDefs.items[(empty complianceReport.facility.permitClassCd ? '' : complianceReport.facility.permitClassCd)]}" />
		</af:selectOneChoice>
		<af:inputText label="Facility Type:" readOnly="True"
			value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty complianceReport.facility.facilityTypeCd ? '' : complianceReport.facility.facilityTypeCd)]}" />
		<af:inputText
			rendered="#{! empty complianceReport.complianceReport.submittedDate && !complianceReport.publicApp}"
			label="#{complianceReport.complianceReport.submitLabel}"
			readOnly="true"
			value="#{complianceReport.complianceReport.submitValue}">
		</af:inputText>
		<%--
		<af:inputText
			rendered="#{empty complianceReport.complianceReport.submittedDate}"
			label=" " readOnly="true" value=" ">
		</af:inputText>
		--%>
		<af:inputText label="Report ID:" readOnly="True"
			value="#{complianceReport.complianceReport.reportCRPTId}" />
		<af:selectOneChoice readOnly="True" label="Report Type:"
			value="#{complianceReport.complianceReport.reportType}">
			<f:selectItems
				value="#{complianceReport.complianceReportTypesDef.items[(empty '')]}" />
		</af:selectOneChoice>
		<af:selectOneChoice readOnly="True" label="Report Status:" rendered="#{!complianceReport.publicApp}"
			value="#{complianceReport.complianceReport.reportStatus}">
			<f:selectItems
				value="#{complianceReport.complianceReportStatusDef.items[(empty '')]}" />
		</af:selectOneChoice>
		<af:selectInputDate readOnly="true"
			rendered="#{!empty complianceReport.complianceReport.submittedDate}"
			label="Submitted Date:"
			value="#{complianceReport.complianceReport.submittedDate}" />
		<%--
	  	<af:inputText label="AFS ID :"
	  		rendered="#{complianceReport.renderAfsInfo}"
    		readOnly="#{!complianceReport.editMode}"
    		maximumLength="3" columns="3"
    		value="#{complianceReport.complianceReport.tvccAfsId}"/>
		<af:panelHorizontal halign="center" rendered="#{complianceReport.renderAfsInfo}">
    	<af:selectInputDate label="AFS Sent Date :"
    		rendered="#{complianceReport.renderAfsInfo}"
    		readOnly="#{!complianceReport.editMode}"
        	value="#{complianceReport.complianceReport.tvccAfsSentDate}" />
        	<af:objectSpacer width="3" height="3" />
	   		<af:objectImage source="/images/Lock_icon1.png"
	    		rendered="#{complianceReport.renderAfsLock}" />
        </af:panelHorizontal>
         --%>
    </af:panelForm>
</af:panelBox>

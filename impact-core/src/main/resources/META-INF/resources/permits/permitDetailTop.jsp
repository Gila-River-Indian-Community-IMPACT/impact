<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="100%">
  <af:panelForm rows="5" maxColumns="3">
    <af:inputText label="Facility ID: " readOnly="true" id="facilityId"
      value="#{permitDetail.currentFacility.facilityId}" />
    <af:inputText label="Facility Name: " readOnly="true"
      value="#{permitDetail.currentFacility.name}" />
    <af:inputText label="Classification:"
      readOnly="True"
      value="#{facilityReference.permitClassDefs.itemDesc[(empty permitDetail.currentFacility.permitClassCd ? '' : permitDetail.currentFacility.permitClassCd)]}" />
    <af:selectOneChoice label="Permit Type: " unselectedLabel=" "
      value="#{permitDetail.permit.permitType}" readOnly="true">
      <mu:selectItems value="#{permitReference.permitTypes}" />
    </af:selectOneChoice>

	<af:selectOneChoice id="permitLevelStatusCd" label="Permit Status: "
		readOnly="true"
		value="#{permitDetail.permit.permitLevelStatusCd}">
		<f:selectItems 
			value="#{permitReference.permitLevelStatusDefs.items[
				(empty permitDetail.permit.permitLevelStatusCd
				? '' : permitDetail.permit.permitLevelStatusCd)]}" />
	</af:selectOneChoice>

		<af:selectInputDate label="Effective Date: " readOnly="true"
      value="#{permitDetail.permit.effectiveDate}" rendered="#{permitDetail.permit.permitType == 'TVPTO'}"/>
    
    <af:inputText label="Permit Number: " readOnly="true"
      value="#{permitDetail.permit.permitNumber}" />
      
      
    <af:selectInputDate label="Newspaper Publish Date: " readOnly="true"
      value="#{permitDetail.permit.publicNoticePublishDate}"
      rendered="#{permitDetail.permit.permitType=='NSR' || permitDetail.permit.permitType=='TVPTO'}" />
       
     
   <af:selectInputDate label="Proposed Permit: " readOnly="true"
      value="#{permitDetail.permit.ppIssueDate}"
      rendered="#{(permitDetail.permit.permitType == 'TVPTO') && permitDetail.permit.ppIssuance.issuanceStatusCd == 'I'}" />
    <af:inputText label="Proposed Permit: " readOnly="true" value="Not Yet Published"
      rendered="#{(permitDetail.permit.permitType == 'TVPTO') && permitDetail.permit.ppIssuance.issuanceStatusCd != 'I'}" />
    <af:inputText label="Proposed Permit: " readOnly="true" value="N/A"
      rendered="#{permitDetail.permit.permitType != 'TVPTO'}" />

    <af:selectInputDate label="Final Permit: " readOnly="true"
      value="#{permitDetail.permit.finalIssueDate}"
      rendered="#{permitDetail.permit.finalIssuance.issuanceStatusCd == 'I'}" />
    <af:inputText label="Final Permit: " readOnly="true" value="Not Yet Issued"
      rendered="#{permitDetail.permit.finalIssuance.issuanceStatusCd != 'I'}" />


    <af:selectInputDate label="Expiration Date: " readOnly="true"
      value="#{permitDetail.permit.expirationDate}" />            

    <af:selectInputDate label="Hearing Notice: " readOnly="true"
      value="#{permitDetail.permit.draftIssuance.hearingNoticePublishDate}" />
    <af:selectInputDate label="Hearing:" readOnly="true"
      value="#{permitDetail.permit.draftIssuance.hearingDate}" />
    <af:selectInputDate label="Public Comments Period End:" readOnly="true"
      value="#{permitDetail.permit.draftIssuance.publicCommentEndDate}" />  

	<af:selectInputDate label="Rescission Date: " readOnly="true"
      value="#{permitDetail.permit.recissionDate}"
      rendered="#{permitDetail.permit.finalIssueDate != null}" />  
        
  </af:panelForm>

  <afh:rowLayout halign="center">
    <af:panelForm>
      <af:outputFormatted
        rendered="#{permitDetail.permit.permitGlobalStatusCD == 'E'}"
        inlineStyle="color: orange; font-weight: bold;"
        value="<b>** Note: This permit has been dead-ended. **</b>" />
      <af:outputFormatted
        rendered="#{permitDetail.permit.permitGlobalStatusCD == 'ID'}"
        inlineStyle="color: orange; font-weight: bold;"
        value="<b>** Note: This permit has been Issued Withdrawn. **</b>" />
      <af:outputFormatted
        rendered="#{permitDetail.permit.permitGlobalStatusCD == 'DP'}"
        inlineStyle="color: orange; font-weight: bold;"
        value="<b>** Note: This permit has been Withdrawn. **</b>" />  
    </af:panelForm>
  </afh:rowLayout>
</af:panelBox>


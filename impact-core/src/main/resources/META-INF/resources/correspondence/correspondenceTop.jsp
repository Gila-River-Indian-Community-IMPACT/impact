<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="1000">
  <af:panelForm rows="2" maxColumns="3">
    <af:inputText label="Facility ID: " readOnly="true"
      value="#{correspondenceDetail.correspondence.facilityID}" />
    <af:inputText label="Facility Name: " readOnly="true"
      value="#{correspondenceDetail.correspondence.facilityNm}" />
    <af:inputText label="Company ID: " readOnly="true"
      value="#{correspondenceDetail.correspondence.companyId}" />
    <af:inputText label="Company Name: " readOnly="true"
      value="#{correspondenceDetail.correspondence.companyName}" />  
    <af:inputText label="Correspondence ID: " readOnly="true"
      value="#{correspondenceDetail.correspondence.corId}" />
	<af:selectOneChoice label="District:" readOnly="true" rendered="#{infraDefs.districtVisible}"
	  value="#{correspondenceDetail.correspondence.district}">
		<f:selectItems
		  value="#{infraDefs.districts}" />
	</af:selectOneChoice>
  </af:panelForm>
</af:panelBox>
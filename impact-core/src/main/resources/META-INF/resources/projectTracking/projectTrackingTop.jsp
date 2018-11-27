<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="1000">
	<af:panelForm rows="2" maxColumns="3">
		<af:inputText label="Project ID: " readOnly="true"
			value="#{projectTrackingDetail.project.projectNumber}" />
		<af:selectOneChoice id="projectTypeCd" 
			label="Project Type: " 
			readOnly="true"
			value="#{projectTrackingDetail.project.projectTypeCd}" >
			<f:selectItems
				value="#{projectTrackingReference.projectTypeDefs.items[
							(empty projectTrackingDetail.project.projectTypeCd
								? '' : projectTrackingDetail.project.projectTypeCd)]}" />
		</af:selectOneChoice>	
      	<af:selectOneChoice label="Creator: " readOnly="true"
			value="#{projectTrackingDetail.project.creatorId}">
			<f:selectItems
				value="#{infraDefs.allActiveUsersDef.allItems}" />
		</af:selectOneChoice>
		<af:inputText label="Created Date: " readOnly="true"
			value="#{projectTrackingDetail.project.createdDate}">
			<af:convertDateTime pattern="MM/dd/yyyy" />
		</af:inputText>
		<af:inputText label="NEPA ID: " readOnly="true"
			rendered="#{projectTrackingDetail.NEPAProject}"
			value="#{projectTrackingDetail.project.NEPAId}" />			
		<af:inputText label="Grant ID: " readOnly="true"
			rendered="#{projectTrackingDetail.grantsProject}"
			value="#{projectTrackingDetail.project.grantId}" />	
		<af:inputText label="Letter ID: " readOnly="true"
			rendered="#{projectTrackingDetail.lettersProject}"
			value="#{projectTrackingDetail.project.letterId}" />	
		<af:inputText label="Contract ID: " readOnly="true"
			rendered="#{projectTrackingDetail.contract}"
			value="#{projectTrackingDetail.project.contractId}" />	
	</af:panelForm>
</af:panelBox>
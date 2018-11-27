<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<af:panelButtonBar>
	<%/* Hidden controls used by popup for navigating to the parent window */%>
	<af:commandLink id="trendReportToComplianceReport"
		styleClass="hiddenControls"
		inlineStyle="visibility: hidden"
		action="#{facilityProfile.showAssociatedComplianceReport}" />
		
	<af:commandLink id="complianceEventHistoryToEventType"
		styleClass="pcHiddenControls" inlineStyle="visibility: hidden"
		action="#{permitConditionDetail.showAssociatedComplianceEventType}" />
		
	<af:commandLink id="permitConditionSupersessionList" 
		styleClass="loadSupersededPermit" inlineStyle = "visibility:hidden"
		action="#{permitDetail.loadPermitFromPermitConditionSearch}" />
		
	<af:commandLink id="permitConditionSupersededByList" 
		styleClass="loadSupersedingPermit" inlineStyle = "visibility:hidden"
		action="#{permitDetail.loadPermitFromPermitConditionSearch}" />
		
	<af:commandLink id="permitConditionSupersedenceList" 
		styleClass="loadPermitFromPermitConditionSearch" inlineStyle = "visibility:hidden"
		action = "#{permitDetail.loadPermitFromPermitConditionSearch }"
		 />
</af:panelButtonBar>
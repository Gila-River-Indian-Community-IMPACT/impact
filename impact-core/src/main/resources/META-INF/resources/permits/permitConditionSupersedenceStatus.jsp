<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="permitConditionSupersedenceStatus">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<f:verbatim><%@ include file="../scripts/tinymce.js"%></f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
				
			<af:panelForm rows="9" maxColumns="1" labelWidth="140">
				<af:objectSpacer height="15" />
				<afh:rowLayout halign="center">
					<af:outputText inlineStyle="color: orange; font-weight: bold;"
						id="conditionStatusActiveNotification"
						rendered="#{permitConditionDetail.displayNoSupersessionNotification}"
						value="** Note: There are no Permit Conditions Superseding or Partially Superseding 
						this Permit Condition. Hence no records are displayed. **" />

					<af:outputText inlineStyle="font-weight: bold;"
						id="conditionStatusActiveNotificationHelp"
						rendered="#{!permitConditionDetail.displayNoSupersessionNotification}"
						value="Permit condition #{permitConditionDetail.supersededPermitConditionId} is completely/partially superseded by the below permit conditions." />
				</afh:rowLayout>
				<af:objectSpacer height="15" />

				<afh:rowLayout halign="left" valign="top">
					<af:panelGroup>
						<f:subview id="permit_condition_supersedence_list">
							<jsp:include flush="true"
										 page="permitConditionSupersedenceList.jsp" />
						</f:subview>	
					</af:panelGroup>
				</afh:rowLayout>
	
				<af:objectSpacer height="10" />
	
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Close" immediate="true" 
							action="#{permitConditionDetail.closePermitConditionStatusDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
	
			</af:panelForm>		
			<%-- hidden controls for navigation to compliance report from popup --%>
			<%@ include file="../util/hiddenControls.jsp"%>
			
		</af:form>
	</af:document>
</f:view>
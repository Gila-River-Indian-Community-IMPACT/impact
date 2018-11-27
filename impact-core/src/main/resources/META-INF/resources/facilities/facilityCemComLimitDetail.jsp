<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="facilityCemComLimitBody"
		title="Facility CEM/COM/CMS Limit Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:panelHeader text="Facility CEM/COM/CMS Limit Detail" size="0" />
			<af:panelForm rows="9" maxColumns="1" labelWidth="140">
			
				<af:objectSpacer height="10" />
				
				<afh:rowLayout halign="left">
						<af:inputText label="Limit ID: " id="limId" 
							readOnly="true"
							rendered="#{!facilityProfile.newFacilityCemComLimit}"
							value="#{facilityProfile.modifyFacilityCemComLimit.limId}">
						</af:inputText>
				</afh:rowLayout>
				
				<afh:rowLayout halign="left">
						<af:inputText label="Monitor ID: " id="monId" 
							readOnly="true"
							rendered="#{!facilityProfile.newFacilityCemComLimit}"
							value="#{facilityProfile.modifyFacilityCemComLimit.monId}">
						</af:inputText>
				</afh:rowLayout>
				
				<af:objectSpacer height="10" />
				
				<afh:rowLayout halign="left">
						<af:inputText label="Limit Description: " id="limitDesc" rows="5"
							readOnly="#{!facilityProfile.facilityCemComLimitEditMode}"
							showRequired="true"
							value="#{facilityProfile.modifyFacilityCemComLimit.limitDesc}" columns="120"
							maximumLength="500">
						</af:inputText>
				</afh:rowLayout>
				
				<af:objectSpacer height="10" />
				
				<afh:rowLayout halign="left">
						<af:inputText label="Source of Limit: " id="limitSource" rows="1"
							readOnly="#{!facilityProfile.facilityCemComLimitEditMode}"
							value="#{facilityProfile.modifyFacilityCemComLimit.limitSource}" columns="120"
							maximumLength="120">
						</af:inputText>
				</afh:rowLayout>
				
				<af:objectSpacer height="10" />
				
				<afh:rowLayout halign="left">
					<af:selectInputDate label="Start Monitoring Limit: " id="startDate"
						readOnly="#{!facilityProfile.facilityCemComLimitEditMode}" valign="middle"
						showRequired="true"
						value="#{facilityProfile.modifyFacilityCemComLimit.startDate}">
						<af:validateDateTimeRange
							minimum="1970-01-01"
							maximum="#{facilityProfile.todaysDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
				
				<af:objectSpacer height="10" />
				
				<afh:rowLayout halign="left">
					<af:selectInputDate label="End Monitoring Limit: " id="endDate"
						readOnly="#{!facilityProfile.facilityCemComLimitEditMode}" valign="middle"
						value="#{facilityProfile.modifyFacilityCemComLimit.endDate}">
						<af:validateDateTimeRange
							minimum="#{facilityProfile.modifyFacilityCemComLimit.startDate}"
							maximum="#{facilityProfile.todaysDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
				
				<af:objectSpacer height="10" />
				
				<afh:rowLayout halign="left">
						<af:inputText label="Additional Information: " id="addlInfo" rows="10"
							readOnly="#{!facilityProfile.facilityCemComLimitEditMode}"
							value="#{facilityProfile.modifyFacilityCemComLimit.addlInfo}" columns="100"
							maximumLength="1000">
						</af:inputText>
				</afh:rowLayout>
				
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Close"
							immediate="true"
							rendered="#{!facilityProfile.facilityCemComLimitEditMode}"
							action="#{facilityProfile.closeFacilityCemComLimitDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
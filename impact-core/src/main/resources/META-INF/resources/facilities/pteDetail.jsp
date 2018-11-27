<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<f:view>
	<af:document id="pteBody">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" id="ptePage"
				title="Permitted Emissions">
				<afh:rowLayout halign="center">
					<af:panelForm rows="3" maxColumns="1" width="600px">
						<af:selectOneChoice
							value="#{facilityProfile.selectedEuEmission.pollutantCd}"
							autoSubmit="true" readOnly="#{!facilityProfile.editable1}"
							label="Pollutant: ">
							<f:selectItems
								value="#{facilityReference.euPollutantDefs.items[(empty facilityProfile.selectedEuEmission.pollutantCd ? '' : facilityProfile.selectedEuEmission.pollutantCd)]}" />
						</af:selectOneChoice>
						<af:panelForm rows="1" maxColumns="3" width="600px">
							<af:outputText value="Potential Emissions Rates:" inlineStyle="font-size: 80%; font-weight: bold;"/>
							<af:inputText
								value="#{facilityProfile.selectedEuEmission.potentialEmissionsLbsHour}"
								id="potentialEmissionsLbsHour" columns="8"
								readOnly="#{!facilityProfile.editable1}" maximumLength="8"
								label="Lbs/Hour">
							</af:inputText>
							<af:inputText
								value="#{facilityProfile.selectedEuEmission.potentialEmissionsTonsYear}"
								id="potentialEmissionsTonsYear" columns="8"
								readOnly="#{!facilityProfile.editable1}" maximumLength="8"
								label="Tons/Year">
							</af:inputText>
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="3" width="600px">
							<af:outputText value="Allowable Emissions Rates:"  inlineStyle="font-size: 80%; font-weight: bold;"/>
							<af:inputText
								value="#{facilityProfile.selectedEuEmission.allowableEmissionsLbsHour}"
								id="allowableEmissionsLbsHour" columns="8"
								readOnly="#{!facilityProfile.editable1}" maximumLength="8"
								label="Lbs/Hour">
							</af:inputText>
							<af:inputText
								value="#{facilityProfile.selectedEuEmission.allowableEmissionsTonsYear}"
								id="allowableEmissionsTonsYear" columns="8"
								readOnly="#{!facilityProfile.editable1}" maximumLength="8"
								label="Tons/Year">
							</af:inputText>
						</af:panelForm>
						<af:inputText
							value="#{facilityProfile.selectedEuEmission.comment}"
							columns="40" maximumLength="100" rows="3"
							readOnly="#{!facilityProfile.editable1}" label="Comments: " />
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton text="Edit"
									rendered="#{!facilityProfile.editable1}"
									disabled="#{facilityProfile.disabledUpdateButton}"
									action="#{facilityProfile.editPTE}" />
								<af:commandButton text="Save"
									rendered="#{facilityProfile.editable1}"
									disabled="#{facilityProfile.disabledUpdateButton}"
									action="#{facilityProfile.savePTE}" />
								<af:commandButton text="Cancel"
									rendered="#{facilityProfile.editable1}"
									disabled="#{facilityProfile.disabledUpdateButton}"
									action="#{facilityProfile.cancelPTE}" />
								<af:commandButton text="Delete"
									rendered="#{!facilityProfile.editable1}"
									disabled="#{facilityProfile.disabledUpdateButton}"
									action="#{facilityProfile.deletePTE}" />
								<af:commandButton text="Close"
									rendered="#{!facilityProfile.editable1}"
									action="#{facilityProfile.closePTEDialog}" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</af:panelForm>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="yearlyInfoDetailBody" title="Reporting Year Detail"
		inlineStyle="width:500px; height:350px;">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:page var="foo" id="yearlyInfoDetailPage"
				title="Reporting Year Detail">
				<af:panelForm rows="4" maxColumns="1" labelWidth="35%" width="98%">
					<af:inputText label="Year: "
						value="#{reportProfile.reportingYear.year}" readOnly="true"
						rendered="#{reportProfile.reportingYear.indatabase}" />
					<af:selectOneChoice label="Year: "
						value="#{reportProfile.reportingYear.year}"
						rendered="#{!reportProfile.reportingYear.indatabase}"
						required="true"
						readOnly="#{!reportProfile.yearlyInfoEditable || !reportProfile.facilityReportEnabler}">
						<f:selectItems value="#{reportProfile.categoryYears}" />
					</af:selectOneChoice>

					<af:selectOneChoice label="Content Type: "
						value="#{reportProfile.reportingYear.contentTypeCd}"
						required="true"
						readOnly="#{!reportProfile.yearlyInfoEditable || !reportProfile.facilityReportEnabler || (!reportProfile.stars2Admin && !reportProfile.allowedToDeleteEmissionsRptInfo)}">
						<f:selectItems value="#{reportProfile.contentTypes}" />
					</af:selectOneChoice>
					
					<af:selectOneChoice label="Regulatory Requirement: "
						value="#{reportProfile.reportingYear.regulatoryRequirementCd}"
						required="true" 
						readOnly="#{! reportProfile.yearlyInfoEditable || !reportProfile.facilityReportEnabler || (!reportProfile.stars2Admin && !reportProfile.allowedToDeleteEmissionsRptInfo)}">
						<f:selectItems
							value="#{reportProfile.regulatoryRequirementTypes}" />
					</af:selectOneChoice>

					<af:selectBooleanCheckbox label="Reporting Enabled? "
						readOnly="#{! reportProfile.yearlyInfoEditable || !reportProfile.facilityReportEnabler}"
						value="#{reportProfile.reportingYear.reportingEnabled}" />
					<af:selectOneChoice label="State: "
						value="#{reportProfile.reportingYear.state}"
						readOnly="#{! reportProfile.yearlyInfoEditable || !reportProfile.facilityReportEnabler}">
						<f:selectItems
							value="#{facilityReference.reportStatusDefs.items[(empty reportProfile.reportingYear.state ? '' : reportProfile.reportingYear.state)]}" />
					</af:selectOneChoice>
<%-- 					<afh:rowLayout halign="left">
						<af:outputFormatted value="#{reportProfile.justificationStr}" />
					</afh:rowLayout>
					<afh:rowLayout halign="center">
						<af:inputText label="Comment: " id="tsJust" rows="5"
							value="#{reportProfile.reportingYear.comment}" columns="40"
							maximumLength="2000" readOnly="#{!reportProfile.yearlyInfoEditable || !reportProfile.facilityReportEnabler}">
						</af:inputText>
					</afh:rowLayout> --%>
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Edit"
								disabled="#{!reportProfile.facilityReportEnabler}"
								action="#{reportProfile.editYearlyInfo}"
								rendered="#{!reportProfile.yearlyInfoEditable && facilityProfile.dapcUser}" />
							<af:commandButton text="Save"
								disabled="#{!reportProfile.facilityReportEnabler}"
								action="#{reportProfile.saveYearlyInfo}"
								rendered="#{reportProfile.yearlyInfoEditable}" />
							<af:commandButton text="Cancel"
								action="#{reportProfile.cancelYearlyInfo}"
								rendered="#{reportProfile.yearlyInfoEditable}" immediate="true" />
							<af:commandButton text="Close"
								rendered="#{!reportProfile.yearlyInfoEditable}"
								action="#{reportProfile.closeReportingYearDialog}" />
								
							<af:commandButton id="deleteBtn" text="Delete" useWindow="true"
									windowWidth="#{confirmWindow.width}"
									windowHeight="#{confirmWindow.height}"
									returnListener="#{reportProfile.deleteEmissionsRptInfo}"
									rendered="#{infraDefs.stars2Admin && !reportProfile.yearlyInfoEditable}"
									disabled="#{!reportProfile.allowedToDeleteEmissionsRptInfo}"
									shortDesc="#{reportProfile.allowedToDeleteEmissionsRptInfo 
													? '' : 'EI enabled status row is associated with one or more EIs' }"
									action="#{confirmWindow.confirm}">
									<t:updateActionListener property="#{confirmWindow.type}"
										value="#{confirmWindow.yesNo}" />
									<t:updateActionListener property="#{confirmWindow.message}"
										value="Click Yes to confirm the deletion of the EI enabled status row." />
								</af:commandButton>
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>
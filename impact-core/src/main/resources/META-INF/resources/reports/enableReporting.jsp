<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Enable Yearly Emissions Inventories">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Enable Yearly Emissions Inventories">
				<%@ include file="../util/header.jsp"%>
				<af:objectSpacer height="10" />
				<afh:rowLayout halign="center">
					<af:panelForm rows="5" maxColumns="1" labelWidth="850" width="900">
						<af:selectBooleanCheckbox value="#{reportProfile.enableAllRptDef}"
							label="Have any report requirements for #{reportProfile.enableYear} changed and if so have new definitions been added to the Service Catalog?"
							id="rptDef" autoSubmit="true" />
						<af:objectSpacer height="15" width="850" />
						<af:selectBooleanCheckbox value="#{reportProfile.enableAllSCC}"
							label="Is the SCC database up-to-date for the reporting year #{reportProfile.enableYear}?  An SCC cannot be used for reporting unless a material is defined for it in the FIRE database."
							id="scc" autoSubmit="true" />
						<af:objectSpacer height="15" width="850" />
						<af:selectBooleanCheckbox value="#{reportProfile.enableAllFIRE}"
							label="Is the FIRE database up-to-date for the reporting year #{reportProfile.enableYear}?  For each SCC/Material, the FIRE database specifies possible pollutants and emissions factor or formula to determine pollutant emissions."
							id="fire" autoSubmit="true" />
					</af:panelForm>
				</afh:rowLayout>
				<af:objectSpacer height="30" width="850" />

				<af:panelForm partialTriggers="rptDef fire scc">
					<afh:rowLayout halign="center" width="850">
					<af:objectSpacer height="5" />
					<jsp:include flush="true" page="./enableReportingSelectionGrid.jsp" />
					</afh:rowLayout>
					<af:objectSpacer height="5" />
					<afh:rowLayout halign="center" width="850">
						<af:outputFormatted
							value="Clicking on the button below will enable Emissions Reporting for the <b>Reporting Year, Content Type, and Regulatory Requirement</b> in each selected row for all facilities that meet the Facility Selection Criteria for the selected row AND which owe a report for that year.<br><br>"
							rendered="#{reportProfile.enableAllRptDef && reportProfile.enableAllFIRE}"/>
					</afh:rowLayout>
					<af:objectSpacer width="10" height="5" />
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Enable Reporting For Entire System"
								rendered="#{reportProfile.enableAllRptDef && reportProfile.enableAllFIRE}"
								action="#{reportProfile.enableAllReporting}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
				<afh:rowLayout halign="center">
					<af:panelForm width="780px" rows="1" maxColumns="2">
						<af:showDetailHeader text="Missing FIRE Factors" disclosed="true">
							<af:selectOneChoice label="Reporting Year" id="reportingYr"
								value="#{reportProfile.missingFactorsYear}">
								<f:selectItems value="#{serviceCatalog.reportingYears}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Facility Class:"
								value="#{reportProfile.facilityClass}" id="facPermitClassCd">
								<f:selectItems
									value="#{facilityReference.permitClassDefs.items[(empty reportProfile.facilityClass ? '' : reportProfile.facilityClass)]}" />
							</af:selectOneChoice>
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton
										text="View Missing FIRE Factors for Actively Used SCCs"
										useWindow="true" windowWidth="600" windowHeight="400"
										action="#{reportProfile.viewMissingFIREFactors}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:showDetailHeader>
					</af:panelForm>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

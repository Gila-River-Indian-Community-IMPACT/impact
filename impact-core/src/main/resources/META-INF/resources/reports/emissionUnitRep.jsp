<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical"
	rendered="#{reportProfile.renderComponent == 'emissionUnits' && !reportProfile.err}">
	<af:panelHeader partialTriggers="eg71_zero forceDR"
		text="Emissions Unit #{reportProfile.emissionUnit.epaEmuId} Summary">
		<af:inputText label="Emissions Unit ID:"
			value="#{reportProfile.emissionUnit.epaEmuId}" readOnly="true" />
		<af:inputText label="AQD Description (read-only):" columns="100"
			rows="4" value="#{reportProfile.emissionUnit.dapcDescription}"
			readOnly="true" rendered="#{reportProfile.internalApp}"
			tip="To edit AQD Description, go to Emissions Unit Information in the Facility Inventory." />
		<af:inputText label="AQD Description (read-only):" columns="100" rows="4" 
			value="#{reportProfile.emissionUnit.dapcDescription}" readOnly="true" 
			rendered="#{!reportProfile.internalApp}"
			tip="Only AQD staff may change the AQD Description."/>
		<afh:rowLayout halign="center"
			rendered="#{reportProfile.emissionUnit.reportingNotNeeded}">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				rendered="#{reportProfile.emissionUnit.notInFacility}"
				value="<b>Error:&nbsp;&nbsp;The information in the Facility Inventory indicates that reporting is not needed for this Emission Unit.&nbsp;&nbsp;Delete it from this report or update this report's Facility Inventory.</b>" />
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				rendered="#{reportProfile.emissionUnit.shouldHaveProcesses}"
				value="Error:&nbsp;&nbsp;The Facility Inventory does not specify any processes for this emissions unit.&nbsp;&nbsp;Update this report's Facility Inventory to include process(es) or mark the unit's emissions <b>Less Than Reporting Requirement</b> or <b>Did Not Operate</b> (did not operate this reporting year)--See Online Help." />
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				rendered="#{reportProfile.emissionUnit.eg71WithEmissions}"
				value="Error:&nbsp;&nbsp;Since the EU Reporting Level is  <b>Less Than Reporting Requirement</b> and the process already specifies emission information you need to do one of the following:<lu><li>Click <b>Delete Emissions</b> to remove them,</li><li>Click <b>Detailed Emissions Reporting</b> to enable emissions reporting for this emissions unit or</li><li>Update this report's Facility Inventory.</li><li>See Online Help for more information.</li></lu>" />
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				rendered="#{reportProfile.emissionUnit.notOperWithEmissions}"
				value="Error:&nbsp;&nbsp;Since this is marked <b>Did Not Operate</b>, you cannot specify any process emissions for this emissions unit.&nbsp;&nbsp;Click <b>Delete Emissions</b> to remove them, remove the <b>Did Not Operate</b> or update this report's Facility Inventory.</b>" />
		</afh:rowLayout>
		<af:panelHorizontal>
			<afh:cellFormat halign="left" width="1%">
				<af:objectSpacer height="1" width="1" />
			</afh:cellFormat>
			<afh:cellFormat width="48%">
				<af:selectOneRadio readOnly="#{! reportProfile.editable  || reportProfile.forceDetailedReporting}"
					label="EU Reporting Level:" autoSubmit="true"
					shortDesc="Indicate detailed reporting will be provided, the EU emitted Less Than Reporting Requirement or the EU did not operate for the entire year (zero emissions)--See Online Help."
					rendered="#{!reportProfile.emissionUnit.notInFacility && !reportProfile.doRptCompare}"
					layout="horizontal" id="eg71_zero"
					value="#{reportProfile.euEmissionChoice}">
					<f:selectItem itemLabel="Detailed Emissions Reporting"
						itemValue="2" />
					<f:selectItem itemLabel="Less Than Reporting Requirement"
						itemValue="1" />
					<f:selectItem itemLabel="Did Not Operate" itemValue="0" />
				</af:selectOneRadio>
				<af:selectOneRadio readOnly="true" id="choice2"
					label="EU Reporting Level:"
					rendered="#{(reportProfile.emissionUnit.orig != null) && reportProfile.doRptCompare}"
					layout="horizontal"
					value="#{reportProfile.emissionUnit.emissionChoice}">
					<f:selectItem itemLabel="Detailed Emissions Reporting"
						itemValue="2" />
					<f:selectItem itemLabel="Less Than Reporting Requirement"
						itemValue="1" />
					<f:selectItem itemLabel="Did Not Operate" itemValue="0" />
				</af:selectOneRadio>
				<af:selectBooleanCheckbox label="Force Detailed Emissions Reporting :"
					id="forceDR" readOnly="#{! reportProfile.editable}"
					autoSubmit="true"
					rendered="#{!reportProfile.emissionUnit.notInFacility && !reportProfile.doRptCompare && (reportProfile.emissionUnit.forceDetailedReporting || reportProfile.emissionUnit.excludedDueToAttributes)}"
					value="#{reportProfile.forceDetailedReporting}" />
			</afh:cellFormat>
			<afh:cellFormat width="48%">
				<af:selectOneRadio readOnly="true" id="choice3"
					label="Compare EU Reporting Level:"
					rendered="#{reportProfile.doRptCompare && (reportProfile.emissionUnit.comp != null)}"
					layout="horizontal"
					value="#{reportProfile.emissionUnit.comp.emissionChoice}">
					<f:selectItem itemLabel="Detailed Emissions Reporting"
						itemValue="2" />
					<f:selectItem itemLabel="Less Than Reporting Requirement"
						itemValue="1" />
					<f:selectItem itemLabel="Did Not Operate" itemValue="0" />
				</af:selectOneRadio>
			</afh:cellFormat>
			<afh:cellFormat halign="right" width="1%">
				<af:objectSpacer height="1" width="1" />
			</afh:cellFormat>
		</af:panelHorizontal>
		<af:panelHorizontal
			rendered="#{!reportProfile.doRptCompare && reportProfile.emissionUnit.phaseOneBoiler}"
			valign="true">
			<af:objectSpacer height="3" width="3" />
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="This emission unit is designated as a Phase I unit.  You must report emissions but fees will not apply to these emissions for this reporting year." />
		</af:panelHorizontal>
	</af:panelHeader>
	<af:showDetailHeader text="Unit Emissions" disclosed="true"
		rendered="#{!reportProfile.emissionUnit.notInFacility && !reportProfile.emissionUnit.eg71OrZero}">
		<afh:rowLayout halign="center">
			<af:table value="#{reportProfile.emissionEuWrapper}"
				bandingInterval="#{reportProfile.doRptCompare?2:1}"
				binding="#{reportProfile.emissionEuWrapper.table}" id="EmissionsTab"
				banding="row" width="1005" var="emissionLine"
				rows="#{reportProfile.pageLimit}">
				<%@ include file="emissionsTable.jsp"%>
				<f:facet name="footer">
					<h:panelGrid width="100%">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
							<af:objectSpacer width="150" height="3"
								rendered="#{!reportProfile.editable}" />
							<%--
							<af:outputLabel value="Total Chargeable Pollutants:"
								rendered="#{!reportProfile.editable}" />
							<af:objectSpacer width="7" height="5" />
							<af:outputFormatted value="#{reportProfile.displayTotal}"
								rendered="#{!reportProfile.editable}" />
							--%>
						</afh:rowLayout>
					</h:panelGrid>
				</f:facet>
			</af:table>
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:objectSpacer width="150" height="10" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:outputFormatted
				value="<b>_____________________________________________________________________________________________________________</b>"
				rendered="#{reportProfile.hapTable}" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:outputFormatted value="#{reportProfile.hapNonAttestationMsg}"
				inlineStyle="font-size:75%" rendered="#{reportProfile.hapTable}" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:table value="#{reportProfile.emissionEuWrapperHAP}"
				rendered="#{reportProfile.hapTable}"
				bandingInterval="#{reportProfile.doRptCompare?2:1}"
				binding="#{reportProfile.emissionEuWrapperHAP.table}"
				id="EmissionsTab2" banding="row" width="1005" var="emissionLine"
				rows="#{reportProfile.displayHapRows}">
				<%@ include file="emissionsTableHAPs.jsp"%>
				<f:facet name="footer">
					<h:panelGrid width="100%">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>							
								<af:commandButton text="Display Some Rows" id="displaySomeRows"
									action="#{reportProfile.displaySomeRows}"
									inlineStyle="font-weight:bold; background-color: rgb(255,255,119)"
									rendered="#{reportProfile.showDisplaySome}">
								</af:commandButton>
								<af:commandButton text="Add Emission" id="AddEmission"
									action="#{reportProfile.addEmission}"
									rendered="#{reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.renderSum}">
								</af:commandButton>
								<af:commandButton text="Delete Selected Emission(s)"
									shortDesc="Only emissions not pre-specified by report or FIRE database may be deleted"
									rendered="#{reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.renderSum}"
									action="#{reportProfile.deleteEmission}">
								</af:commandButton>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</h:panelGrid>
				</f:facet>
			</af:table>
		</afh:rowLayout>
	</af:showDetailHeader>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center" partialTriggers="eg71_zero forceDR">
		<af:panelButtonBar>
			<af:commandButton text="Exit Comparison Mode" rendered="#{reportProfile.doRptCompare}"
				action="#{reportProfile.compareReportOK}" />
			<af:commandButton text="Edit"
				disabled="#{!reportProfile.modTV_SMTV || (!reportProfile.internalEditable || reportProfile.closedForEdit)}"
				action="#{reportProfile.editEmissionUnit}"
				rendered="#{!reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.emissionUnit.notInFacility}" />
			<af:commandButton text="Save"
				action="#{reportProfile.saveEmissionUnit}" useWindow="true"
				windowWidth="700" windowHeight="250"
				rendered="#{reportProfile.emissionsNeedClearing && reportProfile.editable && !reportProfile.doRptCompare}" />
			<af:commandButton text="Save"
				action="#{reportProfile.saveEmissionUnit}"
				rendered="#{!reportProfile.emissionsNeedClearing && reportProfile.editable && !reportProfile.doRptCompare}" />
			<af:commandButton text="Cancel"
				action="#{reportProfile.cancelEmissionUnitEdit}"
				rendered="#{reportProfile.editable && !reportProfile.doRptCompare}"
				immediate="true" />
			<af:commandButton text="Delete Emission Unit From Report"
				disabled="#{!reportProfile.modTV_SMTV || reportProfile.closedForEdit}"
				rendered="#{reportProfile.emissionUnit.notInFacility && !reportProfile.editable && reportProfile.internalEditable}"
				useWindow="true" windowWidth="700" windowHeight="250"
				action="#{reportProfile.requestDeleteEmissionUnit}">
			</af:commandButton>
			<af:commandButton text="Delete Emissions"
				disabled="#{!reportProfile.modTV_SMTV || reportProfile.closedForEdit}"
				rendered="#{!reportProfile.emissionUnit.notInFacility && !reportProfile.editable && reportProfile.internalEditable && !reportProfile.emissionUnit.noPeriods && reportProfile.emissionUnit.eg71OrZero}"
				useWindow="true" windowWidth="700" windowHeight="250"
				action="#{reportProfile.deleteEG71Emissions}">
			</af:commandButton>
			<af:objectSpacer height="10" />
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>

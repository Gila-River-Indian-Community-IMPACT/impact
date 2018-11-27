<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical"
	rendered="#{reportProfile.renderComponent == 'emissionUnitGroups' && !reportProfile.err}">
	<af:panelHeader
		text="Emissions Unit Group #{reportProfile.grpName} Summary" />
	<afh:tableLayout
		rendered="#{reportProfile.grpEmissionUnit.notInFacility}">
		<afh:rowLayout halign="center">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="<b>#{reportProfile.grpEmissionUnit.notInFacilityMsg}</b>" />
		</afh:rowLayout>
	</afh:tableLayout>
	<af:showDetailHeader text="Group Members" disclosed="true">
		<af:panelForm>
			<afh:rowLayout halign="left">
				<af:inputText label="Emissions Unit Group Name:" id="groupName"
					maximumLength="20" value="#{reportProfile.grpName}"
					readOnly="#{!reportProfile.editable}" />
			</afh:rowLayout>
			<afh:rowLayout>
				<afh:cellFormat halign="left" width="43%">
					<af:selectManyShuttle
						leadingHeader="Processes With Same Control Equipment"
						trailingHeader="Current Members" size="10"
						value="#{reportProfile.currentGrpEUs}"
						readOnly="#{!reportProfile.editable}"
						rendered="#{reportProfile.editable && (!reportProfile.doRptCompare || reportProfile.grpEmissionUnit.orig != null)}">
						<f:selectItems value="#{reportProfile.allGrpEUs}" />
					</af:selectManyShuttle>
					<af:table emptyText=" " var="groupEU" bandingInterval="1"
						banding="row" value="#{reportProfile.currentTblGrpEUs}"
						id="euTable"
						rendered="#{!reportProfile.editable && (!reportProfile.doRptCompare || reportProfile.grpEmissionUnit.orig != null)}">
						<af:column formatType="text" headerText="Current Members"
							id="euStringColumn">
							<af:outputText id="euStringText" value="#{groupEU}" />
						</af:column>
					</af:table>
				</afh:cellFormat>
				<afh:cellFormat width="14%" rendered="#{reportProfile.doRptCompare}">
					<af:outputFormatted
						inlineStyle="font-weight:bold; font-size:24px; color: rgb(0, 0, 0);"
						value="&nbsp;:&nbsp;" />
				</afh:cellFormat>
				<afh:cellFormat halign="right" width="43%">
					<af:selectManyShuttle
						leadingHeader="Processes With Same Control Equipment"
						trailingHeader="Current Members" size="10"
						value="#{reportProfile.currentCompareGrpEUs}" readOnly="true"
						rendered="#{reportProfile.editable && reportProfile.doRptCompare && reportProfile.grpEmissionUnit.comp != null}">
						<f:selectItems value="#{reportProfile.allCompareGrpEUs}" />
					</af:selectManyShuttle>
					<af:table emptyText=" " var="groupEU" bandingInterval="1"
						banding="row" value="#{reportProfile.currentTblCompareGrpEUs}"
						id="euTable2"
						rendered="#{!reportProfile.editable && reportProfile.doRptCompare && reportProfile.grpEmissionUnit.comp != null}">
						<af:column formatType="text"
							headerText="Comparison Report Current Members"
							id="euStringColumn2">
							<af:outputText id="euStringText2" value="#{groupEU}" />
						</af:column>
					</af:table>
				</afh:cellFormat>
			</afh:rowLayout>
			<afh:rowLayout>
				<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;"
					label="Click here to delete Group :" id="removeGroup"
					rendered="#{reportProfile.editable}"
					value="#{reportProfile.removeGroup}" />
			</afh:rowLayout>
		</af:panelForm>
	</af:showDetailHeader>
	<af:showDetailHeader text="Group Emissions" disclosed="true"
		rendered="#{!reportProfile.grpEmissionUnit.notInFacility}">
		<afh:rowLayout halign="center">
			<af:table value="#{reportProfile.emissionGroupWrapper}"
				bandingInterval="#{reportProfile.doRptCompare?2:1}"
				binding="#{reportProfile.emissionGroupWrapper.table}"
				id="EmissionsTab" banding="row" width="1005" var="emissionLine"
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
			<af:table value="#{reportProfile.emissionGroupWrapperHAP}"
				rendered="#{reportProfile.hapTable}"
				bandingInterval="#{reportProfile.doRptCompare?2:1}"
				binding="#{reportProfile.emissionGroupWrapperHAP.table}"
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
		<af:objectSpacer height="10" />
	</af:showDetailHeader>
	<afh:rowLayout halign="center"
		rendered="#{!reportProfile.doRptCompare}">
		<af:panelButtonBar>
			<af:commandButton text="Edit" id="edit"
				disabled="#{!reportProfile.modTV_SMTV}"
				action="#{reportProfile.editEmissionGroup}"
				rendered="#{!reportProfile.editable && !reportProfile.doRptCompare && reportProfile.internalEditable && !reportProfile.closedForEdit}" />
			<af:commandButton text="Save"
				action="#{reportProfile.saveEmissionGroup}"
				rendered="#{reportProfile.editable}" />
			<af:commandButton text="Cancel"
				action="#{reportProfile.cancelEmissionGroup}"
				rendered="#{reportProfile.editable}" immediate="true" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<afh:rowLayout halign="center" rendered="#{reportProfile.doRptCompare}">
		<af:objectSpacer height="10" />
		<af:panelButtonBar>
			<af:commandButton text="Exit Comparison Mode" action="#{reportProfile.compareReportOK}" />
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>

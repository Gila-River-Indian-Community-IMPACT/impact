<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="#{reportProfile.allow71Save?'Excluding & Including':'Excluded & Included'} Emissions Units from Detailed Reporting">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="#{reportProfile.allow71Save?'Excluding & Including':'Excluded & Included'} Emissions Units from Detailed Reporting">
				<af:panelForm maxColumns="1">
					<afh:rowLayout halign="left"
						rendered="#{reportProfile.allow71Save}">
						<af:outputFormatted
							value="The <b>Detailed Emissions</b> column should be checked and process level emissions reporting is required unless the emission unit did not operate (had zero emissions) or emitted less than its reporting requirement.  If either of these two conditions are true, click the appropriate reason in the last column to exclude the unit from the reporting requirements for this emissions inventory.</li></lu>" />
					</afh:rowLayout>
					<afh:rowLayout halign="left"
						rendered="#{reportProfile.allow71Save}">
						<af:outputFormatted inlineStyle="color: black;"
							value="<b>Caution</b>:&nbsp;&nbsp;If the detailed emission reporting is specified for an emissions unit you are now choosing to exclude, any emissions information you have already entered for the unit will be lost." />
					</afh:rowLayout>
					<afh:rowLayout halign="center" width="100%">
						<h:panelGrid border="1" align="center">
							<af:table value="#{reportProfile.euEg71Table}"
								partialTriggers="procTab:eg71_zero procTab:include procTab:forceD"
								bandingInterval="1" id="procTab" banding="row"
								var="emissionUnit">
								<f:facet name="header">
									<afh:rowLayout halign="right"
										rendered="#{reportProfile.allow71Save}">
										<af:panelButtonBar>
											<af:commandButton action="#{reportProfile.allDetailed}"
												text="Mark All 'Detailed Emissions Reporting'" />
											<af:commandButton action="#{reportProfile.allEG71}"
												text="Mark All 'Less Than Reporting Requirement'" />
											<af:commandButton action="#{reportProfile.allNotOperate}"
												text="Mark All 'Did Not Operate'" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>
								<af:column sortProperty="epaEmuId" sortable="true" width="50"
									formatType="icon" headerText="Emission Unit">
									<af:outputText value="#{emissionUnit.epaEmuId}"
										rendered="#{!emissionUnit.inGroup}" />
									<af:outputText value="#{emissionUnit.epaEmuId}*"
										rendered="#{emissionUnit.inGroup}" />
								</af:column>
								<af:column sortProperty="description" sortable="true"
									formatType="text" headerText="Company Equipment ID">
									<af:outputText value="#{emissionUnit.description}" />
								</af:column>					
							
								<af:column sortProperty="include" sortable="true"
									formatType="icon" width="90" headerText="Detailed Emissions">
									<af:selectBooleanCheckbox value="#{emissionUnit.include}"
										id="include" autoSubmit="true"
										shortDesc="Check to provide detailed emissions"
										rendered="#{(emissionUnit.include || !emissionUnit.excludedDueToAttributes)}"
										readOnly="#{!reportProfile.adminEditable || emissionUnit.forceDetails}" />
								</af:column>
								<af:column sortProperty="eg71Zero" sortable="true"
									formatType="icon"
									headerText="Exclude Detailed Emissions Reporting">
									<af:selectOneRadio readOnly="#{!reportProfile.adminEditable || emissionUnit.forceDetails}"
										shortDesc="Select to indicate the emission unit emitted less than reporting requirement or did not operate for the entire year (zero emissions)--See Online Help."
										layout="horizontal" id="eg71_zero" autoSubmit="true"
										value="#{emissionUnit.eg71Zero}">
										<f:selectItem itemLabel="Less Than Reporting Requirement"
											itemValue="1" />
										<f:selectItem itemLabel="Did Not Operate" itemValue="0" />
									</af:selectOneRadio>
									<af:outputText inlineStyle="color: orange; font-weight: bold;" 
										value="#{(!emissionUnit.forceDetails && emissionUnit.include && emissionUnit.deMinimis)?'Cannot check Detailed Emissions because it conflicts with the Exemption Status for the Emission Unit in the facility inventory--click Force Detailed Reporting instead':''}" />
									<af:outputText inlineStyle="color: orange; font-weight: bold;"
										value="#{emissionUnit.deleteEmissions?'Any emissions data that you have entered for this Unit will be deleted':''}" />
									<af:outputText inlineStyle="color: orange; font-weight: bold;"
										value="#{(emissionUnit.eg71Zero==null && !emissionUnit.include && !emissionUnit.forceDetails)?'Select some choice in this row':''}" />
								</af:column>
								<f:facet name="footer">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton
												actionListener="#{tableExporter.printTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Printable view" />
											<af:commandButton
												actionListener="#{tableExporter.excelTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Export to excel" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>
							</af:table>
							
						</h:panelGrid>
					</afh:rowLayout>
				</af:panelForm>
				<af:objectSpacer width="100%" height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Save"
							action="#{reportProfile.saveEmissionChoiceChanges}"
							rendered="#{reportProfile.allow71Save}" />
						<af:commandButton
							text="#{reportProfile.adminEditable ? 'Cancel' : 'Close' }"
							action="#{reportProfile.cancelEditTS}" immediate="true" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

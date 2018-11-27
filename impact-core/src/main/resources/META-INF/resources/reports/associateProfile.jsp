<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Select Facility Inventory to associate emissions inventory with">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}">
				<afh:rowLayout>
					<afh:cellFormat halign="left" width="50%">
						<af:panelForm>
							<afh:rowLayout halign="center"
								rendered="#{reportProfile.facilityHistory.rowCount == 1}">
								<af:outputFormatted
									value="The emissions inventory is already associated with the only facility inventory that exists.&nbsp;&nbsp;Cancel out of this operation."
									inlineStyle="color: orange; font-weight: bold;" />
							</afh:rowLayout>
							<afh:rowLayout halign="left"
								rendered="#{reportProfile.facilityHistory.rowCount > 1}">
								<af:outputFormatted
									inlineStyle="font-weight:bold"
									value="&nbsp;&nbsp;&nbsp;&nbsp;Why Change Facility Inventory?" />
							</afh:rowLayout>
							<afh:rowLayout halign="center"
								rendered="#{reportProfile.facilityHistory.rowCount > 1}">
								<af:outputFormatted
									value="<ul><li>To revise a emissions inventory or create a new emissions inventory from an existing emissions inventory you may wish to associate this revised or new emissions inventory with the different facility inventory (maybe the current facility inventory) instead of the facility inventory that the original emissions inventory is associated with.</li><li>To use a facility inventory which more accurately represents how the facility looked during the period of the emissions inventory.&nbsp;&nbsp;Whether you associate with a different facility inventory or not, you can modify the facility inventory that the emissions inventory is associated with to bring it in line with the reporting period.</li></ul>Note that any changes you make to a historic facility inventory will not cause any changes in the current facility inventory.&nbsp;&nbsp;Changing a historic facility inventory will generate a separate historic facility inventory for this emissions inventory--it does not change the facility inventory that previously submitted items are associated with." />
							</afh:rowLayout>
							<afh:rowLayout halign="left"
								rendered="#{reportProfile.facilityHistory.rowCount > 1}">
								<af:outputFormatted
									inlineStyle="font-weight:bold"
									value="&nbsp;&nbsp;&nbsp;&nbsp;What if Selected/Associated Facility Inventory is newer than Reporting Year?" />
							</afh:rowLayout>
							<afh:rowLayout halign="center"
								rendered="#{reportProfile.facilityHistory.rowCount > 1}">
								<af:outputFormatted
									value="The operating status and dates of each EU <b>are used</b> to determine whether that EU is included in the emissions inventory.&nbsp;&nbsp;Therefore if the EU was not operating during the report year, it will not be included.<br><br>The operating status and dates of control equipment and release points <b>are not used</b> in reporting.&nbsp;&nbsp;If the control equipment or release points appear in the facility inventory, under an EU included in the emissions inventory, they will be used by the reporting system.&nbsp;&nbsp;Therefore, the facility inventory associated with the emissions inventory should accurately specify this equipment for the reporting period of the emissions inventory." />
							</afh:rowLayout>
							<afh:rowLayout halign="left"
								rendered="#{reportProfile.facilityHistory.rowCount > 1}">
								<af:outputFormatted
									inlineStyle="font-weight:bold"
									value="&nbsp;&nbsp;&nbsp;&nbsp;What if Associated Facility Inventory is Historic?" />
							</afh:rowLayout>
							<afh:rowLayout halign="center"
								rendered="#{reportProfile.facilityHistory.rowCount > 1}">
								<af:outputFormatted
									value="If the emissions inventory was associated with a historic facility inventory, then that facility inventory is removed from the In Progress Task Table (and all changes lost) when the emissions inventory is switched to another facility inventory.&nbsp;&nbsp;If your emissions inventory is associated with the current facility inventory, then after the emissions inventory is swtiched to another facility inventory, the current facility inventory is kept in the In Progress Task Table if it has been changed (your changes will not be lost)."
									 />
							</afh:rowLayout>
							<afh:rowLayout halign="left"
								rendered="#{reportProfile.facilityHistory.rowCount > 1}">
								<af:outputFormatted
									inlineStyle="font-weight:bold"
									value="&nbsp;&nbsp;&nbsp;&nbsp;What if Associated Facility Inventory is older than Reporting Year?" />
							</afh:rowLayout>
							<afh:rowLayout halign="center"
								rendered="#{reportProfile.facilityHistory.rowCount > 1}">
								<af:outputFormatted
									value="If you associate the emissions inventory with an older facility inventory there may be EUs that did not exist in that facility inventory.  Regardless, some of the processes may use different SCC Codes.  These discrepancies between the existing emissions inventory and the re-associated facility inventory will be highlighted with warning flags in the emissions inventory tree.  Until you actually agree to delete parts of the emissions inventory or explicitly make changes to the emissions inventory, you can re-associate back to the original facility inventory and the emissions inventory will be unchanged from its original contents.  Please note what facility inventory the emissions inventory was originally associated with.  The Facility Inventory Id which is not highlighted in the table is the facility inventory the emissions inventory is currently associated with."
									 />
							</afh:rowLayout>
						</af:panelForm>
					</afh:cellFormat> 
					<afh:cellFormat halign="right" width="50%">
						<af:panelForm>
								<afh:rowLayout halign="center">
									<af:outputFormatted inlineStyle="font-weight:bold"
										value="Click on the Facility Inventory ID you wish to switch the emissions inventory to:" />
								</afh:rowLayout>
								<af:objectSpacer width="100%" height="10" />
								<af:table value="#{reportProfile.facilityHistory}" rows="35"
									bandingInterval="1" id="HistTab" banding="row" var="facility">
									<af:column sortProperty="versionId" sortable="true"
										formatType="text" noWrap="true"
										headerText="Facility Inventory ID">
										<af:commandLink action="#{reportProfile.associateProfile}"
											rendered="#{reportProfile.fpId != facility.fpId}">
											<af:outputText value="Current (#{facility.fpId})"
												rendered="#{facility.versionId == -1}" />
											<af:outputText value="#{facility.fpId}"
												rendered="#{facility.versionId != -1}" />
											<af:setActionListener to="#{reportProfile.fpId}"
												from="#{facility.fpId}" />
											<af:setActionListener to="#{reportProfile.versionId}"
												from="#{facility.versionId}" />
										</af:commandLink>
										<af:outputText value="Current (#{facility.fpId})"
											inlineStyle="font-weight:bold"
											rendered="#{facility.versionId == -1 && reportProfile.fpId == facility.fpId}" />
										<af:outputText value="#{facility.fpId}"
											inlineStyle="font-weight:bold"
											rendered="#{facility.versionId != -1 && reportProfile.fpId == facility.fpId}" />
									</af:column>
									<af:column sortProperty="startDate" sortable="true"
										noWrap="true" formatType="text" headerText="Start Date">
										<af:selectInputDate value="#{facility.startDate}"
											readOnly="true" />
									</af:column>
									<af:column sortProperty="endDate" sortable="false"
										noWrap="true" formatType="text" headerText="End Date">
										<af:selectInputDate value="#{facility.endDate}"
											readOnly="true" />
									</af:column>
									<af:column sortProperty="note" sortable="false"
										formatType="text" headerText="Note">
										<af:outputText value="#{facility.note}" />
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
								<af:objectSpacer width="100%" height="10" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Cancel"
											action="#{reportProfile.cancelEditTS}" />
									</af:panelButtonBar>
								</afh:rowLayout>
						</af:panelForm>
					</afh:cellFormat>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

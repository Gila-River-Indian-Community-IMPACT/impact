<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Service Catalog">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Service Catalog">
				<%@ include file="../admin/serviceCatalogHeader.jsp"%>
				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<af:panelBorder>
							<f:facet name="left">
								<h:panelGrid columns="1" width="250">
									<t:tree2 id="serviceCatalog" value="#{serviceCatalog.treeData}"
										var="node" varNodeToggler="t" clientSideToggle="false">
										<f:facet name="root">
											<h:panelGroup>
												<af:commandMenuItem action="#{serviceCatalog.nodeClicked}"
													onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }">
													<t:graphicImage value="/images/catalog.gif" border="0" />
													<t:outputText value="#{node.description}"
														style="#{serviceCatalog.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
													<t:updateActionListener
														property="#{serviceCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{serviceCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="reportRoot">
											<h:panelGroup>
												<af:commandMenuItem action="#{serviceCatalog.nodeClicked}"
													onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }">
													<t:graphicImage value="/images/folder_open.gif" border="0"
														rendered="#{t.nodeExpanded}" />
													<t:graphicImage value="/images/folder_closed.gif"
														border="0" rendered="#{!t.nodeExpanded}" />
													<t:outputText value="#{node.description}"
														style="#{serviceCatalog.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
													<t:updateActionListener
														property="#{serviceCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{serviceCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="permitRoot">
											<h:panelGroup>
												<af:commandMenuItem action="#{serviceCatalog.nodeClicked}"
													onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }">
													<t:graphicImage value="/images/folder_open.gif" border="0"
														rendered="#{t.nodeExpanded}" />
													<t:graphicImage value="/images/folder_closed.gif"
														border="0" rendered="#{!t.nodeExpanded}" />
													<t:outputText value="#{node.description}"
														style="#{serviceCatalog.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
													<t:updateActionListener
														property="#{serviceCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{serviceCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="category">
											<h:panelGroup>
												<af:commandMenuItem action="#{serviceCatalog.nodeClicked}"
													onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }">
													<t:graphicImage value="/images/folder_open.gif" border="0"
														rendered="#{t.nodeExpanded}" />
													<t:graphicImage value="/images/folder_closed.gif"
														border="0" rendered="#{!t.nodeExpanded}" />
													<t:outputText value="#{node.description}"
														style="#{serviceCatalog.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
													<t:updateActionListener
														property="#{serviceCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{serviceCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="categoryFee">
											<h:panelGroup>
												<af:commandMenuItem action="#{serviceCatalog.nodeClicked}"
													onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }">
													<t:graphicImage value="/images/charge_co.gif" border="0" />
													<t:outputText value="#{node.description}"
														style="#{serviceCatalog.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
													<t:updateActionListener
														property="#{serviceCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{serviceCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="reportGroup">
											<h:panelGroup>
												<af:commandMenuItem action="#{serviceCatalog.nodeClicked}"
													onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }">
													<t:graphicImage value="/images/folder_open.gif" border="0"
														rendered="#{t.nodeExpanded}" />
													<t:graphicImage value="/images/folder_closed.gif"
														border="0" rendered="#{!t.nodeExpanded}" />
													<t:outputText value="#{node.description}"
														style="#{serviceCatalog.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
													<t:updateActionListener
														property="#{serviceCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{serviceCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="report">
											<h:panelGroup>
												<af:commandMenuItem action="#{serviceCatalog.nodeClicked}"
													onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }">
													<t:graphicImage value="/images/permit.gif" border="0" />
													<t:outputText value="#{node.description}"
														style="#{serviceCatalog.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
													<t:updateActionListener
														property="#{serviceCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{serviceCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="fee">
											<h:panelGroup>
												<af:commandMenuItem action="#{serviceCatalog.nodeClicked}"
													onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }">
													<t:graphicImage value="/images/charge_co.gif" border="0" />
													<t:outputText value="#{node.description}"
														style="#{serviceCatalog.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
													<t:updateActionListener
														property="#{serviceCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{serviceCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="reminder">
											<h:panelGroup>
												<af:commandMenuItem action="#{serviceCatalog.nodeClicked}"
													onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }">
													<t:graphicImage value="/images/permit.gif" border="0" />
													<t:outputText value="#{node.description}"
														style="#{serviceCatalog.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
													<t:updateActionListener
														property="#{serviceCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{serviceCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
									</t:tree2>
								</h:panelGrid>
							</f:facet>

							<f:facet name="innerLeft">
								<h:panelGrid columns="1" border="1" width="800">
									<af:panelGroup layout="vertical"
										rendered="#{serviceCatalog.selectedTreeNode.type == 'permitRoot' }">
										<af:panelHeader text="Permit Categories" />
										<af:panelForm>
											<af:inputText label="Name"
												value="#{serviceCatalog.category.categoryDsc}"
												rendered="#{serviceCatalog.addingCategory}" />
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Add New Category"
														action="#{serviceCatalog.addCategory}"
														rendered="#{!serviceCatalog.addingCategory}" />
													<af:commandButton text="Save"
														action="#{serviceCatalog.saveCategory}"
														rendered="#{serviceCatalog.addingCategory}" />
													<af:commandButton text="Cancel"
														action="#{serviceCatalog.reset}"
														rendered="#{serviceCatalog.addingCategory}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>
									</af:panelGroup>

									<af:panelGroup layout="vertical"
										rendered="#{serviceCatalog.selectedTreeNode.type == 'category' }">
										<af:panelHeader text="Category" />
										<af:panelForm>
											<af:inputText label="Name"
												value="#{serviceCatalog.category.categoryDsc}"
												readOnly="true" />
											<af:inputText label="Fee Description"
												value="#{serviceCatalog.fee.feeNm}"
												rendered="#{serviceCatalog.addingCategoryFee}" />
											<af:inputText label="Fee Amount"
												value="#{serviceCatalog.fee.amount}"
												rendered="#{serviceCatalog.addingCategoryFee}" />
											<af:selectInputDate label="Effective Date"
												value="#{serviceCatalog.fee.effectiveDate}"
												rendered="#{serviceCatalog.addingCategoryFee}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
											<af:selectInputDate label="End Date"
												value="#{serviceCatalog.fee.endDate}"
												rendered="#{serviceCatalog.addingCategoryFee}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Edit Category"
														action="#{serviceCatalog.editCategory}"
														rendered="#{!serviceCatalog.editingCategory && !serviceCatalog.addingCategoryFee}" />
													<af:commandButton text="Add Fee"
														action="#{serviceCatalog.addCategoryFee}"
														rendered="#{!serviceCatalog.addingCategoryFee && !serviceCatalog.editingCategory}" />
													<af:commandButton text="Save"
														action="#{serviceCatalog.saveCategory}"
														rendered="#{serviceCatalog.editingCategory}" />
													<af:commandButton text="Save"
														action="#{serviceCatalog.saveCategoryFee}"
														rendered="#{serviceCatalog.addingCategoryFee}" />
													<af:commandButton text="Cancel"
														action="#{serviceCatalog.reset}"
														rendered="#{serviceCatalog.editingCategory || serviceCatalog.addingCategoryFee}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>
									</af:panelGroup>

									<af:panelGroup layout="vertical"
										rendered="#{serviceCatalog.selectedTreeNode.type == 'categoryFee' }">
										<af:panelHeader text="Category Fee" />
										<af:panelForm>
											<af:inputText label="Fee Description"
												value="#{serviceCatalog.fee.feeNm}"
												readOnly="#{!serviceCatalog.editingCategoryFee}" />
											<af:inputText label="Fee Amount"
												value="#{serviceCatalog.fee.amount}"
												readOnly="#{!serviceCatalog.editingCategoryFee}" />
											<af:selectInputDate label="Effective Date"
												value="#{serviceCatalog.fee.effectiveDate}"
												readOnly="#{!serviceCatalog.editingCategoryFee}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
											<af:selectInputDate label="End Date"
												value="#{serviceCatalog.fee.endDate}"
												readOnly="#{!serviceCatalog.editingCategoryFee}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Edit Fee"
														action="#{serviceCatalog.editCategoryFee}"
														rendered="#{!serviceCatalog.editingCategoryFee}" />
													<af:commandButton text="Save"
														action="#{serviceCatalog.saveCategoryFee}"
														rendered="#{serviceCatalog.editingCategoryFee}" />
													<af:commandButton text="Cancel"
														action="#{serviceCatalog.reset}"
														rendered="#{serviceCatalog.editingCategoryFee}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>
									</af:panelGroup>

									<af:panelGroup layout="vertical"
										rendered="#{serviceCatalog.selectedTreeNode.type == 'reportRoot' ||
                        serviceCatalog.selectedTreeNode.type == 'reportGroup' }">
										<af:panelHeader text="Emissions Inventories" />
									</af:panelGroup>

									<af:panelGroup layout="vertical"
										rendered="#{serviceCatalog.selectedTreeNode.type == 'report' }">
										<af:showDetailHeader text="Explanation" disclosed="false">
											<af:outputFormatted styleUsage="instruction"
												value="<ul>
													<li>IMPACT supports one Emissions Inventory Service Catalog template per unique combination of Reporting Year, Content Type, and Regulatory Requirement values. The template for a given combination of values specifies the pollutants for which emission amounts must be entered for each Process included in an Emissions Inventory.</li>
													<li>To create a new Emissions Inventory Service Catalog template:</li>
														<ul>
															<li>Select an existing template that is similar to the one to be created.</li>
															<li>Click on Clone Template.</li>
															<li>Select a Reporting Year.</li>
															<li>Select a Content Type.</li>
															<li>Select a Regulatory Requirement.</li>
															<li>Enter a value for Chargeable Emissions Limit (TPY). This value is the maximum number of tons to be billed per pollutant for the reporting year.</li>
															<li>Enter a value for Minimum Annual Fee. This value is the minimum dollar amount that a facility will be billed per year.</li>
															<li>Enter Facility Selection Criteria.</li>
															<li>Add/delete pollutants as needed. For each pollutant, indicate if it is billed based on allowable instead of actual emissions, specify the display order, and enter a value for QA Threshold.</li>
															<li>Update the Non-Chargeable Pollutants list to reflect changes for the reporting year (if any).</li>
															<li>Enter fee amounts (if any) for first half year and second half year.</li>
															<li>Click Save to save the new template.
														</ul>
													<li>The Fees section supports entering a different fee value for the first half and second half year, in case the fee changes at the beginning of the fiscal year. If the fee is the same throughout the calendar year, simply enter the same value in each fee field.</li>
													<li>An Admin user can edit an existing template by clicking Edit Template, changing pollutant and fee values, and clicking Save. However, existing emissions inventories are not automatically affected until a user accesses and resaves an Emissions Inventory.</li>
					  							</ul>" />
										</af:showDetailHeader>

										<af:panelHeader
											text="Emissions Inventory Service Catalog Template Detail" />

										<af:panelForm>
											<af:inputText label="Name: " required="true"
												value="#{serviceCatalog.report.reportName}" readOnly="true" />
											<af:selectOneChoice label="Content Type: " id="contentType"
												value="#{serviceCatalog.report.contentTypeCd}"
												required="true" readOnly="#{!serviceCatalog.editingReport}">
												<f:selectItems value="#{serviceCatalog.contentTypes}" />
											</af:selectOneChoice>
											<af:selectOneChoice label="Regulatory Requirement: "
												id="regulatoryRequirement"
												value="#{serviceCatalog.report.regulatoryRequirementCd}"
												required="true" readOnly="#{!serviceCatalog.editingReport}">
												<f:selectItems
													value="#{serviceCatalog.regulatoryRequirementTypes}" />
											</af:selectOneChoice>
											<%-- IMPACT currently only supports Per Ton fees --%>
											<af:selectOneRadio id="feeType" label="Fee Type: "
												required="true" value="#{serviceCatalog.feeType}"
												immediate="true" layout="horizontal" readOnly="true">
												<f:selectItem itemLabel="Per Ton" itemValue="unit" />
												<f:selectItem itemLabel="Ton Ranges" itemValue="rnge" />
											</af:selectOneRadio>
											<af:selectOneChoice label="Reporting Year: " id="reportingYr"
												value="#{serviceCatalog.report.reportingYear}"
												required="true" unselectedLabel=""
												readOnly="#{!serviceCatalog.addingReport}">
												<f:selectItems value="#{serviceCatalog.reportingYears}" />
											</af:selectOneChoice>
											<af:inputText label="Chargeable Emissions Limit (TPY): "
												required="true" columns="8"
												value="#{serviceCatalog.report.pollutantCap}"
												maximumLength="8"
												readOnly="#{!serviceCatalog.editingReport}" />
											<af:inputText label="Minimum Annual Fee: " required="true"
												rendered="#{!serviceCatalog.editingReport}" columns="8"
												value="#{serviceCatalog.report.eiMinimumFeeDisplayAsDouble}"
												maximumLength="8"
												readOnly="#{!serviceCatalog.editingReport}">
												<af:convertNumber type="currency" currencySymbol="$" />
											</af:inputText>
											<af:inputText label="Minimum Annual Fee: " required="true"
												rendered="#{serviceCatalog.editingReport}" columns="8"
												value="#{serviceCatalog.report.eiMinimumFee}"
												maximumLength="8">
											</af:inputText>
										</af:panelForm>

										<af:panelHeader text="Facility Selection Criteria" />

										<af:panelForm>
											<af:selectOneChoice id="shapeId" unselectedLabel=""
												label="Facility Location: "
												rendered="#{serviceCatalog.editingReport}"
												value="#{serviceCatalog.report.strShapeId}">
												<f:selectItems
													value="#{serviceCatalogReference.shapeDefs.items[(empty serviceCatalog.report.strShapeId ? '' : serviceCatalog.report.strShapeId)]}" />
											</af:selectOneChoice>
											<af:panelLabelAndMessage for="shapeId"
												label="Facility Location: "
												rendered="#{!serviceCatalog.editingReport}">
												<af:commandLink text="#{serviceCatalog.shapeLabel}"
													action="#{serviceCatalog.displayFacilityLocationOnMap}">
												</af:commandLink>
											</af:panelLabelAndMessage>

											<af:selectManyListbox id="permitClassCds"
												label="Facility Class: "
												readOnly="#{!serviceCatalog.editingReport}"
												value="#{serviceCatalog.report.permitClassCds}">
												<f:selectItems
													value="#{facilityReference.permitClassDefs.items[(empty serviceCatalog.report.permitClassCds ? '' : serviceCatalog.report.permitClassCds)]}" />
											</af:selectManyListbox>

											<af:selectOneChoice id="treatPartialAsFullPeriodFlag"
												unselectedLabel=" "
												readOnly="#{!serviceCatalog.editingReport}"
												label="Require Emissions Reporting if Facility Class Met During Period?: "
												value="#{serviceCatalog.report.treatPartialAsFullPeriodFlag}">
												<f:selectItem itemLabel="Yes" itemValue="Y" />
												<f:selectItem itemLabel="No" itemValue="N" />
											</af:selectOneChoice>

											<af:selectManyListbox id="facilityTypeCds"
												label="Facility Type: "
												rendered="#{serviceCatalog.editingReport}"
												styleClass="FacilityTypeClass x6"
												value="#{serviceCatalog.report.facilityTypeCds}">
												<f:selectItems
													value="#{facilityReference.facilityTypeDefs.items[(empty serviceCatalog.report.facilityTypeCds ? '' : serviceCatalog.report.facilityTypeCds)]}" />
											</af:selectManyListbox>

											<af:selectManyListbox id="facilityTypeCdsRO"
												label="Facility Type: "
												rendered="#{!serviceCatalog.editingReport}" readOnly="true"
												value="#{serviceCatalog.report.facilityTypeCds}">
												<f:selectItems
													value="#{facilityReference.facilityTypeTextDefs.items[(empty serviceCatalog.report.facilityTypeCds ? '' : serviceCatalog.report.facilityTypeCds)]}" />
											</af:selectManyListbox>

										</af:panelForm>

										<af:showDetailHeader text="Criteria Air Pollutants/Other"
											disclosed="true" id="pollutants">

											<af:panelForm>
												<af:outputFormatted inlineStyle="font-size:75%"
													value="QA Threshold marks the minimum amount of reported emissions that will cause additional facility validations to be performed. Not entering a QA Threshold will result in no additional facility validations. The additional facility validations performed can be chosen within the Validation Control definitions list. Note: If any of the PM pollutants are set to be billed based on allowable instead of actual, the system will ensure that all PM pollutants are set to be billed based on allowable instead of actual." />
											</af:panelForm>
											<af:panelForm>
												<af:table id="pollutantTable"
													value="#{serviceCatalog.pollutantTableWrapper}"
													bandingInterval="1" banding="row" var="pollutant"
													width="80%"
													binding="#{serviceCatalog.pollutantTableWrapper.table}">
													<f:facet name="selection">
														<af:tableSelectMany
															disabled="#{!serviceCatalog.editingReport}"
															rendered="#{serviceCatalog.editingReport}" />
													</f:facet>
													<af:column sortProperty="pollutantCd" sortable="true"
														rendered="#{!serviceCatalog.editingReport}" width="75"
														formatType="text" headerText="Pollutant Code">
														<af:inputText readOnly="#{!serviceCatalog.editingReport}"
															value="#{pollutant.pollutantCd}" />
													</af:column>
													<af:column sortProperty="pollutantDsc" sortable="true"
														id="pollutantCd" formatType="text" headerText="Pollutant">
														<af:selectOneChoice value="#{pollutant.pollutantCd}"
															readOnly="#{!serviceCatalog.editingReport || pollutant.deprecated}"
															required="true">
															<f:selectItems
																value="#{facilityReference.pollutantDefs.items[(empty pollutant.pollutantCd ? '' : pollutant.pollutantCd)]}" />
														</af:selectOneChoice>
													</af:column>

													<af:column sortProperty="billedBasedOnPermitted"
														sortable="true" formatType="text"
														headerText="Billed Based on Allowable instead of Actual">
														<afh:rowLayout halign="center">
															<af:selectBooleanCheckbox
																readOnly="#{!serviceCatalog.editingReport}"
																value="#{pollutant.billedBasedOnPermitted}" />
														</afh:rowLayout>
													</af:column>
													<af:column sortProperty="displayOrder" sortable="true"
														formatType="number" headerText="Display Order">
														<af:inputText columns="2" required="true"
															readOnly="#{!serviceCatalog.editingReport}"
															value="#{pollutant.displayOrder}" />
													</af:column>
													<af:column sortProperty="thresholdQa" sortable="true"
														formatType="number" headerText="QA Threshold (TPY)">
														<af:inputText columns="10" required="false"
															readOnly="#{!serviceCatalog.editingReport}"
															value="#{pollutant.thresholdQa}" />
													</af:column>
													<f:facet name="footer">
														<afh:rowLayout halign="center">
															<af:panelButtonBar>
																<af:commandButton text="Add Pollutant"
																	action="#{serviceCatalog.addPollutant}"
																	disabled="#{!serviceCatalog.editingReport}">
																</af:commandButton>
																<af:commandButton text="Delete Selected Pollutants"
																	action="#{serviceCatalog.deletePollutants}"
																	disabled="#{!serviceCatalog.editingReport}">
																</af:commandButton>
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
											</af:panelForm>
										</af:showDetailHeader>

										<af:showDetailHeader text="Non-Chargeable Pollutants"
											disclosed="true" id="nonchargePollutants">
											<af:panelForm>
												<af:outputFormatted inlineStyle="font-size:75%"
													value="Adding a pollutant to this table will indicate that the emissions for that pollutant are 
								excluded from fee calculations. Adding an optional Source Classification Code (SCC) to a row will 
								indicate that emissions for that pollutant are excluded from fee calculations soley for that SCC. 
								Checking the Fugitive Only box will restrict the fee exclusion for the pollutant to fugitive 
								emissions only." />
											</af:panelForm>
											<af:panelForm>
												<af:table value="#{serviceCatalog.ncPollutantTableWrapper}"
													bandingInterval="1" banding="row"
													var="nonchargeableNcPollutant" width="80%"
													binding="#{serviceCatalog.ncPollutantTableWrapper.table}">
													<f:facet name="selection">
														<af:tableSelectMany
															disabled="#{!serviceCatalog.editingReport}"
															rendered="#{serviceCatalog.editingReport}" />
													</f:facet>
													<af:column sortProperty="pollutantCd" sortable="true"
														rendered="#{!serviceCatalog.editingReport}" width="75"
														formatType="text" headerText="Pollutant Code">
														<af:inputText readOnly="true"
															value="#{nonchargeableNcPollutant.pollutantCd}" />
													</af:column>
													<af:column sortProperty="pollutantDsc" sortable="true"
														id="pollutantCd" formatType="text" headerText="Pollutant">
														<af:inputText
															value="#{nonchargeableNcPollutant.pollutantDsc}"
															readOnly="true" required="true">
														</af:inputText>
													</af:column>
													<af:column sortProperty="scc" sortable="true"
														formatType="text" headerText="SCC" width="75">
														<af:inputText readOnly="true"
															value="#{nonchargeableNcPollutant.sccCd.sccId}" />
													</af:column>
													<af:column sortProperty="sccDesc" sortable="true"
														formatType="text" headerText="SCC Description">
														<af:inputText readOnly="true"
															value="#{nonchargeableNcPollutant.sccDesc}" />
													</af:column>
													<af:column sortProperty="fugitiveOnly" sortable="true"
														formatType="text" headerText="Fugitive Only" width="75">
														<afh:rowLayout halign="center">
															<af:selectBooleanCheckbox readOnly="true"
																value="#{nonchargeableNcPollutant.fugitiveOnly}" />
														</afh:rowLayout>
													</af:column>
													<f:facet name="footer">
														<afh:rowLayout halign="center">
															<af:panelButtonBar>
																<af:commandButton text="Add Pollutant" useWindow="true"
																	windowWidth="760" windowHeight="300"
																	action="#{serviceCatalog.startAddNonChargeablePollutant}"
																	disabled="#{!serviceCatalog.editingReport}">
																</af:commandButton>
																<af:commandButton text="Add Pollutant Category"
																	useWindow="true" windowWidth="760" windowHeight="300"
																	action="#{serviceCatalog.startSelectPollutantCategory}"
																	disabled="#{!serviceCatalog.editingReport}">
																</af:commandButton>
																<af:commandButton text="Delete Selected Pollutants"
																	action="#{serviceCatalog.deleteNonChargePollutants}"
																	disabled="#{!serviceCatalog.editingReport}">
																</af:commandButton>
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
											</af:panelForm>
										</af:showDetailHeader>

										<af:showDetailHeader text="Data Import File Pollutants"
											disclosed="true" id="dataImportPollutants">
											<af:panelForm>
												<af:outputFormatted inlineStyle="font-size:75%"
													value="Adding a pollutant to this table will indicate that the emissions for pollutant can be imported from the data import file. 
									Do not add a pollutant to this table if it is already present in the Criteria Air Pollutants/Other table." />
											</af:panelForm>
											<af:panelForm>
												<af:table
													value="#{serviceCatalog.dataImportPoluttantTableWrapper}"
													bandingInterval="1" banding="row" var="dataImportPollutant"
													width="70%"
													binding="#{serviceCatalog.dataImportPoluttantTableWrapper.table}">
													<f:facet name="selection">
														<af:tableSelectMany
															disabled="#{!serviceCatalog.editingReport}"
															rendered="#{serviceCatalog.editingReport}" />
													</f:facet>
													<af:column sortProperty="pollutantCd" sortable="true"
														id="pollutantCd" formatType="text"
														headerText="Pollutant Code"
														rendered="#{!serviceCatalog.editingReport}" width="75">
														<af:inputText label="Pollutant"
															readOnly="#{!serviceCatalog.editingReport}"
															value="#{dataImportPollutant.pollutantCd}" />
													</af:column>
													<af:column sortProperty="pollutantDsc" sortable="true"
														id="pollutant" formatType="text" headerText="Pollutant">
														<af:selectOneChoice
															value="#{dataImportPollutant.pollutantCd}"
															label="Pollutant"
															readOnly="#{!serviceCatalog.editingReport}"
															required="true">
															<f:selectItems
																value="#{facilityReference.pollutantDefs.items[(empty dataImportPollutant.pollutantCd ? '' : dataImportPollutant.pollutantCd)]}" />
														</af:selectOneChoice>
													</af:column>
													<af:column sortProperty="sortOrder" sortable="true"
														id="sortOrder" formatType="number" headerText="Sort Order">
														<af:inputText required="true" columns="3"
															label="Sort Order"
															readOnly="#{!serviceCatalog.editingReport}"
															value="#{dataImportPollutant.sortOrder}" />
													</af:column>
													<f:facet name="footer">
														<afh:rowLayout halign="center">
															<af:panelButtonBar>
																<af:commandButton text="Add Pollutant"
																	action="#{serviceCatalog.addDataImportPollutant}"
																	disabled="#{!serviceCatalog.editingReport}">
																</af:commandButton>
																<af:commandButton text="Delete Selected Pollutants"
																	action="#{serviceCatalog.deleteDataImportPollutants}"
																	disabled="#{!serviceCatalog.editingReport}">
																</af:commandButton>
																<af:commandButton text="Printable view"
																	actionListener="#{tableExporter.printTable}"
																	onclick="#{tableExporter.onClickScript}">
																</af:commandButton>
																<af:commandButton text="Export to excel"
																	actionListener="#{tableExporter.excelTable}"
																	onclick="#{tableExporter.onClickScript}">
																</af:commandButton>
															</af:panelButtonBar>
														</afh:rowLayout>
													</f:facet>
												</af:table>
											</af:panelForm>
										</af:showDetailHeader>

										<af:showDetailHeader text="Fees" disclosed="true">
											<af:panelForm>
												<af:inputText label="First Half: "
													tip="Fee for first half year (January-June)"
													value="#{serviceCatalog.report.feeFirstHalf.amount}"
													readOnly="#{!serviceCatalog.editingReport}"
													rendered="#{!serviceCatalog.editingReport}" required="true">
													<af:convertNumber type="currency" currencySymbol="$" />
												</af:inputText>
												<af:inputText label="First Half: "
													tip="Fee for first half year (January-June)"
													required="true"
													value="#{serviceCatalog.report.feeFirstHalf.amount}"
													rendered="#{serviceCatalog.editingReport}">
													<af:convertNumber type="number" maxFractionDigits="2"
														minFractionDigits="2" />
												</af:inputText>
												<af:inputText label="Second Half: "
													tip="Fee for second half year (July-December)"
													value="#{serviceCatalog.report.feeSecondHalf.amount}"
													readOnly="#{!serviceCatalog.editingReport}"
													rendered="#{!serviceCatalog.editingReport}" required="true">
													<af:convertNumber type="currency" currencySymbol="$" />
												</af:inputText>
												<af:inputText label="Second Half: "
													tip="Fee for second half year (July-December)"
													required="true"
													value="#{serviceCatalog.report.feeSecondHalf.amount}"
													rendered="#{serviceCatalog.editingReport}">
													<af:convertNumber type="number" maxFractionDigits="2"
														minFractionDigits="2" />
												</af:inputText>
											</af:panelForm>
										</af:showDetailHeader>

										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton text="Clone Template" id="cloneSCTemplate"
													action="#{serviceCatalog.cloneReport}"
													rendered="#{!serviceCatalog.editingReport}" />
												<af:commandButton text="Edit Template" id="editSCTemplate"
													action="#{serviceCatalog.editReport}" immediate="true"
													rendered="#{!serviceCatalog.editingReport && serviceCatalog.reportEditable}" />
												<af:commandButton text="Save" id="saveSCTemplate"
													action="#{serviceCatalog.saveReport}"
													rendered="#{serviceCatalog.editingReport && !serviceCatalog.pollutantsDeprecated
									&& !serviceCatalog.ncPollutantsDeprecated && !serviceCatalog.dataImportPollutantsDeprecated}" />
												<af:commandButton text="Cancel" immediate="true"
													id="cancelSCTemplate" action="#{serviceCatalog.cancelEdit}"
													rendered="#{serviceCatalog.editingReport}" />

												<af:commandButton id="deleteBtn" text="Delete"
													useWindow="true" windowWidth="#{confirmWindow.width}"
													windowHeight="#{confirmWindow.height}"
													rendered="#{infraDefs.stars2Admin && !serviceCatalog.editingReport}"
													disabled="#{!serviceCatalog.allowedToDeleteReport}"
													shortDesc="#{serviceCatalog.allowedToDeleteReport 
													? '' : 'service catalog template detail is associated with one or more EIs or enabled EIs' }"
													action="#{confirmWindow.confirm}">
													<t:updateActionListener property="#{confirmWindow.type}"
														value="#{confirmWindow.yesNo}" />
													<t:updateActionListener property="#{confirmWindow.method}"
														value="serviceCatalog.removeReport" />
													<t:updateActionListener property="#{confirmWindow.message}"
														value="Click Yes to confirm the deletion of the service catalog template detail." />
												</af:commandButton>
											</af:panelButtonBar>
										</afh:rowLayout>
									</af:panelGroup>
								</h:panelGrid>
							</f:facet>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
		<f:verbatim><%@ include
				file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/FacilityType-Option.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/wording-filter.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/facility-detail-location.js"%></f:verbatim>
	</af:document>
</f:view>

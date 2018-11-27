<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Field Audit Log">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Field Audit Log">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">

								<af:objectSpacer width="100%" height="7" />
								<af:panelGroup layout='horizontal'>
									<af:outputText value="Note: In order to prevent out of memory issues, search results are limited to 1000 rows."/>
								</af:panelGroup>
								<af:objectSpacer width="100%" height="7" />

								<af:panelGroup layout='horizontal'>
									<af:panelForm rows="1" width="1000" maxColumns="3"
										partialTriggers="category">
										
										<af:selectOneChoice label="Category" autoSubmit="true"
											id="category"
											tip="Please select Field Audit Log category to retrieve."
											value="#{fieldAuditLogSearch.categoryCd}">
											<f:selectItems
												value="#{infraDefs.fieldAuditLogCategories.items[(empty fieldAuditLogSearch.searchFieldAuditLog.categoryCd ? '' : fieldAuditLogSearch.searchFieldAuditLog.categoryCd)]}" />
										</af:selectOneChoice>
										
										<af:selectOneChoice label="Attribute" id="attribute" unselectedLabel="" 
											rendered="#{fieldAuditLogSearch.categoryCd != null}"
											value="#{fieldAuditLogSearch.searchFieldAuditLog.attributeCd}"
											tip="List of attributes in the selected category."
											valign="top">
											<f:selectItems
												value="#{fieldAuditLogSearch.fieldAudLogAttrDefs}" />
										</af:selectOneChoice>
	
										<af:inputText tip="0000000000, 0%, %0%, *0*, 0*" columns="12"
											label="Facility ID"
											rendered="#{fieldAuditLogSearch.categoryCd != null && fieldAuditLogSearch.categoryCd != 'cmp'}"
											value="#{fieldAuditLogSearch.searchFieldAuditLog.facilityId}"
											valign="top" />
										<af:inputText tip="0000000000, 0%, %0%, *0*, 0*" columns="12"
											label="Company ID"
											rendered="#{fieldAuditLogSearch.categoryCd != null}"
											value="#{fieldAuditLogSearch.searchFieldAuditLog.cmpId}"
											valign="top" />
									</af:panelForm>
									<af:panelForm rows="2" maxColumns="1" partialTriggers="category">	
										<af:selectInputDate id="falFromDate" label="From Date"
											rendered="#{fieldAuditLogSearch.categoryCd != null}"
											value="#{fieldAuditLogSearch.beginDt}">
											<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
										</af:selectInputDate>	
										<af:selectInputDate id="falToDate" label="To Date"
											rendered="#{fieldAuditLogSearch.categoryCd != null}"
											value="#{fieldAuditLogSearch.endDt}">
											<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
										</af:selectInputDate>
									</af:panelForm>			
								</af:panelGroup>
									
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{fieldAuditLogSearch.submitSearch}">
										</af:commandButton>

										<af:commandButton text="Reset" immediate="true"
											action="#{fieldAuditLogSearch.reset}" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1"
						rendered="#{fieldAuditLogSearch.hasSearchResults}">
						<af:panelBorder>

							<af:showDetailHeader text="Field Audit Log List" disclosed="true">

								<af:table value="#{fieldAuditLogSearch.resultsWrapper}"
									binding="#{fieldAuditLogSearch.resultsWrapper.table}"
									bandingInterval="1" banding="row" var="fieldAuditLog"
									rows="#{fieldAuditLogSearch.pageLimit}">
									<af:column sortProperty="c01" sortable="true" noWrap="true"
										formatType="text" headerText="Facility Id"
										rendered="#{!fieldAuditLogSearch.cmpSearch}">
										<af:commandLink action="#{fieldAuditLogSearch.submitProfileFacility}"
											text="#{fieldAuditLog.facilityId}">
											<t:updateActionListener
												property="#{fieldAuditLogSearch.selFacilityId}"
												value="#{fieldAuditLog.facilityId}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>
									<af:column sortProperty="c02" sortable="true" formatType="text"
										headerText="Facility Name"
										rendered="#{!fieldAuditLogSearch.cmpSearch}">
										<af:outputText value="#{fieldAuditLog.facilityName}" />
									</af:column>
									<af:column sortProperty="c03" sortable="true"
										noWrap="true" formatType="text" headerText="Company Id">
										<%-- rendered="#{fieldAuditLogSearch.cmpSearch}"--%>
										<af:commandLink action="#{fieldAuditLogSearch.submitProfileCompany}"
											text="#{fieldAuditLog.cmpId}">
											<t:updateActionListener
												property="#{fieldAuditLogSearch.selCmpId}"
												value="#{fieldAuditLog.cmpId}" />
											<t:updateActionListener
												property="#{menuItem_companyProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>
									<af:column sortProperty="c04" sortable="true" formatType="text"
										headerText="Company Name">
										<%-- rendered="#{fieldAuditLogSearch.cmpSearch}">--%>
										<af:outputText value="#{fieldAuditLog.companyName}" />
									</af:column>
									<af:column sortProperty="c05" sortable="true" formatType="text"
										headerText="Category">
										<af:outputText
											value="#{infraDefs.fieldAuditLogCategories.itemDesc[(empty fieldAuditLog.categoryCd ? '' : fieldAuditLog.categoryCd)]}" />
									</af:column>
									<af:column sortProperty="c06" sortable="true" formatType="text"
										headerText="Attribute">
										<af:outputText
											value="#{infraDefs.fieldAuditLogAttributes.itemDesc[(empty fieldAuditLog.attributeCd ? '' : fieldAuditLog.attributeCd)]}" />
									</af:column>
									<af:column sortProperty="c07" sortable="true" formatType="text"
										headerText="Unique Id">
										<af:outputText value="#{fieldAuditLog.uniqueId}" />
									</af:column>
									<af:column sortProperty="c08" sortable="true" formatType="text"
										headerText="Old Value">
										<af:outputText value="#{fieldAuditLog.originalValue}" />
									</af:column>
									<af:column sortProperty="c09" sortable="true" formatType="text"
										headerText="New Value">
										<af:outputText value="#{fieldAuditLog.newValue}" />
									</af:column>
									<af:column sortProperty="c10" sortable="true" formatType="text"
										headerText="User Name">
										<af:selectOneChoice readOnly="true"
											value="#{fieldAuditLog.userId}">
											<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="dateEntered" noWrap="true"
										sortable="true" formatType="text" headerText="Date">
										<af:selectInputDate value="#{fieldAuditLog.dateEntered}"
											readOnly="true">
											<f:convertDateTime dateStyle="full"
												pattern="MM/dd/yyyy HH:mm:ss" />
										</af:selectInputDate>
									</af:column>
									<f:facet name="footer">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton
													actionListener="#{tableExporter.printTable}"
													onclick="#{tableExporter.onClickScriptTS}"
													text="Printable view" />
												<af:commandButton
													actionListener="#{tableExporter.excelTable}"
													onclick="#{tableExporter.onClickScriptTS}"
													text="Export to excel" />
											</af:panelButtonBar>
										</afh:rowLayout>
									</f:facet>
								</af:table>

							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</af:page>
		</af:form>
	</af:document>
</f:view>

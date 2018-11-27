<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Facility Purge Log">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Facility Purge Log">
				<%@ include file="../util/header.jsp"%>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" rendered="#{facilityPurgeLogs.hasSearchResults}">
						<af:panelBorder>
							<af:showDetailHeader text="Facility Purge Log List" disclosed="true">

								<af:table value="#{facilityPurgeLogs.resultsWrapper}"
									binding="#{facilityPurgeLogs.resultsWrapper.table}"
									bandingInterval="1" banding="row" var="purgeLog"
									rows="#{facilityPurgeLogs.pageLimit}">
									<af:column id="facilityId" headerText="Facility Id" formatType="text"
										sortProperty="facilityId" sortable="true" noWrap="true">
										<af:outputText value="#{purgeLog.facilityId }" />
									</af:column>
									<af:column id="facilityName" headerText="Facility Name"
										sortProperty="facilityName" sortable="true" formatType="text">
										<af:outputText value="#{purgeLog.facilityName}" />
									</af:column>
									<af:column id="companyId" headerText="Company Id" 
										sortProperty="companyId" sortable="true" noWrap="true" formatType="text" >
										<af:outputText value="#{purgeLog.companyId}" />
									</af:column>
									<af:column id="companyName" headerText="Company Name" 
										sortProperty="companyName" sortable="true" formatType="text">
										<af:outputText value="#{purgeLog.companyName}" />
									</af:column>
									<af:column id="shutdownDate" headerText="Facility Shutdown Date" 
										sortProperty="shutdownDate" sortable="true" formatType="text">
										<af:selectInputDate value="#{purgeLog.shutdownDate}" readOnly="true" />
									</af:column>
									<af:column id="purgedDate" headerText="Date Purged" 
										sortProperty="purgedDate" sortable="true" formatType="text">
										<af:selectInputDate value="#{purgeLog.purgedDate}" readOnly="true" />
									</af:column>
									
									<af:column id="userId" headerText="User Name" 
										sortProperty="userId" sortable="true" formatType="text">
										<af:selectOneChoice value="#{purgeLog.userId}" readOnly="true">
											<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
										</af:selectOneChoice>
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

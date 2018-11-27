<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Yearly Emissions Inventories Enabled Status">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Yearly Emissions Inventories Enabled Status">

				<jsp:include page="header.jsp" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="facilityHeader">
								<jsp:include page="comFacilityHeader2.jsp" />
							</f:subview>
						</f:facet>

						<f:facet name="right">
							<h:panelGrid columns="1" border="1"
								style="margin-left:auto;margin-right:auto;">
								<af:panelGroup layout="vertical">
									<af:objectSpacer height="10" />

									<af:objectSpacer height="10" />
									<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0"
										rendered="reportProfile.internalApp">
										<afh:rowLayout halign="left">
											<af:outputFormatted
												value="The State is changed by each step of a Emissions Reporting/Invoicing work flow and when a reminder or late correspondence is generated.  The only time this state is used by the system is when bulk reporting reminders are generated.  A reminder is generated if the state is 'Report Required'." />
										</afh:rowLayout>
									</afh:tableLayout>
									<af:panelForm>
										<af:table value="#{reportProfile.rptInfoWrapper}"
											banding="row" binding="#{reportProfile.rptInfoWrapper.table}"
											bandingInterval="1" var="yearlyInfo" width="98%">
											<af:column sortProperty="c01" sortable="true"
												formatType="number" headerText="Year" width="25px">
												<af:commandLink text="#{yearlyInfo.year}" useWindow="true"
													windowWidth="500" windowHeight="375"
													action="#{reportProfile.startEditReportingYear}">
													<t:updateActionListener
														property="#{reportProfile.emissionsRptInfoRow}"
														value="#{yearlyInfo}" />
												</af:commandLink>
											</af:column>
											<af:column sortProperty="c02" sortable="true"
												formatType="text" headerText="Content Type" width="60px" noWrap="true">
												<af:selectOneChoice value="#{yearlyInfo.contentTypeCd}"
													readOnly="true">
													<f:selectItems
														value="#{facilityReference.contentTypeDefs.items[(empty yearlyInfo.contentTypeCd ? '' : yearlyInfo.contentTypeCd)]}" />
												</af:selectOneChoice>
											</af:column>
											<af:column sortProperty="c03" sortable="true"
												formatType="text" headerText="Regulatory Requirement" width="60px" noWrap="true">
												<af:selectOneChoice value="#{yearlyInfo.regulatoryRequirementCd}"
													readOnly="true">
													<f:selectItems
														value="#{facilityReference.regulatoryRequirementDefs.items[(empty yearlyInfo.regulatoryRequirementCd ? '' : yearlyInfo.regulatoryRequirementCd)]}" />
												</af:selectOneChoice>
											</af:column>
											<af:column headerText="Reporting Enabled" formatType="icon"
												sortProperty="c04" sortable="true" width="60px">
												<af:selectBooleanCheckbox
													readOnly="true"
													value="#{yearlyInfo.reportingEnabled}" />
											</af:column>
											<af:column sortProperty="c05" sortable="true"
												formatType="text" headerText="State" width="30px" noWrap="true">
												<af:selectOneChoice value="#{yearlyInfo.state}"
													readOnly="true">
													<f:selectItems
														value="#{facilityReference.reportStatusDefs.items[(empty yearlyInfo.state ? '' : yearlyInfo.state)]}" />
												</af:selectOneChoice>
											</af:column>
											<af:column sortProperty="c06" sortable="true"
												formatType="text" headerText="Comment"
												rendered="#{reportProfile.internalApp}" noWrap="true">
												<af:outputText value="#{yearlyInfo.truncatedComment}" />
											</af:column>
											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton text="Add" useWindow="true"
															windowWidth="500" windowHeight="375"
															disabled="#{!reportProfile.categoryYearsLeft}"
															rendered="#{facilityProfile.dapcUser && reportProfile.facilityReportEnabler}" 
															action="#{reportProfile.startAddReportingYear}" />
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
								</af:panelGroup>
							</h:panelGrid>
						</f:facet>
					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>

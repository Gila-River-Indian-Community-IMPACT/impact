<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Emission Units Permit History">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" id="euPermitHistory" value="#{menuModel.model}"
				title="Emission Units Permit History">
				<af:messages />
				<jsp:include page="header.jsp" />
				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="facilityHeader">
								<jsp:include page="comFacilityHeader.jsp" />
							</f:subview>
						</f:facet>

						<f:facet name="right">
							<h:panelGrid columns="1" border="1" width="950"
								style="margin-left:auto;margin-right:auto;">
								<af:panelGroup layout="vertical">
									<af:objectSpacer height="10" />
									<af:panelForm>
										<af:table value="#{facilityProfile.euHistPermits}" width="99%"
											var="permit" rows="#{facilityProfile.pageLimit}">
											<af:column headerText="Permit Number" sortable="true"
												sortProperty="permitNumber" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:commandLink text="#{permit.permitNumber}"
														action="#{permitDetail.loadPermit}">
														<af:setActionListener to="#{permitDetail.permitID}"
															from="#{permit.permitID}" />
														<t:updateActionListener
															property="#{permitDetail.fromTODOList}" value="false" />
													</af:commandLink>
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="EU Id" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:inputText readOnly="true"
														value="#{permit.permitEU.fpEU.epaEmuId}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="EU Permit Status" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectOneChoice label="EU Permit Status :"
														readOnly="true" value="#{permit.permitEU.permitStatusCd}">
														<mu:selectItems
															value="#{permitReference.permitStatusDefs}" />
													</af:selectOneChoice>
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Application Numbers" sortable="true"
												sortProperty="applicationNumbers">
												<af:panelHorizontal valign="middle" halign="left">
													<af:inputText readOnly="true"
														value="#{permit.applicationNumbers}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Type" sortable="true"
												sortProperty="permitTypeDsc" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectOneChoice unselectedLabel=" " readOnly="true"
														value="#{permit.permitType}">
														<mu:selectItems value="#{permitReference.permitTypes}" />
													</af:selectOneChoice>
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Publication Stage" sortable="true"
												sortProperty="permitGlobalStatusCD" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectOneChoice label="Permit status"
														value="#{permit.permitGlobalStatusCD}" readOnly="true"
														id="soc2">
														<mu:selectItems
															value="#{permitReference.permitGlobalStatusDefs}"
															id="soc2" />
													</af:selectOneChoice>
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Reason(s)" sortable="true"
												sortProperty="permitReasons">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectManyCheckbox label="Reason(s) :" valign="top"
														readOnly="true" value="#{permit.permitReasonCDs}"
														inlineStyle="white-space: nowrap;">
														<f:selectItems value="#{permitSearch.allPermitReasons}" />
													</af:selectManyCheckbox>
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Effective Date" sortable="true"
												sortProperty="effectiveDate" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectInputDate readOnly="true"
														value="#{permit.effectiveDate}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Expiration Date" sortable="true"
												sortProperty="expirationDate" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectInputDate readOnly="true"
														value="#{permit.expirationDate}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Draft Issuance Date" sortable="true"
												sortProperty="draftIssueDate" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectInputDate readOnly="true"
														value="#{permit.draftIssueDate}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Final Issuance Date" sortable="true"
												sortProperty="finalIssueDate" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectInputDate readOnly="true"
														value="#{permit.finalIssueDate}" />
												</af:panelHorizontal>
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
<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Analysis Document">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Analysis Document">
				<%@ include file="../permits/header.jsp"%>

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="permitDetailTop">
								<jsp:include page="permitDetailTop.jsp" />
							</f:subview>
						</f:facet>

						<%
							/* Content begin */
						%>
						<h:panelGrid columns="1" border="1"
							width="#{permitDetail.permitWidth}">
							<afh:rowLayout halign="center">
								<af:panelGroup>
									<af:panelHeader text="Issued Document(s)">
										<afh:tableLayout>
											<afh:rowLayout>
												<afh:cellFormat halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="195" halign="right" valign="top">
															<af:inputText label="Draft :" readOnly="true" />
														</afh:cellFormat>
														<afh:cellFormat width="190">
															<af:goLink targetFrame="_blank"
																rendered="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.draftFlag] != null}"
																destination="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.draftFlag].docURL}"
																text="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.draftFlag].description}" />
														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>

												<afh:cellFormat
												<%-- remove TIVPTO for WY
													rendered="#{permitDetail.permit.permitType == 'TVPTO' || permitDetail.permit.permitType == 'TIVPTO'}"
												--%>
													rendered="#{permitDetail.permit.permitType == 'TVPTO'}"
													halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="195" halign="right" valign="top">
															<af:inputText label="PPP :" readOnly="true" />
														</afh:cellFormat>
														<afh:cellFormat width="190">
															<af:goLink targetFrame="_blank"
																rendered="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.pppFlag] != null}"
																destination="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.pppFlag].docURL}"
																text="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.pppFlag].description}" />
														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>

												<afh:cellFormat
												<%-- remove TIVPTO for WY
													rendered="#{permitDetail.permit.permitType == 'TVPTO' || permitDetail.permit.permitType == 'TIVPTO'}"
												--%>
													rendered="#{permitDetail.permit.permitType == 'TVPTO'}"
													halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="195" halign="right" valign="top">
															<af:inputText label="PP :" readOnly="true" />
														</afh:cellFormat>
														<afh:cellFormat width="200">
															<af:goLink targetFrame="_blank"
																rendered="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.ppFlag] != null}"
																destination="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.ppFlag].docURL}"
																text="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.ppFlag].description}" />
														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>

												<afh:cellFormat halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="195" halign="right" valign="top">
															<af:inputText label="Final :" readOnly="true" />
														</afh:cellFormat>
														<afh:cellFormat width="200">
															<af:goLink targetFrame="_blank"
																rendered="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.finalFlag] != null}"
																destination="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.finalFlag].docURL}"
																text="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.finalFlag].description}" />
														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>
											</afh:rowLayout>
										</afh:tableLayout>
									</af:panelHeader>

									<af:objectSpacer height="5" />

									<af:panelHeader text="Working Copies">
										<af:outputText inlineStyle="font-weight:bold"
											value="Move the version you would like to have issued to the top of the list." />
										<af:objectSpacer height="5" />
										<af:table value="#{permitDetail.tcs}" id="TCTab"
											width="#{permitDetail.permitTableWidth}"
											partialTriggers="ThePage:TCTab:TopButton ThePage:TCTab:delButton ThePage:TCTab:UploadDocButton TCTab:GenerateDocButton TCTab:EditDocButton TCTab:CloneDocButton"
											var="doc" emptyText=" " rows="50">
											<af:column formatType="icon" width="5%"
												rendered="#{permitDetail.editMode}">
												<af:panelButtonBar>
													<af:commandButton text="Top" id="TopButton"
														rendered="#{doc.issuanceStageFlag == null}"
														action="#{permitDetail.topTCDoc}">
														<t:updateActionListener
															property="#{permitDetail.singleDoc}" value="#{doc}" />
													</af:commandButton>
													<af:commandButton text="Delete" id="delButton"
														rendered="#{doc.issuanceStageFlag == null}"
														returnListener="#{permitDetail.deleteDoc}"
														action="#{confirmWindow.confirm}" useWindow="true"
														windowWidth="#{confirmWindow.width}"
														windowHeight="#{confirmWindow.height}">
														<t:updateActionListener property="#{confirmWindow.type}"
															value="#{confirmWindow.yesNo}" />
														<t:updateActionListener
															property="#{confirmWindow.message}"
															value="Click Yes to confirm the deletion of #{doc.description}" />
														<t:updateActionListener
															property="#{permitDetail.singleDoc}" value="#{doc}" />
													</af:commandButton>
												</af:panelButtonBar>
											</af:column>
											<af:column headerText="Description"
												sortProperty="description" sortable="true" formatType="text">
												<af:goLink targetFrame="_blank" destination="#{doc.docURL}"
													text="#{doc.description}" />
											</af:column>
											<af:column headerText="Last modified by"
												sortProperty="lastModifiedBy" sortable="true" width="15%"
												formatType="text">
												<af:selectOneChoice readOnly="true"
													value="#{doc.lastModifiedBy}">
													<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
												</af:selectOneChoice>
											</af:column>
											<af:column headerText="Last modified date"
												sortProperty="lastModifiedTS" sortable="true" width="15%"
												formatType="text">
												<af:selectInputDate readOnly="true"
													value="#{doc.lastModifiedTS}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>
											</af:column>
											<af:column headerText="Issue as" width="10%"
												sortProperty="issuanceStageFlag" sortable="true"
												formatType="text">
												<af:switcher
													facetName="#{empty doc.issuanceStageFlag? 'notIssued': 'issued'}"
													defaultFacet="notIssued">
													<f:facet name="notIssued">
														<af:outputText value="Not issued" />
													</f:facet>
													<f:facet name="issued">
														<af:outputText
															value="#{permitReference.permitIssuanceStageDefs.itemDesc[doc.issuanceStageFlag]}" />
													</f:facet>
												</af:switcher>
											</af:column>

											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton text="Upload" id="UploadDocButton"
															useWindow="true" windowWidth="500" windowHeight="300"
															rendered="#{!permitDetail.readOnlyUser}"
															returnListener="#{permitDetail.docDialogDone}"
															action="#{permitDetail.startUploadTCDoc}" />
														<af:commandButton text="Generate" id="GenerateDocButton"
															useWindow="true" windowWidth="500" windowHeight="300"
															rendered="#{!permitDetail.readOnlyUser}"
															returnListener="#{permitDetail.docDialogDone}"
															action="#{permitDetail.startGenerateTCDoc}" />
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
									</af:panelHeader>
									<%
										/* Terms and conditions end */
									%>

									<af:objectSpacer height="10" />
									<f:subview id="permitDetailButtons">
										<jsp:include page="editOnlyButtons.jsp" />
									</f:subview>

								</af:panelGroup>
							</afh:rowLayout>
						</h:panelGrid>
						<%
							/* Content end */
						%>

					</af:panelBorder>
				</h:panelGrid>
			</af:page>
			<af:iterator value="#{permitDetail}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
			</af:iterator>
		</af:form>
	</af:document>
</f:view>

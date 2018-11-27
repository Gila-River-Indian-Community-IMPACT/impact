<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>

	<af:document title="Permit Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true"
			partialTriggers="ThePage:permitDetailButtons:syncEUs ThePage:permitDetailButtons:switchTo ThePage:permitDetailButtons:convertTo ThePage:permitDetailButtons:deadEnd ThePage:permitDetailButtons:denyPermit ThePage:permitDetailButtons:unDenyPermit ThePage:currectEUs">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				rendered="#{permitDetail.permit != null}" title="Permit Detail">
				<%@ include file="../permits/header.jsp"%>
				
				<af:inputHidden value="#{permitDetail.popupRedirectOutcome}" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="permitDetailTop">
								<jsp:include page="permitDetailTop.jsp" />
							</f:subview>
						</f:facet>
						<%
							/* Top end */
						%>

						<f:facet name="left">
							<f:subview id="treeEU">
								<jsp:include page="treeEU.jsp" />
							</f:subview>
						</f:facet>
						<%
							/* Tree end */
						%>

						<%
							/* Content begin */
						%>
						<h:panelGrid columns="1" border="1"
							width="#{permitDetail.contentWidth}">

							<af:panelGroup>

								<%
									/* Webform begin */
								%>
								<af:switcher defaultFacet="permit"
									facetName="#{permitDetail.selectedTreeNode.type}">

									<%
										/* Permit content begin */
									%>
									<f:facet name="permit">
										<af:panelGroup>
											<af:panelHeader text="#{permitDetail.header}" size="0">
												<af:switcher facetName="#{permitDetail.permit.permitType}"
													defaultFacet="NSR">
													<f:facet name="NSR">
														<f:subview id="ptioPermit">
															<jsp:include page="ptioPermit.jsp" />
														</f:subview>
													</f:facet>
													<f:facet name="TVPTO">
														<f:subview id="tvPermit">
															<jsp:include page="tvPermit.jsp" />
														</f:subview>
													</f:facet>
												<%-- 
													<f:facet name="TIVPTO">
														<f:subview id="tivPermit">
															<jsp:include page="tivPermit.jsp" />
														</f:subview>
													</f:facet> 
												--%>
												</af:switcher>
												<%--
												<f:subview id="permitCommon"
													rendered="#{permitDetail.permit.permitType != 'REG'}"
													<jsp:include page="permitCommonPart.jsp" />
												</f:subview>
												--%>
												<f:subview id="permitCommon"
													rendered="#{permitDetail.permit.permitType == 'TVPTO' || permitDetail.permit.permitType == 'NSR'}">
													<jsp:include page="permitCommonPart.jsp" />
												</f:subview>
											</af:panelHeader>
										</af:panelGroup>
									</f:facet>
									<%
										/* Permit content end */
									%>

									<%
										/* EU group content begin */
									%>
									<f:facet name="euGroup">
										<af:panelGroup layout="vertical">
											<af:panelHeader text="#{permitDetail.groupHeader}" />
											<af:panelForm>
												<af:inputText label="EU group name :"
													readOnly="#{! permitDetail.editMode}" maximumLength="32"
													value="#{permitDetail.selectedEUGroup.name}"
													rendered="#{! permitDetail.selectedEUGroup.individualEUGroup}" />
											</af:panelForm>
											<afh:rowLayout halign="center">
												<af:panelForm>
													<af:selectManyShuttle id="currectEUs"
														onmouseup="submitForm();" leadingHeader="Available EUs"
														trailingHeader="Current EUs" size="15"
														value="#{permitDetail.currentEUs}"
														disabled="#{!permitDetail.editMode}">
														<f:selectItems value="#{permitDetail.excludedEUs}" />
													</af:selectManyShuttle>

													<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;"
														label="Click here to delete Group :" id="removeGroup"
														rendered="#{permitDetail.editMode && permitDetail.groupHeader == 'EU Group'}"
														value="#{permitDetail.removeGroup}" />
												</af:panelForm>
											</afh:rowLayout>
										</af:panelGroup>
									</f:facet>
									<%
										/* EU group content end */
									%>

									<%
										/* EU content begin */
									%>
									<f:facet name="eu">
										<af:switcher facetName="#{permitDetail.permit.permitType}"
											defaultFacet="NSR">
											<f:facet name="NSR">
												<f:subview id="ptioEU">
													<jsp:include page="ptioEU.jsp" />
												</f:subview>
											</f:facet>
											<f:facet name="TVPTO">
												<f:subview id="tvEU">
													<jsp:include page="tvEU.jsp" />
												</f:subview>
											</f:facet>
											<%--  <f:facet name="TIVPTO">
												<f:subview id="tivEU">
													<jsp:include page="tivEU.jsp" />
												</f:subview>
											</f:facet> --%>
										</af:switcher>
									</f:facet>
									<%
										/* EU content end */
									%>

									<%
										/* Excluded EU content begin */
									%>
									<f:facet name="excludedEU">
										<af:panelGroup layout="vertical">
											<af:panelHeader text="Emissions Unit" />
											<af:panelForm labelWidth="200">
												<af:inputText label="EU ID"
													value="#{permitDetail.selectedExcludedEU.epaEmuId}"
													readOnly="true" />
												<af:inputText label="EU description"
													value="#{permitDetail.selectedExcludedEU.euDesc}"
													readOnly="true" />
											</af:panelForm>
										</af:panelGroup>
									</f:facet>
									<%
										/* Excluded EU content end */
									%>
								</af:switcher>
								<%
									/* Webform end */
								%>

								<%-- moved to attachments <af:showDetailHeader text="Analysis Document" disclosed="true"
									rendered="#{permitDetail.selectedTreeNode.type == 'permit'}">
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
													rendered="#{permitDetail.permit.permitType == 'TVPTO' || permitDetail.permit.permitType == 'TIVPTO'}"
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
													rendered="#{permitDetail.permit.permitType == 'TVPTO' || permitDetail.permit.permitType == 'TIVPTO'}"
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
										<afh:rowLayout halign="center">
											<af:table value="#{permitDetail.tcs}" id="TCTab"
												width="#{permitDetail.contentTableWidth}"
												partialTriggers="TCTab:UploadDocButton TCTab:GenerateDocButton TCTab:EditDocButton TCTab:CloneDocButton"
												var="doc" emptyText=" " rows="5">
												<af:column headerText="Description"
													sortProperty="description" sortable="true"
													formatType="text">
													<af:goLink targetFrame="_blank" destination="#{doc.docURL}"
														text="#{doc.description}" />
												</af:column>
												<af:column headerText="Last modified by"
													sortProperty="lastModifiedBy" sortable="true" width="20%"
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
														value="#{doc.lastModifiedTS}" />
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
										</afh:rowLayout>
									</af:panelHeader>
								</af:showDetailHeader>--%>
								<%
									/* Terms and conditions end */
								%>
								<f:subview id="permit_attachments">
										<jsp:include page="permit_attachments.jsp" />
								</f:subview>

								<af:showDetailHeader text="Notes" disclosed="true"
									rendered="#{permitDetail.selectedTreeNode.type == 'permit'}">
									<afh:rowLayout halign="center">
										<af:table id="DAPCComments"
											width="#{permitDetail.contentTableWidth}" rows="5"
											emptyText=" " var="dc"
											partialTriggers="ThePage:DAPCComments:AddButton"
											value="#{permitDetail.permit.dapcComments}">

											<af:column headerText="Note ID" sortable="true" width="10%"
												sortProperty="noteId">
												<af:panelHorizontal valign="middle" halign="left">
													<af:commandLink text="#{dc.noteId}" id="viewNote"
														useWindow="true" windowWidth="650" windowHeight="300"
														action="#{permitDetail.startEditComment}">
														<t:updateActionListener
															property="#{permitDetail.modifyComment}" value="#{dc}" />
													</af:commandLink>
												</af:panelHorizontal>
											</af:column>

											<af:column headerText="Note" sortable="true" width="60%"
												sortProperty="noteTxt">
												<af:panelHorizontal valign="middle" halign="left">
													<af:outputText 
														truncateAt="90"	
														value="#{dc.noteTxt}" 
														shortDesc="#{dc.noteTxt}"/>
												</af:panelHorizontal>
											</af:column>

											<af:column sortProperty="userId" sortable="true" width="10%"
												headerText="User Name">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectOneChoice value="#{dc.userId}" readOnly="true">
														<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
													</af:selectOneChoice>
												</af:panelHorizontal>
											</af:column>

											<af:column sortProperty="dateEntered" sortable="true"
												width="10%" headerText="Date">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectInputDate readOnly="true"
														value="#{dc.dateEntered}" />
												</af:panelHorizontal>
											</af:column>

											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton text="Add Note" id="AddButton"
															useWindow="true" windowWidth="650" windowHeight="300"
															rendered="#{!permitDetail.readOnlyUser}"
															action="#{permitDetail.startAddComment}" />
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
									</afh:rowLayout>
								</af:showDetailHeader>
								<%
									/* AQD comments end */
								%>

								<af:objectSpacer height="10" />
								<f:subview id="permitDetailButtons"
									rendered="#{!permitDetail.readOnlyUser}">
									<jsp:include page="permitDetailButtons.jsp" />
								</f:subview>
								<%
									/* Buttons end */
								%>
							</af:panelGroup>
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
			<%-- hidden controls for navigation to compliance report from popup --%>
			<%@ include file="../util/hiddenControls.jsp"%>
		</af:form>
	</af:document>
</f:view>

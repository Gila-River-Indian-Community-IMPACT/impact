<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>

	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Permit detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Permit Detail">
				<f:facet name="messages">
					<af:messages />
				</f:facet>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						disabled="#{foo.disabled}" type="#{foo.type}"
						rendered="#{foo.rendered}"
						onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }" />
				</f:facet>
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
										<af:switcher facetName="#{permitDetail.permit.permitType}"
											defaultFacet="NSR">
											<f:facet name="NSR">
												<f:subview id="ptioPermit">
													<jsp:include page="ptioPermit.jsp" />
												</f:subview>
											</f:facet>
											<%-- <f:facet name="TVPTI">
												<f:subview id="ptiPermit">
													<jsp:include page="ptioPermit.jsp" />
												</f:subview>
											</f:facet> --%>
											<f:facet name="TVPTO">
												<f:subview id="tvPermit">
													<jsp:include page="tvPermit.jsp" />
												</f:subview>
											</f:facet>
											<%--<f:facet name="REG">
												<f:subview id="regPermit">
													<jsp:include page="regPermit.jsp" />
												</f:subview>
											</f:facet> --%>
											
										</af:switcher>
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
													readOnly="#{! permitDetail.editMode}"
													value="#{permitDetail.selectedEUGroup.name}"
													rendered="#{! permitDetail.selectedEUGroup.individualEUGroup}" />
											</af:panelForm>
											<afh:rowLayout halign="center">
												<af:panelForm>
													<af:selectManyShuttle leadingHeader="Excluded EUs"
														trailingHeader="Current EUs" size="15"
														value="#{permitDetail.currentEUs}"
														disabled="#{!permitDetail.editMode}">
														<f:selectItems value="#{permitDetail.excludedEUs}" />
													</af:selectManyShuttle>
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
											<%--
											<f:facet name="TVPTI">
												<f:subview id="ptiEU">
													<jsp:include page="ptioEU.jsp" />
												</f:subview>
											</f:facet>
											 --%>
											<f:facet name="TVPTO">
												<f:subview id="tvEU">
													<jsp:include page="tvEU.jsp" />
												</f:subview>
											</f:facet>

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

								<af:showDetailHeader disclosed="true" text="AQD comments"
									rendered="#{permitDetail.selectedTreeNode.type == 'permit'}">
									<afh:rowLayout halign="center">
										<af:table id="DAPCComments"
											width="#{permitDetail.contentTableWidth}" rows="5"
											emptyText=" " var="dc"
											partialTriggers="ThePage:DAPCComments:AddButton"
											value="#{permitDetail.permit.dapcComments}">

											<af:column formatType="text" sortProperty="dateEntered"
												sortable="true" width="20%" headerText="Date">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectInputDate readOnly="true"
														value="#{dc.dateEntered}" />
												</af:panelHorizontal>
											</af:column>

											<af:column formatType="text" headerText="Text"
												sortable="true" width="60%" sortProperty="noteTxt">
												<af:panelHorizontal valign="middle" halign="left">
													<af:inputText readOnly="true" value="#{dc.noteTxt}" />
												</af:panelHorizontal>
											</af:column>

											<af:column formatType="text" sortProperty="userId"
												sortable="true" width="20%" headerText="Staff">
												<af:panelHorizontal valign="middle" halign="left">
													<af:selectOneChoice value="#{dc.userId}" readOnly="true">
														<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
													</af:selectOneChoice>
												</af:panelHorizontal>
											</af:column>

										</af:table>
									</afh:rowLayout>
								</af:showDetailHeader>
								<%
									/* AQD comments end */
								%>

								<af:showDetailHeader text="Terms and conditions documents"
									disclosed="true"
									rendered="#{permitDetail.selectedTreeNode.type == 'permit'}">
									<afh:rowLayout halign="center">
										<af:table value="#{permitDetail.tcs}" id="TCTab"
											width="#{permitDetail.contentTableWidth}"
											partialTriggers="TCTab:UploadDocButton TCTab:GenerateDocButton TCTab:EditDocButton TCTab:CloneDocButton"
											var="doc" emptyText=" " rows="5">
											<af:column headerText="Description" formatType="text">
												<af:goLink destination="#{doc.uRL}"
													text="#{doc.description}" />
											</af:column>
											<af:column headerText="Last modified by" formatType="text">
												<af:selectOneChoice readOnly="true"
													value="#{doc.lastModifiedBy}">
													<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
												</af:selectOneChoice>
											</af:column>
											<af:column headerText="Last modified date" formatType="text">
												<af:selectInputDate readOnly="true"
													value="#{doc.lastModifiedTS}" />
											</af:column>
											<af:column headerText="Issued as" formatType="text">
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

											<af:column headerText="Operation">
												<af:switcher
													facetName="#{permitDetail.editMode? 'edit': 'view'}">
													<f:facet name="edit">
														<af:panelButtonBar>
															<af:commandButton text="Edit" id="EditDocButton"
																useWindow="true" windowWidth="500" windowHeight="300"
																returnListener="#{permitDetail.docDialogDone}"
																action="#{permitDetail.startEditDoc}" />
															<af:commandButton text="Clone" id="CloneDocButton"
																useWindow="true" windowWidth="500" windowHeight="300"
																returnListener="#{permitDetail.docDialogDone}"
																action="#{permitDetail.startCloneTCDoc}" />
														</af:panelButtonBar>
													</f:facet>
													<f:facet name="view">
														<af:commandButton text="Detail" useWindow="true"
															windowWidth="500" windowHeight="300"
															returnListener="#{permitDetail.docDialogDone}"
															action="#{permitDetail.startViewDoc}" />
													</f:facet>
												</af:switcher>
											</af:column>

											<f:facet name="footer">
												<af:panelButtonBar>
													<af:commandButton text="Upload" id="UploadDocButton"
														useWindow="true" rendered="#{permitDetail.editMode}"
														windowWidth="500" windowHeight="300"
														returnListener="#{permitDetail.docDialogDone}"
														action="#{permitDetail.startUploadTCDoc}" />
													<af:commandButton text="Generate" id="GenerateDocButton"
														useWindow="true" rendered="#{permitDetail.editMode}"
														windowWidth="500" windowHeight="300"
														returnListener="#{permitDetail.docDialogDone}"
														action="#{permitDetail.startGenerateTCDoc}" />
												</af:panelButtonBar>
											</f:facet>
										</af:table>
									</afh:rowLayout>
								</af:showDetailHeader>
								<%
									/* Terms and conditions end */
								%>

								<af:showDetailHeader text="Attachments" disclosed="true"
									rendered="#{permitDetail.selectedTreeNode.type == 'permit'}">
									<afh:rowLayout halign="center">
										<af:table value="#{permitDetail.attachments}" id="AttachTab"
											width="#{permitDetail.contentTableWidth}"
											partialTriggers="AttachTab:UploadDocButton AttachTab:EditDocButton"
											var="doc" emptyText=" ">
											<af:column headerText="Description" formatType="text">
												<af:goLink destination="#{doc.uRL}"
													text="#{doc.description}" />
											</af:column>
											<af:column headerText="Last modified by" formatType="text">
												<af:selectOneChoice readOnly="true"
													value="#{doc.lastModifiedBy}">
													<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
												</af:selectOneChoice>
											</af:column>
											<af:column headerText="Last modified date" formatType="text">
												<af:selectInputDate readOnly="true"
													value="#{doc.lastModifiedTS}" />
											</af:column>
											<af:column headerText="Operation">
												<af:switcher
													facetName="#{permitDetail.editMode? 'edit': 'view'}">
													<f:facet name="edit">
														<af:commandButton text="Edit" id="EditDocButton"
															useWindow="true" windowWidth="500" windowHeight="300"
															returnListener="#{permitDetail.docDialogDone}"
															action="#{permitDetail.startEditDoc}" />
													</f:facet>
													<f:facet name="view">
														<af:commandButton text="Detail" useWindow="true"
															windowWidth="500" windowHeight="300"
															returnListener="#{permitDetail.docDialogDone}"
															action="#{permitDetail.startViewDoc}" />
													</f:facet>
												</af:switcher>
											</af:column>

											<f:facet name="footer">
												<af:panelButtonBar>
													<af:commandButton text="Upload" id="UploadDocButton"
														useWindow="true" rendered="#{permitDetail.editMode}"
														windowWidth="500" windowHeight="300"
														returnListener="#{permitDetail.docDialogDone}"
														action="#{permitDetail.startUploadAttachDoc}" />
												</af:panelButtonBar>
											</f:facet>
										</af:table>
									</afh:rowLayout>
								</af:showDetailHeader>
								<%
									/* Attachments end */
								%>

								<af:showDetailHeader text="Carbon Copy list" disclosed="true"
									rendered="#{permitDetail.selectedTreeNode.type == 'permit'}">
									<afh:rowLayout halign="center">
										<af:table value="#{permitDetail.permit.contacts}"
											id="contactsTab" width="#{permitDetail.contentTableWidth}"
											var="contact" partialTriggers="addCC" emptyText=" ">
											<f:facet name="selection">
												<af:tableSelectMany rendered="#{permitDetail.editMode}" />
											</f:facet>
											<af:column headerText="Name" width="15%" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:inputText readOnly="true"
														value="#{contact.firstNm} #{contact.lastNm}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Address 1" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:inputText readOnly="true"
														value="#{contact.address.addressLine1}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="Address 2" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:inputText readOnly="true"
														value="#{contact.address.addressLine2}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="City" width="10%" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:inputText readOnly="true"
														value="#{contact.address.cityName}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="State" width="5%" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:inputText readOnly="true"
														value="#{contact.address.state}" />
												</af:panelHorizontal>
											</af:column>
											<af:column headerText="ZIP" width="5%" formatType="text">
												<af:panelHorizontal valign="middle" halign="left">
													<af:inputText readOnly="true"
														value="#{contact.address.zipCode5}" />
												</af:panelHorizontal>
											</af:column>

											<f:facet name="footer">
												<af:panelGroup layout="horizontal"
													rendered="#{permitDetail.editMode}">
													<af:selectOneChoice readOnly="#{! permitDetail.editMode}"
														value="#{permitDetail.cc2Add}">
														<mu:selectItems value="#{permitDetail.facilityContacts}" />
													</af:selectOneChoice>
													<af:commandButton text="Add" id="addCC"
														action="#{permitDetail.addCC}" />
													<af:commandButton text="Delete selected items"
														actionListener="#{permitDetail.deleteCC}" />
												</af:panelGroup>
											</f:facet>
										</af:table>
									</afh:rowLayout>
								</af:showDetailHeader>
								<%
									/* Carbon Copy list end */
								%>

								<af:objectSpacer height="10" />

								<%
									/* Buttons begin */
								%>
								<f:subview id="permitDetailButtons">
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
		</af:form>
	</af:document>
</f:view>

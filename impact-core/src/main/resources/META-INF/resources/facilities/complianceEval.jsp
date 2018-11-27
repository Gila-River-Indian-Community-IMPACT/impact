<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Inspections">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form id="fceForm">
			<af:page var="foo" value="#{menuModel.model}"
				title="Inspections">
				<af:inputHidden value="#{stackTests.popupRedirect}" />
				<jsp:include page="header.jsp" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="comEvalHeader" rendered="#{facilityProfile.facility != null}">
								<jsp:include page="comFacilityHeader4.jsp" />
							</f:subview>
						</f:facet>

						<f:facet name="right">
							<h:panelGrid columns="1" border="1"
								style="margin-left:auto;margin-right:auto;">
								<af:panelGroup layout="vertical">

									<af:objectSpacer height="10" />
									<af:panelForm>
										<af:table id="fceTable" emptyText=" "
											value="#{fceSiteVisits.fceList}" var="fce"
											bandingInterval="1" banding="row" width="98%">
											<af:column sortable="true" sortProperty="id"
												formatType="text" headerText="Inspection ID" width="95px" noWrap="true">
												<af:commandLink rendered="true" text="#{fce.inspId}"
													action="#{fceDetail.submit}">
													<af:setActionListener from="#{fce.id}"
														to="#{fceDetail.fceId}" />
													<af:setActionListener from="#{fce.facilityId}"
														to="#{fceDetail.facilityId}" />
													<af:setActionListener from="#{true}"
														to="#{fceDetail.existingFceObject}" />
													<af:setActionListener from="#{fce.id}"
														to="#{fceSiteVisits.fceId}" />
												</af:commandLink>
											</af:column>
											<af:column
												sortable="true" sortProperty="facilityAfsNumber"
												formatType="text" headerText="AFS ID" width="50px">
												<af:inputText readOnly="true"
													value="#{fce.facilityAfsNumber}" />
											</af:column>
											<jsp:include flush="true" page="../ceta/fceList.jsp" />
											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton text="New Inspection"
															action="#{fceSiteVisits.newFCE}"
															disabled="#{!fceSiteVisits.cetaUpdate}">
															<t:updateActionListener property="#{fceDetail.facility}"
																value="#{null}" />
															<t:updateActionListener property="#{fceDetail.fromTODOList}"
																value="false" />	
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
								</af:panelGroup>
							</h:panelGrid>
						</f:facet>
					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>

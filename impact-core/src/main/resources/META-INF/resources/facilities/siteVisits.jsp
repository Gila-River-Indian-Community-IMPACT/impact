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
		title="Site Visits">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form id="fceForm">
			<af:page var="foo" value="#{menuModel.model}"
				title="Site Visits">
				<af:inputHidden value="#{stackTests.popupRedirect}" />
				<jsp:include page="header.jsp" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="comEvalHeader"
								rendered="#{facilityProfile.facility != null}">
								<jsp:include page="comFacilityHeader4.jsp" />
							</f:subview>
						</f:facet>

						<f:facet name="right">
							<h:panelGrid columns="1" border="1"
								style="margin-left:auto;margin-right:auto;">
								<af:panelGroup layout="vertical">

									<af:objectSpacer height="10" />
								<af:panelForm>
										<af:table id="svTable" emptyText=" "
											value="#{fceSiteVisits.allSiteVisits}" var="sv"
											bandingInterval="1" banding="row" width="98%">
										<%-- 	<f:facet name="header">
												<afh:rowLayout halign="center">
													<af:selectInputDate id="from" label="From :"
														value="#{fceSiteVisits.visitsFrom}" autoSubmit="true" />
													<af:objectSpacer width="10" height="5" />
													<af:selectInputDate id="to" label="To :"
														value="#{fceSiteVisits.visitsTo}" autoSubmit="true" />
													<af:objectSpacer width="10" height="5" />
													<af:panelButtonBar>
														<af:commandButton action="#{fceSiteVisits.allVisits}"
															text="All Visits" />
													</af:panelButtonBar>
												</afh:rowLayout>
											</f:facet> --%>
											<af:column sortable="true" sortProperty="siteId"
												formatType="text" headerText="Site Visit ID" width="85px" noWrap="true">
													<af:commandLink text="#{sv.siteId}" rendered="#{!sv.stackTest}"
														action="#{siteVisitDetail.submitVisit}">
														<t:updateActionListener value="#{sv.id}"
															property="#{siteVisitDetail.visitId}" />
													</af:commandLink>
													<%-- <af:objectSpacer width="3" height="3" />
													<af:objectImage source="/images/Lock_icon1.png"
														rendered="#{sv.afsId!=null}" /> --%>
												<af:commandLink text="#{sv.siteId}"
													action="#{siteVisitDetail.submitStackTestVisitType}"
													rendered="#{sv.stackTest}">
													<t:updateActionListener value="#{sv.facilityId}"
														property="#{siteVisitDetail.facilityIdForStackTestVisitType}" />
													<t:updateActionListener value="#{sv.visitDate}"
														property="#{siteVisitDetail.visitDate}" />
													<t:updateActionListener value="#{sv}"
														property="#{siteVisitDetail.siteVisit}" />
													<t:updateActionListener value="#{sv.id}"
														property="#{siteVisitDetail.visitId}" />
												</af:commandLink>
											</af:column>
									<%-- 		<af:column sortProperty="fpId" sortable="true"
												headerText="FP ID" formatType="text" noWrap="true">
												<af:commandLink action="#{facilityProfile.submitProfile}"
													rendered="#{sv.versionId == -1}">
													<af:outputText value="Current (#{sv.fpId})" />
													<t:updateActionListener property="#{facilityProfile.fpId}"
														value="#{sv.fpId}" />
													<t:updateActionListener
														property="#{menuItem_facProfile.disabled}" value="false" />
												</af:commandLink>
												<af:commandLink action="#{facilityProfile.submitProfile}"
													rendered="#{sv.versionId != -1}">
													<af:outputText value="#{sv.fpId}" />
													<t:updateActionListener property="#{facilityProfile.fpId}"
														value="#{sv.fpId}" />
													<t:updateActionListener
														property="#{menuItem_facProfile.disabled}" value="false" />
												</af:commandLink>
											</af:column> --%>
											<%@ include file="../ceta/visitColumns.jsp"%>
											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton useWindow="true" windowWidth="1000"
															windowHeight="600" text="New Site Visit"
															action="#{fceSiteVisits.newVisit}"
															disabled="#{!fceSiteVisits.cetaUpdate}"
															rendered="#{!fceDetail.editable}" />
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

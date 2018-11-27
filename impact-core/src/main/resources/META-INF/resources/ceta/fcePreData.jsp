<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Pre-inspection Data">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="Inspection Detail" id="eval">
				<%@ include file="../util/branding.jsp"%>
				<af:inputHidden value="#{stackTests.popupRedirect}" />
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"
						onclick="if (#{fceDetail.startDateEditMode}) { alert('Please save or discard your changes'); return false; }" />
				</f:facet>
				<afh:rowLayout halign="center" width="1500"
					rendered="#{!fceDetail.blankOutPage}">
					<h:panelGrid border="1" id="fcePreDataPanelGrid">
						<af:panelBorder id="fcePreDataPanelBorder">
							<f:facet name="top">
								<f:subview id="fceHeader">
									<jsp:include page="fceHeader.jsp" />
								</f:subview>
							</f:facet>
							
							<af:objectSpacer height="10" />	
								
							<afh:rowLayout halign="center">
								<af:outputFormatted rendered="#{fceDetail.inspectionReadOnly}" inlineStyle="color: orange; font-weight: bold;" value="#{fceDetail.inspectionReadOnlyStatement}"/>		
							</afh:rowLayout>
							
							<af:objectSpacer height="10" />	
							
							<afh:rowLayout halign="left">
								<af:panelForm rows="1" maxColumns="1">
									<af:outputText rendered="#{fceDetail.fce.lastInspId == null}"
										value="No prior Inspection found." />
									<af:inputText label="Last Inspection ID:" readOnly="true"
										rendered="#{fceDetail.fce.lastInspId != null}"
										value="#{fceDetail.fce.lastInspId}" />
									<af:selectInputDate label="Last Inspection Date:"
										readOnly="true" rendered="#{fceDetail.fce.lastInspId != null}"
										value="#{fceDetail.fce.lastInspDate}" />
									<af:objectSpacer height="10" />

									<af:panelForm rows="2" maxColumns="1">
										<af:selectInputDate id="startDate" label="Start Date:"
											readOnly="#{!fceDetail.startDateEditMode}"
											value="#{fceDetail.fce.referenceReviewStartDate}">
											<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
										</af:selectInputDate>
									</af:panelForm>
									
									<af:panelForm rows="1" maxColumns="1">
										<af:panelButtonBar rendered="#{!fceDetail.startDateEditMode}">
											<af:commandButton id="setStartDate1"
												text="Set Start Date to Last Inspection Date"
												disabled="#{fceDetail.fce.lastInspId == null || fceDetail.inspectionReadOnly}"
												action="#{fceDetail.resetStartDateToLastInspDate}">
												<t:updateActionListener
													property="#{fceDetail.fce.referenceReviewStartDate}"
													value="#{fceDetail.fce.lastInspDate}" />
											</af:commandButton>
											<af:commandButton id="setStartDate2" 
												text="Set Start Date"
												disabled="#{fceDetail.inspectionReadOnly}"
												action="#{fceDetail.resetStartDateByUser}" >
											</af:commandButton>
										</af:panelButtonBar>

										<af:panelButtonBar rendered="#{fceDetail.startDateEditMode}">
											<af:commandButton id="saveStartDate" text="Save Start Date"
												action="#{fceDetail.saveStartDate}">
											</af:commandButton>
											<af:commandButton id="cancelStartDate" immediate="true"
												text="Cancel Start Date"
												action="#{fceDetail.cancelSetStartDateByUser}">
											</af:commandButton>
										</af:panelButtonBar>
									</af:panelForm>
								</af:panelForm>


							</afh:rowLayout>



							<f:subview id="artifactApplication">
								<jsp:include flush="true" page="fcePreDataApplication.jsp" />
							</f:subview>

							<af:objectSpacer height="10" />

							<f:subview id="artifactPermit">
								<jsp:include flush="true" page="fcePreDataPermit.jsp" />
							</f:subview>

							<af:objectSpacer height="10" />

							<f:subview id="artifactStackTest">
								<jsp:include flush="true" page="fcePreDataStackTest.jsp" />
							</f:subview>

							<af:objectSpacer height="10" />
							
							<f:subview id="artifactComplianceReport">
								<jsp:include flush="true" page="fcePreDataComplianceReport.jsp" />
							</f:subview>

							<f:subview id="artifactCorrespondence">
								<jsp:include flush="true" page="fcePreDataCorrespondence.jsp" />
							</f:subview>

							<f:subview id="artifactEmissionsInventory">
								<jsp:include flush="true" page="fcePreDataEmissionsInventory.jsp" />
							</f:subview>
														
							<af:objectSpacer height="10" />
							<f:subview id="artifactContinuousMonitors">
								<jsp:include flush="true" page="fcePreDataContinuousMonitors.jsp" />
							</f:subview>

							<af:objectSpacer height="10" />
							<f:subview id="artifactAmbientMonitors">
								<jsp:include flush="true" page="fcePreDataAmbientMonitors.jsp" />
							</f:subview>

							<af:objectSpacer height="10" />
							<f:subview id="artifactSiteVisits">
								<jsp:include flush="true" page="fcePreDataSiteVisits.jsp" />
							</f:subview>
							
							
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
			<%-- hidden controls for navigation to compliance report from popup --%>
			<%@ include file="../util/hiddenControls.jsp"%>
		</af:form>

	</af:document>
</f:view>

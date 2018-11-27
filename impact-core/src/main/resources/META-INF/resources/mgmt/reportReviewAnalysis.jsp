<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="FER Review Metrics" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" id="ReportReviewMetrics">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{reportReviewAnalysis.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="FER Review Metrics">
				<%@ include file="../util/header.jsp"%>

				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="left">
						<af:panelHorizontal>
							<af:outputFormatted
								value="<b>Data source:</b> emissions report detail<br><br>" />
							<af:objectSpacer width="10" height="5" />
							<af:commandButton text="Show Explanation"
								rendered="#{!complianceSearch.showExplain}"
								action="#{complianceSearch.turnOn}">
							</af:commandButton>
							<af:commandButton text="Hide Explanation"
								rendered="#{complianceSearch.showExplain}"
								action="#{complianceSearch.turnOff}">
							</af:commandButton>
						</af:panelHorizontal>
					</afh:rowLayout>
					<afh:rowLayout rendered="#{complianceSearch.showExplain}"
						halign="left">
						<af:outputFormatted
							value="This report was designed to track the timely review of annual emissions reports.   You are given the option to include only those that are not reviewed within the 45 day or 90 day standard or to also include those reports that were reviewed in a timely fashion.  Note: This report does not include the workflow data and therefore does not remove any days the review of the report was referred.<br><br>" />
					</afh:rowLayout>
				</afh:tableLayout>

				<afh:rowLayout halign="center" partialTriggers="dolaa">
					<h:panelGrid border="1" width="1000" >
						<af:panelBorder>
							<af:showDetailHeader text="Report Review Filter" disclosed="true">
								<af:panelForm rows="1" width="900" maxColumns="3">
									<af:selectOneChoice id="dolaa" label="District" unselectedLabel=" "
										autoSubmit="true" value="#{reportReviewAnalysis.doLaaCd}">
												<f:selectItems value="#{reportSearch.doLaas}" />
											</af:selectOneChoice>
									<af:selectOneChoice label="Reporting Category" id="category"
												unselectedLabel=" "
												value="#{reportReviewAnalysis.emissionsReportingCategory}">
												<f:selectItems
													value="#{facilityReference.emissionReportsRealDefs.items[(empty reportSearch.emissionsReportingCategory ? '' : reportSearch.emissionsReportingCategory)]}" />
											</af:selectOneChoice>
									<af:panelGroup layout="vertical">
										<af:outputLabel value="Submitted Date" />
										<af:selectInputDate label="From"
											value="#{reportReviewAnalysis.fromDate}">
											<af:validateDateTimeRange minimum="1900-01-01" />
										</af:selectInputDate>
										<af:selectInputDate label="To"
											value="#{reportReviewAnalysis.toDate}">
											<af:validateDateTimeRange minimum="1900-01-01" />
										</af:selectInputDate>
									</af:panelGroup>
								</af:panelForm>
								<af:selectBooleanCheckbox label="Include timely reviewed reports"
										id="includeTimely"
										rendered="#{!empty reportReviewAnalysis.doLaaCd}"
                    					value="#{reportReviewAnalysis.timely}" />
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit"
											action="#{reportReviewAnalysis.submit}" />
										<af:commandButton text="Reset" action="#{reportReviewAnalysis.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout rendered="#{reportReviewAnalysis.hasSearchResults}">
					<jsp:include flush="true" page="reportReviewAnalysisData.jsp" />
				</afh:rowLayout>

			</af:page>
		</af:form>
	</af:document>
</f:view>

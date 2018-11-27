<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<afh:rowLayout halign="center">
	<af:panelGroup layout="vertical">
		<afh:rowLayout halign="center">
			<af:table value="#{reportReviewAnalysis.stats}" bandingInterval="1"
				banding="row" var="stat">
				<f:facet name="header">
					<af:panelGroup layout="vertical">
						<af:outputText value="For reports submitted in the interval #{reportReviewAnalysis.dateRange}" />
					</af:panelGroup>
				</f:facet>
				<af:column sortProperty="doLaa" sortable="true"
					headerText="District" formatType="icon">
					<af:outputText value="#{stat.doLaa}" />
				</af:column>
				<af:column sortProperty="rptCategory" sortable="true"
					headerText="Category" formatType="icon">
					<af:outputText value="#{stat.rptCategory}" />
				</af:column>
				<af:column sortProperty="totRpts" sortable="true"
					headerText="Total Submitted" formatType="icon">
					<af:outputText value="#{stat.totRpts}" />
				</af:column>
				<af:column sortProperty="completionDays" sortable="true"
					headerText="Performance Standard (days)" formatType="icon">
					<af:outputText value="#{stat.completionDays}" />
				</af:column>
				<af:column sortProperty="rptsCompleted" sortable="true"
					headerText="# Timely Reviewed" formatType="icon">
					<af:outputText value="#{stat.rptsCompleted}" />
				</af:column>
				<af:column sortProperty="metric" sortable="true"
					headerText="% Reviewed Timely" formatType="icon">
					<af:outputText value="#{stat.metric}" />
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</afh:rowLayout>
		<af:objectSpacer width="100%" height="15" />
		<afh:rowLayout halign="center">
		<af:outputFormatted rendered="#{empty reportReviewAnalysis.doLaaCd}"
					value="<br>To see the reports that contributed to the metrics, select a single District and click Submit again." />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:table value="#{reportReviewAnalysis.tvList}" bandingInterval="1"
				banding="row" var="reportResults"
				rendered="#{! empty reportReviewAnalysis.tvList}">
				<f:facet name="header">
					<af:panelGroup layout="vertical">
						<af:outputText value="#{reportReviewAnalysis.tvMetricInfo}" />
					</af:panelGroup>
				</f:facet>
				<jsp:include page="reportReviewAnalysisDataAttribs.jsp" />
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</afh:rowLayout>
		<afh:rowLayout halign="center"
			rendered="#{! empty reportReviewAnalysis.tvList}">
			<af:objectSpacer width="100%" height="15" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:objectSpacer width="100%" height="15"
				rendered="#{! empty reportReviewAnalysis.tvList}" />
			<af:table value="#{reportReviewAnalysis.smtvList}"
				bandingInterval="1" banding="row" var="reportResults"
				rendered="#{! empty reportReviewAnalysis.smtvList}">
				<f:facet name="header">
					<af:panelGroup layout="vertical">
						<af:outputText value="#{reportReviewAnalysis.smtvMetricInfo}" />
					</af:panelGroup>
				</f:facet>
				<jsp:include page="reportReviewAnalysisDataAttribs.jsp" />
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</afh:rowLayout>
		<afh:rowLayout halign="center"
			rendered="#{! empty reportReviewAnalysis.smtvList}">
			<af:objectSpacer width="100%" height="15" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:table value="#{reportReviewAnalysis.ntvList}" bandingInterval="1"
				banding="row" var="reportResults"
				rendered="#{! empty reportReviewAnalysis.ntvList}">
				<f:facet name="header">
					<af:panelGroup layout="vertical">
						<af:outputText value="#{reportReviewAnalysis.ntv50MetricInfo} *" />
						<af:outputText value="#{reportReviewAnalysis.ntv100MetricInfo} *" />
					</af:panelGroup>
				</f:facet>
				<jsp:include page="reportReviewAnalysisDataAttribs.jsp" />
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:panelGroup layout="horizontal"
				rendered="#{! empty reportReviewAnalysis.ntvList}">
				<af:outputText value="* Note:" inlineStyle="font-size:75%;" />
				<af:objectSpacer width="10" height="5" />
				<af:outputText value="###"
					inlineStyle="font-size:75%; background-color:#FF9900;" />
				<af:objectSpacer width="5" height="5" />
				<af:outputText value="marks those reports over"
					inlineStyle="font-size:75%;" />
				<af:objectSpacer width="5" height="5" />
				<af:outputText value="#{reportReviewAnalysis.ntv50DaysV}"
					inlineStyle="font-size:75%;" />
				<af:objectSpacer width="5" height="5" />
				<af:outputText value="days late and" inlineStyle="font-size:75%;" />
				<af:objectSpacer width="5" height="5" />
				<af:outputText value="###"
					inlineStyle="font-size:75%; background-color:#FF5050;" />
				<af:objectSpacer width="5" height="5" />
				<af:outputText value="marks both that and also over"
					inlineStyle="font-size:75%;" />
				<af:objectSpacer width="5" height="5" />
				<af:outputText value="#{reportReviewAnalysis.ntv100DaysV}"
					inlineStyle="font-size:75%;" />
				<af:objectSpacer width="5" height="5" />
				<af:outputText value="days late." inlineStyle="font-size:75%;" />
			</af:panelGroup>
		</afh:rowLayout>
	</af:panelGroup>
</afh:rowLayout>

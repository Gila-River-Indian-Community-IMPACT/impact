<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Ambient Monitor Report">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				id="homeMonitorReportDetail" title="Ambient Monitor Report">
				<f:facet name="messages">
				  <af:messages />
				</f:facet>
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<f:subview id="homeMonitorReportTop">
							<jsp:include page="homeMonitorReportTop.jsp" />
						</f:subview>
						<af:panelGroup layout="vertical">
							<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
								<af:selectOneRadio id="legacyReport"
									label="Is this a legacy report?: "
									rendered="#{homeMonitorReportDetail.internalApp}"
									readOnly="true"
									layout="horizontal" value="#{homeMonitorReportDetail.monitorReport.legacyReport}">
									<f:selectItem itemLabel="Yes" itemValue="true" />
									<f:selectItem itemLabel="No" itemValue="false" />
								</af:selectOneRadio>
							</af:panelForm>
							
							<af:panelForm rows="3" maxColumns="1" labelWidth="220px" width="98%"
								partialTriggers="reportType">
								<af:selectOneChoice id="reportType"
									label="Report Type: "
									readOnly="true"
									unselectedLabel="" autoSubmit="true"
									value="#{homeMonitorReportDetail.monitorReport.reportTypeCd}">
									<mu:selectItems value="#{homeMonitorReportDetail.monitorReportTypeDef}" />
								</af:selectOneChoice>
								<af:selectOneChoice id="year"
									label="Reporting Year: "
									readOnly="true"
									unselectedLabel=""
									value="#{homeMonitorReportDetail.monitorReport.year}">
									<f:selectItems value="#{infraDefs.years}" />
								</af:selectOneChoice>
								<af:selectOneChoice id="quarter"
									label="Quarter: "
									readOnly="true"
									rendered="#{!homeMonitorReportDetail.monitorReport.annualReportType}"
									unselectedLabel=""
									value="#{homeMonitorReportDetail.monitorReport.quarter}">
									<f:selectItem itemLabel="First" itemValue="1"/>
									<f:selectItem itemLabel="Second" itemValue="2"/>
									<f:selectItem itemLabel="Third" itemValue="3"/>
									<f:selectItem itemLabel="Fourth" itemValue="4"/>
								</af:selectOneChoice>
							</af:panelForm>
							
							<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
								<af:inputText id="description"
									label="Description: "
									readOnly="true"
									value="#{homeMonitorReportDetail.monitorReport.description}"
									columns="100" rows= "5"
									maximumLength="500" />
							</af:panelForm>
							
							<af:showDetailHeader text="Report Attachments" disclosed="true" 
								id="homeReportAttachments" rendered="true">
								<jsp:include flush="true" page="homeReportAttachments.jsp" />
							</af:showDetailHeader>
							
							<af:objectSpacer height="10"/>

							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Close"
										rendered="#{!homeMonitorReportDetail.editable}"
										action="#{homeMonitorReportDetail.close}" />
								</af:panelButtonBar>
							</afh:rowLayout>
							
						</af:panelGroup>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>


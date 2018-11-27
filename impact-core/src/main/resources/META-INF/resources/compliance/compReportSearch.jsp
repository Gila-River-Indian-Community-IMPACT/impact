<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="#{complianceReportSearch.singleFacility ? 'Compliance Reports' : 'Compliance Report Search'}">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/FacilityType-Option.js"%></f:verbatim>
		<af:form>
			<mu:setProperty property="#{complianceReportSearch.singleFacility}"
				value="false" />
			<af:inputHidden value="#{complianceReportSearch.popupRedirect}" />
			<af:page var="foo" value="#{menuModel.model}"
				title="#{!complianceReport.internalApp?'Compliance Report(s)':'Compliance Report Search'}">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">

					<h:panelGrid border="1" rendered="#{!complianceReport.internalApp}">
						<af:panelBorder>
							<f:facet name="top">
								<f:subview id="facilityHeader">
									<jsp:include page="/facilities/comFacilityHeader2.jsp" />
								</f:subview>
							</f:facet>
						</af:panelBorder>
					</h:panelGrid>

					<h:panelGrid border="1" rendered="#{complianceReport.internalApp}">

						<af:panelBorder>
							<af:showDetailHeader text="Compliance Report Search Criteria"
								disclosed="true">
								<af:panelForm partialTriggers="compReportSearch_ReportType compReportSearch_ByDate" rows="5"
									maxColumns="3">

									<af:inputText columns="10" maximumLength="10"
										label="Facility ID" tip="0000000000, 0%, %0%, *0*, 0*"
										value="#{complianceReportSearch.facilityId}"
										id="compReportSearch_FacilityID" />

									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
										label="Facility Name"
										value="#{complianceReportSearch.facilityName}" valign="top"
										id="compReportSearch_FacilityName" />

									<af:selectOneChoice label="District" rendered="#{infraDefs.districtVisible}"
										value="#{complianceReportSearch.doLaaCd}" unselectedLabel=""
										id="compReportSearch_District">
										<f:selectItems value="#{facilitySearch.doLaas}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="compReportSearch_DistHidden" rendered="#{!infraDefs.districtVisible}"/>
									
									<af:selectOneChoice label="Facility Class"
										value="#{complianceReportSearch.permitClassCd}"
										unselectedLabel="" inlineStyle="width: 125px;"
										id="compReportSearch_FacilityClass">
										<f:selectItems
											value="#{facilityReference.permitClassDefs.items[0]}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Facility Type"
										value="#{complianceReportSearch.facilityTypeCd}"
										unselectedLabel="" styleClass="FacilityTypeClass x6"
										id="compReportSearch_FacilityType">
										<f:selectItems
											value="#{facilityReference.facilityTypeDefs.items[0]}" />
									</af:selectOneChoice>

									<af:inputText label="Report ID" columns="10"
										tip="CRPT000001, CRPT%, %01% *01*, CRPT*"
										value="#{complianceReportSearch.reportCRPTId}"
										id="compReportSearch_ReportID" />

									<af:selectOneChoice id="compReportSearch_ReportType" label="Report Type"
										unselectedLabel=" " autoSubmit="true"
										value="#{complianceReportSearch.reportType}">
										<f:selectItems
											value="#{complianceReport.complianceReportTypesDef.items[(empty complianceReportSearch.reportType ? '' : complianceReportSearch.reportType)]}" />
									</af:selectOneChoice>

									<af:outputText value=" "
										rendered="#{complianceReportSearch.reportType==null}" />

									<af:selectOneChoice id="compReportSearch_CemsCategory" label="Category"
										unselectedLabel=""
										rendered="#{complianceReportSearch.reportType=='cems'}"
										value="#{complianceReportSearch.otherCategoryCd}">
										<f:selectItems
											value="#{complianceReportSearch.complianceCemsTypeDef.allSearchItems}" />
									</af:selectOneChoice>

									<af:selectOneChoice id="compReportSearch_OneCategory" label="Category"
										unselectedLabel=""
										rendered="#{complianceReportSearch.reportType=='one'}"
										value="#{complianceReportSearch.otherCategoryCd}">
										<f:selectItems
											value="#{complianceReportSearch.complianceOneTypeDef.allSearchItems}" />
									</af:selectOneChoice>

									<af:selectOneChoice id="compReportSearch_OtherCategory" label="Category"
										unselectedLabel=""
										rendered="#{complianceReportSearch.reportType=='othr'}"
										value="#{complianceReportSearch.otherCategoryCd}">
										<f:selectItems
											value="#{complianceReportSearch.complianceOtherTypeDef.allSearchItems}" />
									</af:selectOneChoice>

									<af:selectOneChoice id="compSearchReport_Status" label="Status"
										unselectedLabel=" "
										value="#{complianceReportSearch.reportStatus}">
										<f:selectItems
											value="#{complianceReportSearch.statusDef.items[(empty complianceReportSearch.reportStatus ? '' : complianceReportSearch.reportStatus)]}" />
									</af:selectOneChoice>

									<af:inputText label="Comments" columns="25"
										value="#{complianceReportSearch.dapcReviewComments}"
										tip="acme%, %acme% *acme*, acme*"
										id="compSearchReport_Comments" />

									<af:selectOneChoice label="Company"
										value="#{complianceReportSearch.cmpId}" unselectedLabel=""
										id="compSearchReport_Company" >
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>

									<af:selectOneChoice id="compReportSearch_Accepted" label="Accepted"
										unselectedLabel=" "
										value="#{complianceReportSearch.reportAccepted}">
										<f:selectItems
											value="#{complianceReportSearch.acceptedDef.items[(empty complianceReportSearch.reportAccepted ? '' : complianceReportSearch.reportAccepted)]}" />
									</af:selectOneChoice>

									<af:selectOneChoice id="compReportSearch_ByDate" label="By Date"
										unselectedLabel=" " autoSubmit="true"
										value="#{complianceReportSearch.dateBy}">
										<f:selectItems
											value="#{complianceReportSearch.dateFieldsDef.items[(empty complianceReportSearch.dateBy ? '' : complianceReportSearch.dateBy)]}" />
									</af:selectOneChoice>

									<af:selectInputDate label="From Date"
										rendered="#{complianceReportSearch.dateBy!=null}"
										value="#{complianceReportSearch.dtBegin}"
										id="compReportSearch_FromDate" >
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>

									<af:selectInputDate label="To Date"
										rendered="#{complianceReportSearch.dateBy!=null}"
										value="#{complianceReportSearch.dtEnd}"
										id="compReportSearch_ToDate" >
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>

								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{complianceReportSearch.submitSearch}"
											id="compReportSearch_SubmitBtn" >
										</af:commandButton>

										<af:commandButton text="Reset"
											action="#{complianceReportSearch.reset}"
											id="compReportSearch_ResetBtn" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout
					rendered="#{complianceReportSearch.hasSearchResults || !complianceReport.internalApp || complianceReportSearch.singleFacility}"
					width="100%" halign="center">
					<h:panelGrid width="100%" border="1" rendered="true">
						<af:panelBorder>
							<af:showDetailHeader text="Compliance Reports" disclosed="true">
								<jsp:include flush="true" page="compReportSearchResults.jsp" />
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>


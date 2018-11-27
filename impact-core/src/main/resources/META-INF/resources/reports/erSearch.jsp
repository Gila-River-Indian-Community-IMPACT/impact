<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Emissions Inventory Search">
		    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
			<af:inputHidden value="#{reportSearch.popupRedirect}" />
			<af:page var="foo" value="#{menuModel.model}"
				title="Emissions Inventory Search">
				<%@ include file="../util/header.jsp"%>

				<jsp:useBean id="reportSearch" scope="session"
					class="us.oh.state.epa.stars2.webcommon.reports.ReportSearch" />
				<jsp:setProperty name="reportSearch" property="fromFacility"
					value="false" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true"
								partialTriggers="eiSearch_Year eiSearch_Category">
								<afh:rowLayout>
									<afh:cellFormat halign="left" width="33%">
										<af:panelForm maxColumns="1" labelWidth="40%">

											<af:inputText tip="0000000000, 0%, %0%, *0*, 0*" columns="10" maximumLength="10"
												label="Facility ID" value="#{reportSearch.facilityId}"
												id="eiSearch_FacilityID" />

											<af:inputText tip="acme%, %acme%, *acme*, acme*" columns="25"
												label="Facility Name" value="#{reportSearch.facilityName}"
												valign="top"
												id="eiSearch_FacilityName" />

											<af:selectOneChoice label="District" unselectedLabel=" " rendered="#{infraDefs.districtVisible}"
												value="#{reportSearch.doLaaCd}"
												id="eiSearch_District" >
												<f:selectItems value="#{reportSearch.doLaas}" />
											</af:selectOneChoice>
											<%-- This is to make the page Layout unchanged with District not rendered --%>
											<af:inputHidden id="eiSearch_DistHidden" rendered="#{!infraDefs.districtVisible}"/>

											<af:inputText columns="13" label="Inventory ID" tip="EI0000001, EI%, %01% *01*, EI*"
												value="#{reportSearch.emissionsInventoryId}"
												id="eiSearch_InventoryID" />

											<af:selectOneChoice label="Company"
												value="#{reportSearch.cmpId}" unselectedLabel=""
												id="eiSearch_Company" >
												<f:selectItems value="#{companySearch.allCompanies}" />
											</af:selectOneChoice>

										</af:panelForm>
									</afh:cellFormat>
									<afh:cellFormat width="33%">
										<af:panelForm maxColumns="1" labelWidth="40%">
											<af:selectOneChoice label="Year" value="#{reportSearch.year}"
												id="eiSearch_Year" autoSubmit="true" unselectedLabel=" "
												inlineStyle="width:61px">
												<f:selectItems value="#{infraDefs.years}" />
											</af:selectOneChoice>

											<af:selectOneChoice label="Content Type" id="eiContent_type"
												autoSubmit="true" unselectedLabel=" "
												value="#{reportSearch.contentTypeCd}">
												<f:selectItems
													value="#{facilityReference.contentTypeDefs.allSearchItems}" />
											</af:selectOneChoice>

											<af:selectOneChoice label="Regulatory Requirement" id="eiRegulatory_requirement"
												autoSubmit="true" unselectedLabel=" "
												value="#{reportSearch.regulatoryRequirementCd}">
												<f:selectItems
													value="#{facilityReference.regulatoryRequirementDefs.allSearchItems}" />
											</af:selectOneChoice>

											<af:selectOneChoice label="Reporting State" unselectedLabel=" "
												value="#{reportSearch.reportingState}"
												id="eiSearch_ReportingState">
												<f:selectItems
													value="#{facilityReference.reportOfEmissionsStateDefs.allSearchItems}" />
											</af:selectOneChoice>
											<%--
											<af:selectOneChoice label="EIS State" unselectedLabel=" "
												value="#{reportSearch.eisStatusCd}">
												<f:selectItems
													value="#{facilityReference.reportEisStatusDefs.allSearchItems}" />
											</af:selectOneChoice>
											--%>

										</af:panelForm>
									</afh:cellFormat>
									<afh:cellFormat width="33%">
										<af:panelForm maxColumns="1" labelWidth="40%">
											<af:selectInputDate label="From Received Date"
												value="#{reportSearch.beginDt}"
												id="eiSearch_FromReceivedDate" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
											<af:selectInputDate label="To Received Date"
												value="#{reportSearch.endDt}"
												id="eiSearch_ToReceivedDate" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>	
										</af:panelForm>
									</afh:cellFormat>
								</afh:rowLayout>
								<af:objectSpacer height="30" />
										<afh:rowLayout halign="center">
											<af:outputLabel value="Range:" />
											<af:objectSpacer width="5" height="10" />
											<af:outputLabel value="["
												inlineStyle="font-size:larger; font-weight:bold;" />
											<af:objectSpacer width="2" height="10" />
											<af:inputText value="#{reportSearch.minEmissions}"
												tip="25, 25.3, 2.53E1"
												maximumLength="9" columns="9"
												id="eiSearch_RangeMin" >
												<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
												<f:validateDoubleRange minimum="0.0" maximum="1.0E38" />
											</af:inputText>
											<af:outputLabel value=","
												inlineStyle="font-size:larger; font-weight:bold;" />
											<af:objectSpacer width="3" height="10" />
											<af:inputText value="#{reportSearch.maxEmissions}"
												tip="95, 95.3, 9.53E1"
												maximumLength="9" columns="9"
												id="eiSearch_RangeMax" >
												<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
												<f:validateDoubleRange minimum="0.0" maximum="1.0E38" />
											</af:inputText>
											<af:objectSpacer width="2" height="10" />
											<af:outputLabel value=")"
												inlineStyle="font-size:larger; font-weight:bold;" />
											<af:objectSpacer width="3" height="10" />
											<af:selectOneChoice value="#{reportSearch.unit}"
											id="eiSearch_Unit" >
												<f:selectItems
													value="#{facilityReference.emissionUnitReportingDefs.allSearchItems}" />
											</af:selectOneChoice>
											<af:objectSpacer width="15" height="10" />
											<af:selectOneChoice label="Pollutant"
												tip="Pollutant dropdown list is populated based on the pollutants in submitted emissions inventories. When a pollutant is selected, search results include submitted inventories only."
												value="#{reportSearch.pollutantCd}"
												unselectedLabel=" "
												id="eiSearch_Pollutant" >
												<f:selectItems value="#{reportSearch.reportPollutants}" />
											</af:selectOneChoice>
										</afh:rowLayout>
								<af:objectSpacer height="30" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit"
											action="#{reportSearch.submitSearch}"
											id="eiSearch_SubmitBtn" />
										<af:commandButton text="Reset" action="#{reportSearch.reset}"
										id="eiSearch_ResetBtn" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" rendered="#{reportSearch.hasSearchResults}">
						<af:panelBorder>
							<af:showDetailHeader text="Emissions Inventory List"
								disclosed="true">
								<af:panelForm partialTriggers="standardList invoiceTrackingList">
									<af:panelHorizontal>
										<af:outputLabel value="List Type:"/>
										<af:selectBooleanRadio id="standardList" group="listType"
												autoSubmit="true" text="Standard"
												value="#{reportSearch.standardList}"/>
										<af:selectBooleanRadio id="invoiceTrackingList" group="listType"
												autoSubmit="true" text="Invoice Tracking"
												value="#{reportSearch.invoiceTrackingList}"/>
									</af:panelHorizontal>
									<af:switcher defaultFacet="StandardList"
										facetName="#{reportSearch.listType}">
											<f:facet name="StandardList">
												<f:subview id="idstandardlist">
													<jsp:include flush="true" page="erSearchTable.jsp" />
												</f:subview>	
											</f:facet>
									<f:facet name="InvoiceTrackingList">
										<f:subview id="idinvoicetrackinglist">
											<jsp:include flush="true" page="erSearchInvoiceTrackingTable.jsp" />
										</f:subview>
									</f:facet>
								</af:switcher>
							</af:panelForm>		
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Permit search">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Permit search">
				<%@ include file="../permits/header.jsp"%>

				<mu:setProperty value="false"
					property="#{permitSearch.fromFacility}" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000">
						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="1" maxColumns="3" partialTriggers="pt">
									<af:inputText columns="25" rows="1" label="Facility ID"
										value="#{permitSearch.facilityID}" maximumLength="10"
										tip="0000000000, 0%, %0%, *0*, 0*" />
									<af:inputText columns="25" rows="1" label="Facility Name"
										tip="acme%, %acme% *acme*, acme*"
										value="#{permitSearch.facilityName}" maximumLength="100"/>
									<af:inputText tip="A0000504, A%, %50%, *50*, A*"
										label="Application number" columns="10" rows="1"
										value="#{permitSearch.applicationNumber}" maximumLength="8" />
									<af:inputText label="EU ID" columns="10" rows="1"
										value="#{permitSearch.euId}" maximumLength="10"
										tip="APT001, APT00%, %T%, *0*, A*, *1"/>

									<af:selectOneChoice label="Company"
										value="#{permitSearch.cmpId}" unselectedLabel="">
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>

									<af:inputText columns="10" rows="1" label="Permit Number"
										value="#{permitSearch.permitNumber}" maximumLength="10"
										tip="P0000002, P000000%, *0*, P*, *2"/>
									<af:inputText columns="10" rows="1" label="Legacy Permit Number"
										value="#{permitSearch.legacyPermitNumber}" maximumLength="10"
										tip="0000002, 000000%, *0*, 0*, *2"/>
									<af:selectOneChoice label="Permit Type" id="pt" tip=""
										autoSubmit="TRUE" unselectedLabel=" "
										value="#{permitSearch.permitType}">
										<mu:selectItems value="#{permitSearch.permitTypes}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Permit Reason" unselectedLabel=" "
										value="#{permitSearch.permitReason}">
										<f:selectItems value="#{permitSearch.permitReasons}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Permit Publication Stage"
										unselectedLabel=" "
										value="#{permitSearch.permitGlobalStatusCd}" readOnly="false">
										<mu:selectItems value="#{permitSearch.permitGlobalStatusDefs}" />
									</af:selectOneChoice>
									<af:selectOneChoice id="permitLevelStatusCd" 
										label="Permit Status"
										unselectedLabel=" "
										value="#{permitSearch.permitLevelStatusCd}" readOnly="false">
										<f:selectItems value="#{permitReference.permitLevelStatusDefs.items[0]}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="EU Permit Status"
										unselectedLabel=" " value="#{permitSearch.permitEUStatusCd}"
										readOnly="false" rendered="false">
										<mu:selectItems value="#{permitSearch.permitStatusCds}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Invoice Fee Balance"
										value="#{permitSearch.permitFeeBalanceTypeCd}" readOnly="false">
										<f:selectItems value="#{permitSearch.permitFeeBalanceTypeDefs}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Date Field" id="df"
										autoSubmit="true" unselectedLabel=" "
										value="#{permitSearch.dateBy}" readOnly="false">
										<mu:selectItems value="#{permitSearch.permitDateBy}" />
									</af:selectOneChoice>
									<af:selectInputDate label="From"
										readOnly="#{permitSearch.dateBy == null}"
										value="#{permitSearch.beginDt}" partialTriggers="df"
										tip="Search will return results inclusive of this date.">
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>
									<af:selectInputDate label="To"
										readOnly="#{permitSearch.dateBy == null}"
										value="#{permitSearch.endDt}" partialTriggers="df"
										tip="Search will return results inclusive of this date.">
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>

								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit" id="submitPermitSearch"
											action="#{permitSearch.search}" />
										<af:commandButton text="Reset" action="#{permitSearch.reset}" id="resetPermitSearch" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1"
						rendered="#{permitSearch.hasSearchResults}">
						<af:showDetailHeader text="Permit List" disclosed="true">
							<af:panelForm partialTriggers="standardList nsrBillingList">
								<af:panelHorizontal>
									<af:outputLabel value="List Type:"/>
									<af:selectBooleanRadio id="standardList" group="pemitListType"
											autoSubmit="true" text="Standard"
											value="#{permitSearch.standardList}"/>
									<af:selectBooleanRadio id="nsrBillingList" group="pemitListType"
											autoSubmit="true" text="NSR Billing"
											value="#{permitSearch.NSRBillingList}"/>
								</af:panelHorizontal>
								<af:switcher defaultFacet="StandardList"
									facetName="#{permitSearch.permitListType}">
									<f:facet name="StandardList">
										<f:subview id="idstandardlist">
											<jsp:include flush="true" page="permitSearchTable.jsp" />
										</f:subview>	
									</f:facet>
									<f:facet name="NSRBillingList">
										<f:subview id="idnsrbillinglist">
											<jsp:include flush="true" page="permitSearchNsrBillingTable.jsp" />
										</f:subview>
									</f:facet>
								</af:switcher>
							</af:panelForm>		
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

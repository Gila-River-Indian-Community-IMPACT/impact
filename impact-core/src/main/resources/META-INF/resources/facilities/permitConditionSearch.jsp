<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Permit Conditions">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Permit Conditions">

				<jsp:include page="header.jsp" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="facilityHeader">
								<jsp:include page="comFacilityHeader2.jsp" />
							</f:subview>
						</f:facet>

						<h:panelGrid border="1"
							style="margin-left:auto;margin-right:auto;">
							<afh:rowLayout halign="center">
								<h:panelGrid border="1" width="1720">
									<af:panelBorder>
										<af:showDetailHeader text="Search Criteria" disclosed="true">
											<af:panelForm rows="1" maxColumns="5"
												partialTriggers="permitTypeCd dateBy">
												<af:inputText id="permitNumber" 
													label="Permit Number"  
													maximumLength="8" columns="10" rows="1"
													value="#{permitConditionSearch.searchCriteria.permitNumber}"
													tip="P0000002, P000000%, *0*, *2" />
												<af:inputText id="legacyPermitNumber" 
													label="Legacy Permit Number"
													maximumLength="32" columns="10" rows="1"
													value="#{permitConditionSearch.searchCriteria.legacyPermitNumber}"
													tip="0000002, 000000%, *0*, 0*, *2" />
												<af:inputText id="euId" 
													label="EU ID" 
													maximumLength="6" columns="10" rows="1"
													value="#{permitConditionSearch.searchCriteria.euId}"
													tip="APT001, APT00%, APT*, *1" />
													
												<af:selectOneChoice id="permitTypeCd" 
													label="Permit Type"
													autoSubmit="true"
													unselectedLabel=" "
													valueChangeListener="#{permitConditionSearch.permitTypeCdValueChanged}"
													value="#{permitConditionSearch.searchCriteria.permitTypeCd}" >
													<mu:selectItems value="#{permitSearch.permitTypes}" />
												</af:selectOneChoice>
												<af:selectOneChoice id="permitLevelStatusCd" 
													label="Permit Status"
													unselectedLabel=" "
													value="#{permitConditionSearch.searchCriteria.permitLevelStatusCd}" >
													<f:selectItems
														value="#{permitReference.permitLevelStatusDefs.items[0]}" />
												</af:selectOneChoice>
												<af:selectOneRadio id="isShowAllOrOnlyIssuedFinal"
													label="Permit Issuance Status" layout="vertical"
													value="#{permitConditionSearch.searchCriteria.showOnlyIssuedFinal}">
													<f:selectItem itemLabel="Show All" itemValue="false" />
													<f:selectItem itemLabel="Show Issued Final"
														itemValue="true" />
												</af:selectOneRadio>
												
												<af:inputText id="permitConditionNumber" 
													label="Condition Number"  
													maximumLength="11" columns="11" rows="1"
													value="#{permitConditionSearch.searchCriteria.permitConditionNumber}"
													tip="25.A, 15%, *14* 12*, *2" />
												<af:selectOneChoice id="permitConditionStatusCd" 
													label="Condition Status"
													unselectedLabel=" "
													value="#{permitConditionSearch.searchCriteria.permitConditionStatusCd}" >
													<f:selectItems value="#{permitReference.permitConditionStatusDefs.items[0]}" />
												</af:selectOneChoice>
												<af:selectOneChoice id="permitConditionCategoryCd" 
													label="Category"
													unselectedLabel=" "
													value="#{permitConditionSearch.searchCriteria.permitConditionCategoryCd}" >
													<f:selectItems value="#{permitReference.permitConditionCategoryDefs.items[0]}" />
												</af:selectOneChoice>
												
												<af:inputText id="conditionTextPlain" 
													label="Condition Text"
													maximumLength="1000" columns="30" rows="1"
													value="#{permitConditionSearch.searchCriteria.conditionTextPlain}" 
													tip="acme, *acme*, acme* "/>
												<af:outputText />
												<af:outputText />
													
												<af:selectOneChoice id="dateBy" 
													label="Date Field"
													unselectedLabel=" "
													autoSubmit="true"
													valueChangeListener="#{permitConditionSearch.dateByValueChanged}"
													value="#{permitConditionSearch.searchCriteria.dateBy}" >
													<mu:selectItems value="#{permitConditionSearch.permitConditionSearchDateBy}" />
												</af:selectOneChoice>
												<af:selectInputDate id="beginDt" 
													label="From"
													autoSubmit="true"
													disabled="#{empty permitConditionSearch.searchCriteria.dateBy}"
													value="#{permitConditionSearch.searchCriteria.beginDt}"
													tip="Search will return results inclusive of this date.">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>
												<af:selectInputDate id="endDt"
													label="To"
													autoSubmit="true"
													disabled="#{empty permitConditionSearch.searchCriteria.dateBy}"
													value="#{permitConditionSearch.searchCriteria.endDt}"
													tip="Search will return results inclusive of this date.">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>
											</af:panelForm>
												
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Submit"
														action="#{permitConditionSearch.submitSearch}" />
													<af:commandButton text="Reset"
														action="#{permitConditionSearch.reset}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:showDetailHeader>
									</af:panelBorder>
								</h:panelGrid>
							</afh:rowLayout>
						</h:panelGrid>
					</af:panelBorder>
				</h:panelGrid>
				
				<af:objectSpacer width="100%" height="15" />

				<jsp:include flush="true" page="permitConditionSearchTable.jsp" />
				
				<%-- hidden controls for navigation to compliance report from popup --%>
				<%@ include file="../util/hiddenControls.jsp"%>
			</af:page>
		</af:form>
	</af:document>
</f:view>

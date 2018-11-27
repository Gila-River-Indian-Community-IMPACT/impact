<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Enforcement Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" >
				<af:inputHidden value="#{enforcementSearch.popupRedirect}" />
				<%@ include file="../util/header.jsp"%>

				<mu:setProperty value="false"
					property="#{enforcementSearch.fromFacility}" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000">
						<af:panelBorder>
							<af:showDetailHeader text="Enforcement Action Search Criteria" disclosed="true">
								<af:panelForm rows="1" maxColumns="4">
									<af:inputText columns="20" label="Facility ID"
										value="#{enforcementSearch.facilityId}"
										maximumLength="10" tip="0000000000, 0%, %0%, *0*, 0*"
										id="eaSearch_FacilityID" />
									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="20"
										label="Facility Name"
										value="#{enforcementSearch.facilityName}" valign="top"
										id="eaSearch_FacilityName" />
									<af:selectOneChoice label="District" rendered="#{infraDefs.districtVisible}"
										value="#{enforcementSearch.doLaaCd}" unselectedLabel=""
										id="eaSearch_District" >
										<f:selectItems value="#{facilitySearch.doLaas}"/>
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="eaSearch_DistHidden" rendered="#{!infraDefs.districtVisible}"/>
									
				                    <af:selectOneChoice label="County"
				                      value="#{enforcementSearch.countyCd}"
				                      unselectedLabel=""
				                      id="eaSearch_County" >
				                      <f:selectItems value="#{infraDefs.counties}" />
				                    </af:selectOneChoice>
				                    <af:selectOneChoice label="Company"
										value="#{enforcementSearch.cmpId}" unselectedLabel=""
										inlineStyle="width:380px"
										id="eaSearch_Company" >
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>
									<af:inputText tip="ENF%, %0001% *0001*, ENF0001*" columns="25" 
										label="Enforcement Action ID"
										value="#{enforcementSearch.searchEnfId}"
										maximumLength="15"
										id="eaSearch_EnforcementActionID" />
									<af:inputText label="Docket Number" id="eaSearch_DocketNumber"
										value="#{enforcementSearch.docketNumber}" maximumLength="7" columns="10"
										tip="XXXX-XX, 0%, %0%, *0*, 0*" />
									<af:selectOneChoice label="Enforcement Action Type" tip=" "
                    					unselectedLabel=" " value="#{enforcementSearch.actionTypeCd}"
                    					id="eaSearch_EnforcementActionType" >
                    					<f:selectItems value="#{enforcementSearch.enforcementActionTypeDef.items[(empty enforcementSearch.actionTypeCd ? 0 : enforcementSearch.actionTypeCd)]}" />
                  					</af:selectOneChoice>
                  					<af:inputText label=" " id="dummy" readOnly="true"
										value=" " maximumLength="7" columns="10" />
									
									<af:selectOneChoice label="Date Field" id="eaSearch_DateField"
										autoSubmit="true" unselectedLabel=" "
										value="#{enforcementSearch.eventCd}" readOnly="false"
										inlineStyle="width: 340px">
										<mu:selectItems id="enforcementActionEvents"
										value="#{enforcementActionDetail.enforcementActionEventDefs}" />
									</af:selectOneChoice>
									<af:selectInputDate label="From"
										readOnly="#{enforcementSearch.eventCd == null}"
										value="#{enforcementSearch.beginActionDt}" partialTriggers="eaSearch_DateField"
										tip="Search will return results inclusive of this date."
										id="eaSearch_From">
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>
									<af:selectInputDate label="To"
										readOnly="#{enforcementSearch.eventCd == null}"
										value="#{enforcementSearch.endActionDt}" partialTriggers="eaSearch_DateField"
										tip="Search will return results inclusive of this date."
										id="eaSearch_To">
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>
								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit"
											action="#{enforcementSearch.search}"
											id="eaSearch_SubmitBtn">
										</af:commandButton>
										<af:commandButton text="Reset"
											action="#{enforcementSearch.reset}"
											id="eaSearch_ResetBtn" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1200"
						rendered="#{enforcementSearch.hasSearchResults}">
						<af:showDetailHeader text="Enforcement Action List" disclosed="true">
							<jsp:include flush="true" page="enforcementSearchTable.jsp" />
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

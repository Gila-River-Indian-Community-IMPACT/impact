<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document title="Application Search">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:inputHidden value="#{applicationSearch.popupRedirect}" />
			<af:page var="foo" value="#{menuModel.model}"
				title="Application Search">

				<%@ include file="../util/header.jsp"%>

				<mu:setProperty value="false"
         		 property="#{applicationSearch.fromFacility}" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm maxColumns="2" rows="5"
									partialTriggers="appSearch_RequestType">
									<af:inputText label="Facility ID" columns="10"
										maximumLength="10" tip="0000000000, 0%, %0%, *0*, 0*"
										value="#{applicationSearch.facilityID}" 
										id="appSearch_FacilityID"/>

									<af:inputText label="Facility Name"
										tip="acme%, %acme% *acme*, acme*"
										value="#{applicationSearch.facilityName}" maximumLength="60"
										id="appSearch_FacilityName" />

									<af:inputText tip="A0000504, A%, %50% *50*, A*"
										label="Application number"
										value="#{applicationSearch.applicationNumber}"
										maximumLength="8"
										id="appSearch_ApplicationNumber" />

									<af:selectOneChoice label="District " unselectedLabel=" " rendered="#{infraDefs.districtVisible}"
										value="#{applicationSearch.doLaaCd}"
										inlineStyle="width:136px;"
										id="appSearch_District">
										<f:selectItems value="#{infraDefs.districts}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="appSearch_DistHidden" rendered="#{!infraDefs.districtVisible}"/>
									
									<af:selectOneChoice label="County " unselectedLabel=" "
										value="#{applicationSearch.countyCd}"
										id="appSearchCounty">
										<f:selectItems value="#{infraDefs.counties}" />
									</af:selectOneChoice>
									
									<af:selectOneChoice label="Request Type"
										id="appSearch_RequestType" unselectedLabel=" "
										value="#{applicationSearch.applicationType}" autoSubmit="true">
										<mu:selectItems
											value="#{applicationReference.applicationTypeDefs}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="NSR Reason " unselectedLabel=" "
										value="#{applicationSearch.ptioReasonCd}"
										rendered="#{applicationSearch.applicationType == 'PTIO'}"
										id="appSearch_NSRReason">
										<mu:selectItems
											value="#{applicationReference.ptioAppPurposeDefs.items[(empty applicationSearch.ptioReasonCd? '' :applicationSearch.ptioReasonCd)]}" />
									</af:selectOneChoice>
									
									<af:selectBooleanCheckbox label="Legacy NSR Application"
										value="#{applicationSearch.legacyStatePTOFlag}"
										rendered="#{applicationSearch.applicationType == 'PTIO'}"
										id="appSearch_isLegacy" />
									<af:selectOneChoice label="PBR Type" unselectedLabel=" "
										rendered="#{applicationSearch.applicationType == 'PBR'}"
										value="#{applicationSearch.pbrTypeCd}"
										id="appSearch_PBRType">
										<f:selectItems
											value="#{applicationReference.pbrTypeDefs.items[(empty applicationDetail.pbrTypeCd? '': applicationDetail.pbrTypeCd)]}" />
									</af:selectOneChoice>

									<af:inputText label="Permit Number"
										value="#{applicationSearch.permitNumber}" maximumLength="8"
										id="appSearch_PermitNumber" />

									<af:inputText label="EU ID" value="#{applicationSearch.euId}"
									id="appSearch_EUID" />

									<af:selectOneChoice label="Company"
										value="#{applicationSearch.companyName}" unselectedLabel=""
										id="appSearch_Company">
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>

								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit"
											action="#{applicationSearch.search}"
											id="appSearch_SubmitBtn" />
										<af:commandButton text="Reset"
											action="#{applicationSearch.reset}"
											id="appSearch_ResetBtn" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>


				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1"
						rendered="#{applicationSearch.hasSearchResults}">
						<af:showDetailHeader text="Request List" disclosed="true">
							<jsp:include flush="true" page="applicationSearchTable.jsp" />
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>


			</af:page>
		</af:form>
	</af:document>
</f:view>

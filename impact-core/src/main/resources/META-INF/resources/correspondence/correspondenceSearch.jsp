<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Correspondence Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Correspondence Search">
				<%@ include file="header.jsp"%>

				<mu:setProperty value="false"
					property="#{correspondenceSearch.fromFacility}" />

				<mu:setProperty value="none"
					property="#{correspondenceSearch.fromLinkedTo}" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="8" width="1000" maxColumns="2"
									partialTriggers="category corrSearch_DateField corrSearch_CorrDirection corrSearch_District corrSearch_County">

									<af:inputText columns="15" label="Facility ID"
										maximumLength="10"
										value="#{correspondenceSearch.facilityId}"
										id="corrSearch_FacilityID" />

									<af:inputText columns="20" label="Facility Name"
										maximumLength="100"
										value="#{correspondenceSearch.facilityNm}"
										tip="acme%, %acme% *acme*, acme*"
										id="corrSearch_FacilityName" />

									<af:selectOneChoice label="Company"
										value="#{correspondenceSearch.companyId}" unselectedLabel=""
										id="corrSearch_Company" >
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>
									
									<af:inputText columns="10" rows="1" label="Correspondence ID"
										value="#{correspondenceSearch.corId}" maximumLength="10"
										tip="COR000002, COR001%, *0*, C*, *2"
										id="corrSearch_CorrID" />
																		
									<af:selectOneChoice label="Correspondence Type"
										id="corrSearch_CorrType"
										unselectedLabel=""
										value="#{correspondenceSearch.correspondenceTypeCd}">
										<f:selectItems
											value="#{correspondenceSearch.correspondenceDef}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Correspondence Direction"
										id="corrSearch_CorrDirection"
										autoSubmit="true"
										unselectedLabel=""
										value="#{correspondenceSearch.directionCd}">
										<f:selectItems
											value="#{correspondenceSearch.correspondenceDirectionDef}" />
									</af:selectOneChoice>
									
									<af:selectOneChoice label="District" unselectedLabel=" " rendered="#{infraDefs.districtVisible}"
										id="corrSearch_District"
										autoSubmit="true"
										value="#{correspondenceSearch.district}"
										inlineStyle="width:136px;" >
										<f:selectItems value="#{correspondenceSearch.districts}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="corrSearch_DistHidden" rendered="#{!infraDefs.districtVisible}"/>									
									<af:selectOneChoice label="County" unselectedLabel=" "
										id="corrSearch_County"
										autoSubmit="true"
										value="#{correspondenceSearch.countyCd}">
										<f:selectItems value="#{correspondenceSearch.counties}" />
									</af:selectOneChoice>
													
									<af:selectOneChoice label="City" unselectedLabel=" "
										value="#{correspondenceSearch.cityCd}"
										id="corrSearch_City" >
										<f:selectItems value="#{correspondenceSearch.cities}" />
									</af:selectOneChoice>
									
									<af:selectOneChoice label="Correspondence Category"
										id="corrSearch_CorrCategory"
										unselectedLabel=""
										value="#{correspondenceSearch.correspondenceCategoryCd}">
										<f:selectItems
											value="#{correspondenceSearch.correspondenceCategoryDef}" />
									</af:selectOneChoice>

									<af:inputText columns="40" label="Additional Info"
										maximumLength="1000"
										value="#{correspondenceSearch.additionalInfo}"
										tip="acme%, %acme% *acme*, acme*"
										id="corrSearch_AdditionalInfo" />
										
									<af:selectOneChoice label="Date Field" id="corrSearch_DateField"
										autoSubmit="true" unselectedLabel=" "
										value="#{correspondenceSearch.dateField}" readOnly="false">
										<mu:selectItems value="#{correspondenceSearch.correspondenceDateFields}" />
									</af:selectOneChoice>

									<af:selectInputDate label="From"
										maximumLength="12"
										readOnly="#{correspondenceSearch.dateField == null}"
										value="#{correspondenceSearch.startDate}"
										tip="Search will return results inclusive of this date."
										id="corrSearch_From" >
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>

									<af:selectInputDate label="To"
										maximumLength="12"
										readOnly="#{correspondenceSearch.dateField == null}"
										value="#{correspondenceSearch.endDate}"
										tip="Search will return results inclusive of this date."
										id="corrSearch_To" >
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>

								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{correspondenceSearch.submitSearch}"
											id="corrSearch_SubmitBtn" >
										</af:commandButton>
										<af:commandButton text="Reset"
											action="#{correspondenceSearch.reset}"
											id="corrSearch_ResetBtn" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1"
						rendered="#{correspondenceSearch.hasSearchResults}">
						<af:panelBorder>
							<af:showDetailHeader text="Correspondence List" disclosed="true">
								<jsp:include flush="true" page="correspondenceSearchTable.jsp" />
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</af:page>
		</af:form>
	</af:document>
</f:view>

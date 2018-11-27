<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Stack Test Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<f:verbatim><%@ include	file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include file="../scripts/FacilityType-Option.js"%></f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Stack Test Search">
				<%@ include file="../util/header.jsp"%>

				<mu:setProperty value="false"
					property="#{stackTestSearch.fromFacility}" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000">
						<af:panelBorder>
							<af:showDetailHeader text="Stack Test Search Criteria"
								disclosed="true">
								<af:panelForm rows="1" maxColumns="3">
									<af:inputText columns="25" label="Facility ID" id="STSearch_FacilityID"
										value="#{stackTestSearch.facilityId}"
										maximumLength="10" tip="0000000000, 0%, %0%, *0*, 0*" />
									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25" id="STSearch_FacilityName"
										label="Facility Name" value="#{stackTestSearch.facilityName}"
										valign="top" />
									<af:selectOneChoice label="Facility Class" id="STSearch_FacilityClass"
										value="#{stackTestSearch.permitClassCd}"
										unselectedLabel="">
										<f:selectItems
											value="#{facilityReference.permitClassDefs.items[0]}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Facility Type" id="STSearch_FacilityType"
										value="#{stackTestSearch.facilityTypeCd}"
										unselectedLabel="" styleClass="FacilityTypeClass x6">
										<f:selectItems
											value="#{facilityReference.facilityTypeDefs.items[0]}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="District" id="STSearch_District" rendered="#{infraDefs.districtVisible}"
										value="#{stackTestSearch.doLaaCd}" unselectedLabel="">
										<f:selectItems value="#{facilitySearch.doLaas}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="districtHidden" rendered="#{!infraDefs.districtVisible}"/>
									<af:selectOneChoice label="County" id="STSearch_County"
										value="#{stackTestSearch.countyCd}" unselectedLabel="">
										<f:selectItems value="#{infraDefs.counties}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Test State" id="STSearch_TestState"
										value="#{stackTestSearch.emissionTestState}" unselectedLabel="">
										<f:selectItems value="#{compEvalDefs.emissionsTestStateDef.allSearchItems}" />
									</af:selectOneChoice>
									<af:inputText label="Stack Test ID" tip="STCK000002, STCK00000%, *0*, STCK*, *2" id="STSearch_StackTestID"
										value="#{stackTestSearch.stckId}" columns="12"
										maximumLength="12" />
									<af:inputText label="Associated Inspection ID" id="STSearch_AssociatedInspectionID"
										value="#{stackTestSearch.inspId}" tip="INSP000002, INSP00000%, *0*, INSP*, *2" columns="12"
										maximumLength="12" />
									<af:selectOneChoice label="Reviewer" tip=" " id="STSearch_Reviewer"
										unselectedLabel=" " value="#{stackTestSearch.reviewer}">
										<f:selectItems value="#{infraDefs.basicUsersDef.allSearchItems}" />
									</af:selectOneChoice>
									<af:objectSpacer height="15" />
									<af:selectBooleanCheckbox label="Failed Pollutants Only" id="STSearch_FailedPollutantsCb"
										value="#{stackTestSearch.failedPolls}" />
									<af:selectOneChoice label="Company" id="STSearch_Company"
										value="#{stackTestSearch.cmpId}" unselectedLabel="">
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>
									<af:selectOneChoice id="STSearch_StackTestMethod" label="Stack Test Method"
										value="#{stackTestSearch.stackTestMethodCd}" unselectedLabel=""
										inlineStyle="width:422.5px">
										<f:selectItems
											value="#{compEvalDefs.stackTestMethodDef.allSearchItems}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Date Field" id="STSearch_DateField" 
										autoSubmit="true" unselectedLabel=" "
										value="#{stackTestSearch.dateBy}" readOnly="false">
										<mu:selectItems value="#{stackTestSearch.stackTestDateBy}" />
									</af:selectOneChoice>
									<af:selectInputDate label="From" id="STSearch_From"
										readOnly="#{stackTestSearch.dateBy == null}"
										value="#{stackTestSearch.beginDt}" partialTriggers="STSearch_DateField"
										tip="Search will return results inclusive of this date.">
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>
									<af:selectInputDate label="To" id="STSearch_To"
										readOnly="#{stackTestSearch.dateBy == null}"
										value="#{stackTestSearch.endDt}" partialTriggers="STSearch_DateField"
										tip="Search will return results inclusive of this date.">
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>
								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit" id="STSearch_SubmitBtn"
											action="#{stackTestSearch.search}">
										</af:commandButton>
										<af:commandButton text="Reset" id="STSearch_ResetBtn"
											action="#{stackTestSearch.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1200"
						rendered="#{stackTestSearch.hasSearchResults}">
						<af:showDetailHeader text="Stack Test List" disclosed="true">
							<af:table var="st" bandingInterval="1" banding="row" width="100%"
								value="#{stackTestSearch.resultsWrapper}"
								binding="#{stackTestSearch.resultsWrapper.table}"
								rows="#{stackTestSearch.pageLimit}">
								<jsp:include flush="true" page="../ceta/firstStackTestColumns.jsp" />
								<af:column sortProperty="cmpId" sortable="true" formatType="text"
									headerText="Company ID">
										<af:commandLink action="#{companyProfile.submitProfile}"
											text="#{st.cmpId}">
										<t:updateActionListener property="#{companyProfile.cmpId}"
											value="#{st.cmpId}" />
										<t:updateActionListener
											property="#{menuItem_companyProfile.disabled}" value="false" />
										</af:commandLink>
								</af:column>
								<af:column sortProperty="companyName" sortable="true" formatType="text"
									headerText="Company Name" noWrap="true">
										<af:outputText value="#{st.companyName}" />
								</af:column>
								<jsp:include flush="true" page="../ceta/stackTestList.jsp" />
								<af:column sortable="true" sortProperty="inspId"
									formatType="icon" headerText="Inspection ID">
									<af:commandLink rendered="true" text="#{st.inspId}"
										action="#{fceDetail.submit}">
										<af:setActionListener from="#{st.fceId}"
											to="#{fceDetail.fceId}" />
										<af:setActionListener from="#{st.facilityId}"
											to="#{fceDetail.facilityId}" />
										<af:setActionListener from="#{true}"
											to="#{fceDetail.existingFceObject}" />
										<af:setActionListener from="#{st.fceId}"
											to="#{fceSiteVisits.fceId}" />
									</af:commandLink>
								</af:column>

								<f:facet name="footer">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton
												actionListener="#{tableExporter.printTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Printable view" />
											<af:commandButton
												actionListener="#{tableExporter.excelTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Export to excel" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>
							</af:table>
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

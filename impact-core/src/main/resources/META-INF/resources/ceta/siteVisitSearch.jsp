<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Site Visit Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<f:verbatim><%@ include	file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include file="../scripts/FacilityType-Option.js"%></f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Site Visit Search">
				<%@ include file="../util/header.jsp"%>
				<af:inputHidden value="#{stackTests.popupRedirect}" />
				<mu:setProperty value="false"
					property="#{siteVisitSearch.fromFacility}" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000">
						<af:panelBorder>
							<af:showDetailHeader text="Site Visit Search Criteria" disclosed="true">
								<af:panelForm rows="1" maxColumns="4">
									<af:inputText columns="25" label="Facility ID"
										value="#{siteVisitSearch.facilityId}"
										maximumLength="10" tip="0000000000, 0%, %0%, *0*, 0*" />
									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
										label="Facility Name" value="#{siteVisitSearch.facilityName}"
										valign="top" />
									<af:selectOneChoice label="Facility Class"
										value="#{siteVisitSearch.permitClassCd}"
										unselectedLabel="">
										<f:selectItems
											value="#{facilityReference.permitClassDefs.items[0]}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Facility Type"
										value="#{siteVisitSearch.facilityTypeCd}"
										unselectedLabel="" styleClass="FacilityTypeClass x6">
										<f:selectItems
											value="#{facilityReference.facilityTypeDefs.items[0]}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="District" rendered="#{infraDefs.districtVisible}"
										value="#{siteVisitSearch.doLaaCd}" unselectedLabel="">
										<f:selectItems value="#{facilitySearch.doLaas}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="districtHidden" rendered="#{!infraDefs.districtVisible}"/>
				                    <af:selectOneChoice label="County"
				                      value="#{siteVisitSearch.countyCd}"
				                      unselectedLabel="">
				                      <f:selectItems value="#{infraDefs.counties}" />
				                    </af:selectOneChoice>
					                 <af:inputText columns="15" label="Site Visit ID" tip="SITE000002, SITE00000%, *0*, SITE*, *2"
										value="#{siteVisitSearch.siteId}" />
					                <af:selectOneChoice id="visitTypeChoice" label="Visit Type"
										unselectedLabel=""
										value="#{siteVisitSearch.visitTypeCd}">
										<f:selectItems
											value="#{compEvalDefs.siteVisitTypeDef.items[(empty sv.visitType ? '' : sv.visitType)]}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Compliance Issue"
										value="#{siteVisitSearch.complianceIssued}" autoSubmit="true"
										unselectedLabel="">
										<f:selectItem itemLabel="Yes" itemValue="Y" />
										<f:selectItem itemLabel="No" itemValue="N" />
									</af:selectOneChoice>
									<af:inputText columns="15" label="Associated Inspection ID" tip="INSP000002, INSP00000%, *0*, INSP*, *2"
										value="#{siteVisitSearch.inspId}" />
									<af:selectOneChoice label="Evaluator" tip=" "
                    					unselectedLabel=" " value="#{siteVisitSearch.evaluator}">
                    					<f:selectItems value="#{infraDefs.basicUsersDef.allSearchItems}" />
                  					</af:selectOneChoice>
                  					<af:selectOneChoice label="Company" value="#{siteVisitSearch.cmpId}"
										unselectedLabel="">
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>
									<af:selectInputDate id="visitAfterDate" label="From Visit Date"
                    					value="#{siteVisitSearch.beginVisitDt}"> 
                    					<af:validateDateTimeRange minimum="1900-01-01"/>
                    				</af:selectInputDate>
									
									<af:selectInputDate id="visitBeforeDate" label="To Visit Date"
                    					value="#{siteVisitSearch.endVisitDt}" > 
                    					<af:validateDateTimeRange minimum="1900-01-01"/>
                    				</af:selectInputDate>	
				                </af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit"
											action="#{siteVisitSearch.search}">
										</af:commandButton>
										<af:commandButton text="Reset"
											action="#{siteVisitSearch.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1200"
						rendered="#{siteVisitSearch.hasSearchResults}">
						<af:showDetailHeader text="Site Visit List" disclosed="true">
							<jsp:include flush="true" page="siteVisitSearchTable.jsp" />
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

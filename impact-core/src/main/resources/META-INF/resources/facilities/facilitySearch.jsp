<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Facility Search">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Facility Search">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="2" width="1000" maxColumns="3">

									<af:inputText columns="10" maximumLength="10"
										label="Facility ID" tip="0000000000, 0%, %0%, *0*, 0*"
										value="#{facilitySearch.facilityId}" />

									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
										label="Facility Name" value="#{facilitySearch.facilityName}"
										valign="top" />

									<af:selectOneChoice label="Company"
										value="#{facilitySearch.companyName}" unselectedLabel="">
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>

									<af:inputText label="Physical Address 1"
										tip="street%, %street%, *street*, street*"
										value="#{facilitySearch.address1}" />

									<af:selectOneChoice label="County"
										value="#{facilitySearch.countyCd}" unselectedLabel="">
										<f:selectItems value="#{infraDefs.counties}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="District" rendered="#{infraDefs.districtVisible}"
										value="#{facilitySearch.doLaaCd}" unselectedLabel="">
										<f:selectItems value="#{infraDefs.districts}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="districtHidden" rendered="#{!infraDefs.districtVisible}"/>
									
									<af:selectOneChoice label="Facility Class"
										value="#{facilitySearch.permitClassCd}" unselectedLabel="">
										<f:selectItems
											value="#{facilityReference.permitClassDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Facility Type"
										value="#{facilitySearch.facilityTypeCd}" unselectedLabel=""
										styleClass="FacilityTypeClass x6">
										<f:selectItems
											value="#{facilityReference.facilityTypeDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Operating Status" unselectedLabel=""
										value="#{facilitySearch.operatingStatusCd}">
										<f:selectItems
											value="#{facilityReference.operatingStatusDefs.items[0]}" />
									</af:selectOneChoice>
								</af:panelForm>

								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{facilitySearch.submitSearch}">
										</af:commandButton>

										<af:commandButton text="Reset"
											action="#{facilitySearch.reset}" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

				<jsp:include flush="true" page="facilitySearchTable.jsp" />

			</af:page>
		</af:form>
		<f:verbatim><%@ include
				file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/FacilityType-Option.js"%></f:verbatim>
	</af:document>
</f:view>






<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Facility Creation Request Search">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Facility Creation Request Search">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="2" width="1000" maxColumns="3">

									<af:inputText columns="10" maximumLength="10"
										label="Request ID" tip="0000000000, 0%, %0%, *0*, 0*"
										value="#{facilityRequestSearch.requestId}" />

									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
										label="Facility Name"
										value="#{facilityRequestSearch.facilityName}" valign="top" />

									<af:selectOneChoice label="Company"
										value="#{facilityRequestSearch.companyName}"
										unselectedLabel="">
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>

									<af:inputText label="Physical Address 1"
										tip="street%, %street%, *street*, street*"
										value="#{facilityRequestSearch.address1}" />

									<af:selectOneChoice label="County"
										value="#{facilityRequestSearch.countyCd}" unselectedLabel="">
										<f:selectItems value="#{infraDefs.counties}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="District" rendered="#{infraDefs.districtVisible}"
										value="#{facilityRequestSearch.doLaaCd}" unselectedLabel="">
										<f:selectItems value="#{infraDefs.districts}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="disctrictHidden" rendered="#{!infraDefs.districtVisible}"/>
									
									<af:selectOneChoice label="Facility Type"
										value="#{facilityRequestSearch.facilityTypeCd}"
										unselectedLabel="" styleClass="FacilityTypeClass x6">
										<f:selectItems
											value="#{facilityReference.facilityTypeDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Operating Status" unselectedLabel=""
										value="#{facilityRequestSearch.operatingStatusCd}">
										<f:selectItems
											value="#{facilityReference.operatingStatusDefs.items[0]}" />
									</af:selectOneChoice>

									<af:inputText columns="9" maximumLength="9" label="Contact ID"
										tip="000000000, *0*, 0*" value="#{facilityRequestSearch.cntId}" />

									<af:inputText tip="smith, *smith*, smith*" columns="20"
										maximumLength="20" label="Last Name"
										value="#{facilityRequestSearch.lastName}" valign="top" />

									<af:inputText tip="john, *john*, john*" columns="20"
										maximumLength="20" label="First Name"
										value="#{facilityRequestSearch.firstName}" />

									<af:inputText tip="jsmith, *jsmith*, jsmith*" columns="30"
										maximumLength="50" label="CROMERR Username"
										value="#{facilityRequestSearch.externalUsername}" />
										
									<af:inputText tip="(555)555-1212, *5551212" columns="30"
										maximumLength="30" label="Primary Phone No."
										value="#{facilityRequestSearch.phone}" valign="top" />
									
									<af:inputText tip="user@wyo.gov, *user*, user*" columns="30"
										maximumLength="30" label="Email"
										value="#{facilityRequestSearch.email}" valign="top" />
										
									<af:selectOneChoice label="Request State"
										value="#{facilityRequestSearch.requestStateCd}"
										unselectedLabel="">
										<f:selectItems
											value="#{facilityReference.facilityRequestStatusDefs.items[0]}" />
									</af:selectOneChoice>
										
								</af:panelForm>

								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{facilityRequestSearch.submitSearch}">
										</af:commandButton>

										<af:commandButton text="Reset"
											action="#{facilityRequestSearch.reset}" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

				<jsp:include flush="true" page="facilityRequestSearchTable.jsp" />

			</af:page>
		</af:form>
		<f:verbatim><%@ include
				file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/FacilityType-Option.js"%></f:verbatim>
	</af:document>
</f:view>
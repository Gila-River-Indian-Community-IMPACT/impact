<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Emission Unit Operating Status Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Emission Unit Operating Status Search">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="2" maxColumns="3">

									<af:inputText columns="10" maximumLength="10"
										label="Facility ID" tip="0000000000, 0%, %0%, *0*, 0*"
										value="#{euStatusSearch.facilityId}" />

									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
										label="Facility Name" value="#{euStatusSearch.facilityName}"
										valign="top" />

									<af:selectOneChoice label="District" inlineStyle="#{infraDefs.hidden}"
										value="#{euStatusSearch.doLaaCd}" unselectedLabel="">
										<f:selectItems value="#{euStatusSearch.doLaas}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Facility Class"
										value="#{euStatusSearch.permitClassCd}" unselectedLabel="">
										<f:selectItems
											value="#{facilityReference.permitClassDefs.items[(empty euStatusSearch.permitClassCd ? '' : euStatusSearch.permitClassCd)]}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Facility Operating Status" unselectedLabel=""
										value="#{euStatusSearch.facilityOperatingStatusCd}">
										<f:selectItems
											value="#{facilityReference.operatingStatusDefs.items[(empty euStatusSearch.facilityOperatingStatusCd ? '' : euStatusSearch.facilityOperatingStatusCd)]}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="EU Operating Status:"
										value="#{euStatusSearch.euOperatingStatusCd}" unselectedLabel="">
										<f:selectItems
											value="#{facilityProfile.euOperatingStatusDefs.items[(empty euStatusSearch.euOperatingStatusCd ? '' : euStatusSearch.euOperatingStatusCd)]}" />
									</af:selectOneChoice>

								</af:panelForm>

								<af:objectSpacer width="70%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="70%" height="20" />
										<af:commandButton text="Submit"
											action="#{euStatusSearch.submitSearch}">
										</af:commandButton>

										<af:commandButton text="Reset"
											action="#{euStatusSearch.reset}" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="70%" height="15" />

				<jsp:include flush="true" page="euStatusTable.jsp" />

			</af:page>
		</af:form>
	</af:document>
</f:view>


<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Invoice Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Invoice Search">
				<%@ include file="../util/header.jsp"%>

				<jsp:useBean id="invoiceSearch" scope="session"
					class="us.oh.state.epa.stars2.app.invoice.InvoiceSearch" />
				<jsp:setProperty name="invoiceSearch" property="fromFacility"
					value="false" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<afh:rowLayout>
									<afh:cellFormat halign="left" width="33%">

										<af:panelForm maxColumns="1" labelWidth="40%">

											<af:inputText tip="0000000000, 0%, %0%, *0*, 0*" columns="10" maximumLength="10"
												label="Facility ID" value="#{invoiceSearch.facilityId}" />

											<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
												label="Facility Name" value="#{invoiceSearch.facilityName}" />

											<af:inputText columns="10" label="Invoice ID"
												value="#{invoiceSearch.invoiceId}" />
										</af:panelForm>
									</afh:cellFormat>

									<afh:cellFormat width="33%">
										<af:panelForm maxColumns="1" labelWidth="40%">
											<af:inputText columns="10" label="Revenue ID"
												value="#{invoiceSearch.revenueId}" />

											<af:selectOneChoice label="Revenue Type" unselectedLabel=" "
												value="#{invoiceSearch.revenueTypeCd}" tip=" ">
												<f:selectItems value="#{invoiceSearch.revenueTypes}" />
											</af:selectOneChoice>

											<af:selectOneChoice label="Invoice State" unselectedLabel=" "
												value="#{invoiceSearch.invoiceStateCd}">
												<f:selectItems value="#{facilityReference.invoiceStates.items[(empty invoiceSearch.invoiceStateCd ? '' : invoiceSearch.invoiceStateCd)]}" />
											</af:selectOneChoice>

											<af:selectOneChoice label="District" inlineStyle="#{infraDefs.hidden}" unselectedLabel=" "
												value="#{invoiceSearch.doLaaCd}">
												<f:selectItems value="#{invoiceSearch.doLaas}" />
											</af:selectOneChoice>
										</af:panelForm>
									</afh:cellFormat>

									<afh:cellFormat width="33%">
										<af:panelForm rows="3" width="300" maxColumns="1"
											labelWidth="40%">
											<af:selectInputDate label="Invoice Begin Date"
												value="#{invoiceSearch.beginDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
											<af:selectInputDate label="Invoice End Date"
												value="#{invoiceSearch.endDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

											<%--<af:selectOneChoice label="Revenue State" unselectedLabel=" "
												value="#{invoiceSearch.revenueStateCd}">
												<f:selectItems value="#{facilityReference.revenueSearchStates.items[(empty invoiceSearch.revenueStateCd ? '' : invoiceSearch.revenueStateCd)]}" />
											</af:selectOneChoice>--%>

										</af:panelForm>
									</afh:cellFormat>
								</afh:rowLayout>

								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{invoiceSearch.submitSearch}" />

										<af:commandButton text="Reset" action="#{invoiceSearch.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1"
						rendered="#{invoiceSearch.hasSearchResults}">
						<af:showDetailHeader text="Invoice List" disclosed="true">
							<jsp:include flush="true" page="invSearchTable.jsp" />
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>


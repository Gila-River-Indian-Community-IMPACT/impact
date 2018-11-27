<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Company Search">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Company Search">
				<jsp:include page="../util/header.jsp" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="2" width="1000" maxColumns="3">

									<af:inputText columns="9" maximumLength="9" label="Company ID"
										tip="000000000, *0*, 0*" value="#{companySearch.companyId}" />

									<af:inputText tip="acme, *acme*, acme*" columns="25"
										maximumLength="80" label="Company Name"
										value="#{companySearch.companyName}" valign="top" />

									<af:inputText columns="10" maximumLength="10" label="CROMERR ID"
										tip="0000000000, *0*, 0*" value="#{companySearch.externalCompanyId}" />

									<af:inputText tip="acme, *acme*, acme*" columns="25"
										maximumLength="80" label="Company Alias"
										value="#{companySearch.companyAlias}" valign="top" />

									<af:inputText tip="(555)555-1212, *5551212" columns="10"
										maximumLength="20" label="Phone"
										value="#{companySearch.phone}" valign="top" />

								</af:panelForm>


								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{companySearch.submitSearch}">
										</af:commandButton>

										<af:commandButton text="Reset" action="#{companySearch.reset}" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

				<jsp:include flush="true" page="companySearchTable.jsp" />

			</af:page>
		</af:form>
	</af:document>
</f:view>


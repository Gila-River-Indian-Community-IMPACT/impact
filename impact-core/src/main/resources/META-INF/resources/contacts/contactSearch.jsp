<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Contact Search">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Contact Search">
				<jsp:include page="../util/header.jsp" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelHorizontal halign="left">
									<af:panelForm rows="1" width="1000" labelWidth="30%"
										maxColumns="3">
										<af:inputText columns="9" maximumLength="9" label="Contact ID"
											tip="000000000, *0*, 0*" value="#{contactSearch.cntId}" />

										<af:inputText tip="smith, *smith*, smith*" columns="20"
											maximumLength="20" label="Last Name"
											value="#{contactSearch.lastName}" valign="top" />



										<af:inputText tip="(555)555-1212, *5551212" columns="30"
											maximumLength="30" label="Primary Phone No."
											value="#{contactSearch.phone}" valign="top" />

										<af:selectOneChoice value="#{contactSearch.companyId}"
											label="Company Name: " unselectedLabel=" ">
											<f:selectItems value="#{infraDefs.companies}" />
										</af:selectOneChoice>


										<af:inputText tip="john, *john*, john*" columns="20"
											maximumLength="20" label="First Name"
											value="#{contactSearch.firstName}" />

										<af:inputText tip="jsmith, *jsmith*, jsmith*" columns="30"
											maximumLength="50" label="CROMERR Username"
											value="#{contactSearch.externalUsername}" />


										<af:selectOneChoice label="Status"
											value="#{contactSearch.active}" unselectedLabel=""
											rendered="#{contactDetail.dapcUser}">
                                            <f:selectItem itemLabel="Active" itemValue="Y" />
											<f:selectItem itemLabel="Inactive" itemValue="N" />

										</af:selectOneChoice>




										<af:inputText tip="peter, *peter*, peter*" columns="20"
											maximumLength="20" label="Preferred Name"
											value="#{contactSearch.preferredName}" />



									</af:panelForm>
								</af:panelHorizontal>


								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{contactSearch.submitSearch}">
										</af:commandButton>

										<af:commandButton text="Reset" action="#{contactSearch.reset}" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

				<jsp:include flush="true" page="contactSearchTable.jsp" />

			</af:page>
		</af:form>
	</af:document>
</f:view>


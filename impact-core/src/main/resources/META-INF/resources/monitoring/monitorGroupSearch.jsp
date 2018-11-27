<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Monitor Group Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:inputHidden value="#{monitorGroupSearch.popupRedirect}" />
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Monitor Group Search">
				<%@ include file="header.jsp"%>
				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="3" width="1000" maxColumns="3"
									partialTriggers="">

									<af:inputText columns="60" label="Group Name"
										maximumLength="60" tip="acme%, %acme% *acme*, acme*"
										value="#{monitorGroupSearch.groupName}" />

									<af:inputText columns="10" label="Group ID"
										maximumLength="20" tip="0000000000, 0%, %0%, *0*, 0*"
										value="#{monitorGroupSearch.mgrpId}" />

									<af:selectOneChoice label="Company"
									    rendered="#{!monitorGroupSearch.publicApp}"
										disabled="#{infraDefs.ambientMonitorReportUploader}"
										value="#{monitorGroupSearch.companyId}" unselectedLabel="">
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Monitor Reviewer"
										unselectedLabel=""
										rendered="#{!monitorGroupSearch.publicApp}"
										value="#{monitorGroupSearch.monitorReviewerId}" >
										<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
									</af:selectOneChoice>

									<af:inputText columns="10" label="Facility ID"
										rendered="#{!monitorGroupSearch.publicApp}"
										disabled="#{infraDefs.ambientMonitorReportUploader}"
										maximumLength="32" tip="0000000000, 0%, %0%, *0*, 0*"
										value="#{monitorGroupSearch.facilityId}" />

									<af:inputText columns="25" label="Facility Name"
										rendered="#{!monitorGroupSearch.publicApp}"
										disabled="#{infraDefs.ambientMonitorReportUploader}"
										maximumLength="32"  tip="acme%, %acme% *acme*, acme*"
										value="#{monitorGroupSearch.facilityName}" />
								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{monitorGroupSearch.submitSearch}">
										</af:commandButton>
										<af:commandButton text="Reset"
											action="#{monitorGroupSearch.reset}" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>



				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1"
						rendered="#{monitorGroupSearch.hasSearchResults}">
						<af:panelBorder>
							<af:showDetailHeader text="Monitor Group List" disclosed="true">
								<jsp:include flush="true" page="monitorGroupSearchTable.jsp" />
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</af:page>
		</af:form>
	</af:document>
</f:view>

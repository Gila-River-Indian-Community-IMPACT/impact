<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Split Facility">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:inputHidden value="#{splitFacility.popupRedirect}" />
			<af:page var="foo" value="#{menuModel.model}" id="splitFacility"
				title="Split Facility">

				<jsp:include page="header.jsp" />

				<afh:rowLayout halign="center">
					<af:panelForm rows="3" maxColumns="1" width="100%">
						<af:inputText label="Facility ID:"
							value="#{splitFacility.facilityId}" id="facId" columns="10"
							maximumLength="10" showRequired="true" />
						<af:inputText label="New Facility Name:"
							value="#{splitFacility.facilityName}" id="facName" columns="55"
							maximumLength="55" showRequired="true" />
						<af:selectOneChoice label="New Facility Company Name:"
							id="ownerCompanyCd" value="#{splitFacility.ownerCompanyId}"
							autoSubmit="true" showRequired="true">
							<f:selectItems value="#{infraDefs.companies}" />
						</af:selectOneChoice>
					</af:panelForm>
				</afh:rowLayout>

				<af:objectSpacer height="20" />
				<afh:rowLayout halign="center">
					<af:panelForm width="100%">
						<af:panelButtonBar>
							<af:commandButton id="subButn" text="Submit Split Facility"
								disabled="#{facilityProfile.readOnlyUser || splitFacility.buttonClicked}"
								action="#{splitFacility.confirm}" useWindow="true"
								windowWidth="600" windowHeight="200">
							</af:commandButton>
							<af:commandButton text="Reset"
								action="#{splitFacility.resetSplitFacility}" />
						</af:panelButtonBar>
					</af:panelForm>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

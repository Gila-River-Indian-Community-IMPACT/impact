<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm width="1200px">
	<af:panelBorder>
		<f:facet name="left">
			<af:panelHeader text="Base Contact Information" size="2">
				<jsp:include flush="true" page="baseContact.jsp" />
			</af:panelHeader>
		</f:facet>
		<f:facet name="right">
			<af:panelHeader text="Target Contact Search Criteria" size="2">
				<af:panelForm width="600px" rows="5" labelWidth="30%" maxColumns="1">
					<af:inputText tip="smith, *smith*, smith*" columns="20"
						maximumLength="20" label="Last Name"
						disabled="#{!mergeContact.editableDuplicateSearch}"
						value="#{mergeContact.duplicateLastName}" valign="top" />

					<af:inputText tip="john, *john*, john*" columns="20"
						maximumLength="20" label="First Name"
						disabled="#{!mergeContact.editableDuplicateSearch}"
						value="#{mergeContact.duplicateFirstName}" />

					<af:inputText tip="jsmith@acme.com, *jsmith*, *@acme.com"
						columns="30" maximumLength="50" label="Email"
						disabled="#{!mergeContact.editableDuplicateSearch}"
						value="#{mergeContact.duplicateEmail}" />
				</af:panelForm>
			</af:panelHeader>
		</f:facet>
	</af:panelBorder>



	<af:objectSpacer width="100%" height="15" />
	<afh:rowLayout halign="center">
		<af:panelBorder>
			<f:facet name="left">
				<afh:rowLayout halign="center">
					<af:panelForm width="500px">
					</af:panelForm>
				</afh:rowLayout>
			</f:facet>
			<f:facet name="right">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Modify Target Contact Search Criteria"
							rendered="#{!mergeContact.editableDuplicateSearch}"
							action="#{mergeContact.modifyDuplicateContactSearch}">
						</af:commandButton>

						<af:commandButton text="Search"
							rendered="#{mergeContact.editableDuplicateSearch}"
							action="#{mergeContact.submitDuplicateSearch}">
						</af:commandButton>
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>
		</af:panelBorder>
	</afh:rowLayout>
</af:panelForm>
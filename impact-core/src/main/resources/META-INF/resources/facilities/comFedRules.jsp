<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="facilityHeader">
				<jsp:include page="comFacilityHeader.jsp" />
			</f:subview>
		</f:facet>

		<f:facet name="right">
			<h:panelGrid columns="1" border="1" width="950"
				style="margin-left:auto;margin-right:auto;">
				<af:panelGroup layout="vertical">
					<af:panelHeader text="Rules & Regs Applicability" size="0" />
					<jsp:include flush="true" page="comFacFedRules.jsp" />

					<af:objectSpacer height="10" />
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Edit Rules"
								action="#{facilityProfile.editFacility}"
								rendered="#{facilityProfile.dapcUser && !facilityProfile.editable}"
								disabled="#{facilityProfile.disabledUpdateButton}" />
							<af:commandButton text="Save"
								action="#{facilityProfile.saveFacilityFedRules}"
								rendered="#{facilityProfile.editable}" />
							<af:commandButton text="Cancel"
								action="#{facilityProfile.cancelEdit}"
								rendered="#{facilityProfile.editable}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelGroup>
			</h:panelGrid>
		</f:facet>
		
	</af:panelBorder>
</h:panelGrid>

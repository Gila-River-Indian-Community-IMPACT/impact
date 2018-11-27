<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="naicsBody">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" id="naicsPage"
				title="NAICS Detail">
				<af:panelForm rows="2" maxColumns="1" labelWidth="70">
					<afh:rowLayout halign="center">
						<af:selectOneChoice value="#{facilityProfile.modifyNaics.value}"
							readOnly="#{!facilityProfile.editable1}" label="NAICS: ">
							<f:selectItems value="#{infraDefs.naicsSelectDefs}" />
						</af:selectOneChoice>
					</afh:rowLayout>
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Edit"
								rendered="#{!facilityProfile.editable1}"
								disabled="#{facilityProfile.disabledUpdateButton}"
								action="#{facilityProfile.editFacilityNaics}" />
							<af:commandButton text="Save"
								rendered="#{facilityProfile.editable1}"
								disabled="#{facilityProfile.disabledUpdateButton}"
								action="#{facilityProfile.saveFacilityNaics}" />
							<af:commandButton text="Cancel"
								rendered="#{facilityProfile.editable1 && !facilityProfile.newNaics}"
								disabled="#{facilityProfile.disabledUpdateButton}"
								action="#{facilityProfile.cancelFacilityNaics}" />
							<af:commandButton text="Delete"
								rendered="#{!facilityProfile.editable1}"
								disabled="#{facilityProfile.disabledUpdateButton}"
								action="#{facilityProfile.deleteFacilityNaics}" />
							<af:commandButton text="Close"
								rendered="#{facilityProfile.newNaics || !facilityProfile.editable1}"
								action="#{facilityProfile.closeFacilityNaicsDialog}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>
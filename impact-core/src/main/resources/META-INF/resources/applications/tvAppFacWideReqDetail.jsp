<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document title="Facility-Wide Requirement">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:panelForm>
				<af:inputText id="description" label="Facility-Wide Requirement:"
					value="#{applicationDetail.facWideReq.description}" columns="60" rows="4"
					maximumLength="250" showRequired="true"
					readOnly="#{!applicationDetail.editModeFacWideReq}" />
				<af:inputText id="proposedMethod"
					label="Proposed Method to Demonstrate Compliance:"
					showRequired="true"
					readOnly="#{!applicationDetail.editModeFacWideReq}"
					value="#{applicationDetail.facWideReq.proposedMethod}" columns="60" rows="4"
					maximumLength="250" />

				<af:objectSpacer height="10" />
				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							disabled="#{!applicationDetail.editAllowed}"
							rendered="#{!applicationDetail.editModeFacWideReq}"
							action="#{applicationDetail.changeEditModeFacWideReq}" />
						<af:commandButton text="Save"
							disabled="#{!applicationDetail.editAllowed}"
							rendered="#{applicationDetail.editModeFacWideReq}"
							action="#{applicationDetail.saveFacilityWideRequirement}" />
						<af:commandButton text="Cancel"
							rendered="#{applicationDetail.editModeFacWideReq}"	
							actionListener="#{applicationDetail.cancelEditModeFacWideReq}" />
						<af:commandButton text="Delete"
							disabled="#{!applicationDetail.editAllowed}"
							action="#{applicationDetail.deleteFacilityWideRequirement}"
							rendered="#{!applicationDetail.editModeFacWideReq && !(empty applicationDetail.facWideReq.requirementId)}" />
						<af:commandButton text="Close"
							rendered="#{!applicationDetail.editModeFacWideReq}"
							actionListener="#{applicationDetail.closeFacWideReqDialog}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
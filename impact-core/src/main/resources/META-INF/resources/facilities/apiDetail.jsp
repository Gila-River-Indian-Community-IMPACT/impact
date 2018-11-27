<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="apiBody" inlineStyle="width:400px; height:230px;">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:page var="foo" id="apiPage" title="API Detail">
				<af:panelForm rows="2" maxColumns="1" labelWidth="30" width="270">
					<afh:rowLayout halign="center">
						<af:inputText value="#{facilityProfile.modifyApiGroup.apiNo}"
							styleClass="digit-field" columns="10"
							readOnly="#{!facilityProfile.editable1}" label="API:"
							maximumLength="7" shortDesc="API Number must be 6 or 7 digits."
							tip="* Enter the last 6 or 7 digits of the API number (digits only)" />
					</afh:rowLayout>
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Edit"
								rendered="#{!facilityProfile.editable1}"
								disabled="#{facilityProfile.disabledUpdateButton}"
								action="#{facilityProfile.editFacilityApi}" />
							<af:commandButton text="Save"
								rendered="#{facilityProfile.editable1}"
								disabled="#{facilityProfile.disabledUpdateButton}"
								action="#{facilityProfile.saveFacilityApi}" />
							<af:commandButton text="Cancel"
								rendered="#{facilityProfile.editable1 && !facilityProfile.modifyApiGroup.newObject}"
								disabled="#{facilityProfile.disabledUpdateButton}"
								action="#{facilityProfile.cancelFacilityApi}" />
							<af:commandButton text="Delete"
								rendered="#{!facilityProfile.editable1}"
								disabled="#{facilityProfile.disabledUpdateButton}"
								action="#{facilityProfile.deleteFacilityApi}" />
							<af:commandButton text="Close"
								rendered="#{!facilityProfile.modifyApiGroup.newObject && !facilityProfile.editable1}"
								action="#{facilityProfile.closeFacilityApiDialog}" />
							<af:commandButton text="Close"
								rendered="#{facilityProfile.modifyApiGroup.newObject && facilityProfile.editable1}"
								action="#{facilityProfile.closeNewFacilityApiDialog}"
								onclick="window.close();" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
	<f:verbatim><%@ include
			file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
	<f:verbatim><%@ include
			file="../scripts/wording-filter.js"%></f:verbatim>
	<f:verbatim><%@ include
			file="../scripts/facility-detail-apis.js"%></f:verbatim>
</f:view>
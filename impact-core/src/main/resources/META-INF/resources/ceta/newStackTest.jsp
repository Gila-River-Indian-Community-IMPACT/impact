<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="body" title="New Stack Test"
		partialTriggers="messages">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<%@ include file="../util/validate.js"%>
			<af:messages id="message" />
			<af:page>
				<af:panelForm>
					<af:inputText label="Facility ID :"
						value="#{stackTestSearch.newStackTestFacilityID}"
						maximumLength="20" readOnly="true" />


					<f:facet name="footer">
						<af:panelButtonBar>
							<af:commandButton text="Create Stack Test"
								actionListener="#{stackTestSearch.createNewStackTest}"
								onclick="return isCreated();return false;" />
							<af:commandButton text="Cancel" immediate="true"
								actionListener="#{stackTestSearch.cancelNewStackTest}" />
						</af:panelButtonBar>
					</f:facet>
				</af:panelForm>
			</af:page>
		</af:form>
		<f:verbatim>
			<script type="text/javascript">
				var created = false;
				function isCreated() {
					if (created) {
						return false;
					} else {
						created = true;
						return true;
					}
				}
			</script>
		</f:verbatim>
	</af:document>
</f:view>
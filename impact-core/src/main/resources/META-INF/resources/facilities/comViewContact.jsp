<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Facility Contact Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />

			<af:panelForm>

				<f:subview id="contact">
					<jsp:include flush="true" page="../contacts/contact.jsp" />
				</f:subview>

				<af:objectSpacer height="20" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Delete" useWindow="true" windowWidth="700"
							windowHeight="500" action="#{contactDetail.deleteContact}"
							disabled="true"
							rendered="#{!contactDetail.editable}" />
						<af:commandButton text="Edit"
							action="#{contactDetail.editContact}"
							disabled="#{contactDetail.disabledUpdateButton}"
							rendered="#{!contactDetail.editable}" />
						<af:commandButton text="Save"
							action="#{facilityProfile.saveContactDetailStaging}"
							rendered="#{contactDetail.editable}" />
						<af:commandButton text="Cancel"
							action="#{facilityProfile.cancelEditContactDetailStaging}"
							rendered="#{contactDetail.editable && !contactDetail.dapcUser}"
							immediate="true" />
						<af:commandButton text="Return"
							action="#{facilityProfile.closeDialog}"
							rendered="#{!contactDetail.editable && !contactDetail.dapcUser}"
							immediate="true" />
					</af:panelButtonBar>
				</afh:rowLayout>


			</af:panelForm>
		</af:form>
	</af:document>
</f:view>

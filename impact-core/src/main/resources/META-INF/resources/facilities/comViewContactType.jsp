<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Facility Contact Type Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<f:subview id="contact">
				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="center">
						<af:panelHeader messageType="Information"
							text="Contact Type Information" />
					</afh:rowLayout>
				</afh:tableLayout>
				<af:objectSpacer height="20" />
				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="left">
						<af:outputFormatted
							rendered="#{facilityProfile.facContact.message != null}"
							inlineStyle="color: orange; font-weight: bold;"
							value="#{facilityProfile.facContact.message}" />
					</afh:rowLayout>
				</afh:tableLayout>
				<af:panelForm rows="3" maxColumns="1" labelWidth="250" width="600"
					partialTriggers="startDate">
					<af:inputText label="First Name:"
						value="#{facilityProfile.facContact.contact.firstNm}" columns="40"
						maximumLength="40" readOnly="true" id="firstNm"
						showRequired="true" />
					<af:inputText label="Middle Name:"
						value="#{facilityProfile.facContact.contact.middleNm}"
						columns="40" maximumLength="40" readOnly="true" id="middleNm"
						showRequired="true" />
					<af:inputText label="Last Name:"
						value="#{facilityProfile.facContact.contact.lastNm}" columns="40"
						maximumLength="40" readOnly="true" id="lastNm" showRequired="true" />
					<af:inputText label="Company Title:"
						value="#{facilityProfile.facContact.contact.companyTitle}"
						columns="40" maximumLength="40" readOnly="true" id="companyTitle"
						showRequired="true" />
					<af:selectOneChoice value="#{facilityProfile.facContact.contact.companyId}"
						label="Contact's Company Name: "
						readOnly="true"
						showRequired="true" id="contactCompany">
						<f:selectItems value="#{infraDefs.companies}" />
					</af:selectOneChoice>
					<af:selectOneChoice label="Type:"
						value="#{facilityProfile.facContact.contactType.contactTypeCd}"
						readOnly="true">
						<f:selectItems
							value="#{infraDefs.contactTypes.items[(empty facilityProfile.facContact.contactType.contactTypeCd ? '' : facilityProfile.facContact.contactType.contactTypeCd)]}" />
					</af:selectOneChoice>
					<af:selectInputDate label="Start Date:" id="startDate"
						autoSubmit="true" required="true"
						value="#{facilityProfile.facContact.contactType.startDate}"
						readOnly="#{facilityProfile.contactDatesReadOnly}"
						immediate="false">
						<af:validateDateTimeRange minimum="1900-01-01" />
					</af:selectInputDate>
					<af:selectInputDate label="End Date:" id="endDate"
						value="#{facilityProfile.facContact.contactType.endDate}"
						readOnly="#{facilityProfile.contactDatesReadOnly}">
						<af:validateDateTimeRange
							minimum="#{facilityProfile.facContact.contactType.startDate}" />
					</af:selectInputDate>
				</af:panelForm>
				<af:objectSpacer height="20" />
				<af:panelForm rows="3" maxColumns="1" labelWidth="250" width="600"
					partialTriggers="startDate" onmouseover="triggerContactTypeUpdate();">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Edit Contact Type Dates"
								action="#{facilityProfile.editContactType}"
								disabled="#{!facilityProfile.dapcUser && myTasks.fromHomeContact}"
								rendered="#{!facilityProfile.readOnlyUser && !facilityProfile.editable1}" />
							<af:commandButton text="Delete Contact Type"
								action="#{facilityProfile.deleteContactType}"
								disabled="#{facilityProfile.contactTypeDelDisable}"
								rendered="#{facilityProfile.dapcUser && !facilityProfile.readOnlyUser && !facilityProfile.editable1}" />
							<af:commandButton text="Save"
								id="contactTypeSaveButton"
								action="#{facilityProfile.saveEditContactType}"
								onmouseover="triggerContactTypeUpdate();"
								rendered="#{facilityProfile.editable1}" />
							<af:commandButton text="Return"
								action="#{facilityProfile.closeDialog}"
								rendered="#{!facilityProfile.editable1}" immediate="true" />
							<af:commandButton text="Cancel"
								action="#{facilityProfile.cancelEditContactType}"
								rendered="#{facilityProfile.editable1}" immediate="true" />
						</af:panelButtonBar>
					</afh:rowLayout>

				</af:panelForm>
			</f:subview>
		</af:form>
		<f:verbatim>
			<script type="text/javascript">
				function triggerContactTypeUpdate(){
					if(document.activeElement.id=="contact:startDate"){
			    		input = document.getElementById("contact:startDate");
			    		input.blur();
					} 
	
				}
			</script>
		</f:verbatim>
	</af:document>
</f:view>
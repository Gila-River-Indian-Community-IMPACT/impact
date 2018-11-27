<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Facility Add Contact Type">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />

			<f:subview id="contact">
				<af:panelForm partialTriggers="contactType contactId contactListType">
					<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
						<afh:rowLayout halign="left">
							<af:panelHeader messageType="information"
								text="Assign contact type" />
						</afh:rowLayout>
					</afh:tableLayout>
					<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0"
						partialTriggers="contactType contactPerson">
						<afh:rowLayout halign="center">
							<af:outputFormatted
								rendered="#{facilityProfile.facContact.message != null}"
								inlineStyle="color: orange; font-weight: bold;"
								value="<b>#{facilityProfile.facContact.message}</b>" />
						</afh:rowLayout>
					</afh:tableLayout>
					<af:selectOneChoice label="Type:"
						rendered="#{facilityProfile.facContact.contactType.contactTypeCd == null || facilityProfile.facContact.contactType.contactTypeCd != 'ownr'}"
						value="#{facilityProfile.facContactTypeCd}" id="contactType"
						autoSubmit="true" required="false">
						<f:selectItems
							value="#{infraDefs.notOwnerContactTypes.items[(empty facilityProfile.facContactTypeCd ? '' : facilityProfile.facContactTypeCd)]}" />
					</af:selectOneChoice>
					<af:selectOneRadio id="contactListType" label="Contact List Type:"
						rendered="#{facilityProfile.dapcUser}"
						disabled="#{facilityProfile.facContact.contactType.contactTypeCd == null || facilityProfile.facContact.contactType.contactId == -1}"
						value="#{facilityProfile.useGlobalContactList}" autoSubmit="true">
						<f:selectItem itemLabel="Show contacts for this company only"
							itemValue="false" />
						<f:selectItem
							itemLabel="Show all contacts in global contacts list"
							itemValue="true" />
					</af:selectOneRadio>
					<af:selectOneChoice label="Contact:"
						value="#{facilityProfile.facContact.contactType.contactId}"
						id="contactId" autoSubmit="true"
						disabled="#{facilityProfile.facContact.contactType.contactTypeCd == null || facilityProfile.facContact.contactType.contactId == -1}"
						showRequired="#{facilityProfile.facContact.contactType.contactTypeCd != null}"
						tip="If you do not see the person (contact) you wish to assign in the global contact list, return to the previous screen and click on 'Create Contact Person' to add a new global contact.">
						<f:selectItems value="#{facilityProfile.contactsList}" />
					</af:selectOneChoice>
					<af:selectInputDate label="Start Date:" id="startDate"
						value="#{facilityProfile.facContact.contactType.startDate}"
						disabled="#{facilityProfile.facContact.contactType.contactTypeCd == null || facilityProfile.facContact.contactType.contactId == -1}"
						showRequired="#{facilityProfile.facContact.contactType.contactTypeCd != null}">
					</af:selectInputDate>

					<af:objectSpacer height="20" />

					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Save"
								disabled="#{facilityProfile.facContact.contactType.contactTypeCd == null || facilityProfile.facContact.contactType.contactId == -1}"
								action="#{facilityProfile.saveEditContactType}" />
							<af:commandButton text="Cancel"
								action="#{facilityProfile.closeDialog}" immediate="true" />
						</af:panelButtonBar>
					</afh:rowLayout>

					<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
						<afh:rowLayout halign="center">
							<af:outputFormatted
								rendered="#{facilityProfile.facContact.contactType.contactTypeCd == null || facilityProfile.facContact.contactType.contactTypeCd != 'ownr'}"
								value="Note: you may assign multiple contact persons for all contact types. If a contact ceases to be a contact type, click on the corresponding row in the table on the previous screen and assign an end date." />
						</afh:rowLayout>
					</afh:tableLayout>
					<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
						<afh:rowLayout halign="center">
							<af:outputFormatted
								rendered="#{facilityProfile.facContact.contactType.contactTypeCd != null && facilityProfile.facContact.contactType.contactTypeCd == 'ownr'}"
								value="Note: you may assign multiple contact persons as the facility owner(s). If a contact ceases to be an owner, click on the corresponding row in the table on the previous screen and assign an end date." />
						</afh:rowLayout>
					</afh:tableLayout>
				</af:panelForm>
			</f:subview>
		</af:form>
	</af:document>
</f:view>
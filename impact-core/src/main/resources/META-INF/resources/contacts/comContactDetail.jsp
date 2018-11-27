<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="contactHeader">
				<jsp:include page="contactHeader.jsp" />
			</f:subview>
		</f:facet>

		<f:facet name="right">
			<h:panelGrid columns="1" border="1" width="750"
				rendered="#{contactDetail.contact != null}"
				style="margin-left:auto;margin-right:auto;">


				<af:panelForm>
					<f:subview id="contact">
						<jsp:include flush="true" page="../contacts/contact.jsp" />
					</f:subview>


					<af:objectSpacer height="20" />
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Delete" useWindow="true"
								windowWidth="700" windowHeight="500"
								action="#{contactDetail.deleteContact}"
								disabled="#{!empty contactDetail.contact.contactTypes or contactDetail.disabledUpdateButton}"
								rendered="#{!contactDetail.editable}" />
							<af:commandButton text="Edit"
								action="#{contactDetail.editContact}"
								disabled="#{contactDetail.disabledUpdateButton}"
								rendered="#{!contactDetail.editable}" />
							<af:commandButton text="Save"
								action="#{contactDetail.saveContactDetail}"
								rendered="#{contactDetail.editable}" />
							<af:commandButton text="Cancel"
								action="#{contactDetail.cancelEditContact}"
								rendered="#{contactDetail.editable && contactDetail.dapcUser}"
								immediate="true" />
							<af:commandButton text="Time Out All Current Associations"
								useWindow="true" windowWidth="700" windowHeight="500"
								rendered="#{contactDetail.dapcUser}"
								action="#{contactDetail.timeOutContact}"
								disabled="#{contactDetail.disabledUpdateButton || contactDetail.editable}" />
							<af:commandButton text="Merge Contact"
								action="#{mergeContact.submitDuplicateSearch}"
								rendered="#{contactDetail.dapcUser}"
								disabled="#{contactDetail.disabledUpdateButton || contactDetail.editable || !contactDetail.contact.active}"
								immediate="true">
								<t:updateActionListener property="#{mergeContact.baseContact}"
									value="#{contactDetail.contact}" />
							</af:commandButton>
							<af:commandButton text="Send Email" useWindow="true"
								windowWidth="650" windowHeight="300"
								disabled="#{contactDetail.readOnlyUser}"
								rendered="#{!contactDetail.editable}"
								action="#{contactDetail.setupEmail}" />

						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</h:panelGrid>
		</f:facet>

		<f:facet name="bottom">
		</f:facet>

	</af:panelBorder>
</h:panelGrid>

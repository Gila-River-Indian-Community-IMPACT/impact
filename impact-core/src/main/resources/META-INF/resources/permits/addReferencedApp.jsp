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
			<af:page var="foo" value="#{menuModel.model}" id="referencedAppPage"
				title="Add Application">
				<af:panelForm rows="2" maxColumns="1" labelWidth="44%" fieldWidth="56%">
					<afh:rowLayout halign="center">
						  <af:selectOneChoice label="Associated Submitted Application Numbers :" 
              				readOnly ="#{!permitDetail.editMode1}" value="#{permitDetail.newReferencedAppNumber}">
              <f:selectItems
                value="#{permitDetail.associatedSubmittedApplicationNumbers}" />	
            </af:selectOneChoice>
					</afh:rowLayout>
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Edit" shortDesc="#{permitDetail.editButtonText}"
								rendered="#{!permitDetail.editMode1 && !permitDetail.readOnlyUser && permitDetail.editAllowed}"
								action="#{permitDetail.editReferencedApp}" 
								disabled = "#{permitDetail.applicationNumbersToAssocaiteCount == '0'}"/>
							
							<af:commandButton text="Save" id="saveButton"
								rendered="#{permitDetail.editMode1}"
								action="#{permitDetail.addReferencedApp}" />
						<af:commandButton text="Cancel" id="cancelButton"
								rendered="#{permitDetail.editMode1 && permitDetail.editApplications}"
								action="#{permitDetail.cancelPopup}"  />
						<af:commandButton text="Delete" shortDesc="#{permitDetail.deleteButtonText}"
								rendered="#{!permitDetail.editMode1 && !permitDetail.readOnlyUser && permitDetail.editAllowed}"
								action="#{permitDetail.deleteReferencedApp}" 
								disabled="#{!permitDetail.allowedToDeleteReferencedApp}"/>
						<af:commandButton text="Close"
								rendered="#{!permitDetail.editMode1}"
								action="#{permitDetail.closeDialog}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>
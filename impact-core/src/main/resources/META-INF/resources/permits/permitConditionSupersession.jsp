<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="supersessionDetailBody" title="Supersession Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>

		<af:form>
			<af:messages />
			<af:panelHeader text="Superseded Condition Detail" size="0" />
			<af:panelForm rows="6" maxColumns="1" labelWidth="150">

				<af:panelHorizontal valign="top">
					<af:inputText id="plainSupersededPermit" readOnly="true"
						label="Permit Number :"
						rendered="#{!permitConditionDetail.newPermitConditionSupersession}"
						value="#{permitConditionDetail.conditionSupersession.supersededPermitNumber}" />
					<af:selectOneChoice autoSubmit="true" id="supersededPermit"
						label="Permit Number :"
						rendered="#{permitConditionDetail.newPermitConditionSupersession}"
						showRequired="true" unselectedLabel="Permit Number"
						valueChangeListener="#{permitConditionDetail.supersededPermitChanged}">
						<f:selectItems id="possibleSupersededPermits"
							value="#{permitConditionDetail.otherActivePermitsForFacility}" />
					</af:selectOneChoice>
				</af:panelHorizontal>
				
				<af:panelHorizontal valign="top"
					rendered="#{!permitConditionDetail.newPermitConditionSupersession}">
					<af:inputText  readOnly="true" label="Condition Id :"
						rendered="#{!permitConditionDetail.newPermitConditionSupersession}"
						id="supersededCondId"
						value="#{permitConditionDetail.conditionSupersession.supersededpcondId}" />
				</af:panelHorizontal>
				
				<af:panelHorizontal valign="top">
					<af:inputText readOnly="true" label="Condition Number :"
						rendered="#{!permitConditionDetail.newPermitConditionSupersession}"
						id="plainsupersededNbr"
						value="#{permitConditionDetail.conditionSupersession.supersededPermitCondiditionNumber}" />
					<af:selectManyListbox label="Condition Id(s) :"
						rendered="#{permitConditionDetail.newPermitConditionSupersession}"
						autoSubmit="false" id="supersededCondition" showRequired="true"
						partialTriggers="supersededPermit"
						value="#{permitConditionDetail.conditionSupersession.supersededPermitConditionIds}">
						<f:selectItems id="possibleSupersededConditions"
							value="#{permitConditionDetail.possibleSupersededConditions}" />
					</af:selectManyListbox>
				</af:panelHorizontal>
				
				<af:panelHorizontal valign="top">
					<af:selectOneChoice autoSubmit="false" label="Superseding Option :" 
						readOnly="#{!permitConditionDetail.newPermitConditionSupersession && !permitConditionDetail.editPermitConditionSupersession}"
						id="supersedingOption" showRequired="true"
						value="#{permitConditionDetail.conditionSupersession.supersedingOption}">
						<f:selectItems id="supersededOptionCd"
							value="#{permitReference.conditionSupersedenceStatusDefs.items[
									empty permitConditionDetail.conditionSupersession.supersedingOption 
									? '' : permitConditionDetail.conditionSupersession.supersedingOption]}" />
					</af:selectOneChoice>
				</af:panelHorizontal>

				<af:panelHorizontal valign="top">
					<af:inputText maximumLength="4000" label="Comments :"
						readOnly="#{!permitConditionDetail.newPermitConditionSupersession && !permitConditionDetail.editPermitConditionSupersession}"
						autoSubmit="false" id="supersededComments" columns="62" rows="40"
						value="#{permitConditionDetail.conditionSupersession.comments}">
					</af:inputText>
				</af:panelHorizontal>

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							rendered="#{!permitConditionDetail.newPermitConditionSupersession 
									&& !permitConditionDetail.editPermitConditionSupersession
									&& permitConditionDetail.permitConditionEditAllowed}"
							id="editSuperceded"
							action="#{permitConditionDetail.startToEditPermitConditionSupersession}" />
						<af:commandButton id="deleteSuperceded" text="Delete"
							useWindow="true" windowWidth="#{confirmWindow.width}"
							windowHeight="#{confirmWindow.height}"
							rendered="#{!permitConditionDetail.newPermitConditionSupersession 
									&& !permitConditionDetail.editPermitConditionSupersession
									&& permitConditionDetail.permitConditionEditAllowed}"
							shortDesc="#{permitDetail.editAllowed 
													? '' : 'Permit condition supersession cannot be deleted.' }"
							returnListener="#{permitConditionDetail.deletePermitConditionSupersession}"
							action="#{confirmWindow.confirm}">
							<t:updateActionListener property="#{confirmWindow.type}"
								value="#{confirmWindow.yesNo}" />
							<t:updateActionListener property="#{confirmWindow.message}"
								value="Click Yes to confirm the deletion of the permit condition supersession." />
						</af:commandButton>
						<af:commandButton text="Close"
							rendered="#{!permitConditionDetail.newPermitConditionSupersession 
							&& !permitConditionDetail.editPermitConditionSupersession}"
							id="closeSuperceded"
							action="#{permitConditionDetail.closePermitConditionSupersessionDialog}" />
						<af:commandButton text="Save"
							rendered="#{permitConditionDetail.newPermitConditionSupersession 
							|| permitConditionDetail.editPermitConditionSupersession}"
							id="saveSuperceded"
							action="#{permitConditionDetail.savePermitConditionSupersession}" />
						<af:commandButton text="Cancel"
							rendered="#{permitConditionDetail.newPermitConditionSupersession 
							|| permitConditionDetail.editPermitConditionSupersession}"
							id="cancelSuperceded"
							action="#{permitConditionDetail.cancelPermitConditionSupersessionDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>

			</af:panelForm>
		</af:form>


	</af:document>
</f:view>

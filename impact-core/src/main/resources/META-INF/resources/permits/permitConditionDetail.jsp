<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="permitConditionBody" title="Permit Condition Detail" >
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<f:verbatim><%@ include file="../scripts/tinymce.js"%></f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:inputHidden id="conditionTextPlain"
				value="#{permitConditionDetail.modifyPermitCondition.conditionTextPlain}" />
			<af:panelHeader text="Permit Condition Detail" size="0" />
			<t:div style="overflow:auto;height:800px">
				<af:panelForm rows="9" maxColumns="1" labelWidth="160">

					<af:inputText maximumLength="20" label="Condition ID :"
						rendered="#{!permitConditionDetail.editMode1}" readOnly="true"
						id="pcondId"
						value="#{permitConditionDetail.modifyPermitCondition.pcondId}"
						columns="20">
					</af:inputText>

					<af:inputText maximumLength="30" label="Condition Number :"
						showRequired="true" readOnly="#{!permitConditionDetail.editMode1}"
						id="permitConditionNumber"
						value="#{permitConditionDetail.modifyPermitCondition.permitConditionNumber}">
					</af:inputText>

					<af:selectOneChoice label="Condition Status :" showRequired="true"
						readOnly="#{!permitConditionDetail.editMode1}"
						id="permitConditionStatusCd"
						value="#{permitConditionDetail.modifyPermitCondition.permitConditionStatusCd}"
						unselectedLabel="">
						<mu:selectItems id="permitConditionStatusCd"
							value="#{permitReference.permitConditionStatusDefs.items[0]}" />
					</af:selectOneChoice>

					<af:selectManyListbox label="Category :"
						readOnly="#{!permitConditionDetail.editMode1}"
						id="permitConditionCategoryCds"
						value="#{permitConditionDetail.modifyPermitCondition.permitConditionCategoryCds}">
						<f:selectItems
							value="#{permitReference.permitCategoryDefs.items[
								(empty permitConditionDetail.modifyPermitCondition.permitConditionCategoryCds
										? '' : permitConditionDetail.modifyPermitCondition.permitConditionCategoryCds)]}" />
					</af:selectManyListbox>

					<af:panelForm rows="1" maxColumns="2" width="100px"
						labelWidth="140px" partialTriggers="facilityWide">
						<af:selectOneChoice id="facilityWide" label="Facility Wide? :"
							unselectedLabel=" "
							readOnly="#{!permitConditionDetail.editMode1}" autoSubmit="true"
							value="#{permitConditionDetail.modifyPermitCondition.facilityWide}">
							<f:selectItem itemLabel="Yes" itemValue="Y" />
							<f:selectItem itemLabel="No" itemValue="N" />
						</af:selectOneChoice>
						<af:selectManyShuttle size="15" id="associatedEUs"
							rendered="#{permitConditionDetail.modifyPermitCondition.facilityWide=='N'}"
							leadingHeader="Available Emission Units"
							trailingHeader="Associated Emission Units"
							disabled="#{!permitConditionDetail.editMode1}"
							value="#{permitConditionDetail.associatedEUs}">
							<f:selectItems value="#{permitConditionDetail.availableEUs}" />
						</af:selectManyShuttle>
					</af:panelForm>

					<af:inputText id="mceEditor" label="Condition Text :"
						styleClass="mceEditor" columns="80" rows="12"
						value="#{permitConditionDetail.modifyPermitCondition.conditionTextHTML}" />

					<afh:rowLayout halign="left" valign="top">
						<af:panelGroup
							rendered="#{!permitConditionDetail.newPermitCondition}">
							<af:objectSpacer width="10" height="1" />
							<f:subview id="permit_condition_compliance_event_list">
								<jsp:include flush="true"
									page="permitConditionComplianceEventList.jsp" />
							</f:subview>
						</af:panelGroup>
					</afh:rowLayout>

					<afh:rowLayout halign="left" valign="top">
						<af:panelGroup
							rendered="#{!permitConditionDetail.newPermitCondition}">
							<af:objectSpacer width="10" height="1" />
							<f:subview id="permit_condition_supersession_list">
								<jsp:include flush="true"
									page="permitConditionSupersessionList.jsp" />
							</f:subview>
						</af:panelGroup>
					</afh:rowLayout>

					<afh:rowLayout halign="left" valign="top">
						<af:panelGroup
							rendered="#{!permitConditionDetail.newPermitCondition}">
							<af:objectSpacer width="10" height="1" />
							<f:subview id="permit_condition_supersedence_list">
								<jsp:include flush="true"
									page="permitConditionSupersededByList.jsp" />
							</f:subview>
						</af:panelGroup>
					</afh:rowLayout>

				</af:panelForm>
			</t:div>

			<af:objectSpacer height="15" />

			<afh:rowLayout halign="center">
				<af:panelButtonBar>
					<af:commandButton id="editBtn" text="Edit"
						rendered="#{!permitConditionDetail.editMode1 
										&& permitConditionDetail.permitConditionEditAllowed}"
						action="#{permitConditionDetail.editPermitCondition}" />
					<af:commandButton id="deleteBtn" text="Delete"
						rendered="#{!permitConditionDetail.editMode1
									&& permitConditionDetail.permitConditionEditAllowed}"
						useWindow="true"
						disabled="#{!permitConditionDetail.allowedToDeletePermitCondition}"
						shortDesc="#{permitConditionDetail.allowedToDeletePermitCondition
													? '' : 'Cannot delete this Permit Condition since it is either Superseding / Superseded by other Permit Condition(s).' }"
						windowWidth="#{confirmWindow.width}"
						windowHeight="#{confirmWindow.height}"
						returnListener="#{permitConditionDetail.deletePermitCondition}"
						action="#{confirmWindow.confirm}">
						<t:updateActionListener property="#{confirmWindow.type}"
							value="#{confirmWindow.yesNo}" />
						<t:updateActionListener property="#{confirmWindow.message}"
							value="Click Yes to confirm the deletion of permit condition #{permitConditionDetail.modifyPermitCondition.pcondId}." />
					</af:commandButton>
					<af:commandButton id="closeBtn" text="Close" immediate="true"
						rendered="#{!permitConditionDetail.editMode1}"
						action="#{permitConditionDetail.closePermitConditionDialog}" />
					<af:commandButton id="saveBtn" text="Save"
						rendered="#{permitConditionDetail.editMode1}"
						onclick="setContentTextPlain()"
						action="#{permitConditionDetail.savePermitCondition}" />
					<af:commandButton id="cancelBtn" text="Cancel" immediate="true"
						rendered="#{permitConditionDetail.editMode1}"
						action="#{permitConditionDetail.cancelPermitCondition}" />
				</af:panelButtonBar>
			</afh:rowLayout>
		</af:form>

		<f:verbatim>
			<script type="text/javascript">
				function setContentTextPlain() {
					document.getElementById('conditionTextPlain').value = tinyMCE.activeEditor.getContent({format: 'text'});
				}
			</script>
		</f:verbatim>

	</af:document>
</f:view>
<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="complianceStatusHistoryBody"
		title="Compliance Status History">
		<af:form>
			<af:panelHeader text="Compliance Status History" size="0" />

			<af:panelForm rows="2" maxColumns="2" labelWidth="200">
				<afh:rowLayout halign="left">
					<af:inputText maximumLength="20" label="Permit Condition ID : "
						readOnly="true" id="pcondId"
						value="#{!permitConditionDetail.fromFacilityPermitConditionSearch 
									? permitConditionDetail.modifyPermitCondition.pcondId
									: permitConditionDetail.permitConditionSearchLineItem.pcondId}" 
						columns="35">
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:selectOneChoice label="Permit Type : " unselectedLabel=" "
				      value="#{!permitConditionDetail.fromFacilityPermitConditionSearch
				      			? permitConditionDetail.permit.permitType
				      			: permitConditionDetail.permitConditionSearchLineItem.permitTypeCd}" 
				      readOnly="true">
				      	<mu:selectItems value="#{permitReference.permitTypes}" />
				    </af:selectOneChoice>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:inputText maximumLength="35" label="Permit Condition Number : "
						readOnly="true" id="pcondNumber"
						value="#{!permitConditionDetail.fromFacilityPermitConditionSearch
									? permitConditionDetail.modifyPermitCondition.permitConditionNumber
									: permitConditionDetail.permitConditionSearchLineItem.permitConditionNumber}"
						columns="35">
					</af:inputText>
				</afh:rowLayout>

				<af:selectManyListbox label="Category :"
					rendered="#{!permitConditionDetail.fromFacilityPermitConditionSearch}"
					readOnly="true"
					id="permitConditionCategoryCds"
					value="#{permitConditionDetail.modifyPermitCondition.permitConditionCategoryCds}">
					<f:selectItems
						value="#{permitReference.permitCategoryDefs.items[
								(empty permitConditionDetail.modifyPermitCondition.permitConditionCategoryCds
										? '' : permitConditionDetail.modifyPermitCondition.permitConditionCategoryCds)]}" />
				</af:selectManyListbox>
				
				<af:selectManyListbox label="Category :"
					rendered="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
					readOnly="true"
					id="permitConditionCategoryCds2"
					value="#{permitConditionDetail.permitConditionSearchLineItem.permitConditionCategoryCds}">
					<f:selectItems
						value="#{permitReference.permitCategoryDefs.items[
								(empty permitConditionDetail.permitConditionSearchLineItem.permitConditionCategoryCds
										? '' : permitConditionDetail.permitConditionSearchLineItem.permitConditionCategoryCds)]}" />
				</af:selectManyListbox>

			</af:panelForm>

			<af:panelForm rows="9" maxColumns="1" labelWidth="140">
				<afh:rowLayout halign="left" valign="top">
					<af:panelGroup>
						<af:objectSpacer width="10" height="1" />

						<f:subview id="permit_condition_compliance_event_list">
							<jsp:include flush="true"
								page="permitConditionComplianceEventList.jsp" />
						</f:subview>

					</af:panelGroup>
				</afh:rowLayout>

				<af:objectSpacer height="20" />

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Close" immediate="true"
							action="#{permitConditionDetail.closeComplianceEventHistoryDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>

			</af:panelForm>
		</af:form>

	</af:document>
</f:view>
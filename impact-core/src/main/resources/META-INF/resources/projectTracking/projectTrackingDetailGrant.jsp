<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:objectSpacer height="10" />

<af:panelForm rows="1" maxColumns="2" labelWidth="250px" width="98%">
	<h:panelGrid columns="2" width="98%">
		<afh:cellFormat valign="top">
			<af:selectOneChoice id="outreachCategoryCd"
				label="Outreach Category: "
				readOnly="#{!projectTrackingDetail.editMode}"
				value="#{projectTrackingDetail.project.outreachCategoryCd}">
				<f:selectItems
					value="#{projectTrackingReference.outreachCategoryDefs.items[
						(empty projectTrackingDetail.project.outreachCategoryCd
								? '' : projectTrackingDetail.project.outreachCategoryCd)]}" />
			</af:selectOneChoice>
		</afh:cellFormat>
		<afh:cellFormat width="25%" valign="top">
			<af:table id="grantsAccountantUserIds" width="100%"
				value="#{projectTrackingDetail.grantAccountantsWrapper}"
				binding="#{projectTrackingDetail.grantAccountantsWrapper.table}"
				var="accountant" bandingInterval="1" banding="row">
				<f:facet name="selection">
					<af:tableSelectMany shortDesc="Select"
						rendered="#{projectTrackingDetail.editMode}" />
				</f:facet>
				<af:column sortable="false" formatType="text"
					headerText="Accountant Contact(s)">
					<af:selectOneChoice unselectedLabel="" value="#{accountant.value}"
						readOnly="#{!projectTrackingDetail.editMode}">
						<f:selectItems
							value="#{empty accountant.value 
										? projectTrackingDetail.grantAccountantUserDefs.items[0] 
										: infraDefs.allUsersDef.items[accountant.value]}" />
					</af:selectOneChoice>
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton id="addBtn"
								disabled="#{projectTrackingDetail.readOnlyUser}"
								rendered="#{projectTrackingDetail.editMode}" text="Add"
								action="#{projectTrackingDetail.project.addAccountant}" />
							<af:commandButton id="delBtn"
								disabled="#{projectTrackingDetail.readOnlyUser}"
								rendered="#{projectTrackingDetail.editMode}"
								text="Delete Selected"
								action="#{projectTrackingDetail.deleteGrantAccountants}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</afh:cellFormat>
	</h:panelGrid>
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="250px" width="98%">
	<af:selectOneChoice id="grantStatusCd" label="Grant Status: "
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.grantStatusCd}">
		<f:selectItems
			value="#{projectTrackingReference.grantStatusDefs.items[
						(empty projectTrackingDetail.project.grantStatusCd
								? '' : projectTrackingDetail.project.grantStatusCd)]}" />
	</af:selectOneChoice>
	<af:inputText id="totalAmountRO" label="Total Dollar Amount: "
		rendered="#{!projectTrackingDetail.editMode}" readOnly="true"
		value="#{projectTrackingDetail.project.totalAmount}">
		<af:convertNumber type='currency' locale="en-US" minFractionDigits="2" />
	</af:inputText>
	<af:inputText id="totalAmount" label="Total Dollar Amount: "
		rendered="#{projectTrackingDetail.editMode}" readOnly="false"
		columns="15" maximumLength="14"
		value="#{projectTrackingDetail.project.totalAmount}">
		<f:converter 
			converterId="us.oh.state.epa.stars2.webcommon.converter.BigDecimalConverter" /> 
	</af:inputText>
</af:panelForm>
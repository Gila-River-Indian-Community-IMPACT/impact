<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:objectSpacer height="10" />

<af:panelForm rows="1" maxColumns="2" labelWidth="250px" width="98%">
	<af:selectOneChoice id="contractTypeCd" label="Contract Type: "
		unselectedLabel=""
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.contractTypeCd}">
		<f:selectItems
			value="#{projectTrackingReference.contractTypeDefs.items[
						(empty projectTrackingDetail.project.contractTypeCd
								? '' : projectTrackingDetail.project.contractTypeCd)]}" />
	</af:selectOneChoice>
	<af:inputText id="vendorName" label="Vendor Name: "
	 	readOnly="#{!projectTrackingDetail.editMode}"
	 	columns="50" maximumLength="250" 
	 	rows="#{projectTrackingDetail.vendorNameRows}"
		value="#{projectTrackingDetail.project.vendorName}" />
	<af:inputText id="vendorNumber" label="Vendor Number: "
	 	readOnly="#{!projectTrackingDetail.editMode}"
	 	columns="20" maximumLength="18" 
		tip="VC followed by up to 16-digits"
		value="#{projectTrackingDetail.project.vendorNumber}" />	
	<af:selectOneChoice id="contractStatusCd" label="Contract Status: "
		unselectedLabel=""
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.contractStatusCd}">
		<f:selectItems
			value="#{projectTrackingReference.contractStatusDefs.items[
						(empty projectTrackingDetail.project.contractStatusCd
								? '' : projectTrackingDetail.project.contractStatusCd)]}" />
	</af:selectOneChoice>
	<af:selectInputDate id="contractExpirationDate" label="Contract Expiration Date: "
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.contractExpirationDate}" />
	<af:selectInputDate id="monitoringOperationsEndDate"
		label="Monitoring Operations End Date: "
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.monitoringOperationsEndDate}" />

	<%/* second column */%>

	<af:inputText id="totalAwardRO" label="Total Award: "
		rendered="#{!projectTrackingDetail.editMode}" readOnly="true"
		value="#{projectTrackingDetail.project.totalAward}">
		<af:convertNumber type='currency' locale="en-US" minFractionDigits="2" />
	</af:inputText>
	<af:inputText id="totalAward" label="Total Award: "
		rendered="#{projectTrackingDetail.editMode}" readOnly="false"
		columns="15" maximumLength="14"
		value="#{projectTrackingDetail.project.totalAward}">
		<f:converter 
			converterId="us.oh.state.epa.stars2.webcommon.converter.BigDecimalConverter" /> 
	</af:inputText>
	<af:inputText id="MSANumber" label="MSA Number: "
		readOnly="#{!projectTrackingDetail.editMode}"
		columns="15" maximumLength="12"
		value="#{projectTrackingDetail.project.MSANumber}" />
	<af:selectOneChoice id="OCIOApproval" label="OCIO Approval: "
		unselectedLabel=""
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.OCIOApproval}" >
		<f:selectItem itemValue="Y" itemLabel="Yes" />
		<f:selectItem itemValue="N" itemLabel="No" />
	</af:selectOneChoice>			
	<af:inputText id="contractNumber" label="CON Number: "
		readOnly="#{!projectTrackingDetail.editMode}"
		columns="15" maximumLength="13"
		tip="CON-XXXX-XXXX"
		value="#{projectTrackingDetail.project.contractNumber}" />
	<af:inputText id="AGHeatTicketNumber" label="AG Heat Ticket Number: "
		readOnly="#{!projectTrackingDetail.editMode}"
		columns="15" maximumLength="10"
		value="#{projectTrackingDetail.project.AGHeatTicketNumber}" />
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="250px" width="98%">
	<h:panelGrid columns="2" width="98%">
		<afh:cellFormat width="40%" valign="top">
			<af:outputLabel value="Budget: " />
			<af:table id="budgetList" width="100%"
				value="#{projectTrackingDetail.project.budgetList}"
				var="budget" varStatus="v" 
				bandingInterval="1" banding="row">
				<af:column id="edit" formatType="text" headerText="Row Id"
					width="5%" noWrap="true">
					<af:panelHorizontal valign="middle" halign="left">
						<af:commandLink useWindow="true" windowWidth="375"
							windowHeight="400" inlineStyle="padding-left:5px;"
							action="#{projectTrackingDetail.startToViewBudget}">
							<af:inputText value="#{v.index+1}"
								readOnly="true" valign="middle">
								<af:convertNumber pattern="000" />
							</af:inputText>
							<t:updateActionListener 
								property="#{projectTrackingDetail.budget}"
								value="#{budget}" />
						</af:commandLink>
					</af:panelHorizontal>
				</af:column>

				<af:column formatType="text" headerText="BFY"
					width="10%"
					sortable="true" sortProperty="BFY" >
					<af:selectOneChoice unselectedLabel="" value="#{budget.BFY}"
						readOnly="true">
						<f:selectItems value="#{infraDefs.oddYears}" />
					</af:selectOneChoice>
				</af:column>
				<af:column formatType="text" headerText="Function"
					width="15%"
					sortable="true" sortProperty="budgetFunctionCd" >
					<af:selectOneChoice value="#{budget.budgetFunctionCd}"
						readOnly="true">
						<f:selectItems
							value="#{empty budget.budgetFunctionCd
										? projectTrackingReference.budgetFunctionDefs.items[0] 
										: projectTrackingReference.budgetFunctionDefs.items[budget.budgetFunctionCd]}" />
					</af:selectOneChoice>
				</af:column>
				<af:column formatType="number" headerText="Amount"
					width="15%"
					sortable="true" sortProperty="amount" >
					<af:inputText id="amount" label="Amount: "
						readOnly="true"
						value="#{budget.amount}">
						<af:convertNumber type='currency' locale="en-US"
							minFractionDigits="2" />
					</af:inputText>
					
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton id="addBudgetBtn"
								text="Add"
								disabled="#{projectTrackingDetail.readOnlyUser}"
								useWindow="true" windowWidth="375"
								windowHeight="400" inlineStyle="padding-left:5px;"
								action="#{projectTrackingDetail.startToAddBudget}" />
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</afh:cellFormat>
		<afh:cellFormat width="30%" valign="top">
			<af:table id="contractAccountantUserIds" width="100%"
				value="#{projectTrackingDetail.contractAccountantsWrapper}"
				binding="#{projectTrackingDetail.contractAccountantsWrapper.table}"
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
										? projectTrackingDetail.contractAccountantUserDefs.items[0] 
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
								action="#{projectTrackingDetail.deleteContractAccountants}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</afh:cellFormat>
	</h:panelGrid>
</af:panelForm>
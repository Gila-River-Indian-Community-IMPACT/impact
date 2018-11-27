<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table emptyText=" " value="#{permitSearch.permits}" width="99%"
	var="permit" rows="#{permitSearch.pageLimit}">
	<af:column headerText="Permit Number" sortable="true"
		sortProperty="permitNumber" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:commandLink text="#{permit.permitNumber}"
				action="#{permitDetail.loadPermit}">
				<af:setActionListener to="#{permitDetail.permitID}"
					from="#{permit.permitID}" />
				<t:updateActionListener property="#{permitDetail.fromTODOList}"
					value="false" />
			</af:commandLink>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Facility ID" sortable="true"
		sortProperty="facilityId" formatType="text"
		rendered="#{permitSearch.fromFacility == 'false'}">
		<af:panelHorizontal valign="middle" halign="center">
			<af:commandLink text="#{permit.facilityId}"
				action="#{facilityProfile.submitProfile}"
				inlineStyle="white-space: nowrap;">
				<t:updateActionListener property="#{facilityProfile.fpId}"
					value="#{permit.fpId}" />
				<t:updateActionListener property="#{menuItem_facProfile.disabled}"
					value="false" />
			</af:commandLink>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Facility Name" sortable="true"
		sortProperty="facilityNm"
		rendered="#{permitSearch.fromFacility == 'false'}" noWrap="false">
		<af:panelHorizontal valign="middle" halign="left">
			<af:inputText readOnly="true" value="#{permit.facilityNm}" />
		</af:panelHorizontal>
	</af:column>
	<af:column sortProperty="cmpId" sortable="true" formatType="text"
		headerText="Company ID">
		<af:panelHorizontal valign="middle" halign="left">
			<af:commandLink action="#{companyProfile.submitProfile}"
				text="#{permit.cmpId}">
				<t:updateActionListener property="#{companyProfile.cmpId}"
					value="#{permit.cmpId}" />
				<t:updateActionListener
					property="#{menuItem_companyProfile.disabled}" value="false" />
			</af:commandLink>
		</af:panelHorizontal>
	</af:column>
	<af:column sortProperty="companyName" sortable="true" formatType="text"
		headerText="Company Name" noWrap="false">
		<af:panelHorizontal valign="middle" halign="left">
			<af:outputText value="#{permit.companyName}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Type" sortable="true" sortProperty="permitType"
		formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectOneChoice unselectedLabel=" " readOnly="true"
				value="#{permit.permitType}">
				<mu:selectItems value="#{permitReference.permitTypes}" />
			</af:selectOneChoice>  
		</af:panelHorizontal>
	</af:column>

	<af:column sortProperty="actionType" sortable="true" formatType="text"
		headerText="Action">
		<af:panelHorizontal valign="middle" halign="left">
		<af:outputText
			value="#{permitReference.permitActionTypeDefs.itemDesc[(empty permit.actionType ? '' : permit.actionType)]}" />
		</af:panelHorizontal>
	</af:column>

	<af:column headerText="Publication Stage" sortable="true"
		sortProperty="permitGlobalStatusCD" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectOneChoice label="Permit status"
				value="#{permit.permitGlobalStatusCD}" readOnly="true" id="soc2">
				<mu:selectItems value="#{permitReference.permitGlobalStatusDefs}"
					id="soc2" />
			</af:selectOneChoice>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Reason(s)" sortable="true"
		sortProperty="primaryReasonCD">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectManyCheckbox label="Reason(s) :" valign="top"
				readOnly="true" value="#{permit.permitReasonCDs}">
				<f:selectItems value="#{permitSearch.allPermitReasons}" />
			</af:selectManyCheckbox>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Public Notice Date" sortable="true"
		sortProperty="publicNoticePublishDate" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.publicNoticePublishDate}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Final Issuance Date" sortable="true"
		sortProperty="finalIssueDate" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.finalIssueDate}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Last Invoice Ref. Date" sortable="true"
		sortProperty="lastInvoiceRefDate" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.lastInvoiceRefDate}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Initial Invoice Amount" sortable="true"
		sortProperty="initialInvoiceAmount" formatType="number">
			<af:outputText value="#{permit.initialInvoiceAmount}">
				<af:convertNumber locale="en-US" type="currency" minFractionDigits="2"/>
			</af:outputText>
	</af:column>
	<af:column headerText="Final Invoice Amount" sortable="true"
		sortProperty="finalInvoiceAmount" formatType="number">
			<af:outputText value="#{permit.finalInvoiceAmount}">
				<af:convertNumber locale="en-US" type="currency" minFractionDigits="2"/>
			</af:outputText>	
	</af:column>
	<af:column headerText="Total Charges" sortable="true"
		sortProperty="totalCharges" formatType="number">
			<af:outputText value="#{permit.totalCharges}">
				<af:convertNumber locale="en-US" type="currency" minFractionDigits="2"/>
			</af:outputText>	
	</af:column>
	<af:column headerText="Total Payments and Credits" sortable="true"
		sortProperty="totalPayments" formatType="number">
			<af:outputText value="#{permit.totalPayments}">
				<af:convertNumber locale="en-US" type="currency" minFractionDigits="2"/>
			</af:outputText>	
	</af:column>
	<af:column headerText="Current Balance" sortable="true"
		sortProperty="currentBalance" formatType="number">
			<af:outputText value="#{permit.currentBalance}">
				<af:convertNumber locale="en-US" type="currency" minFractionDigits="2"/>
			</af:outputText>
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add Permit" useWindow="true"
					windowWidth="650" windowHeight="450" action="dialog:newPermit"
					rendered="#{permitSearch.fromFacility == 'true' && permitSearch.addPermitAble}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>


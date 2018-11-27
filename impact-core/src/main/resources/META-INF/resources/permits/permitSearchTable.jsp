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
		sortProperty="permitNumber" formatType="text" width="60px">
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
		headerText="Company ID" width="80px">
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
		headerText="Company Name" noWrap="false" width="105px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:outputText value="#{permit.companyName}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Legacy Permit No." sortable="true"
		sortProperty="legacyPermitNumber" formatType="text" width="70px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:commandLink text="#{permit.legacyPermitNumber}"
				action="#{permitDetail.loadPermit}">
				<af:setActionListener to="#{permitDetail.permitID}"
					from="#{permit.permitID}" />
				<t:updateActionListener property="#{permitDetail.fromTODOList}"
					value="false" />
			</af:commandLink>
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
	<af:column headerText="Permit Status" sortable="true"
		sortProperty="permitLevelStatusCd" formatType="text" width="55px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectOneChoice label="Permit status"
				value="#{permit.permitLevelStatusCd}" readOnly="true">
				<f:selectItems value="#{permitReference.permitLevelStatusDefs.items[
									(empty permit.permitLevelStatusCd
									? '' : permit.permitLevelStatusCd)]}" />
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
		sortProperty="permitGlobalStatusCD" formatType="text" width="75px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectOneChoice label="Permit status"
				value="#{permit.permitGlobalStatusCD}" readOnly="true" id="soc2">
				<mu:selectItems value="#{permitReference.permitGlobalStatusDefs}"
					id="soc2" />
			</af:selectOneChoice>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Reason(s)" sortable="true"
		sortProperty="primaryReasonCD" width="55px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectManyCheckbox label="Reason(s) :" valign="top"
				readOnly="true" value="#{permit.permitReasonCDs}">
				<f:selectItems value="#{permitSearch.allPermitReasons}" />
			</af:selectManyCheckbox>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Public Notice Date" sortable="true"
		sortProperty="publicNoticePublishDate" formatType="text" width="60px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.publicNoticePublishDate}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Expiration Date" sortable="true"
		sortProperty="expirationDate" formatType="text" width="60px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.expirationDate}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Permit Sent Out Date" sortable="true"
		sortProperty="permitSentOutDate" formatType="text" width="55px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.permitSentOutDate}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Effective Date" sortable="true"
		sortProperty="effectiveDate" formatType="text" width="55px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.effectiveDate}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Final Issuance Date" sortable="true"
		sortProperty="finalIssueDate" formatType="text" width="55px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.finalIssueDate}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Permit Basis Date" sortable="true"
		sortProperty="permitBasisDt" formatType="text" width="55px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.permitBasisDt}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Rescission Date" sortable="true"
		sortProperty="recissionDate" formatType="text" width="65px">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.recissionDate}" />
		</af:panelHorizontal>
	</af:column>
	<af:column sortProperty="description" sortable="true"
		formatType="text" headerText="Description" noWrap="false">
		<af:panelHorizontal valign="middle" halign="left">
			<af:outputText truncateAt="300" 
			value="#{permit.description}" 
			shortDesc="#{permit.description}"/>
		</af:panelHorizontal>
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add Permit" useWindow="true"
					windowWidth="650" windowHeight="750" action="dialog:newPermit"
					rendered="#{permitSearch.fromFacility == 'true' && permitSearch.addPermitAble}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>


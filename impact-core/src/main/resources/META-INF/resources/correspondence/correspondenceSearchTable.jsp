<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table emptyText=" " value="#{correspondenceSearch.resultsWrapper}"
	binding="#{correspondenceSearch.resultsWrapper.table}" width="98%"
	bandingInterval="1" banding="row" var="correspondenceList"
	rows="#{correspondenceSearch.pageLimit}">
	<af:column sortProperty="correspondenceID" sortable="true" formatType="text"
		headerText="Correspondence ID" width="80px">
		<af:commandLink action="#{correspondenceDetail.submitFromJsp}">
			<af:outputText value="#{correspondenceList.corId}" />
			<t:updateActionListener
				property="#{correspondenceDetail.correspondenceID}"
				value="#{correspondenceList.correspondenceID}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="facilityID" sortable="true" formatType="text"
		headerText="Facility ID"
		rendered="#{correspondenceSearch.fromFacility == 'false'}">
		<af:commandLink action="#{facilityProfile.submitProfile}"
			text="#{correspondenceList.facilityID}">
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{correspondenceList.fpId}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="facilityNm" sortable="true" formatType="text"
		headerText="Facility Name"
		rendered="#{correspondenceSearch.fromFacility == 'false'}">
		<af:outputText value="#{correspondenceList.facilityNm}" />
	</af:column>
	<af:column sortProperty="companyId" sortable="true" formatType="text"
		headerText="Company ID"
		rendered="#{correspondenceSearch.fromFacility == 'false'}">
		<af:commandLink action="#{companyProfile.submitProfile}"
			text="#{correspondenceList.companyId}">
			<t:updateActionListener property="#{companyProfile.cmpId}"
				value="#{correspondenceList.companyId}" />
			<t:updateActionListener
				property="#{menuItem_companyProfile.disabled}" value="false" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="companyName" sortable="true" formatType="text"
		headerText="Company Name"
		rendered="#{correspondenceSearch.fromFacility == 'false'}">
		<af:outputText value="#{correspondenceList.companyName}" />
	</af:column>
	<af:column sortProperty="district" sortable="true" formatType="text" rendered="#{correspondenceSearch.fromFacility == 'false' && infraDefs.districtVisible}"
		headerText="District" width="60px">
		<af:selectOneChoice
			readOnly="true"
			value="#{correspondenceList.district}">
			<f:selectItems
				value="#{infraDefs.districts}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="countyNm" sortable="true" formatType="text" rendered="#{correspondenceSearch.fromFacility == 'false'}"
		headerText="County">
		<af:selectOneChoice
			readOnly="true"
			value="#{correspondenceList.countyCd}">
			<f:selectItems
				value="#{correspondenceSearch.counties}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="cityNm" sortable="true" formatType="text" rendered="#{correspondenceSearch.fromFacility == 'false'}"
		headerText="City">
		<af:selectOneChoice
			readOnly="true"
			value="#{correspondenceList.cityCd}">
			<f:selectItems
				value="#{correspondenceSearch.cities}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="directionCd" sortable="true" formatType="text"
		headerText="Correspondence Direction" width="170px">
		<af:selectOneChoice
			readOnly="true"
			value="#{correspondenceList.directionCd}">
			<f:selectItems
				value="#{correspondenceSearch.correspondenceDirectionDef}" />
		</af:selectOneChoice>
	</af:column>	
	<af:column sortProperty="correspondenceTypeDescription" sortable="true" formatType="text"
		headerText="Correspondence Type" width="145px">
		<af:outputText
			value="#{correspondenceList.correspondenceTypeDescription}" />
	</af:column>
	<af:column sortProperty="correspondenceCategoryDesc" sortable="true" formatType="text"
		headerText="Correspondence Category" width="80px">
		<af:selectOneChoice
			readOnly="true"
			value="#{correspondenceList.correspondenceCategoryCd}">
			<f:selectItems
				value="#{correspondenceSearch.correspondenceCategoryDef}" />
		</af:selectOneChoice>
	</af:column>	
	<af:column sortProperty="dateGenerated" sortable="true" formatType="text"
		headerText="Date Generated" width="62px">
		<af:selectInputDate readOnly="true" value="#{correspondenceList.dateGenerated}" />
	</af:column>
	<af:column sortProperty="receiptDate" sortable="true" formatType="text"
		headerText="Receipt Date" width="62px">
		<af:selectInputDate readOnly="true" value="#{correspondenceList.receiptDate}" />
	</af:column>
	<af:column sortProperty="additionalInfo" sortable="true" formatType="text"
		headerText="Additional Info" width="157px">
		<af:outputText truncateAt="25" 
			value="#{correspondenceList.additionalInfo}" 
			shortDesc="#{correspondenceList.additionalInfo}"/>
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="New Correspondence"				  
				   action="#{createCorrespondence.refreshCreateCorrespondence}"
				   disabled="#{correspondenceDetail.readOnlyUser}"
				   rendered="#{correspondenceDetail.internalApp && correspondenceSearch.fromLinkedTo != 'enf' && correspondenceSearch.fromFacility == 'true'}">
				 <t:updateActionListener value="#{correspondenceSearch.facilityId}" 
				      property="#{createCorrespondence.facilityId}"/>  
			</af:commandButton>	   
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>

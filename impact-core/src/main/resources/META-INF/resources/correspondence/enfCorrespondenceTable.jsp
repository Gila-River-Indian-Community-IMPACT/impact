<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table emptyText=" " value="#{correspondenceSearch.enfResultsWrapper}"
	binding="#{correspondenceSearch.enfResultsWrapper.table}" width="98%"
	bandingInterval="1" banding="row" var="correspondenceList"
	rows="#{correspondenceSearch.pageLimit}">
	<af:column sortProperty="correspondenceID" sortable="true" formatType="text"
		headerText="Correspondence ID">
		<af:commandLink action="#{correspondenceDetail.submitFromJsp}">
			<af:outputText value="#{correspondenceList.corId}" />
			<t:updateActionListener
				property="#{correspondenceDetail.correspondenceID}"
				value="#{correspondenceList.correspondenceID}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="directionCd" sortable="true" formatType="text"
		headerText="Correspondence Direction">
		<af:selectOneChoice
			readOnly="true"
			value="#{correspondenceList.directionCd}">
			<f:selectItems
				value="#{correspondenceSearch.correspondenceDirectionDef}" />
		</af:selectOneChoice>
	</af:column>	
	<af:column sortProperty="correspondenceTypeDescription" sortable="true" formatType="text"
		headerText="Correspondence Type">
		<af:outputText
			value="#{correspondenceList.correspondenceTypeDescription}" />
	</af:column>
	<af:column sortProperty="correspondenceCategoryCd" sortable="true" formatType="text"
		headerText="Correspondence Category">
		<af:selectOneChoice
			readOnly="true"
			value="#{correspondenceList.correspondenceCategoryCd}">
			<f:selectItems
				value="#{correspondenceSearch.correspondenceCategoryDef}" />
		</af:selectOneChoice>
	</af:column>	
	<af:column sortProperty="dateGenerated" sortable="true" formatType="text"
		headerText="Date Generated">
		<af:selectInputDate readOnly="true" value="#{correspondenceList.dateGenerated}" />
	</af:column>
	<af:column sortProperty="receiptDate" sortable="true" formatType="text"
		headerText="Receipt Date">
		<af:selectInputDate readOnly="true" value="#{correspondenceList.receiptDate}" />
	</af:column>
	<af:column sortProperty="additionalInfo" sortable="true" formatType="text"
		headerText="Additional Info">
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

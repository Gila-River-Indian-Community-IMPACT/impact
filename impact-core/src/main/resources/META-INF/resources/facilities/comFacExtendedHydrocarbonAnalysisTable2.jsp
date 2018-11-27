<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm>
	<af:table id="hydrocarbonAnalysisTable2"
		value="#{facilityProfile.hydrocarbonAnalysisSampleDetailWrapper}" rows="0"
		bandingInterval="1" banding="row" var="hcSampleDetailRow" width="80%"
		binding="#{facilityProfile.hydrocarbonAnalysisSampleDetailWrapper.table}">
		
		<f:facet name="header">
        	<af:outputText value="Hydrocarbon Analysis Sample Detail"/>
      	</f:facet>
		
		<af:column id="sampleDetail" headerText=" " sortable="false"
			width="150" formatType="text">
			<af:outputText value="#{hcSampleDetailRow.rowDesc}" />
		</af:column>
		
		<af:column id="gas" headerText="Gas" sortable="false" width="100" formatType="text">
			<af:inputText value="#{hcSampleDetailRow.gasValue}" rendered="#{!hcSampleDetailRow.dateRow}" 
				readOnly="#{!facilityProfile.editable}" columns="20"  maximumLength="#{hcSampleDetailRow.maximumLength}" />
			<af:selectInputDate value="#{hcSampleDetailRow.gasValue }" rendered="#{hcSampleDetailRow.dateRow}"
				readOnly="#{!facilityProfile.editable}" columns="10">
				<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
			</af:selectInputDate>
		</af:column>
	
		<af:column id="oil" headerText="Oil/Condensate" sortable="false" width="100" formatType="text">
			<af:inputText value="#{hcSampleDetailRow.oilValue}" rendered="#{!hcSampleDetailRow.dateRow}"
				readOnly="#{!facilityProfile.editable}" columns="20" maximumLength="#{hcSampleDetailRow.maximumLength}" />
			<af:selectInputDate value="#{hcSampleDetailRow.oilValue }" rendered="#{hcSampleDetailRow.dateRow}"
				readOnly="#{!facilityProfile.editable}" columns="10">
				<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
			</af:selectInputDate>
		</af:column>
	
		<af:column id="producedWater" headerText="Produced Water" sortable="false" width="100" formatType="text">
			<af:inputText value="#{hcSampleDetailRow.waterValue}" rendered="#{!hcSampleDetailRow.dateRow}"
				readOnly="#{!facilityProfile.editable}" columns="20" maximumLength="#{hcSampleDetailRow.maximumLength}" />
			<af:selectInputDate value="#{hcSampleDetailRow.waterValue }" rendered="#{hcSampleDetailRow.dateRow}"
				readOnly="#{!facilityProfile.editable}" columns="10">
				<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
			</af:selectInputDate>
		</af:column>	
		
		<f:facet name="footer">
			<h:panelGrid width="100%">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton actionListener="#{tableExporter.printTable}"
							onclick="#{tableExporter.onClickScript}" text="Printable view" />
						<af:commandButton actionListener="#{tableExporter.excelTable}"
							onclick="#{tableExporter.onClickScript}" text="Export to excel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</h:panelGrid>
		</f:facet>
	</af:table>
</af:panelForm>
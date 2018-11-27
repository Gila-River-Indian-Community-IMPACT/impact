<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm>
	<af:table id="hydrocarbonAnalysisTable1"
		value="#{facilityProfile.hydrocarbonAnalysisWrapper}" rows="0"
		bandingInterval="1" banding="row" var="hcAnalysisPollutant" width="80%"
		binding="#{facilityProfile.hydrocarbonAnalysisWrapper.table}">
		
		<f:facet name="header">
        	<af:outputText value="Extended Hydrocarbon Analysis"/>
      	</f:facet>
		
		<af:column id="component" headerText="Component (mol%)" sortable="false"
			width="150" formatType="text">
			<af:outputText value="#{hcAnalysisPollutant.pollutantDesc}" />
		</af:column>
		
		<af:column id="gas" headerText="Gas" sortable="false" width="100" formatType="number">
			<af:inputText value="#{hcAnalysisPollutant.gas}" 
				readOnly="#{!facilityProfile.editable || hcAnalysisPollutant.readOnly}" columns="10" maximumLength="10"/>
		</af:column>
	
		<af:column id="oil" headerText="Oil/Condensate" sortable="false" width="100" formatType="number">
			<af:inputText value="#{hcAnalysisPollutant.oil}" 
				readOnly="#{!facilityProfile.editable || hcAnalysisPollutant.readOnly}" columns="10" maximumLength="10"/>
		</af:column>
	
		<af:column id="producedWater" headerText="Produced Water" sortable="false" width="100" formatType="number">
			<af:inputText value="#{hcAnalysisPollutant.producedWater}" 
				readOnly="#{!facilityProfile.editable || hcAnalysisPollutant.readOnly}" columns="10" maximumLength="10"/>
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

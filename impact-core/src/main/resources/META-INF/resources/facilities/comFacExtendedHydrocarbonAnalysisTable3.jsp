<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm>
	<af:table id="hydrocarbonAnalysisTable3"
		value="#{facilityProfile.decanePropertiesWrapper}"
		bandingInterval="1" banding="row" var="property" width="80%"
		binding="#{facilityProfile.decanePropertiesWrapper.table}">
		
		<f:facet name="header">
        	<af:outputText value="Decanes+ Sample Properties"/>
      	</f:facet>
		
		<af:column id="label" headerText=""
			sortable="false" width="120" formatType="text">
			<af:outputText value="#{property.label}" />
		</af:column>
		
		<af:column id="oilCondensate" headerText="Oil/Condensate" 
			sortable="false" width="60" formatType="number">
			<af:inputText 
				readOnly="#{!facilityProfile.editable}" 
				columns="10" maximumLength="10"
				value="#{property.oilCondenstanteValue}" >
			</af:inputText>		
		</af:column>
	
		<af:column id="producedWater" headerText="Produced Water" 
			sortable="false" width="60" formatType="number">
			<af:inputText 
				readOnly="#{!facilityProfile.editable}"
				columns="10" maximumLength="10"
				value="#{property.producedWaterValue}" >
			</af:inputText>			 
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

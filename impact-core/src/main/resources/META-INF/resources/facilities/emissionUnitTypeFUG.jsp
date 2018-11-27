<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm>
	<af:selectBooleanCheckbox id="fugLeaks" autoSubmit="true"
             label="Fugitive Emission Type - Fugitive Leaks at Oil and Gas Sites:"
             value="#{facilityProfile.emissionUnit.emissionUnitType.fugitiveLeaks}"
             readOnly="#{!facilityProfile.editable}"/>
</af:panelForm>

	<af:panelForm partialTriggers="fugLeaks">
		<af:outputLabel value="Total Count of Component: " rendered="#{facilityProfile.emissionUnit.emissionUnitType.fugitiveLeaks}"/>
		<af:table id="componentCountTable" 
			value="#{facilityProfile.fugComponentsWrapper}" rendered="#{facilityProfile.emissionUnit.emissionUnitType.fugitiveLeaks}"
			bandingInterval="1" banding="row" var="component" 
			binding="#{facilityProfile.fugComponentsWrapper.table}">
			<af:column id="componentCd" headerText="Component" noWrap="true"
				sortable="false" width="120" formatType="text">
				<af:outputText value="#{component.componentName}" />
			</af:column>
			
			<af:column id="gas" headerText="Gas" noWrap="true"
				sortable="false" width="100" formatType="number">
				<af:inputText value="#{component.gas}" showRequired="true" columns="7" maximumLength="7"
					readOnly="#{!facilityProfile.editable}" >
					<af:convertNumber type='number' locale="en-US" pattern="###,###" />
					<f:validateLongRange minimum="0" maximum="999999" /> 
				</af:inputText>
			</af:column>
			
			<af:column id="heavyOil" headerText="#{infraDefs.fugComponentHOHeader}" noWrap="true"
				sortable="false" width="130" formatType="number">
				<af:inputText value="#{component.heavyOil}" showRequired="true" columns="7" maximumLength="7"
					readOnly="#{!facilityProfile.editable}" >
					<af:convertNumber type='number' locale="en-US" pattern="###,###" />
					<f:validateLongRange minimum="0" maximum="999999" /> 
				</af:inputText>
			</af:column>
	
			<af:column id="lightOil" headerText="#{infraDefs.fugComponentLOHeader}" noWrap="true"
				sortable="false" width="130" formatType="number">
				<af:inputText value="#{component.lightOil}" showRequired="true" columns="7" maximumLength="7"
					readOnly="#{!facilityProfile.editable}" >
					<af:convertNumber type='number' locale="en-US" pattern="###,###" />
					<f:validateLongRange minimum="0" maximum="999999" /> 
				</af:inputText>
			</af:column>
			
			<af:column id="water" headerText="Water/Condensate" noWrap="true"
				sortable="false" width="130" formatType="number">
				<af:inputText value="#{component.water}" showRequired="true" columns="7" maximumLength="7"
					readOnly="#{!facilityProfile.editable}" >
					<af:convertNumber type='number' locale="en-US" pattern="###,###" />
					<f:validateLongRange minimum="0" maximum="999999" /> 
				</af:inputText>
			</af:column>	
			<f:facet name="footer">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton actionListener="#{tableExporter.printTable}"
							onclick="#{tableExporter.onClickScript}" text="Printable view" />
						<af:commandButton actionListener="#{tableExporter.excelTable}"
							onclick="#{tableExporter.onClickScript}" text="Export to excel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>						
		</af:table>
	</af:panelForm>

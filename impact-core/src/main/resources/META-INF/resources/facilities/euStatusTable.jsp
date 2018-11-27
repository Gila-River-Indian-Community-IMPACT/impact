<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center" rendered="#{euStatusSearch.hasSearchResults}">
	<h:panelGrid border="1">
		<af:panelBorder>
			<af:table value="#{euStatusSearch.eus}" bandingInterval="1"
				banding="row" var="emissionUnit">
				<af:column sortProperty="facilityId" sortable="true" noWrap="true"
					formatType="text" headerText="Facility Id">
					<af:commandLink action="#{facilityProfile.submitProfile}"
						text="#{emissionUnit.facilityId}">
						<t:updateActionListener property="#{facilityProfile.fpId}"
							value="#{emissionUnit.fpId}" />
						<t:updateActionListener property="#{menuItem_facProfile.disabled}"
							value="false" />
					</af:commandLink>
				</af:column>
				<af:column sortProperty="facilityName" sortable="true" formatType="text"
					headerText="Facility Name">
					<af:outputText value="#{emissionUnit.facilityName}" />
				</af:column>
				<af:column sortProperty="doLaaCd" sortable="true" formatType="text"
					headerText="District">
					<af:selectOneChoice readOnly="true" inlineStyle="#{infraDefs.hidden}" value="#{emissionUnit.doLaaCd}">
						<f:selectItems value="#{facilitySearch.doLaas}" />
					</af:selectOneChoice>
				</af:column>
				<af:column sortProperty="facilityOpStatusCd" sortable="true" formatType="icon"
					headerText="Facility Operating Status">
					<af:outputText
						value="#{facilityReference.operatingStatusDefs.itemDesc[(empty emissionUnit.facilityOpStatusCd ? '' : emissionUnit.facilityOpStatusCd)]}" />
				</af:column>
				<af:column sortProperty="facilityPermitClassCd" sortable="true" formatType="icon"
					headerText="Facility Class">
					<af:outputText
						value="#{facilityReference.permitClassDefs.itemDesc[(empty emissionUnit.facilityPermitClassCd ? '' : emissionUnit.facilityPermitClassCd)]}" />
				</af:column>
				<af:column sortProperty="epaEmuId" sortable="true" formatType="icon">
					<f:facet name="header">
						<af:outputText value="AQD Emissions Unit ID" />
					</f:facet>
					<af:commandLink action="#{facilityProfile.submitFacEmissionUnit}">
						<af:outputText value="#{emissionUnit.epaEmuId}" />
						<t:updateActionListener property="#{facilityProfile.epaEmuId}"
							value="#{emissionUnit.epaEmuId}" />
						<t:updateActionListener property="#{facilityProfile.fpId}"
							value="#{emissionUnit.fpId}" />
					</af:commandLink>
				</af:column>
				<af:column sortProperty="euOpStatusCd" sortable="true" formatType="icon">
					<f:facet name="header">
						<af:outputText value="EU Operating Status" />
					</f:facet>
					<af:outputText
						value="#{facilityReference.euOperatingStatusDefs.itemDesc[(empty emissionUnit.euOpStatusCd ? '' : emissionUnit.euOpStatusCd)]}" />
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
		</af:panelBorder>
	</h:panelGrid>
</afh:rowLayout>

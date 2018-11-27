<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:showDetailHeader text="Calculation of Emissions" size="1" disclosed="true" rendered="#{reportProfile.calcEmissionRow.emissionCalcMethodCd > 200}">
	<af:panelForm maxColumns="1">
		<afh:rowLayout id="row1" halign="left">
			<af:outputFormatted styleUsage="instruction"
				value="Engineering estimate.  Please refer to the Methods Explained section for more details." />
		</afh:rowLayout>
	</af:panelForm>
</af:showDetailHeader>
<af:showDetailHeader text="Calculation of Emissions" size="1" disclosed="false" rendered="#{reportProfile.calcEmissionRow.emissionCalcMethodCd < 200}">
	<af:panelForm maxColumns="1">
		<afh:rowLayout id="row1" halign="left">
			<af:inputText label="Pollutant:" readOnly="true"
				value="#{facilityReference.pollutantDefs.itemDesc[reportProfile.calcEmissionRow.pollutantCd]}" />
		</afh:rowLayout>
		<afh:rowLayout id="row2" halign="left"
			rendered="#{reportProfile.calcWrapperComputeError == null}">
			<af:outputFormatted styleUsage="instruction"
				value="Based upon 1,000 units of emission" />
		</afh:rowLayout>
		<afh:rowLayout id="row3" halign="left"
			rendered="#{reportProfile.calcWrapperComputeError != null}">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="#{reportProfile.calcWrapperComputeError}" />
		</afh:rowLayout>
		<afh:rowLayout id="row4" halign="center"
			rendered="#{reportProfile.calcWrapperComputeError == null}">
			<af:table value="#{reportProfile.methodBasedCalcWrapper}" bandingInterval="1"
				binding="#{reportProfile.methodBasedCalcWrapper.table}" id="fireTab"
				banding="row" width="98%" var="calcLine">
				<af:column formatType="text" headerText="Stream Label">
					<af:outputText value="#{calcLine.streamLabel}" />
				</af:column>
				<af:column formatType="icon" headerText="Flow Percent">
					<af:outputText value="#{calcLine.percent}">
						<af:convertNumber pattern="##0.####" />
					</af:outputText>
				</af:column>
				<af:column formatType="icon" headerText="Input">
					<af:outputText value="#{calcLine.inputStr}" />
				</af:column>
				<af:column formatType="text" headerText="Type">
					<af:outputText value="#{calcLine.type}" />
				</af:column>
				<af:column formatType="text" headerText="Device ID">
					<af:outputText value="#{calcLine.deviceName}" />
				</af:column>
				<af:column formatType="icon" headerText="Hours Uncontrolled">
					<af:outputText value="#{calcLine.annualAdjust}">
					</af:outputText>
				</af:column>
				<af:column formatType="icon" headerText="Actual Hours">
					<af:outputText value="#{reportProfile.webPeriod.hoursPerYear}">
					</af:outputText>
				</af:column>
				<af:column formatType="text" headerText="Pollutant Control Used"
					id="pollutant">
					<af:selectOneChoice value="#{calcLine.pollutantControlCd}"
						readOnly="true">
						<f:selectItems
							value="#{facilityReference.nonToxicPollutantDefs.items[(empty calcLine.pollutantControlCd ? '' : calcLine.pollutantControlCd)]}" />
					</af:selectOneChoice>
				</af:column>
				<af:column formatType="icon" headerText="Control Efficiency %" rendered="#{reportProfile.calcEmissionRow.emissionCalcMethodCd > 100}">
					<af:outputText value="#{calcLine.operContEff}">
					</af:outputText>
				</af:column>
				<af:column formatType="icon" headerText="Capture Efficiency %">
					<af:outputText value="#{calcLine.captureEff}">
					</af:outputText>
				</af:column>
				<af:column formatType="icon" headerText="Output">
					<af:outputText value="#{calcLine.outputC}">
					</af:outputText>
				</af:column>
				<af:column formatType="icon" headerText="Fugitive">
					<af:outputText value="#{calcLine.fugitiveFractionC}">
					</af:outputText>
				</af:column>
				<af:column formatType="icon" headerText="Stack">
					<af:outputText value="#{calcLine.stackFractionC}">
					</af:outputText>
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="right">
						<af:panelButtonBar>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
						<af:objectSpacer width="200" height="3" />
						<af:outputLabel value="Fugitive Total:" />
						<af:objectSpacer width="7" height="5" />
						<af:outputText value="#{reportProfile.calcFugitiveEmissions}" />
						<af:objectSpacer width="14" height="5" />
						<af:outputLabel value="Stack Total:" />
						<af:objectSpacer width="7" height="5" />
						<af:outputText value="#{reportProfile.calcStackEmissions}" />
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</afh:rowLayout>
		<af:objectSpacer width="7" height="5" />
		<afh:rowLayout id="row5" halign="center"
			rendered="#{reportProfile.calcWrapperComputeError == null}">
			<af:outputFormatted styleUsage="instruction"
				value="For each 1,000 units of <i>#{facilityReference.pollutantDefs.itemDesc[reportProfile.calcEmissionRow.pollutantCd]}</i> pollutant generated by the process:" />
		</afh:rowLayout>
		<afh:rowLayout id="row6" halign="center"
			rendered="#{reportProfile.calcWrapperComputeError == null}">
			<af:outputFormatted styleUsage="instruction"
				value="#{reportProfile.calcFugitiveEmissions} units of fugitive emissions are released" />
		</afh:rowLayout>
		<afh:rowLayout id="row7" halign="center"
			rendered="#{reportProfile.calcWrapperComputeError == null}">
			<af:outputFormatted styleUsage="instruction" value="and" />
		</afh:rowLayout>
		<afh:rowLayout id="row8" halign="center"
			rendered="#{reportProfile.calcWrapperComputeError == null}">
			<af:outputFormatted styleUsage="instruction"
				value="#{reportProfile.calcStackEmissions} units of stack emissions are released." />
		</afh:rowLayout>
	</af:panelForm>
</af:showDetailHeader>
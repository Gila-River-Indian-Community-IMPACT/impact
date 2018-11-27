<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

	<af:panelForm>
		<af:panelHeader text="Air Programs" size="4" />                  
    	<afh:rowLayout halign="center">
    	<af:panelForm rows="1" maxColumns="1" width="600" >
			<af:table value="#{enforcementSearch.airPrograms}" bandingInterval="1" 
					banding="row" var="airProgram"
					rows="#{facilityProfile.pageLimit}" width="100%">
            	<af:column sortProperty="pollutantCd" sortable="true" formatType="text" headerText="Air Program" >
            		<af:selectOneChoice value="#{airProgram.pollutantCd}" readOnly="true" >
						<f:selectItems value="#{compEvalDefs.airPrograms.allSearchItems}" />
					</af:selectOneChoice>                          
            	</af:column>
            	<af:column sortProperty="pollutantCompCd" sortable="true" formatType="text" 
            		headerText="Compliance Status">
					<af:selectOneChoice value="#{airProgram.pollutantCompCd}" 
						readOnly="#{!enforcementSearch.okToEditComplianceStatus}"
						inlineStyle="#{'N' == airProgram.pollutantCompCd?'color: orange; font-weight: bold;':''}">
						<f:selectItems
							value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
					</af:selectOneChoice>                         
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
		</afh:rowLayout>
		
		<af:panelHeader text="NESHAPS" size="4" 
				rendered="#{enforcementSearch.facility.neshaps}" />
		<afh:rowLayout halign="center">
		<af:panelForm rows="1" maxColumns="1" width="600" >
			<af:table value="#{enforcementSearch.facility.neshapsSubpartsCompCds}" 
					bandingInterval="1" 
					banding="row" var="neshapsSubpart"
					rows="#{facilityProfile.pageLimit}" 
					rendered="#{enforcementSearch.facility.neshaps}" width="98%">
				<f:facet name="selection">
					<af:tableSelectMany rendered="#{facilityProfile.dapcUpdateRenderComp}" />
				</f:facet>
				<af:column sortProperty="pollutantCd" sortable="true" formatType="text" 
					headerText="NESHAPS Subpart">
					<af:selectOneChoice value="#{neshapsSubpart.pollutantCd}" readOnly="true">
						<f:selectItems value="#{infraDefs.neshapsSubparts.items[(empty neshapsSubpart.pollutantCd ? '' : neshapsSubpart.pollutantCd)]}" />
					</af:selectOneChoice>
				</af:column>
            	<af:column sortProperty="pollutantCompCd" sortable="true" formatType="text" 
            		headerText="Compliance Status">
					<af:selectOneChoice value="#{neshapsSubpart.pollutantCompCd}" 
						readOnly="#{!enforcementSearch.okToEditComplianceStatus}"
						inlineStyle="#{'N' == neshapsSubpart.pollutantCompCd?'color: orange; font-weight: bold;':''}">
						<f:selectItems
							value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
					</af:selectOneChoice>                         
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
	</afh:rowLayout>

	<af:panelHeader text="NSPS" size="4" 
		rendered="#{facilityProfile.dapcUser && enforcementSearch.facility.nsps}" />
	<afh:rowLayout halign="center">
		<af:panelForm rows="1" maxColumns="1" width="600" rendered="#{facilityProfile.dapcUser}">
			<af:table value="#{enforcementSearch.facility.nspsPollutantsCompCds}" bandingInterval="1" 
					banding="row" var="nspsPollutant" 
					rendered="#{enforcementSearch.facility.nsps}"  width="98%">
				<f:facet name="selection">
					<af:tableSelectMany rendered="#{facilityProfile.dapcUpdateRenderComp}" />
				</f:facet>
				<af:column sortProperty="pollutantCd" sortable="true" formatType="text" headerText="NSPS Pollutant">
					<af:selectOneChoice value="#{nspsPollutant.pollutantCd}" readOnly="true">
						<f:selectItems value="#{facilityReference.nspsPollutantDefs.items[(empty nspsPollutant.pollutantCd ? '' : nspsPollutant.pollutantCd)]}" />
					</af:selectOneChoice>
				</af:column>
            	<af:column sortProperty="pollutantCompCd" sortable="true" formatType="text" 
            		headerText="Compliance Status">
					<af:selectOneChoice value="#{nspsPollutant.pollutantCompCd}"
						inlineStyle="#{'N' == nspsPollutant.pollutantCompCd?'color: orange; font-weight: bold;':''}"
						readOnly="#{!enforcementSearch.okToEditComplianceStatus}">
						<f:selectItems
							value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
					</af:selectOneChoice>                         
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
	</afh:rowLayout>
	
	<af:panelHeader text="PSD" size="4" rendered="#{enforcementSearch.facility.psd}" />
	<afh:rowLayout halign="center">
		<af:panelForm rows="1" maxColumns="1" width="600" rendered="#{enforcementSearch.facility.psd}" >
			<af:table value="#{enforcementSearch.facility.psdPollutantsCompCds}" bandingInterval="1" 
					banding="row" var="psdPollutant"  width="98%">
				<f:facet name="selection">
					<af:tableSelectMany rendered="#{facilityProfile.dapcUpdateRenderComp}" />
				</f:facet>
				<af:column sortProperty="pollutantCd" sortable="true" formatType="text" headerText="PSD Pollutant">
					<af:selectOneChoice value="#{psdPollutant.pollutantCd}" readOnly="true">
						<f:selectItems value="#{facilityReference.psdPollutantDefs.items[(empty psdPollutant.pollutantCd ? '' : psdPollutant.pollutantCd)]}" />
					</af:selectOneChoice>
				</af:column>
            	<af:column sortProperty="pollutantCompCd" sortable="true" formatType="text" 
            		headerText="Compliance Status">
					<af:selectOneChoice value="#{psdPollutant.pollutantCompCd}"
						inlineStyle="#{'N' == psdPollutant.pollutantCompCd?'color: orange; font-weight: bold;':''}"
						readOnly="#{!enforcementSearch.okToEditComplianceStatus}">
						<f:selectItems
							value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
					</af:selectOneChoice>                         
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
	</afh:rowLayout>

	<af:panelHeader text="Non-attainment NSR" size="4" rendered="#{enforcementSearch.facility.nsrNonattainment}" />
	<afh:rowLayout halign="center">
		<af:panelForm rows="1" maxColumns="1" width="600" rendered="#{enforcementSearch.facility.nsrNonattainment}" >
			<af:table value="#{enforcementSearch.facility.nsrPollutantsCompCds}" bandingInterval="1" 
					banding="row" var="nsrPollutant"  width="98%">
				<f:facet name="selection">
					<af:tableSelectMany rendered="#{facilityProfile.dapcUpdateRenderComp}" />
				</f:facet>
				<af:column sortProperty="pollutantCd" sortable="true" formatType="text" headerText="Non-attainment NSR Pollutant">
					<af:selectOneChoice value="#{nsrPollutant.pollutantCd}" readOnly="true">
						<f:selectItems value="#{facilityReference.nsrPollutantDefs.items[(empty nsrPollutant.pollutantCd ? '' : nsrPollutant.pollutantCd)]}" />
					</af:selectOneChoice>
				</af:column>
            	<af:column sortProperty="pollutantCompCd" sortable="true" formatType="text" 
            		headerText="Compliance Status">
					<af:selectOneChoice value="#{nsrPollutant.pollutantCompCd}"
						inlineStyle="#{'N' == nsrPollutant.pollutantCompCd?'color: orange; font-weight: bold;':''}"
						readOnly="#{!enforcementSearch.okToEditComplianceStatus}">
						<f:selectItems
							value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
					</af:selectOneChoice>                         
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
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Edit" action="#{enforcementSearch.editComplianceStatus}"
				disabled="#{!enforcementSearch.cetaUpdate}"
				rendered="#{!enforcementSearch.okToEditComplianceStatus}"/>
			<af:commandButton text="Save" action="#{enforcementSearch.saveComplianceStatus}" 
				rendered="#{enforcementSearch.okToEditComplianceStatus}" />
			<af:commandButton text="Cancel" action="#{enforcementSearch.cancelEditComplianceStatus}"
				rendered="#{enforcementSearch.okToEditComplianceStatus}" />
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelForm>
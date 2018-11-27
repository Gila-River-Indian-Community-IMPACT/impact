<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Data Entry Wizard">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<f:verbatim>
			<style>
				div.dataEntryWizardTable>table.x2d>tbody>tr>td {
					vertical-align: top;
		        }
			</style>
		</f:verbatim> 
		<af:form>
			<af:page id="dataEntryWizard" var="foo" value="#{menuModel.model}">
				<af:panelHeader text="Data Entry Wizard" />

				<f:facet name="messages">
					<af:messages />
				</f:facet>
				<af:panelForm
					partialTriggers="eus:zeroEmissions 
									 eus:emissionPeriods:hoursPerYear
									 eus:emissionPeriods:maus:throughput
									 eus:emissionPeriods:vars:value">
					<afh:rowLayout halign="left">
						<af:panelButtonBar>
							<af:commandLink id="selectAllLnk" text="Mark All as Did Not Operate"
								action="#{reportProfile.markAllEUsAsDidNotOperate}" />
							<af:outputText value="|" />
							<af:commandLink id="selectNoneLnk" text="Mark All as Operated"
								action="#{reportProfile.markAllEUsAsOperated}" />
						</af:panelButtonBar>
					</afh:rowLayout>
					<t:div style="overflow:auto;width:1400px;height:750px">
						<afh:rowLayout halign="center" valign="top" width="100%">
							<af:table id="eus" 
								width="100%"
								rows="100"  styleClass="dataEntryWizardTable"
								var="row" value="#{reportProfile.report.eus}" >

								<af:column headerText="EU ID" 
									formatType="text" width="3%">
									<af:outputText id="epaEmuId" value="#{row.epaEmuId}" />
								</af:column>

								<af:column headerText="EU Description" 
									formatType="text" width="15%">
									<af:outputText id="dapcDescriptionLbl"
										inlineStyle="font-weight: bold" value="AQD Description:" />
									<af:outputText id="dapcDescription"
										value="#{row.dapcDescription}" />
									<af:objectSpacer height="10" />
									<af:outputText id="regulatedUserDscLbl"
										inlineStyle="font-weight: bold" value="Company Description:" />
									<af:outputText id="regaulatedUserDsc"
										value="#{row.regulatedUserDsc}" />
								</af:column>

								<af:column headerText="Did Not Operate" 
									formatType="icon" width="5%">
									<af:selectBooleanCheckbox id="zeroEmissions" 
										autoSubmit="true"
										value="#{row.zeroEmissions}">
									</af:selectBooleanCheckbox>
								</af:column>
								
								<af:column headerText="Process Information" 
									width="82%">
									<af:table id="emissionPeriods" styleClass="dataEntryWizardTable"
										rendered="#{!row.zeroEmissions}" 
										width="100%" 
										var="period" value="#{row.periods}" >
										
										<af:column headerText="SCC ID" 
											rendered="#{!period.notInFacility}"
											formatType="number" width="5%" >
											<af:outputText id="sccId" 
												shortDesc="#{period.sccCode.completeDescription}"
												value="#{period.sccCode.sccId}" />
										</af:column>

										<af:column headerText="Operating Hours"
											rendered="#{!period.notInFacility}" 
											formatType="number" width="5%" >
											<af:inputText id="hoursPerYear"
												validator="#{period.validateOperatingHours}" 
												maximumLength="10"
												value="#{period.hoursPerYear}" columns="10" >
												<af:convertNumber type="number" maxFractionDigits="2"
													minFractionDigits="2" />
											</af:inputText>
										</af:column>

										<af:column headerText="Material(s)" 
											rendered="#{!period.notInFacility}"
											width="50%" >
											<af:table id="maus" 
												width="100%" 
												var="maus" value="#{period.maus}" >
												
												<af:column headerText="Select Only One" 
													rendered="#{period.hasMultipleMaterials}"
													formatType="text" width="5%" >
													<af:commandLink id="selectLnk" 
														text="Select" 
														action="#{reportProfile.selectMaterial}" >
														<t:updateActionListener
															property="#{reportProfile.selectedPeriod}"
															value="#{period}" />
														<t:updateActionListener
															property="#{reportProfile.selectedMaterial}"
															value="#{maus}" />	
													</af:commandLink>	
												</af:column>
												
												<af:column headerText="Material" 
													formatType="text" width="15%">
													<af:selectOneChoice id="material" 
														readOnly="true"
														value="#{maus.material}" >
														<f:selectItems
															value="#{facilityReference.materialDefs.items[(empty maus.material	? '' : maus.material)]}" />
													</af:selectOneChoice>
												</af:column>

												<af:column headerText="Throughput" formatType="number"
													width="5%">
													<af:inputText id="throughput" 
														shortDesc="Amount of material (in Throughput Units) used annually"
														readOnly="#{!maus.belongs}"
														maximumLength="13" columns="13"
 														value="#{maus.throughput}" >
														<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
													</af:inputText>
												</af:column>

												<af:column headerText="Action" 
													formatType="text" width="5%">
													<af:outputText id="action" 
														value="#{maus.action}" />
												</af:column>

												<af:column headerText="Units" 
													formatType="text" width="5%">
													<af:selectOneChoice id="measure" 
														readOnly="true"
														value="#{maus.measure}">
														<f:selectItems
															value="#{facilityReference.unitDefs.items[(empty maus.measure ? '' : maus.measure)]}" />
													</af:selectOneChoice>
												</af:column>
												
											</af:table>
										</af:column>
										
										<af:column headerText="Variables"
											rendered="#{!period.notInFacility}"
											width="30%" >
											<af:table id="vars" 
												rendered="#{period.hasVariables}"
												width="100%" 
												var="var" value="#{period.vars}" >
												
												<af:column headerText="Variable" 
													formatType="text" width="10%">
													<af:outputText id="variable" value="#{var.variable}" />
												</af:column>

												<af:column headerText="Value"
													formatType="number"	width="10%">
													<af:inputText id="value" 
														maximumLength="13" columns="13"		
														value="#{var.value}" >
														<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
													</af:inputText>
												</af:column>

												<af:column headerText="Units & Meaning" 
													formatType="text" width="80%" >
													<af:outputText id="meaning" 
														value="#{var.meaning}" />
												</af:column>
												
											</af:table>
										</af:column>
										
									</af:table>
								</af:column>
								
							</af:table>
						</afh:rowLayout>
					</t:div>
				</af:panelForm>

				<af:objectSpacer height="20" />

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton id="saveBtn" text="Save"
							action="#{reportProfile.saveDataFromWizard}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

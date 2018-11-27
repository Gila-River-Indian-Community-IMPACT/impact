<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Facility/EU Emissions Summary">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{facilityEmissions.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Facility Emissions Summary">
				<%@ include file="../util/header.jsp"%>
				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<af:panelBorder>
							<af:showDetailHeader text="Selection Criteria" disclosed="true">
								<af:panelForm rows="1" maxColumns="4">
									<afh:cellFormat>
										<h:panelGrid border="1">
											<af:inputText columns="10" label="Facility ID"
												readOnly="#{!facilityEmissions.notInProgress}"
												value="#{facilityEmissions.facilityId}" />
										</h:panelGrid>
										<h:panelGrid border="1">
											<af:inputText label="First Report Year" maximumLength="4"
												readOnly="#{!facilityEmissions.notInProgress}" columns="4"
												value="#{facilityEmissions.firstYear}" />
											<af:inputText label="Last Report Year" maximumLength="4"
												readOnly="#{!facilityEmissions.notInProgress}" columns="4"
												value="#{facilityEmissions.lastYear}" />
										</h:panelGrid>
										<h:panelGrid>
											<af:selectManyListbox label="EU Types" size="12"
												rendered="#{facilityEmissions.notInProgress}"
												value="#{facilityEmissions.selectedEUs}">
												<f:selectItems value="#{facilityEmissions.allEUs}" />
											</af:selectManyListbox>
											<af:table value="#{facilityEmissions.selectedEUs}" var="row"
												rendered="#{!facilityEmissions.notInProgress && !facilityEmissions.selectedAllEUs}">
												<af:column headerText="Selected EU Types" formatType="icon"
													sortable="true" sortProperty="row">
													<af:outputText value="#{row}" />
												</af:column>
											</af:table>
											<af:table value="#{facilityEmissions.selectedEUsIsAll}" var="row"
												rendered="#{!facilityEmissions.notInProgress && facilityEmissions.selectedAllEUs}">
												<af:column headerText="Selected EU Types" formatType="icon"
													sortable="true" sortProperty="row">
													<af:outputText value="#{row}" />
												</af:column>
											</af:table>
										</h:panelGrid>
									</afh:cellFormat>
									<afh:cellFormat>
										<h:panelGrid border="1">
											<af:selectManyListbox label="Counties" size="18"
												rendered="#{facilityEmissions.notInProgress}"
												value="#{facilityEmissions.selectedCounties}">
												<f:selectItems value="#{facilityEmissions.allCounties}" />
											</af:selectManyListbox>
											<af:table value="#{facilityEmissions.selectedCounties}" var="row"
												rendered="#{!facilityEmissions.notInProgress && !facilityEmissions.selectedAllCounties}">
												<af:column headerText="Selected Counties"
													sortable="true" sortProperty="row">
													<af:selectOneChoice value="#{row}" readOnly="true" >
														<f:selectItems value="#{infraDefs.counties}"/>
													</af:selectOneChoice>
												</af:column>
											</af:table>
											<af:table value="#{facilityEmissions.selectedCountiesIsAll}" var="row"
												rendered="#{!facilityEmissions.notInProgress && facilityEmissions.selectedAllCounties}">
												<af:column headerText="Selected Counties"
													sortable="true" sortProperty="row">
													<af:outputText value="#{row}" />
												</af:column>
											</af:table>
										</h:panelGrid>
									</afh:cellFormat>
									<afh:cellFormat>
										<h:panelGrid border="1">
											<af:panelForm rows="4" maxColumns="1" id="polls2"
												partialTriggers="processInfo sicInfo naicsInfo includeAll">
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.includeAll}" autoSubmit="true"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include All Below" id="includeAll" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.includeCurr}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include Current Facility Info" id="includeCurr" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.facTots}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include Facility Total Rows" id="facTots" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.rowEuTot}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include EU Total Rows" id="euTots" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnSic}"
													readOnly="#{!facilityEmissions.notInProgress}"
													autoSubmit="true" immediate="true"
													label="Include SIC Codes" id="sicInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnNaics}"
													readOnly="#{!facilityEmissions.notInProgress}"
													autoSubmit="true" immediate="true"
													label="Include NAICS Codes" id="naicsInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnSicNaicsDesc}"
													rendered="#{facilityEmissions.columnSic || facilityEmissions.columnNaics}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include SIC/NAICS Descriptions" id="descInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnEuDapc}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include EU AQD Desc." id="dapcInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnEuCompany}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include EU Company Desc." id="companyInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnEuInstallDates}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include EU Install Dates" id="euDatesInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnProcess}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include Process Rows" id="processInfo"
													autoSubmit="true" immediate="true" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnSccDesc}"
													rendered="#{facilityEmissions.columnProcess}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include SCC Desciptions" id="sccInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnPid}"
													rendered="#{facilityEmissions.columnProcess}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include Process ID" id="pIdInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnPdesc}"
													rendered="#{facilityEmissions.columnProcess}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include Process Desc" id="pDescInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnCE}"
													rendered="#{facilityEmissions.columnProcess}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include Control Equipment" id="ceInfo" />
											</af:panelForm>
										</h:panelGrid>
									</afh:cellFormat>
									<afh:cellFormat>
										<h:panelGrid border="1">
											<af:panelForm rows="2" maxColumns="1" id="euStatus">
												<af:selectBooleanCheckbox value="#{facilityEmissions.sdEUs}"
													readOnly="#{!facilityEmissions.notInProgress}"
													shortDesc="EU operated at beginning of period and shutdown before the end of period (or no shutdown date given)."
													label="Include EUs Shutdown in Period" id="sdEUs" />
												<af:selectBooleanCheckbox value="#{facilityEmissions.opEUs}"
													readOnly="#{!facilityEmissions.notInProgress}"
													shortDesc="This option includes EUs shutdown in the period"
													label="Include Operating & Shutdown EUs" id="opEUs" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.removeNull}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Remove EUs/Processes with low/no Emissions" id="rmNull" />
											</af:panelForm>
										</h:panelGrid>
										<h:panelGrid border="1">
											<af:selectOneRadio
												readOnly="#{!facilityEmissions.notInProgress}"
												id="excludeIfReports" value="#{facilityEmissions.rptLevel}">
												<f:selectItem itemLabel="Include all matching facilities"
													itemValue="0" />
												<f:selectItem itemLabel="Exclude if no Emissions Inventories"
													itemValue="1" />
												<f:selectItem
													itemLabel="Exclude if no SMTV/TV Emissions Inventories"
													itemValue="2" />
												<f:selectItem itemLabel="Exclude if no TV Emissions Inventories"
													itemValue="3" />
											</af:selectOneRadio>
										</h:panelGrid>
										<h:panelGrid border="1">
											<af:panelForm rows="4" maxColumns="1" id="polls3"
												partialTriggers="permitAllInfo">
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnAllPermit}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include All Permits" id="permitAllInfo"
													autoSubmit="true" immediate="true" />
												<%-- 
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnPtiPermit}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include PTI Permits" id="permitPtiInfo" />
													--%>
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnTvPtoPermit}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include Title V Permits" id="permitTvPtoInfo" />
												<af:selectBooleanCheckbox
													value="#{facilityEmissions.columnPtioPermit}"
													readOnly="#{!facilityEmissions.notInProgress}"
													label="Include NSR Permits"
													shortDesc="Includes state NSRs issued with 'FENSR' enabled check box"
													id="permitPtioInfo" />
										
											</af:panelForm>
										</h:panelGrid>
									</afh:cellFormat>
								</af:panelForm>
								<h:panelGrid border="1">
									<af:panelForm rows="4" maxColumns="1" id="polls4">
										<af:selectBooleanCheckbox
											value="#{facilityEmissions.ferPolls}"
											readOnly="#{!facilityEmissions.notInProgress}"
											label="Include FER Pollutants" id="ferPolls" />
										<af:selectBooleanCheckbox value="#{facilityEmissions.esPolls}"
											readOnly="#{!facilityEmissions.notInProgress}"
											label="Include ES Pollutants" id="esPolls" />
										<af:selectBooleanCheckbox
											value="#{facilityEmissions.eisPolls}"
											readOnly="#{!facilityEmissions.notInProgress}"
											label="Include EIS Pollutants" id="eisPolls" />
										<af:selectOneChoice
											value="#{facilityEmissions.extraPollutantCd}"
											label="Additional Pollutant" unselectedLabel=" "
											readOnly="#{!facilityEmissions.notInProgress}">
											<f:selectItems
												value="#{facilityReference.nonToxicPollutantDefs.items[(empty facilityEmissions.extraPollutantCd ? '' : facilityEmissions.extraPollutantCd)]}" />
										</af:selectOneChoice>
									</af:panelForm>
								</h:panelGrid>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
				<af:objectSpacer height="6" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Generate Facility Emissions Inventory"
							rendered="#{facilityEmissions.notInProgress}"
							action="#{facilityEmissions.startOperation}" />
						<af:commandButton text="Reset"
							rendered="#{facilityEmissions.notInProgress}"
							action="#{facilityEmissions.reset}" />
						<af:commandButton text="Cancel Inventory Generation"
							rendered="#{!facilityEmissions.notInProgress}"
							action="#{facilityEmissions.cancel}" />
					</af:panelButtonBar>
				</afh:rowLayout>
				<af:panelGroup layout="vertical"
					rendered="#{!facilityEmissions.notInProgress}">
					<af:objectSpacer height="6" />
					<af:progressIndicator id="progressid" value="#{facilityEmissions}">
						<af:outputFormatted value="#{facilityEmissions.value} % Complete" />
					</af:progressIndicator>
				</af:panelGroup>
				<af:panelGroup layout="vertical">
					<af:objectSpacer height="6" />
					<afh:rowLayout halign="center">
						<af:outputFormatted
							value="#{facilityEmissions.candidateIncludedStr}" />
					</afh:rowLayout>
				</af:panelGroup>
				<af:panelGroup layout="vertical"
					rendered="#{facilityEmissions.errorStr != null}">
					<af:objectSpacer height="6" />
					<afh:rowLayout halign="center">
						<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
							value="#{facilityEmissions.errorStr}" />
					</afh:rowLayout>
				</af:panelGroup>
				<af:objectSpacer height="6" />
				<afh:rowLayout halign="center"
					rendered="#{facilityEmissions.notInProgress && facilityEmissions.datagridEmpty}">
					<af:outputText value="No Matching Results Found" />
				</afh:rowLayout>
				<af:panelGroup
					rendered="#{facilityEmissions.notInProgress && !facilityEmissions.datagridEmpty}">
					<af:table value="#{facilityEmissions.datagrid}" var="row"
						bandingInterval="1" id="datagridTab" banding="row" rows="600">
						<af:column headerText="#{facilityEmissions.labels[0]}"
							sortable="true">
							<af:outputText value="#{row[0]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[1]}"
							sortable="true">
							<af:outputText value="#{row[1]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[2]}"
							sortable="true">
							<af:outputText value="#{row[2]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[3]}"
							sortable="true">
							<af:outputText value="#{row[3]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[4]}"
							sortable="true">
							<af:outputText value="#{row[4]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[5]}"
							sortable="true">
							<af:outputText value="#{row[5]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[6]}">
							<af:outputText value="#{row[6]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[7]}">
							<af:outputText value="#{row[7]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[8]}"
							rendered="#{facilityEmissions.lastIndex >= 8 && 8 != facilityEmissions.pollsFromGrpsColumn && 8 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[8]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[9]}"
							rendered="#{facilityEmissions.lastIndex >= 9 && 9 != facilityEmissions.pollsFromGrpsColumn && 9 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[9]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[10]}"
							rendered="#{facilityEmissions.lastIndex >= 10 && 10 != facilityEmissions.pollsFromGrpsColumn && 10 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[10]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[11]}"
							rendered="#{facilityEmissions.lastIndex >= 11 && 11 != facilityEmissions.pollsFromGrpsColumn && 11 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[11]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[12]}"
							rendered="#{facilityEmissions.lastIndex >= 12 && 12 != facilityEmissions.pollsFromGrpsColumn && 12 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[12]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[13]}"
							rendered="#{facilityEmissions.lastIndex >= 13 && 13 != facilityEmissions.pollsFromGrpsColumn && 13 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[13]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[14]}"
							rendered="#{facilityEmissions.lastIndex >= 14 && 14 != facilityEmissions.pollsFromGrpsColumn && 14 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[14]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[15]}"
							rendered="#{facilityEmissions.lastIndex >= 15 && 15 != facilityEmissions.pollsFromGrpsColumn && 15 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[15]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[16]}"
							rendered="#{facilityEmissions.lastIndex >= 16 && 16 != facilityEmissions.pollsFromGrpsColumn && 16 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[16]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[17]}"
							rendered="#{facilityEmissions.lastIndex >= 17 && 17 != facilityEmissions.pollsFromGrpsColumn && 17 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[17]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[18]}"
							rendered="#{facilityEmissions.lastIndex >= 18 && 18 != facilityEmissions.pollsFromGrpsColumn && 18 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[18]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[19]}"
							rendered="#{facilityEmissions.lastIndex >= 19 && 19 != facilityEmissions.pollsFromGrpsColumn && 19 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[19]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[20]}"
							rendered="#{facilityEmissions.lastIndex >= 20 && 20 != facilityEmissions.pollsFromGrpsColumn && 20 != facilityEmissions.corrIdIndex}">
							<af:outputText value="#{row[20]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[21]}"
							rendered="#{facilityEmissions.lastIndex >= 21}">
							<af:outputText value="#{row[21]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[22]}"
							rendered="#{facilityEmissions.lastIndex >= 22}">
							<af:outputText value="#{row[22]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[23]}"
							rendered="#{facilityEmissions.lastIndex >= 23}">
							<af:outputText value="#{row[23]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[24]}"
							rendered="#{facilityEmissions.lastIndex >= 24}">
							<af:outputText value="#{row[24]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[25]}"
							rendered="#{facilityEmissions.lastIndex >= 25}">
							<af:outputText value="#{row[25]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[26]}"
							rendered="#{facilityEmissions.lastIndex >= 26}">
							<af:outputText value="#{row[26]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[27]}"
							rendered="#{facilityEmissions.lastIndex >= 27}">
							<af:outputText value="#{row[27]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[28]}"
							rendered="#{facilityEmissions.lastIndex >= 28}">
							<af:outputText value="#{row[28]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[29]}"
							rendered="#{facilityEmissions.lastIndex >= 29}">
							<af:outputText value="#{row[29]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[30]}"
							rendered="#{facilityEmissions.lastIndex >= 30}">
							<af:outputText value="#{row[30]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[31]}"
							rendered="#{facilityEmissions.lastIndex >= 31}">
							<af:outputText value="#{row[31]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[32]}"
							rendered="#{facilityEmissions.lastIndex >= 32}">
							<af:outputText value="#{row[32]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[33]}"
							rendered="#{facilityEmissions.lastIndex >= 33}">
							<af:outputText value="#{row[33]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[34]}"
							rendered="#{facilityEmissions.lastIndex >= 34}">
							<af:outputText value="#{row[34]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[35]}"
							rendered="#{facilityEmissions.lastIndex >= 35}">
							<af:outputText value="#{row[35]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[36]}"
							rendered="#{facilityEmissions.lastIndex >= 36}">
							<af:outputText value="#{row[36]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[37]}"
							rendered="#{facilityEmissions.lastIndex >= 37}">
							<af:outputText value="#{row[37]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[38]}"
							rendered="#{facilityEmissions.lastIndex >= 38}">
							<af:outputText value="#{row[38]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[39]}"
							rendered="#{facilityEmissions.lastIndex >= 39}">
							<af:outputText value="#{row[39]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[40]}"
							rendered="#{facilityEmissions.lastIndex >= 40}">
							<af:outputText value="#{row[40]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[41]}"
							rendered="#{facilityEmissions.lastIndex >= 41}">
							<af:outputText value="#{row[41]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[42]}"
							rendered="#{facilityEmissions.lastIndex >= 42}">
							<af:outputText value="#{row[42]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[43]}"
							rendered="#{facilityEmissions.lastIndex >= 43}">
							<af:outputText value="#{row[43]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[44]}"
							rendered="#{facilityEmissions.lastIndex >= 44}">
							<af:outputText value="#{row[44]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[45]}"
							rendered="#{facilityEmissions.lastIndex >= 45}">
							<af:outputText value="#{row[45]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[46]}"
							rendered="#{facilityEmissions.lastIndex >= 46}">
							<af:outputText value="#{row[46]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[47]}"
							rendered="#{facilityEmissions.lastIndex >= 47}">
							<af:outputText value="#{row[47]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[48]}"
							rendered="#{facilityEmissions.lastIndex >= 48}">
							<af:outputText value="#{row[48]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[49]}"
							rendered="#{facilityEmissions.lastIndex >= 49}">
							<af:outputText value="#{row[49]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[50]}"
							rendered="#{facilityEmissions.lastIndex >= 50}">
							<af:outputText value="#{row[50]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[51]}"
							rendered="#{facilityEmissions.lastIndex >= 51}">
							<af:outputText value="#{row[51]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[52]}"
							rendered="#{facilityEmissions.lastIndex >= 52}">
							<af:outputText value="#{row[52]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[53]}"
							rendered="#{facilityEmissions.lastIndex >= 53}">
							<af:outputText value="#{row[53]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[54]}"
							rendered="#{facilityEmissions.lastIndex >= 54}">
							<af:outputText value="#{row[54]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[55]}"
							rendered="#{facilityEmissions.lastIndex >= 55}">
							<af:outputText value="#{row[55]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[56]}"
							rendered="#{facilityEmissions.lastIndex >= 56}">
							<af:outputText value="#{row[56]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[57]}"
							rendered="#{facilityEmissions.lastIndex >= 57}">
							<af:outputText value="#{row[57]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[58]}"
							rendered="#{facilityEmissions.lastIndex >= 58}">
							<af:outputText value="#{row[58]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[59]}"
							rendered="#{facilityEmissions.lastIndex >= 59}">
							<af:outputText value="#{row[59]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[60]}"
							rendered="#{facilityEmissions.lastIndex >= 60}">
							<af:outputText value="#{row[60]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[61]}"
							rendered="#{facilityEmissions.lastIndex >= 61}">
							<af:outputText value="#{row[61]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[62]}"
							rendered="#{facilityEmissions.lastIndex >= 62}">
							<af:outputText value="#{row[62]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[63]}"
							rendered="#{facilityEmissions.lastIndex >= 63}">
							<af:outputText value="#{row[63]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[64]}"
							rendered="#{facilityEmissions.lastIndex >= 64}">
							<af:outputText value="#{row[64]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[65]}"
							rendered="#{facilityEmissions.lastIndex >= 65}">
							<af:outputText value="#{row[65]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[66]}"
							rendered="#{facilityEmissions.lastIndex >= 66}">
							<af:outputText value="#{row[66]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[67]}"
							rendered="#{facilityEmissions.lastIndex >= 67}">
							<af:outputText value="#{row[67]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[68]}"
							rendered="#{facilityEmissions.lastIndex >= 68}">
							<af:outputText value="#{row[68]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[69]}"
							rendered="#{facilityEmissions.lastIndex >= 69}">
							<af:outputText value="#{row[69]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[70]}"
							rendered="#{facilityEmissions.lastIndex >= 70}">
							<af:outputText value="#{row[70]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[71]}"
							rendered="#{facilityEmissions.lastIndex >= 71}">
							<af:outputText value="#{row[71]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[72]}"
							rendered="#{facilityEmissions.lastIndex >= 72}">
							<af:outputText value="#{row[72]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[73]}"
							rendered="#{facilityEmissions.lastIndex >= 73}">
							<af:outputText value="#{row[73]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[74]}"
							rendered="#{facilityEmissions.lastIndex >= 74}">
							<af:outputText value="#{row[74]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[75]}"
							rendered="#{facilityEmissions.lastIndex >= 75}">
							<af:outputText value="#{row[75]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[76]}"
							rendered="#{facilityEmissions.lastIndex >= 76}">
							<af:outputText value="#{row[76]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[77]}"
							rendered="#{facilityEmissions.lastIndex >= 77}">
							<af:outputText value="#{row[77]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[78]}"
							rendered="#{facilityEmissions.lastIndex >= 78}">
							<af:outputText value="#{row[78]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[79]}"
							rendered="#{facilityEmissions.lastIndex >= 79}">
							<af:outputText value="#{row[79]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[80]}"
							rendered="#{facilityEmissions.lastIndex >= 80}">
							<af:outputText value="#{row[80]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[81]}"
							rendered="#{facilityEmissions.lastIndex >= 81}">
							<af:outputText value="#{row[81]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[82]}"
							rendered="#{facilityEmissions.lastIndex >= 82}">
							<af:outputText value="#{row[82]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[83]}"
							rendered="#{facilityEmissions.lastIndex >= 83}">
							<af:outputText value="#{row[83]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[84]}"
							rendered="#{facilityEmissions.lastIndex >= 84}">
							<af:outputText value="#{row[84]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[85]}"
							rendered="#{facilityEmissions.lastIndex >= 85}">
							<af:outputText value="#{row[85]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[86]}"
							rendered="#{facilityEmissions.lastIndex >= 86}">
							<af:outputText value="#{row[86]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[87]}"
							rendered="#{facilityEmissions.lastIndex >= 87}">
							<af:outputText value="#{row[87]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[88]}"
							rendered="#{facilityEmissions.lastIndex >= 88}">
							<af:outputText value="#{row[88]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[89]}"
							rendered="#{facilityEmissions.lastIndex >= 89}">
							<af:outputText value="#{row[89]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[90]}"
							rendered="#{facilityEmissions.lastIndex >= 90}">
							<af:outputText value="#{row[90]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[91]}"
							rendered="#{facilityEmissions.lastIndex >= 91}">
							<af:outputText value="#{row[91]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[92]}"
							rendered="#{facilityEmissions.lastIndex >= 92}">
							<af:outputText value="#{row[92]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[93]}"
							rendered="#{facilityEmissions.lastIndex >= 93}">
							<af:outputText value="#{row[93]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[94]}"
							rendered="#{facilityEmissions.lastIndex >= 94}">
							<af:outputText value="#{row[94]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[95]}"
							rendered="#{facilityEmissions.lastIndex >= 95}">
							<af:outputText value="#{row[95]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[96]}"
							rendered="#{facilityEmissions.lastIndex >= 96}">
							<af:outputText value="#{row[96]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[97]}"
							rendered="#{facilityEmissions.lastIndex >= 97}">
							<af:outputText value="#{row[97]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[98]}"
							rendered="#{facilityEmissions.lastIndex >= 98}">
							<af:outputText value="#{row[98]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[99]}"
							rendered="#{facilityEmissions.lastIndex >= 99}">
							<af:outputText value="#{row[99]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[100]}"
							rendered="#{facilityEmissions.lastIndex >= 100}">
							<af:outputText value="#{row[100]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[101]}"
							rendered="#{facilityEmissions.lastIndex >= 101}">
							<af:outputText value="#{row[101]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[102]}"
							rendered="#{facilityEmissions.lastIndex >= 102}">
							<af:outputText value="#{row[102]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[103]}"
							rendered="#{facilityEmissions.lastIndex >= 103}">
							<af:outputText value="#{row[103]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[104]}"
							rendered="#{facilityEmissions.lastIndex >= 104}">
							<af:outputText value="#{row[104]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[105]}"
							rendered="#{facilityEmissions.lastIndex >= 105}">
							<af:outputText value="#{row[105]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[106]}"
							rendered="#{facilityEmissions.lastIndex >= 106}">
							<af:outputText value="#{row[106]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[107]}"
							rendered="#{facilityEmissions.lastIndex >= 107}">
							<af:outputText value="#{row[107]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[108]}"
							rendered="#{facilityEmissions.lastIndex >= 108}">
							<af:outputText value="#{row[108]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[109]}"
							rendered="#{facilityEmissions.lastIndex >= 109}">
							<af:outputText value="#{row[109]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[110]}"
							rendered="#{facilityEmissions.lastIndex >= 110}">
							<af:outputText value="#{row[110]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[111]}"
							rendered="#{facilityEmissions.lastIndex >= 111}">
							<af:outputText value="#{row[111]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[112]}"
							rendered="#{facilityEmissions.lastIndex >= 112}">
							<af:outputText value="#{row[112]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[113]}"
							rendered="#{facilityEmissions.lastIndex >= 113}">
							<af:outputText value="#{row[113]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[114]}"
							rendered="#{facilityEmissions.lastIndex >= 114}">
							<af:outputText value="#{row[114]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[115]}"
							rendered="#{facilityEmissions.lastIndex >= 115}">
							<af:outputText value="#{row[115]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[116]}"
							rendered="#{facilityEmissions.lastIndex >= 116}">
							<af:outputText value="#{row[116]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[117]}"
							rendered="#{facilityEmissions.lastIndex >= 117}">
							<af:outputText value="#{row[117]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[118]}"
							rendered="#{facilityEmissions.lastIndex >= 118}">
							<af:outputText value="#{row[118]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[119]}"
							rendered="#{facilityEmissions.lastIndex >= 119}">
							<af:outputText value="#{row[119]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[120]}"
							rendered="#{facilityEmissions.lastIndex >= 120}">
							<af:outputText value="#{row[120]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[121]}"
							rendered="#{facilityEmissions.lastIndex >= 121}">
							<af:outputText value="#{row[121]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[122]}"
							rendered="#{facilityEmissions.lastIndex >= 122}">
							<af:outputText value="#{row[122]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[123]}"
							rendered="#{facilityEmissions.lastIndex >= 123}">
							<af:outputText value="#{row[123]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[124]}"
							rendered="#{facilityEmissions.lastIndex >= 124}">
							<af:outputText value="#{row[124]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[125]}"
							rendered="#{facilityEmissions.lastIndex >= 125}">
							<af:outputText value="#{row[125]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[126]}"
							rendered="#{facilityEmissions.lastIndex >= 126}">
							<af:outputText value="#{row[126]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[127]}"
							rendered="#{facilityEmissions.lastIndex >= 127}">
							<af:outputText value="#{row[127]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[128]}"
							rendered="#{facilityEmissions.lastIndex >= 128}">
							<af:outputText value="#{row[128]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[129]}"
							rendered="#{facilityEmissions.lastIndex >= 129}">
							<af:outputText value="#{row[129]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[130]}"
							rendered="#{facilityEmissions.lastIndex >= 130}">
							<af:outputText value="#{row[130]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[131]}"
							rendered="#{facilityEmissions.lastIndex >= 131}">
							<af:outputText value="#{row[131]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[132]}"
							rendered="#{facilityEmissions.lastIndex >= 132}">
							<af:outputText value="#{row[132]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[133]}"
							rendered="#{facilityEmissions.lastIndex >= 133}">
							<af:outputText value="#{row[133]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[134]}"
							rendered="#{facilityEmissions.lastIndex >= 134}">
							<af:outputText value="#{row[134]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[135]}"
							rendered="#{facilityEmissions.lastIndex >= 135}">
							<af:outputText value="#{row[135]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[136]}"
							rendered="#{facilityEmissions.lastIndex >= 136}">
							<af:outputText value="#{row[136]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[137]}"
							rendered="#{facilityEmissions.lastIndex >= 137}">
							<af:outputText value="#{row[137]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[138]}"
							rendered="#{facilityEmissions.lastIndex >= 138}">
							<af:outputText value="#{row[138]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[139]}"
							rendered="#{facilityEmissions.lastIndex >= 139}">
							<af:outputText value="#{row[139]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[140]}"
							rendered="#{facilityEmissions.lastIndex >= 140}">
							<af:outputText value="#{row[140]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[141]}"
							rendered="#{facilityEmissions.lastIndex >= 141}">
							<af:outputText value="#{row[141]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[142]}"
							rendered="#{facilityEmissions.lastIndex >= 142}">
							<af:outputText value="#{row[142]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[143]}"
							rendered="#{facilityEmissions.lastIndex >= 143}">
							<af:outputText value="#{row[143]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[144]}"
							rendered="#{facilityEmissions.lastIndex >= 144}">
							<af:outputText value="#{row[144]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[145]}"
							rendered="#{facilityEmissions.lastIndex >= 145}">
							<af:outputText value="#{row[145]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[146]}"
							rendered="#{facilityEmissions.lastIndex >= 146}">
							<af:outputText value="#{row[146]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[147]}"
							rendered="#{facilityEmissions.lastIndex >= 147}">
							<af:outputText value="#{row[147]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[148]}"
							rendered="#{facilityEmissions.lastIndex >= 148}">
							<af:outputText value="#{row[148]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[149]}"
							rendered="#{facilityEmissions.lastIndex >= 149}">
							<af:outputText value="#{row[149]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[150]}"
							rendered="#{facilityEmissions.lastIndex >= 150}">
							<af:outputText value="#{row[150]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[151]}"
							rendered="#{facilityEmissions.lastIndex >= 151}">
							<af:outputText value="#{row[151]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[152]}"
							rendered="#{facilityEmissions.lastIndex >= 152}">
							<af:outputText value="#{row[152]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[153]}"
							rendered="#{facilityEmissions.lastIndex >= 153}">
							<af:outputText value="#{row[153]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[154]}"
							rendered="#{facilityEmissions.lastIndex >= 154}">
							<af:outputText value="#{row[154]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[155]}"
							rendered="#{facilityEmissions.lastIndex >= 155}">
							<af:outputText value="#{row[155]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[156]}"
							rendered="#{facilityEmissions.lastIndex >= 156}">
							<af:outputText value="#{row[156]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[157]}"
							rendered="#{facilityEmissions.lastIndex >= 157}">
							<af:outputText value="#{row[157]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[158]}"
							rendered="#{facilityEmissions.lastIndex >= 158}">
							<af:outputText value="#{row[158]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[159]}"
							rendered="#{facilityEmissions.lastIndex >= 159}">
							<af:outputText value="#{row[159]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[160]}"
							rendered="#{facilityEmissions.lastIndex >= 160}">
							<af:outputText value="#{row[160]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[161]}"
							rendered="#{facilityEmissions.lastIndex >= 161}">
							<af:outputText value="#{row[161]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[162]}"
							rendered="#{facilityEmissions.lastIndex >= 162}">
							<af:outputText value="#{row[162]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[163]}"
							rendered="#{facilityEmissions.lastIndex >= 163}">
							<af:outputText value="#{row[163]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[164]}"
							rendered="#{facilityEmissions.lastIndex >= 164}">
							<af:outputText value="#{row[164]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[165]}"
							rendered="#{facilityEmissions.lastIndex >= 165}">
							<af:outputText value="#{row[165]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[166]}"
							rendered="#{facilityEmissions.lastIndex >= 166}">
							<af:outputText value="#{row[166]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[167]}"
							rendered="#{facilityEmissions.lastIndex >= 167}">
							<af:outputText value="#{row[167]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[168]}"
							rendered="#{facilityEmissions.lastIndex >= 168}">
							<af:outputText value="#{row[168]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[169]}"
							rendered="#{facilityEmissions.lastIndex >= 169}">
							<af:outputText value="#{row[169]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[170]}"
							rendered="#{facilityEmissions.lastIndex >= 170}">
							<af:outputText value="#{row[170]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[171]}"
							rendered="#{facilityEmissions.lastIndex >= 171}">
							<af:outputText value="#{row[171]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[172]}"
							rendered="#{facilityEmissions.lastIndex >= 172}">
							<af:outputText value="#{row[172]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[173]}"
							rendered="#{facilityEmissions.lastIndex >= 173}">
							<af:outputText value="#{row[173]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[174]}"
							rendered="#{facilityEmissions.lastIndex >= 174}">
							<af:outputText value="#{row[174]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[175]}"
							rendered="#{facilityEmissions.lastIndex >= 175}">
							<af:outputText value="#{row[175]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[176]}"
							rendered="#{facilityEmissions.lastIndex >= 176}">
							<af:outputText value="#{row[176]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[177]}"
							rendered="#{facilityEmissions.lastIndex >= 177}">
							<af:outputText value="#{row[177]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[178]}"
							rendered="#{facilityEmissions.lastIndex >= 178}">
							<af:outputText value="#{row[178]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[179]}"
							rendered="#{facilityEmissions.lastIndex >= 179}">
							<af:outputText value="#{row[179]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[180]}"
							rendered="#{facilityEmissions.lastIndex >= 180}">
							<af:outputText value="#{row[180]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[181]}"
							rendered="#{facilityEmissions.lastIndex >= 181}">
							<af:outputText value="#{row[181]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[182]}"
							rendered="#{facilityEmissions.lastIndex >= 182}">
							<af:outputText value="#{row[182]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[183]}"
							rendered="#{facilityEmissions.lastIndex >= 183}">
							<af:outputText value="#{row[183]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[184]}"
							rendered="#{facilityEmissions.lastIndex >= 184}">
							<af:outputText value="#{row[184]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[185]}"
							rendered="#{facilityEmissions.lastIndex >= 185}">
							<af:outputText value="#{row[185]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[186]}"
							rendered="#{facilityEmissions.lastIndex >= 186}">
							<af:outputText value="#{row[186]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[187]}"
							rendered="#{facilityEmissions.lastIndex >= 187}">
							<af:outputText value="#{row[187]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[188]}"
							rendered="#{facilityEmissions.lastIndex >= 188}">
							<af:outputText value="#{row[188]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[189]}"
							rendered="#{facilityEmissions.lastIndex >= 189}">
							<af:outputText value="#{row[189]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[190]}"
							rendered="#{facilityEmissions.lastIndex >= 190}">
							<af:outputText value="#{row[190]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[191]}"
							rendered="#{facilityEmissions.lastIndex >= 191}">
							<af:outputText value="#{row[191]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[192]}"
							rendered="#{facilityEmissions.lastIndex >= 192}">
							<af:outputText value="#{row[192]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[193]}"
							rendered="#{facilityEmissions.lastIndex >= 193}">
							<af:outputText value="#{row[193]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[194]}"
							rendered="#{facilityEmissions.lastIndex >= 194}">
							<af:outputText value="#{row[194]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[195]}"
							rendered="#{facilityEmissions.lastIndex >= 195}">
							<af:outputText value="#{row[195]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[196]}"
							rendered="#{facilityEmissions.lastIndex >= 196}">
							<af:outputText value="#{row[196]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[197]}"
							rendered="#{facilityEmissions.lastIndex >= 197}">
							<af:outputText value="#{row[197]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[198]}"
							rendered="#{facilityEmissions.lastIndex >= 198}">
							<af:outputText value="#{row[198]}" />
						</af:column>
						<af:column headerText="#{facilityEmissions.labels[199]}"
							rendered="#{facilityEmissions.lastIndex >= 199}">
							<af:outputText value="#{row[199]}" />
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</af:panelGroup>

			</af:page>
		</af:form>
	</af:document>
</f:view>

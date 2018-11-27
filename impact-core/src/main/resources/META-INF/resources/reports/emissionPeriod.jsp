<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:panelGroup layout="vertical"
	rendered="#{reportProfile.renderComponent == 'emissionPeriods' && !reportProfile.err}">
	<af:panelHeader text="Process & Emissions Detail" />
	<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0"
		rendered="#{reportProfile.webPeriod.notInFacility}">
		<afh:rowLayout halign="center">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="<b>Error:&nbsp;&nbsp;The SCC of this period is not in the Emission Unit's Process.&nbsp;&nbsp;Delete it from this report or update this report's Facility Inventory.</b>" />
		</afh:rowLayout>
	</afh:tableLayout>
	<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0"
		rendered="#{!reportProfile.webPeriod.notInFacility && reportProfile.webPeriod.sccCode.sccId == null}">
		<afh:rowLayout halign="center">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="<b>Error:&nbsp;&nbsp;No SCC is specified for the Process.&nbsp;&nbsp;Update this report's Facility Inventory with an SCC or other appropriate correction.</b>" />
		</afh:rowLayout>
	</afh:tableLayout>
	<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0"
		rendered="#{!reportProfile.webPeriod.notInFacility && reportProfile.sccDepreciated}">
		<afh:rowLayout halign="center">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="<b>Error:&nbsp;&nbsp;#{reportProfile.webPeriod.sccCode.createDeprecateInfo}&nbsp;&nbsp;It is not active for this report year.&nbsp;&nbsp;Update this report's Facility Inventory with an active SCC.</b>" />
		</afh:rowLayout>
	</afh:tableLayout>
	<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0"
		rendered="#{!reportProfile.webPeriod.notInFacility && reportProfile.noMaterialDefined}">
		<afh:rowLayout halign="center">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="<b>Error:&nbsp;&nbsp;There are no entries in the FIRE database for this SCC. No emissions reporting can be done with this SCC until AQD adds entries for it in the FIRE database.&nbsp;&nbsp;Contact AQD for help.</b>" />
		</afh:rowLayout>
	</afh:tableLayout>
	<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0"
		rendered="#{!reportProfile.webPeriod.notInFacility && reportProfile.noRemainingFireRows}">
		<afh:rowLayout halign="center">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="<b>Error:&nbsp;&nbsp;No emissions reporting can be done with this SCC because all associated FIRE rows are inactive.&nbsp;&nbsp;Update this report's Facility Inventory with another SCC or contact AQD for help.</b>" />
		</afh:rowLayout>
	</afh:tableLayout>
	<af:showDetailHeader text="#{reportProfile.sccCodeStrLine}"
		disclosed="#{reportProfile.webPeriod.notInFacility}">
		<afh:rowLayout>
			<afh:cellFormat halign="left" width="48%">
				<af:switcher defaultFacet="noCompareSCC"
					facetName="#{(!reportProfile.doRptCompare)? 'noCompareSCC' : 'compareSCC'}">
					<f:facet name="noCompareSCC">
						<af:panelForm rendered="#{reportProfile.webPeriod != null}">
							<af:inputText label="SCC Level 1:"
								value="#{reportProfile.webPeriod.sccCode.sccLevel1Desc}"
								readOnly="true" />
							<af:inputText label="SCC Level 2:"
								value="#{reportProfile.webPeriod.sccCode.sccLevel2Desc}"
								readOnly="true" />
							<af:inputText label="SCC Level 3:"
								value="#{reportProfile.webPeriod.sccCode.sccLevel3Desc}"
								readOnly="true" />
							<af:inputText label="SCC Level 4:"
								value="#{reportProfile.webPeriod.sccCode.sccLevel4Desc}"
								readOnly="true" />
						</af:panelForm>
					</f:facet>
					<f:facet name="compareSCC">
						<af:panelForm rendered="#{reportProfile.webPeriod.orig != null}">
							<af:inputText label="SCC Level 1:"
								value="#{reportProfile.webPeriod.orig.sccCode.sccLevel1Desc}"
								readOnly="true" />
							<af:inputText label="SCC Level 2:"
								value="#{reportProfile.webPeriod.orig.sccCode.sccLevel2Desc}"
								readOnly="true" />
							<af:inputText label="SCC Level 3:"
								value="#{reportProfile.webPeriod.orig.sccCode.sccLevel3Desc}"
								readOnly="true" />
							<af:inputText label="SCC Level 4:"
								value="#{reportProfile.webPeriod.orig.sccCode.sccLevel4Desc}"
								readOnly="true" />
						</af:panelForm>
					</f:facet>
				</af:switcher>
			</afh:cellFormat>
			<afh:cellFormat width="4%">
			</afh:cellFormat>
			<afh:cellFormat halign="right" width="48%">
				<af:panelForm
					rendered="#{reportProfile.doRptCompare && reportProfile.webPeriod.comp != null && reportProfile.compareScc == null}">
					<af:outputFormatted value="<b>SCC code not specified</b>" />
				</af:panelForm>
				<af:panelForm
					rendered="#{reportProfile.doRptCompare && reportProfile.webPeriod.comp != null && reportProfile.compareScc != null}">
					<af:inputText label="SCC Level 1:"
						value="#{reportProfile.compareScc.sccLevel1Desc}" readOnly="true" />
					<af:inputText label="SCC Level 2:"
						value="#{reportProfile.compareScc.sccLevel2Desc}" readOnly="true" />
					<af:inputText label="SCC Level 3:"
						value="#{reportProfile.compareScc.sccLevel3Desc}" readOnly="true" />
					<af:inputText label="SCC Level 4:"
						value="#{reportProfile.compareScc.sccLevel4Desc}" readOnly="true" />
				</af:panelForm>
			</afh:cellFormat>
		</afh:rowLayout>
	</af:showDetailHeader>
	<af:showDetailHeader
		text="Material Information, Annual Average Operating Schedule & Throughput Percent"
		disclosed="true"
		rendered="#{!reportProfile.webPeriod.notInFacility && reportProfile.webPeriod.sccCode.sccId != null && reportProfile.nothingWrong2}">
		<afh:tableLayout width="100%">
			<afh:cellFormat width="100%">
				<afh:rowLayout halign="left" partialTriggers="tsRadio">
					<af:panelForm width="100%" maxColumns="1">
						<af:outputLabel value="Justification:"
								rendered="#{reportProfile.webPeriod.tradeSecretS}" />
						<af:inputText id="tsJust" rows="#{reportProfile.webPeriod.tsRows}"
							rendered="#{reportProfile.webPeriod.tradeSecretS}"
							value="#{reportProfile.webPeriod.tradeSecretSText}" columns="160"
							maximumLength="2000" readOnly="#{!reportProfile.editableM}">
						</af:inputText>
					</af:panelForm>
				</afh:rowLayout>
			</afh:cellFormat>
		</afh:tableLayout>
		<afh:tableLayout width="100%">
			<afh:rowLayout>
				<afh:cellFormat halign="left" width="50%">
					<af:panelForm>
						<af:panelForm  maxColumns="2">
								<af:inputText label="Maximum Hours Per Day:" id="hoursPerDay"
									columns="10" maximumLength="10"
									readOnly="#{!reportProfile.editableM || (!reportProfile.tradeSecretVisible && reportProfile.webPeriod.tradeSecretS)}"
								    value="#{reportProfile.webPeriod.hoursPerDay}"/>
								<af:inputText label="Maximum Days Per Week:" id="daysPerWeek"
									columns="10" maximumLength="10"
									readOnly="#{!reportProfile.editableM || (!reportProfile.tradeSecretVisible && reportProfile.webPeriod.tradeSecretS)}"	
									value="#{reportProfile.webPeriod.daysPerWeek}"/>
								<af:inputText label="Maximum Weeks Per Year:" id="weeksPerYear"
									columns="10" maximumLength="10"
									readOnly="#{!reportProfile.editableM || (!reportProfile.tradeSecretVisible && reportProfile.webPeriod.tradeSecretS)}" 
									value="#{reportProfile.webPeriod.weeksPerYear}"/>
								<af:inputText label="Actual Hours:" id="hoursPerYear"
									showRequired="true" columns="10" maximumLength="10"
									rendered="#{!reportProfile.doRptCompare}"
									readOnly="#{!reportProfile.editableM || (!reportProfile.tradeSecretVisible && reportProfile.webPeriod.tradeSecretS)}"
									value="#{reportProfile.webPeriod.hoursPerYear}">
									<af:convertNumber type="number" maxFractionDigits="2"
										minFractionDigits="2" />
								</af:inputText>
						</af:panelForm>
					</af:panelForm>
					<af:panelForm maxColumns="1"
						rendered="#{!reportProfile.webPeriod.notInFacility && reportProfile.doRptCompare}" >
						<af:objectSpacer height="4" width="3" />
						<af:inputText label="Inventory Year" readOnly="true"
							value="Actual Hours" inlineStyle="font-weight:bold;" />
						<af:inputText label="#{reportProfile.report.reportYear}:"
							shortDesc="The year the emissions inventory covers"
							rendered="#{!reportProfile.tradeSecretVisible && reportProfile.webPeriod.orig.tradeSecretS}"
							value="#{reportProfile.webPeriod.orig.hoursPerYear==null?'':'XXXX'}"
							readOnly="true"
							inlineStyle="color: orange; font-weight: bold;#{reportProfile.webPeriod.hpyDiff? 'font-weight:bold; font-size:larger;' : '' }" />
						<af:inputText label="#{reportProfile.report.reportYear}:"
							shortDesc="The year the emissions inventory covers"
							rendered="#{reportProfile.tradeSecretVisible || !reportProfile.webPeriod.orig.tradeSecretS}"
							value="#{reportProfile.webPeriod.orig.hoursPerYear==null?'No Info':reportProfile.webPeriod.orig.hoursPerYear}"
							readOnly="true"
							inlineStyle="#{reportProfile.webPeriod.orig.tradeSecretS?'color: orange; font-weight: bold;':''}#{reportProfile.webPeriod.hpyDiff? 'color:orange; font-weight:bold; font-size:larger;' : '' }" />
						<af:inputText label="#{reportProfile.compareReport.reportYear}:"
							shortDesc="The year the emissions inventory covers"
							rendered="#{!reportProfile.tradeSecretVisible && reportProfile.webPeriod.comp.tradeSecretS}"
							value="#{reportProfile.webPeriod.comp.hoursPerYear==null?'No info':'XXXX'}"
							inlineStyle="#{reportProfile.webPeriod.comp.hoursPerYear!=null?'color: orange; font-weight: bold;':''}"
							readOnly="true" />
						<af:inputText label="#{reportProfile.compareReport.reportYear}:"
							shortDesc="The year the emissions inventory covers"
							rendered="#{reportProfile.tradeSecretVisible || !reportProfile.webPeriod.comp.tradeSecretS}"
							value="#{reportProfile.webPeriod.comp.hoursPerYear==null?'No info':reportProfile.webPeriod.comp.hoursPerYear}"
							inlineStyle="#{(reportProfile.webPeriod.comp.hoursPerYear!=null && reportProfile.webPeriod.comp.tradeSecretS)?'color: orange; font-weight: bold;':''}"
							readOnly="true" />
					</af:panelForm>
				</afh:cellFormat>
				<afh:cellFormat halign="left" width="50%">
					<af:panelForm maxColumns="2">
						<af:inputText label="Winter (Jan-Feb, Dec)%:" id="winter"
							showRequired="true" columns="10" maximumLength="10"
							readOnly="#{! reportProfile.editableM || reportProfile.winterReport}" 
							value="#{reportProfile.webPeriod.winterThroughputPct}"/>
						<af:inputText label="Spring (Mar-May)%:" id="spring"
							showRequired="true" columns="10" maximumLength="10"
							readOnly="#{! reportProfile.editableM || reportProfile.winterReport}"
							value="#{reportProfile.webPeriod.springThroughputPct}" />
						<af:inputText label="Summer (Jun-Aug)%:" id="summer"
							showRequired="true" columns="10" maximumLength="10"
							readOnly="#{! reportProfile.editableM || reportProfile.winterReport}" 
							value="#{reportProfile.webPeriod.summerThroughputPct}"/>
						<af:inputText label="Fall (Sep-Nov)%:" id="fall"
							showRequired="true" columns="10" maximumLength="10"
							readOnly="#{! reportProfile.editableM || reportProfile.winterReport}" 
							value="#{reportProfile.webPeriod.fallThroughputPct}"/>
							
						<af:objectSeparator/>
						
 						<af:inputText label="Hours of Operation (First Half Year)%:" id="hoursOfOperationFH"
							showRequired="true" columns="10" maximumLength="10"
							rendered="#{!reportProfile.feesSame}"
							autoSubmit="true" partialTriggers="hoursOfOperationSH"
							value="#{(!reportProfile.tradeSecretVisible && reportProfile.webPeriod.tradeSecretS)?'XXXX':reportProfile.webPeriod.firstHalfHrsOfOperationPct}"
							inlineStyle="#{reportProfile.webPeriod.tradeSecretS?'color: orange; font-weight: bold;':''}"
							valueChangeListener="#{reportProfile.webPeriod.updateSecondHalfHrsOfOperationPct}"
							readOnly="#{!reportProfile.editableM || (!reportProfile.tradeSecretVisible && reportProfile.webPeriod.tradeSecretS)}"/>
						<af:inputText label="Hours of Operation (Second Half Year)%:" id="hoursOfOperationSH"
							showRequired="true" columns="10" maximumLength="10"
							autoSubmit="true" partialTriggers="hoursOfOperationFH"
							rendered="#{!reportProfile.feesSame}"
							value="#{(!reportProfile.tradeSecretVisible && reportProfile.webPeriod.tradeSecretS)?'XXXX':reportProfile.webPeriod.secondHalfHrsOfOperationPct}"
							inlineStyle="#{reportProfile.webPeriod.tradeSecretS?'color: orange; font-weight: bold;':''}"
							valueChangeListener="#{reportProfile.webPeriod.updateFirstHalfHrsOfOperationPct}"
							readOnly="#{!reportProfile.editableM || (!reportProfile.tradeSecretVisible && reportProfile.webPeriod.tradeSecretS)}"/>
					</af:panelForm>
				</afh:cellFormat>
			</afh:rowLayout>
		</afh:tableLayout>
		<afh:tableLayout width="100%">
			<afh:rowLayout halign="center"
				rendered="#{reportProfile.webPeriod.autoPopulated}">
				<afh:cellFormat width="100%">
					<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
						value="The Schedule and Season % Information have been completed with the same values as the previous process you viewed or edited. If these values are not correct, please change them.#{reportProfile.savedWebPeriod.tradeSecretS?'&nbsp;&nbsp;<b>Is this schedule information a trade secret?&nbsp;&nbsp;If so, mark it trade secret here.</b>':''}" />
				</afh:cellFormat>
			</afh:rowLayout>
		</afh:tableLayout>
		<afh:tableLayout width="100%">
			<afh:rowLayout>
				<afh:cellFormat width="50%">
					<af:objectSpacer height="10" />
					<af:panelForm>
						<af:table value="#{reportProfile.periodMaterialWrapper}"
							rendered="#{!reportProfile.doRptCompare}"
							binding="#{reportProfile.periodMaterialWrapper.table}"
							id="mateialTab" banding="row" var="materialLine">
							<af:column headerText="Select Only One" id="selectM" rendered="#{reportProfile.periodMaterialWrapper.table.rowCount > 1}">
								<af:commandLink text="select" id="selectMButton1"
									rendered="#{reportProfile.materialSelected && !materialLine.notActive && reportProfile.editableM && (reportProfile.tradeSecretVisible || !reportProfile.webPeriod.tradeSecretS)}"
									disabled="#{materialLine.belongs}"
									action="dialog:confirmChangeMaterial" useWindow="true"
										windowWidth="600" windowHeight="300">
									<t:updateActionListener property="#{reportProfile.materialRow}"
										value="#{materialLine}" />
								</af:commandLink>
								<af:commandLink text="select" id="selectMButton2"
									rendered="#{!reportProfile.materialSelected && !materialLine.notActive && reportProfile.editableM && (reportProfile.tradeSecretVisible || !reportProfile.webPeriod.tradeSecretS)}"
									disabled="#{materialLine.belongs}"
									action="#{reportProfile.changeMaterial}">
									<t:updateActionListener property="#{reportProfile.materialRow}"
										value="#{materialLine}" />
								</af:commandLink>
								<af:outputText value="pending" inlineStyle="color: orange; font-weight: bold;"
									rendered="#{!reportProfile.editableM && !reportProfile.materialSelected}">
								</af:outputText>
								<af:outputText value="selected"
									rendered="#{!reportProfile.editableM && materialLine.belongs}">
								</af:outputText>
							</af:column>
							<%--<af:column headerText="Material Information">--%>
							<af:column sortProperty="c01" sortable="false" formatType="text"
								headerText="Material" id="material">
								<af:selectOneChoice value="#{materialLine.material}"
									readOnly="true"
									rendered="#{reportProfile.tradeSecretVisible || !reportProfile.webPeriod.tradeSecretS}">
									<f:selectItems
										value="#{facilityReference.materialDefs.items[(empty materialLine.material ? '' : materialLine.material)]}" />
								</af:selectOneChoice>
								<af:outputText value="expired SCC/Material combination"
								rendered="#{materialLine.notActive}"
								inlineStyle="color: orange; font-weight: bold;"
								shortDesc="This Material is no longer active for this SCC Code.  Pick a different Material or SCC Code">
							</af:outputText>
							</af:column>
							<af:column sortProperty="c02" sortable="false" headerText="Action">
								<af:outputText value="#{materialLine.action}"
									rendered="#{reportProfile.tradeSecretVisible || !reportProfile.webPeriod.tradeSecretS}" />
							</af:column>
							<%-- <af:column rendered="false" headerText="Trade Secret">
								<afh:rowLayout>
									<af:selectBooleanCheckbox readOnly="true"
										value="#{materialLine.tradeSecretM}"
										rendered="#{materialLine.belongs}" />
									<af:commandLink
										rendered="#{!reportProfile.editableM && materialLine.tradeSecretMText != null && materialLine.belongs}"
										text=" why" id="viewEditMaterialTS1" useWindow="true"
										inlineStyle="color: orange; font-weight: bold;" windowWidth="800"
										windowHeight="500"
										action="#{reportProfile.editViewMaterialTS}">
										<t:updateActionListener
											property="#{reportProfile.secretMaterialRow}"
											value="#{materialLine}" />
									</af:commandLink>
									<af:commandLink
										rendered="#{reportProfile.editableM && materialLine.belongs && reportProfile.tradeSecretVisible}"
										text="#{materialLine.tradeSecretMText == null?' add':' why'}"
										id="viewEditMaterialTS2" useWindow="true" windowWidth="800"
										windowHeight="500"
										action="#{reportProfile.editViewMaterialTS}">
										<t:updateActionListener
											property="#{reportProfile.secretMaterialRow}"
											value="#{materialLine}" />
									</af:commandLink>
								</afh:rowLayout>
							</af:column> --%>
							<%--</af:column>--%>
							<%--<af:column headerText="Throughput Information">--%>
							<af:column formatType="number" sortProperty="c03" sortable="false"
								headerText="Throughput">
								<af:inputText
									value="#{ reportProfile.throughputVisible ? materialLine.throughput:'Confidential'}"
									shortDesc="Amount of material (in Throughput Units) used annually"
									maximumLength="13" columns="13" id="Throughput"
									rendered="#{materialLine.belongs && materialLine.throughput!=null}"
									inlineStyle="#{!reportProfile.throughputVisible?'color: orange; font-weight: bold;':''}"
									readOnly="#{materialLine.notActive || !reportProfile.editableM || !reportProfile.throughputVisible || materialLine.throughput==null}">
									<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
								</af:inputText>
								<af:inputText value="#{materialLine.throughput}"
									shortDesc="Amount of material (in Throughput Units) used annually"
									maximumLength="13" columns="13" id="Throughput_"
									rendered="#{(reportProfile.editableM && materialLine.throughput==null && materialLine.belongs) && !reportProfile.doRptCompare}"
									readOnly="#{materialLine.notActive || !reportProfile.throughputVisible }">
									<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
								</af:inputText>
								<af:outputText value="pending" inlineStyle="color: orange; font-weight: bold;"
									shortDesc="Amount of material (in Throughput Units) used annually"
									rendered="#{(! reportProfile.editableM && materialLine.throughput==null && materialLine.belongs) && !reportProfile.doRptCompare}">
								</af:outputText>
							</af:column>
							<af:column rendered="#{reportProfile.confidentialLabelVisible}" headerText="Confidential">
								<afh:rowLayout partialTriggers="tsCheckBox">
  	 									<af:selectBooleanCheckbox 
  	 									disabled="#{!reportProfile.editableM ||reportProfile.disableConfidentialLink}" 
										valueChangeListener="#{reportProfile.TsValueChange}"
										id="tsCheckBox"
										value="#{materialLine.tradeSecretT}"
										rendered="true" autoSubmit="true"/>
 									<af:commandLink
 										disabled="#{reportProfile.disableConfidentialLink}"
										rendered="#{!reportProfile.eiConfidentialDataAccess && materialLine.tradeSecretT}"										
										text="Justification" id="viewEditThroughputTS1" useWindow="true"
										inlineStyle="color: orange; font-weight: bold;" windowWidth="400"
										windowHeight="250"
										partialTriggers="tsCheckBox"
										action="#{reportProfile.editViewThroughputTS}">
 									<t:updateActionListener
 											property="#{reportProfile.secretMaterialRow}"
 											value="#{materialLine}" />
									</af:commandLink> 
									<af:commandLink
										disabled="#{reportProfile.disableConfidentialLink}"
										rendered="#{reportProfile.eiConfidentialDataAccess && materialLine.tradeSecretT}"
										text="#{materialLine.tradeSecretTText == null?' add':'Justification'}"
										id="viewEditThroughputTS2" useWindow="true" windowWidth="400"
										windowHeight="250"
										partialTriggers="tsCheckBox"
										action="#{reportProfile.editViewThroughputTS}">
  										<t:updateActionListener
											property="#{reportProfile.secretMaterialRow}"
											value="#{materialLine}" />
									</af:commandLink>
								</afh:rowLayout>
							</af:column>							
							<af:column sortProperty="c04" sortable="false" formatType="text"
								headerText="Units">
								<af:selectOneChoice value="#{materialLine.measure}"
									shortDesc="The units used to measure throughput--any emissions factor is in these units"
									readOnly="true"
									rendered="#{reportProfile.tradeSecretVisible || !reportProfile.webPeriod.tradeSecretS}">
									<f:selectItems
										value="#{facilityReference.unitDefs.items[(empty materialLine.measure ? '' : materialLine.measure)]}" />
								</af:selectOneChoice>
							</af:column>
							<%--</af:column>--%>
						</af:table>
						<af:outputText rendered="#{reportProfile.materialSize > 1 && !reportProfile.doRptCompare && reportProfile.editableM}" 
							inlineStyle="font-size:100%; color:orange; font-weight: bold;"
							value="Caution: Changing the material will re-initialize the Process Emissions data."/>
					</af:panelForm>
				</afh:cellFormat>
				<afh:cellFormat width="50%">
					<af:panelForm>
						<af:table value="#{reportProfile.periodVariableWrapper}"
							rendered="#{!reportProfile.doRptCompare}"
							id="variableTab" banding="row" var="variableLine">
							<af:column headerText="Variable" formatType="icon">
								<af:outputText value="#{variableLine.variable}"
									rendered="#{reportProfile.tradeSecretVisible || !variableLine.tradeSecret}"
									shortDesc="Variable that appears in one of the factor formulas used when OEPA (auto calculate) factor used" />
							</af:column>
							<af:column formatType="number" headerText="Amount in #{reportProfile.webPeriod.currentMaterialName}">
								<af:inputText
									value="#{(reportProfile.tradeSecretVisible || !variableLine.tradeSecret)?variableLine.value:'XXXXX'}"
									maximumLength="13" shortDesc="value assigned to the variable"
									rendered="#{variableLine.value != null}"
									id="Amount"
									inlineStyle="#{variableLine.tradeSecret?'color: orange; font-weight: bold;':''}"
									columns="13" readOnly="#{! reportProfile.editableM}">
									<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
								</af:inputText>
								<af:inputText value="#{variableLine.value}" maximumLength="13"
									shortDesc="value assigned to the variable" id="Amount_"
									rendered="#{reportProfile.editableM && variableLine.value == null}"
									inlineStyle="#{variableLine.tradeSecret?'color: orange; font-weight: bold;':''}"
									columns="13"
									readOnly="#{!reportProfile.tradeSecretVisible && variableLine.tradeSecret}">
									<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
								</af:inputText>
								<af:outputText value="pending" inlineStyle="color: orange; font-weight: bold;"
									shortDesc="value assigned to the variable"
									rendered="#{(!reportProfile.editableM && variableLine.value == null) && !reportProfile.doRptCompare}">
								</af:outputText>
							</af:column>
							<af:column headerText="Units & Meaning">
								<af:outputText value="#{variableLine.meaning}"
									rendered="#{reportProfile.tradeSecretVisible || !variableLine.tradeSecret}"
									shortDesc="What does the amount mean?" />
							</af:column>
							<%--<af:column rendered="false" headerText="Trade Secret">
								<afh:rowLayout>
									<af:selectBooleanCheckbox readOnly="#{true}"
										value="#{variableLine.tradeSecret}" />
									<af:commandLink
										rendered="#{!reportProfile.editableM && variableLine.tradeSecretText != null}"
										text=" why" id="viewEditVariableTS" useWindow="true"
										inlineStyle="color: orange; font-weight: bold;" windowWidth="800"
										windowHeight="500"
										action="#{reportProfile.editViewVariableTS}">
										<t:updateActionListener
											property="#{reportProfile.secretVariableRow}"
											value="#{variableLine}" />
									</af:commandLink>
									<af:commandLink
										rendered="#{reportProfile.editableM && reportProfile.tradeSecretVisible}"
										text="#{variableLine.tradeSecretText == null?' add':' why'}"
										id="viewEditVariableTS2" useWindow="true" windowWidth="800"
										windowHeight="500"
										action="#{reportProfile.editViewVariableTS}">
										<t:updateActionListener
											property="#{reportProfile.secretVariableRow}"
											value="#{variableLine}" />
									</af:commandLink>
								</afh:rowLayout>
							</af:column>--%>
						</af:table>
						<af:outputText rendered="#{reportProfile.variableSize == 0 && !reportProfile.doRptCompare}" 
							inlineStyle="font-size:75%; color:#666"
							value="The variables table is empty because there are no variables in the formula associated with the FIRE rows for this process."/>
					</af:panelForm>
				</afh:cellFormat>
			</afh:rowLayout>
		</afh:tableLayout>
		<af:panelForm rendered="#{!reportProfile.doRptCompare}" rows="1">
			<af:showDetailHeader text="Explanation"
				disclosed="#{!reportProfile.webPeriod.zeroHours && !reportProfile.webPeriod.allNonVariablesSet && !reportProfile.doRptCompare &&
								!reportProfile.webPeriod.notInFacility && reportProfile.webPeriod.sccCode.sccId != null && reportProfile.nothingWrong}">
				<af:outputFormatted styleUsage="instruction"
					value="<p>To complete emissions reporting for this process, you have to provide values above for <b>Schedule</b>, <b>Season Percents</b> and <b>Material Throughput</b> in the units specified by <b>Units</b>.
					If there is a choice of more than one <b>Material</b>, you must select which is most appropriate, otherwise no action is needed on your part. The word pending appears each place
					a value is needed.</p>"/>
			</af:showDetailHeader>
			<af:showDetailHeader text="Explanation"
				disclosed="#{!reportProfile.webPeriod.zeroHours && !reportProfile.webPeriod.allVariablesSet && !reportProfile.doRptCompare &&
								!reportProfile.webPeriod.notInFacility && reportProfile.webPeriod.sccCode.sccId != null && reportProfile.nothingWrong}">
				<af:outputFormatted styleUsage="instruction"
					value="<p>A variable table appears for this process for the reason below and you must enter in the variable <b>Amount</b> as defined in the <b>Units & Meaning column</b>:
					The #{infraDefs.deqName} auto-calculate factor emissions method is used for a pollutant and it uses a factor formula from the FIRE database which uses the variable.</p>
					<p>The word pending appears each place a value for Amount is needed.  For example, if the Amount for <b>S</b> (% Sulfur content) is 7.5, that means the <b>Material</b> contains 7.5% sulfur
					and if <b>HCs</b> (Solid Heat Content (Btu/Lb)) is 12,540, that means there are 12,540 BTUs per pound of <b>Material</b>.</p>"/>
			</af:showDetailHeader>
		</af:panelForm>		<af:objectSpacer height="10" />
		<afh:rowLayout halign="center"
			rendered="#{reportProfile.webPeriod.zeroHours && !reportProfile.editableM && !reportProfile.doRptCompare}">
			<af:outputFormatted inlineStyle="color: rgb(0,0,255);"
				value="You indicated this process did not operate because either <b>Actual Hours</b> or <b>Throughput</b> is set to zero.  If this is correct, no further action is needed on this process." />
		</afh:rowLayout>
		<af:objectSpacer height="10"
			rendered="#{reportProfile.webPeriod.zeroHours && !reportProfile.editableM && !reportProfile.doRptCompare}" />
		<afh:rowLayout halign="center"
			rendered="#{reportProfile.mustSelectDiffMaterial && !reportProfile.editableM && !reportProfile.doRptCompare}">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="<b>The selected material can not be used for this report year.  Select a different one.</b>" />
		</afh:rowLayout>
		<af:objectSpacer height="10"
			rendered="#{reportProfile.mustSelectDiffMaterial && !reportProfile.editableM && !reportProfile.doRptCompare}" />
		<afh:rowLayout halign="center"
			rendered="#{!reportProfile.doRptCompare && !reportProfile.readOnlyUser}">
			<af:panelButtonBar
				rendered="#{!reportProfile.webPeriod.notInFacility}">
				<af:commandButton text="Edit Material/Schedule/Seasons"
					id ="editMaterialScheduleSeasonsButton"
					disabled="#{!reportProfile.modTV_SMTV}"
					action="#{reportProfile.editMaterial}"
					rendered="#{!reportProfile.editable && !reportProfile.editableM && reportProfile.internalEditable && !reportProfile.closedForEdit}" />
				<af:commandButton text="Save" action="#{reportProfile.saveMaterial}"
					id="saveButton"
					rendered="#{reportProfile.editableM}" />
				<af:commandButton text="Reset Schedule/Seasons"
					id="resetScheduleSeasonsButton"
					action="#{reportProfile.resetSchedSeason}"
					rendered="#{reportProfile.editableM}" />
				<af:commandButton text="Cancel" id="CancelM1"
					action="#{reportProfile.cancelMaterial}"
					rendered="#{reportProfile.editableM}" immediate="true" />
				<af:commandButton text="Create Group With This Process"
					id="createGroupWithThisProcessButton"
					disabled="#{!reportProfile.modTV_SMTV}"
					action="#{reportProfile.createGroup}"
					rendered="false" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</af:showDetailHeader>
	<afh:rowLayout halign="center"
		rendered="#{reportProfile.webPeriod.notInFacility}">
		<af:objectSpacer height="10" />
		<af:commandButton text="Delete Period From Report" id="deletePeriod"
			disabled="#{!reportProfile.modTV_SMTV}" useWindow="true"
			rendered="#{reportProfile.internalEditable && !reportProfile.closedForEdit}"
			windowWidth="700" windowHeight="250"
			action="#{reportProfile.requestDeletePeriod}" />
	</afh:rowLayout>
	<af:showDetailHeader text="Material/Throughput Comparison"
		disclosed="true"
		rendered="#{!reportProfile.webPeriod.notInFacility && reportProfile.doRptCompare}">
		<afh:tableLayout width="100%" halign="left">
			<afh:rowLayout>
				<afh:cellFormat width="50%">
					<af:objectSpacer height="10" />
					<af:panelForm>
						<af:table value="#{reportProfile.periodMaterialCompWrapper}"
							binding="#{reportProfile.periodMaterialCompWrapper.table}"
							bandingInterval="2" id="mateialTabC" banding="row"
							var="materialLine">
							<af:column sortProperty="inventoryYear"
								rendered="#{reportProfile.doRptCompare}" sortable="false"
								headerText="Inventory Year" formatType="text" width="75">
								<af:inputText label="Inventory Year"
									shortDesc="The year the emissions inventory covers"
									readOnly="true" value="#{materialLine.reportYear}" />
							</af:column>
							<af:column headerText="Material Information">
								<af:column sortProperty="c01" sortable="false" formatType="text"
									headerText="Material">
									<af:selectOneChoice value="#{materialLine.material}"
										inlineStyle="#{materialLine.materialDiff? 'color:orange; font-weight:bold; font-size:larger;' : '' } #{reportProfile.webPeriod.tradeSecretS? 'color: orange; font-weight: bold;' : '' }"
										readOnly="true"
										rendered="#{reportProfile.tradeSecretVisible || !materialLine.tradeSecretM}">
										<f:selectItems
											value="#{facilityReference.materialDefs.items[(empty materialLine.material ? '' : materialLine.material)]}" />
									</af:selectOneChoice>
								</af:column>
								<af:column sortProperty="c02" sortable="false"
									headerText="Action">
									<af:outputText value="#{materialLine.action}"
										rendered="#{reportProfile.tradeSecretVisible || !materialLine.tradeSecretM}" />
								</af:column>
								<%-- <af:column rendered="false" headerText="Trade Secret">
									<afh:rowLayout>
										<af:selectBooleanCheckbox readOnly="true"
											value="#{materialLine.tradeSecretM}" />
										<af:commandLink
											rendered="#{!reportProfile.editableM && materialLine.tradeSecretMText != null}"
											text=" why" id="viewEditMaterialTS1C" useWindow="true"
											inlineStyle="color: orange; font-weight: bold;" windowWidth="800"
											windowHeight="500"
											action="#{reportProfile.editViewMaterialTS}">
											<t:updateActionListener
												property="#{reportProfile.secretMaterialRow}"
												value="#{materialLine}" />
										</af:commandLink>
									</afh:rowLayout>
								</af:column> --%>
							</af:column>
							<af:column headerText="Throughput Information">
								<af:column formatType="number" sortProperty="c03"
									sortable="false" headerText="Throughput">
									<af:inputText value="#{materialLine.throughput}"
										maximumLength="13" columns="13" readOnly="true"
										rendered="#{(reportProfile.tradeSecretVisible || !materialLine.tradeSecretM) && materialLine.throughput != null}"
										inlineStyle="#{materialLine.throughputDiff? 'color:orange; font-weight:bold; font-size:larger;' : '' } #{materialLine.tradeSecretM?'color: orange; font-weight: bold;':''}">
										<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
									</af:inputText>
									<af:inputText value="XXXXX" readOnly="true"
										rendered="#{!reportProfile.tradeSecretVisible && materialLine.tradeSecretM && materialLine.throughput != null}"
										inlineStyle="color: orange; font-weight: bold;#{materialLine.throughputDiff?'font-weight:bold; font-size:larger;':''}">
									</af:inputText>
									<af:inputText value="No info" readOnly="true"
										rendered="#{materialLine.throughput == null}"
										inlineStyle="#{materialLine.throughputDiff?'color:orange; font-weight:bold; font-size:larger;':''}">
									</af:inputText>
								</af:column>
								<af:column sortProperty="c04" sortable="false" formatType="text"
									headerText="Units">
									<af:selectOneChoice value="#{materialLine.measure}"
										readOnly="true"
										rendered="#{reportProfile.tradeSecretVisible || !materialLine.tradeSecretM}">
										<f:selectItems
											value="#{facilityReference.unitDefs.items[(empty materialLine.measure ? '' : materialLine.measure)]}" />
									</af:selectOneChoice>
								</af:column>
								<%-- <af:column rendered="false" headerText="Trade Secret">
									<afh:rowLayout>
										<af:selectBooleanCheckbox readOnly="true"
											value="#{materialLine.tradeSecretT}" />
										<af:commandLink
											rendered="#{!reportProfile.editableM && materialLine.tradeSecretTText != null}"
											text=" why" id="viewEditThroughputTS1C" useWindow="true"
											inlineStyle="color: orange; font-weight: bold;" windowWidth="800"
											windowHeight="500"
											action="#{reportProfile.editViewThroughputTS}">
											<t:updateActionListener
												property="#{reportProfile.secretMaterialRow}"
												value="#{materialLine}" />
										</af:commandLink>
									</afh:rowLayout>
								</af:column> --%>
							</af:column>
						</af:table>
					</af:panelForm>
				</afh:cellFormat>
				<afh:cellFormat width="50%">
					<af:panelForm
						rendered="#{reportProfile.periodVariableCompWrapper.table.rowCount > 0}">
						<af:table value="#{reportProfile.periodVariableCompWrapper}"
							binding="#{reportProfile.periodVariableCompWrapper.table}"
							bandingInterval="2" id="variableTab" banding="row"
							var="variableLine">
							<af:column sortProperty="reportYear"
								rendered="#{reportProfile.doRptCompare}" sortable="false"
								headerText="Inventory Year" formatType="text" width="75">
								<af:inputText label="Inventory Year"
									shortDesc="The year the emissions inventory covers"
									readOnly="true" value="#{variableLine.reportYear}" />
							</af:column>
							<af:column headerText="Variable" formatType="icon">
								<af:outputText value="#{variableLine.variable}"
									rendered="#{reportProfile.tradeSecretVisible || !variableLine.tradeSecret}" />
							</af:column>
							<af:column formatType="number" headerText="Amount in Material">
								<af:inputText value="#{variableLine.value}" maximumLength="13"
									columns="13"
									inlineStyle="#{variableLine.diffMark? 'color:orange; font-weight:bold; font-size:larger;' : '' } #{variableLine.tradeSecret?'color: orange; font-weight: bold;':''}"
									rendered="#{reportProfile.tradeSecretVisible || !variableLine.tradeSecret}"
									readOnly="true">
									<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
								</af:inputText>
								<af:inputText value="XXXXX" readOnly="true"
									rendered="#{variableLine.value != null && !reportProfile.tradeSecretVisible && variableLine.tradeSecret}"
									inlineStyle="color: orange; font-weight: bold;#{variableLine.diffMark?'font-weight:bold; font-size:larger;' : ''}" />
								<af:inputText value="No info" readOnly="true"
									rendered="#{variableLine.value == null}"
									inlineStyle="#{variableLine.diffMark?'color:orange; font-weight:bold; font-size:larger;':''}">
								</af:inputText>
							</af:column>
							<af:column headerText="Units & Meaning">
								<af:outputText value="#{variableLine.meaning}"
									rendered="#{reportProfile.tradeSecretVisible || !variableLine.tradeSecret}"
									shortDesc="What does the amount mean?" />
							</af:column>
							<%-- <af:column rendered="false" headerText="Trade Secret">
								<afh:rowLayout>
									<af:selectBooleanCheckbox readOnly="#{true}"
										value="#{variableLine.tradeSecret}" />
									<af:commandLink
										rendered="#{!reportProfile.editableM && variableLine.tradeSecretText != null}"
										text=" why" id="viewEditVariableTSc" useWindow="true"
										inlineStyle="color: orange; font-weight: bold;" windowWidth="800"
										windowHeight="500"
										action="#{reportProfile.editViewVariableTS}">
										<t:updateActionListener
											property="#{reportProfile.secretVariableRow}"
											value="#{variableLine}" />
									</af:commandLink>
									<af:commandLink
										rendered="#{reportProfile.editableM && reportProfile.tradeSecretVisible}"
										text="#{variableLine.tradeSecretText == null?' add':' why'}"
										id="viewEditVariableTSc2" useWindow="true" windowWidth="800"
										windowHeight="500"
										action="#{reportProfile.editViewVariableTS}">
										<t:updateActionListener
											property="#{reportProfile.secretVariableRow}"
											value="#{variableLine}" />
									</af:commandLink>
								</afh:rowLayout>
							</af:column> --%>
						</af:table>
					</af:panelForm>
				</afh:cellFormat>
			</afh:rowLayout>
		</afh:tableLayout>
	</af:showDetailHeader>
	
	<%--<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0"
		rendered="#{!reportProfile.webPeriod.zeroHours && !reportProfile.webPeriod.allNonVariablesSet && !reportProfile.doRptCompare &&
				!reportProfile.webPeriod.notInFacility && reportProfile.webPeriod.sccCode.sccId != null && reportProfile.nothingWrong}">
		<afh:rowLayout halign="left">
			<af:panelGroup>
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="<br>You can not complete emissions reporting for this process until you have provided values above for the " />
				<af:outputFormatted value="<b>Schedule</b>" />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;" value=", " />
				<af:outputFormatted value="<b>Season Percents</b>" />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;" value=" and Material " />
				<af:outputFormatted value="<b>Throughput</b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="in the units specified by " />
				<af:outputFormatted value="<b>X-Units</b>" />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value=".&nbsp;&nbsp;If there is a choice of more than one " />
				<af:outputFormatted value="<b>Material</b>" />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value=", you must select which is most appropriate, otherwise no action is needed on your part.  The word " />
				<af:outputFormatted value="<b>pending</b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="appears each place a value is needed.<br>" />
			</af:panelGroup>
		</afh:rowLayout>
	</afh:tableLayout>
	<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0"
		rendered="#{!reportProfile.webPeriod.zeroHours && !reportProfile.webPeriod.allVariablesSet && !reportProfile.doRptCompare &&
				!reportProfile.webPeriod.notInFacility && reportProfile.webPeriod.sccCode.sccId != null && reportProfile.nothingWrong}">
		<afh:rowLayout halign="left">
			<af:panelGroup>
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="<br>A variable table appears for this process for #{(reportProfile.factorFormulaUsed && reportProfile.webPeriod.reqVars != null)?'one of the reasons ':'the reason '}below and you must enter in the variable " />
				<af:outputFormatted value="<b>Amount</b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="as defined in the " />
				<af:outputFormatted value="<b>Units & Meaning</b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="column<b>:</b>" />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="<lu><li>Some Criteria Air Pollutants and Hazardous Air Pollutants (HAPs) are typically emitted from this process and their calculated emissions can be based on FIRE database formulas that use #{reportProfile.webPeriod.reqVars}.</lu></li>"
					rendered="#{reportProfile.webPeriod.reqVars != null}" />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="<lu><li>The OEPA auto-calculate factor emissions method is used for a pollutant and it uses a factor formula from the FIRE database which uses the variable.</lu></li>"
					rendered="#{reportProfile.factorFormulaUsed}" />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="<br><br>The word " />
				<af:outputFormatted value="<b>pending </b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="appears each place a value for " />
				<af:outputFormatted value="<b>Amount </b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="is needed.&nbsp;&nbsp;For example, if the " />
				<af:outputFormatted value="<b>Amount </b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;" value="for " />
				<af:outputFormatted value="<b>S</b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="(% Sulfur content) is 7.5, that means the " />
				<af:outputFormatted value="<b>Material </b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="contains 7.5% sulfur and if " />
				<af:outputFormatted value="<b>HCs </b> " />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="(Solid Heat Content (Btu/Lb)) is 12,540, that means there are 12,540 BTUs per pound of " />
				<af:outputFormatted value="<b>Material</b>" />
				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;" value="<b>.</b>" />
			</af:panelGroup>
		</afh:rowLayout>
	</afh:tableLayout>--%>
	<af:showDetailHeader
		text="#{reportProfile.doRptCompare?'Process Emissions Comparison':'Process Emissions'}"	
		disclosed="true"	
		rendered="#{!reportProfile.webPeriod.notInFacility && reportProfile.webPeriod.sccCode.sccId != null && reportProfile.nothingWrong && (!reportProfile.webPeriod.zeroHours || reportProfile.doRptCompare)}"
		partialTriggers="EmissionsTab:methodId EmissionsTab:HoursUncontrolled EmissionsTab:annAdjAll EmissionsTab:annAdjAll2 EmissionsTab2:AddEmission EmissionsTab2:methodId EmissionsTab2:HoursUncontrolled EmissionsTab2:annAdjAll EmissionsTab2:annAdjAll2">
		<afh:rowLayout halign="center">
			<af:table value="#{reportProfile.emissionPeriodWrapper}"
				bandingInterval="#{reportProfile.doRptCompare?2:1}"
				binding="#{reportProfile.emissionPeriodWrapper.table}"
				id="EmissionsTab" banding="row" width="1250" var="emissionLine"
				rows="#{reportProfile.pageLimit}">
				<%@ include file="emissionsTable.jsp"%>
				<f:facet name="footer">
					<h:panelGrid width="100%">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton text="Add Emission" id="AddEmission"
									action="#{reportProfile.addEmission}"
									rendered="#{reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.renderSum && !reportProfile.hapTable}">
								</af:commandButton>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
							<af:objectSpacer width="150" height="3"
								rendered="#{!reportProfile.editable}" />
							<%--
							<af:outputLabel value="Total Chargeable Pollutants:"
								rendered="#{!reportProfile.editable}" />
							<af:objectSpacer width="7" height="5" />
							<af:outputFormatted value="#{reportProfile.displayTotal}"
								rendered="#{!reportProfile.editable}" />
							--%>
						</afh:rowLayout>
					</h:panelGrid>
				</f:facet>
			</af:table>
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:objectSpacer width="150" height="10" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:outputFormatted
				value="<b>_____________________________________________________________________________________________________________</b>"
				rendered="#{reportProfile.hapTable}" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:outputFormatted value="#{reportProfile.hapNonAttestationMsg}"
				inlineStyle="font-size:75%" rendered="#{reportProfile.hapTable}" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:table value="#{reportProfile.emissionPeriodWrapperHAP}"
				rendered="#{reportProfile.hapTable}"
				bandingInterval="#{reportProfile.doRptCompare?2:1}"
				binding="#{reportProfile.emissionPeriodWrapperHAP.table}"
				id="EmissionsTab2" banding="row" width="1250" var="emissionLine"
				rows="#{reportProfile.displayHapRows}">
				<%@ include file="emissionsTableHAPs.jsp"%>
				<f:facet name="footer">
					<h:panelGrid width="100%">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>							
								<af:commandButton text="Display Some Rows" id="displaySomeRows"
									action="#{reportProfile.displaySomeRows}"
									inlineStyle="font-weight:bold; background-color: rgb(255,255,119)"
									rendered="#{reportProfile.showDisplaySome}">
								</af:commandButton>
								<af:commandButton text="Add Emission" id="AddEmission"
									action="#{reportProfile.addEmission}"
									rendered="#{reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.renderSum}">
								</af:commandButton>
								<af:commandButton text="Delete Selected Emission(s)" id="deleteSelectedEmissionsButton"
									shortDesc="Only emissions not pre-specified by report or FIRE database may be deleted"
									rendered="#{reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.renderSum}"
									action="#{reportProfile.deleteEmission}">
								</af:commandButton>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</h:panelGrid>
				</f:facet>
			</af:table>
		</afh:rowLayout>
		<af:objectSpacer height="10" />
		<afh:rowLayout halign="center">
			<af:panelButtonBar
				rendered="#{!reportProfile.webPeriod.notInFacility && !reportProfile.doRptCompare && !reportProfile.readOnlyUser}" onmouseover="triggerPeriodUpdate();">
				<af:commandButton
					text="#{reportProfile.expandedExp ? 'Edit Explanations': 'Edit Emissions'}"
					id="edit2" disabled="#{!reportProfile.modTV_SMTV || !reportProfile.materialSelected}"
					action="#{reportProfile.editEmission}"
					rendered="#{!reportProfile.editableM && !reportProfile.editable && reportProfile.internalEditable && !reportProfile.closedForEdit}" />
				<af:commandButton text="Save" action="#{reportProfile.saveEmission}"
					id="saveEmissionsButton"
					rendered="#{reportProfile.editable}" />
				<af:commandButton text="Cancel" id="CancelE2"
					action="#{reportProfile.cancelEmissionEdit}"
					rendered="#{reportProfile.editable}" immediate="true" />
			</af:panelButtonBar>
			<af:objectSpacer width="5" />
			<af:panelButtonBar>
				<af:commandButton text="Exit Comparison Mode"
					action="#{reportProfile.compareReportOK}"
					rendered="#{reportProfile.doRptCompare}" />
				<%-- hide these buttons since we don't want expand/collapse explanation capabilities in IMPACT
				<af:commandButton text="Expand Explanations"
					action="#{reportProfile.expandExp}"
					rendered="#{!reportProfile.expandedExp}" />
				<af:commandButton text="Collapse Explanations"
					action="#{reportProfile.collapseExp}"
					rendered="#{reportProfile.expandedExp}" />
				<af:commandButton text="Expand Explanations More"
					action="#{reportProfile.expandExpMore}"
					rendered="#{reportProfile.expandedExp && reportProfile.editable}" /> --%>
			</af:panelButtonBar>
		</afh:rowLayout>
	</af:showDetailHeader>
</af:panelGroup>

<f:verbatim>
	<script type="text/javascript">
				function triggerPeriodUpdate(){
					var activeId = document.activeElement.id;
					if(activeId != null){
			    		input = document.getElementById(activeId);
			    		input.blur();
					} 
				}
			</script>
</f:verbatim>

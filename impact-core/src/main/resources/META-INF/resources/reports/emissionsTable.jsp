<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:column sortProperty="inventoryYear"
	rendered="#{reportProfile.doRptCompare}" sortable="false"
	headerText="Inventory Year" formatType="text" width="75">
	<af:inputText label="Inventory Year"
		shortDesc="The year the emissions inventory covers" readOnly="true"
		value="#{emissionLine.reportYear}" />
</af:column>
<af:column sortProperty="c01" sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}"
	rendered="#{reportProfile.expandedExp}" formatType="text"
	headerText="Pollutant" id="pollutant">
	<af:selectOneChoice value="#{emissionLine.pollutantCd}"
		readOnly="#{!reportProfile.editable || !emissionLine.newLine}">
		<f:selectItems
			value="#{facilityReference.nonToxicPollutantDefs.items[(empty emissionLine.pollutantCd ? '' : emissionLine.pollutantCd)]}" />
	</af:selectOneChoice>
</af:column>
<af:column headerText="Criteria Air Pollutants/Other" sortable="false" sortProperty="capSection"
	rendered="#{!reportProfile.expandedExp}" width="350">
	<af:column sortProperty="Pollutant" sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}"
		formatType="text" headerText="Pollutant" id="pollutant2" width="300">
		<af:selectOneChoice value="#{emissionLine.pollutantCd}"
			readOnly="#{!reportProfile.editable || !emissionLine.newLine}">
			<f:selectItems
				value="#{facilityReference.nonToxicPollutantDefs.items[(empty emissionLine.pollutantCd ? '' : emissionLine.pollutantCd)]}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="Pollutant" sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}" rendered="false"
		headerText="Code" formatType="text" width="75">
		<af:outputText value="#{emissionLine.pollutantCd}" truncateAt="50" shortDesc="#{emissionLine.pollutantCd}"/>
	</af:column>
</af:column>
<af:column headerText=" " sortProperty="c04" sortable="false"
	rendered="#{!reportProfile.editable && !reportProfile.expandedExp && !reportProfile.doRptCompare && reportProfile.renderComponent != 'emissionPeriods'}"
	formatType="icon"  width="50">
	<af:commandButton text="Locate" id="locate"
		rendered="#{!reportProfile.expandedExp && reportProfile.renderComponent != 'emissionPeriods'}"
		shortDesc="Click to see where report has emissions of this pollutant"
		action="#{reportProfile.locatePollutant}">
		<t:updateActionListener
			property="#{reportProfile.emissionRowPollutant}"
			value="#{emissionLine}" />
	</af:commandButton>
</af:column>




<af:column sortProperty="Method Used" sortable="false"
	formatType="text" headerText="Method Used" width="250"
	rendered="#{!reportProfile.renderSum && !reportProfile.expandedExp}">
	<af:commandLink useWindow="true" windowWidth="1200" windowHeight="700"
		text="Throughput-based factor (pending)"
		action="#{reportProfile.viewExplanation}"
		rendered="#{(emissionLine.emissionCalcMethodCd == 109) && (emissionLine.fireRef == null) && (empty emissionLine.factorNumericValue) && (emissionLine.numFireRows > 1) && !reportProfile.editable && !reportProfile.doRptCompare && reportProfile.renderComponent == 'emissionPeriods'}">
		<t:updateActionListener property="#{reportProfile.calcEmissionRow}"
			value="#{emissionLine}" />
		<t:updateActionListener property="#{reportProfile.emissionRowMethod}"
			value="#{emissionLine}" />
	</af:commandLink>
	<af:commandLink useWindow="true" windowWidth="1200" windowHeight="700"
		action="#{reportProfile.viewExplanation}"
		rendered="#{!reportProfile.editable && reportProfile.renderComponent == 'emissionPeriods'}">
		<t:updateActionListener property="#{reportProfile.calcEmissionRow}"
			value="#{emissionLine}" />
		<t:updateActionListener property="#{reportProfile.emissionRowMethod}"
			value="#{emissionLine}" />
		<af:selectOneChoice value="#{emissionLine.emissionCalcMethodCd}"
			rendered="#{emissionLine.emissionCalcMethodCd != null && (emissionLine.emissionCalcMethodCd != 109 || (emissionLine.emissionCalcMethodCd == 109 && (emissionLine.numFireRows <= 1 || (emissionLine.numFireRows > 1 && !empty emissionLine.factorNumericValue))))}"
			readOnly="#{!reportProfile.editable}" id="methodIdReadOnly"
			valueChangeListener="#{emissionLine.emissionCalcMethodChangeListener}"
			unselectedLabel=" "
			shortDesc="Method the facility used for calculating emissions"
			inlineStyle="#{emissionLine.emissionCalcMethodDiff? 'color:orange; font-weight:bold; font-size:larger;' : '' }">
			<f:selectItems
				value="#{facilityReference.emissionCalcMethodDefs.items[(empty emissionLine.emissionCalcMethodCd ? '' : emissionLine.emissionCalcMethodCd)]}" />
		</af:selectOneChoice>
	</af:commandLink>
	<af:selectOneChoice value="#{emissionLine.emissionCalcMethodCd}"
		rendered="#{reportProfile.editable}"
		readOnly="#{!reportProfile.editable}" id="methodId" autoSubmit="true"
		valueChangeListener="#{emissionLine.emissionCalcMethodChangeListener}"
		unselectedLabel=" "
		shortDesc="Method the facility used for calculating emissions"
		inlineStyle="#{emissionLine.emissionCalcMethodDiff? 'color:orange; font-weight:bold; font-size:larger;' : '' }">
		<f:selectItems
			value="#{facilityReference.userSelectableEmissionCalcMethodDefs.items[(empty emissionLine.emissionCalcMethodCd ? '' : emissionLine.emissionCalcMethodCd)]}" />
	</af:selectOneChoice>
	<af:inputText value="No Info" readOnly="true"
		shortDesc="Method the facility used for calculating emissions"
		rendered="#{emissionLine.emissionCalcMethodDiff && emissionLine.emissionCalcMethodCd == null && !reportProfile.editable}"
		inlineStyle="color:orange; font-weight:bold; font-size:larger;" />
	<af:commandLink useWindow="true" windowWidth="1200" windowHeight="700"
		action="#{reportProfile.viewExplanation}"
		rendered="#{!reportProfile.editable && reportProfile.renderComponent == 'emissionPeriods'}">
		<t:updateActionListener property="#{reportProfile.calcEmissionRow}"
			value="#{emissionLine}" />
		<t:updateActionListener property="#{reportProfile.emissionRowMethod}"
			value="#{emissionLine}" />
		<af:outputText value="pending"
			inlineStyle="color: orange; font-weight: bold;"
			rendered="#{(!reportProfile.renderSum && ! reportProfile.editable && emissionLine.emissionCalcMethodCd==null) && !reportProfile.doRptCompare}"
			shortDesc="Method the facility used for calculating emissions">
		</af:outputText>
	</af:commandLink>
	<af:outputText value="expired factor"
		rendered="#{(emissionLine.expiredFire && (emissionLine.emissionCalcMethodCd == 109) && (emissionLine.fireRef != null) && (emissionLine.numFireRows > 0) && ! reportProfile.editable) && !reportProfile.doRptCompare}"
		inlineStyle="color: orange; font-weight: bold;"
		shortDesc="This factor is no longer active.  Pick a different factor or different calculation method">
	</af:outputText>
	<af:outputText value="Uncontrolled factor input by user. Available factors: #{emissionLine.activeFireRows}"
		rendered="#{emissionLine.factorNumericValueOverride && emissionLine.activeFireRows > 0 && !reportProfile.editable && !reportProfile.doRptCompare}"
		shortDesc="This factor was entered by the user and WebFIRE factors are available.">
	</af:outputText>
	<af:outputText value="Uncontrolled factor input by user."
		rendered="#{emissionLine.factorNumericValueOverride && emissionLine.activeFireRows <= 0 && !reportProfile.editable && !reportProfile.doRptCompare}"
		shortDesc="This factor was entered by the user and no WebFIRE factors are available.">
	</af:outputText>
	<af:outputText value="Available factors: #{emissionLine.activeFireRows}"
		rendered="#{!emissionLine.factorNumericValueOverride && emissionLine.activeFireRows > 0 && !reportProfile.editable && !reportProfile.doRptCompare}"
		shortDesc="The number of available factors.">
	</af:outputText>
</af:column>




<af:column sortProperty="annualAdjust" sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}"
	rendered="#{!reportProfile.renderSum && !reportProfile.expandedExp}"
	headerText="Hours Uncontrolled" formatType="number" width="50">
	<af:inputText id="HoursUncontrolled" autoSubmit="true"
		shortDesc="Number of hours of operation during which controls were off"
		readOnly="#{! reportProfile.editable || reportProfile.renderSum || (emissionLine.emissionCalcMethodCd == null || emissionLine.emissionCalcMethodCd > 200) && null==emissionLine.annualAdjust}"
		value="#{emissionLine.annualAdjust}" maximumLength="8" columns="8">
		<mu:convertSigDigNumber pattern="#,##0.##" />
	</af:inputText>
	<af:selectBooleanCheckbox readOnly="false" rendered="#{emissionLine.chgAllAnnualAdjRender && emissionLine.adjustExists}"
			id="annAdjAll" value="#{emissionLine.chgAllAnnualAdj}"
			text="Set all to #{emissionLine.annualAdjust}" autoSubmit="true"
			shortDesc="Set the same number of hours uncontrolled everywhere needed" />
	<af:selectBooleanCheckbox readOnly="false" rendered="#{emissionLine.chgAllAnnualAdjRender && !emissionLine.adjustExists}"
			id="annAdjAll2" value="#{emissionLine.chgAllAnnualAdj}"
			text="Clear all" autoSubmit="true"
			shortDesc="Set the same number of hours uncontrolled everywhere needed" />
	<af:outputText value="pending" inlineStyle="color: orange; font-weight: bold;"
		rendered="#{(! reportProfile.editable && emissionLine.annualAdjust==null && emissionLine.emissionCalcMethodCd < 200) && !reportProfile.doRptCompare}"
		shortDesc="Needed when factor is used: Number of hours of operation during which controls were off">
	</af:outputText>
</af:column>





<af:column formatType="number" sortProperty="factorNumericValue"
	rendered="#{!reportProfile.renderSum && !reportProfile.expandedExp}"
	sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}"
	headerText="Uncontrolled Emissions Factor (Lbs/Throughput Units)"
	width="50">
	<af:inputText value="#{emissionLine.factorNumericValue}" id="Uncontrolled_Emissions_Factor"
		shortDesc="Factor to determine emissions prior to controls"
		maximumLength="10" columns="10"
		inlineStyle="#{emissionLine.factorNumericValueDiff? 'color:orange; font-weight:bold; font-size:larger;' : '' } #{emissionLine.tradeSecretF?'color: orange;' : ''} #{(emissionLine.emissionCalcMethodCd == null || (!emissionLine.factorNumericValueOverride && emissionLine.emissionCalcMethodCd > 200 && !reportProfile.editable) || (emissionLine.emissionCalcMethodCd > 200 && reportProfile.editable))? 'color: silver;':'' }"
		rendered="true"
		readOnly="#{ !reportProfile.editable || reportProfile.renderSum || emissionLine.emissionCalcMethodCd == null || emissionLine.emissionCalcMethodCd > 200}"
		valueChangeListener="#{emissionLine.factorNumericValueChangeListener}" autoSubmit="true">
		<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
	</af:inputText>
	<af:inputText value="XXXXX" readOnly="true"
		shortDesc="Factor to determine emissions prior to controls"
		rendered="#{!reportProfile.tradeSecretVisible && emissionLine.tradeSecretF && emissionLine.factorNumericValue != null && !reportProfile.editable}"
		inlineStyle="font-weight:bold; font-size:larger; color: orange; font-weight: bold;" />
	<af:inputText value="No Info" readOnly="true"
		shortDesc="Factor to determine emissions prior to controls"
		rendered="#{emissionLine.factorNumericValueDiff && emissionLine.factorNumericValue == null && !reportProfile.editable}"
		inlineStyle="color:orange; font-weight:bold; font-size:larger;" />
	<af:outputText value="pending variable amount" inlineStyle="color: orange; font-weight: bold;"
		rendered="#{(emissionLine.emissionCalcMethodCd == 109 && emissionLine.fireRef != null && emissionLine.factorNumericValue == null && ! reportProfile.editable) && !reportProfile.doRptCompare}"
		shortDesc="Factor formula requires a variable. The formula variables have not been given values">
	</af:outputText>
	<af:outputText value="pending" inlineStyle="color: orange; font-weight: bold;"
		shortDesc="Factor to determine emissions prior to controls"
		rendered="#{(!reportProfile.renderSum && ! reportProfile.editable && emissionLine.factorNumericValue==null && emissionLine.emissionCalcMethodCd < 200) && !reportProfile.doRptCompare}">
	</af:outputText>
</af:column>






<af:column formatType="number" sortProperty="c081"
	rendered="#{!reportProfile.renderSum && !reportProfile.expandedExp}"
	sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}"
	headerText="Time-based Factor (LBS/Hour)">
	<af:inputText value="#{emissionLine.timeBasedFactorNumericValue}" id="Time_Based_Emissions_Factor"
		shortDesc="Factor to determine controlled emissions"
		maximumLength="10" columns="10"
		inlineStyle="#{emissionLine.timeBasedFactorNumericValueDiff? 'color:orange; font-weight:bold; font-size:larger;' : '' } #{emissionLine.tradeSecretF?'color: orange;' : ''}"
		rendered="#{(reportProfile.tradeSecretVisible || !emissionLine.tradeSecretF) && emissionLine.emissionCalcMethodCd < 100}"
		readOnly="#{!reportProfile.editable || reportProfile.renderSum || (emissionLine.emissionCalcMethodCd == null)}">
		<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
	</af:inputText>
	<af:inputText value="XXXXX" readOnly="true"
		shortDesc="Factor to determine emissions prior to controls"
		rendered="#{!reportProfile.tradeSecretVisible && emissionLine.tradeSecretF && emissionLine.timeBasedFactorNumericValue != null && !reportProfile.editable}"
		inlineStyle="font-weight:bold; font-size:larger; color: orange;" />
	<af:inputText value="No Info" readOnly="true"
		shortDesc="Factor to determine emissions prior to controls"
		rendered="#{emissionLine.timeBasedFactorNumericValueDiff && emissionLine.timeBasedFactorNumericValue == null && !reportProfile.editable}"
		inlineStyle="color:orange; font-weight:bold; font-size:larger;" />
	<af:outputText value="pending"
		shortDesc="Factor to determine controlled emissions"
		rendered="#{(!reportProfile.renderSum && !reportProfile.editable && emissionLine.timeBasedFactorNumericValue==null && emissionLine.emissionCalcMethodCd < 100) && !reportProfile.doRptCompare}"
		inlineStyle="color: orange; font-weight: bold;">
	</af:outputText>
</af:column>



<af:column rendered="false" headerText="Trade Secret" sortable="false" sortProperty="tradeSecretF">
	<afh:rowLayout>
		<af:selectBooleanCheckbox readOnly="#{true}"
			value="#{emissionLine.tradeSecretF}" />
		<af:commandLink
			rendered="#{!reportProfile.editable && emissionLine.tradeSecretFText != null}"
			text="why" id="viewEditFactorTS1" useWindow="true"
			inlineStyle="color: orange; font-weight: bold;" windowWidth="800"
			windowHeight="500" action="#{reportProfile.editViewFactorTS}">
			<t:updateActionListener property="#{reportProfile.secretEmissionRow}"
				value="#{emissionLine}" />
		</af:commandLink>
		<af:commandLink
			rendered="#{reportProfile.editable && reportProfile.tradeSecretVisible}"
			text="#{emissionLine.tradeSecretFText == null?' add':' why'}"
			id="viewEditFactorTS2" useWindow="true" windowWidth="800"
			windowHeight="500" action="#{reportProfile.editViewFactorTS}">
			<t:updateActionListener property="#{reportProfile.secretEmissionRow}"
				value="#{emissionLine}" />
		</af:commandLink>
	</afh:rowLayout>
</af:column>


<af:column headerText="Emissions Reported" sortable="false" sortProperty="emissionsReported"
	rendered="#{!reportProfile.expandedExp}" width="300">

	<af:column formatType="number" sortProperty="fugitiveEmissionsV"
		sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}" headerText="Fugitive Amount" width="90">
		<af:inputText value="#{emissionLine.fugitiveEmissions}"
			maximumLength="12" columns="12" id="Fugitive_Amount"
			readOnly="#{! reportProfile.editable || reportProfile.renderSum || (emissionLine.emissionCalcMethodCd == null || emissionLine.emissionCalcMethodCd < 200)}"
			inlineStyle="#{emissionLine.fugitiveEmissionsDiff? 'color:orange; font-weight:bold; font-size:larger;' : '' }">
			<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
		</af:inputText>
		<af:inputText value="No Info" readOnly="true"
			rendered="#{emissionLine.fugitiveEmissionsDiff && !reportProfile.editable && emissionLine.fugitiveEmissions == null}"
			inlineStyle="color:orange; font-weight:bold; font-size:larger;" />
		<af:outputText value="pending" inlineStyle="color: orange; font-weight: bold;"
			rendered="#{(!reportProfile.renderSum && !reportProfile.editable && emissionLine.fugitiveEmissions==null && emissionLine.emissionCalcMethodCd > 200) && !reportProfile.doRptCompare}">
		</af:outputText>
	</af:column>



	<af:column formatType="number" sortProperty="stackEmissionsV"
		sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}" headerText="Stack Amount" width="90">
		<af:inputText value="#{emissionLine.stackEmissions}"
			maximumLength="12" columns="12" id="Stack_Amount"
			readOnly="#{! reportProfile.editable || reportProfile.renderSum ||(emissionLine.emissionCalcMethodCd == null || emissionLine.emissionCalcMethodCd < 200)}"
			inlineStyle="#{emissionLine.stackEmissionsDiff? 'color:orange; font-weight:bold; font-size:larger;' : '' }">
			<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
		</af:inputText>
		<af:inputText value="No Info" readOnly="true"
			rendered="#{emissionLine.stackEmissionsDiff && !reportProfile.editable && emissionLine.stackEmissions == null}"
			inlineStyle="color:orange; font-weight:bold; font-size:larger;" />
		<af:outputText value="pending"
			rendered="#{(!reportProfile.renderSum && ! reportProfile.editable && emissionLine.stackEmissions==null && emissionLine.emissionCalcMethodCd > 200) && !reportProfile.doRptCompare}"
			inlineStyle="color: orange; font-weight: bold;">
		</af:outputText>
	</af:column>



	<af:column formatType="number" sortProperty="totalEmissionsV"
		sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}" headerText="Total" width="90">
		<af:outputText value="#{emissionLine.totalEmissions}"
			inlineStyle="#{emissionLine.totalEmissionsDiff?'color:orange; font-weight:bold; font-size:larger;' : '' }">
		</af:outputText>
	</af:column>



	<af:column sortProperty="emissionsUnitNumerator" sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}"
		formatType="icon" headerText="Units" width="30">
		<af:selectOneChoice value="#{emissionLine.emissionsUnitNumerator}"
			readOnly="#{! reportProfile.editable || reportProfile.renderSum}">
			<f:selectItems
				value="#{facilityReference.emissionUnitReportingDefs.items[(empty emissionLine.emissionsUnitNumerator ? '' : emissionLine.emissionsUnitNumerator)]}" />
		</af:selectOneChoice>
<%-- 		<af:commandButton --%>
<%-- 			rendered="#{!reportProfile.editable  && emissionLine.annualAdjust != null && !reportProfile.renderSum}" --%>
<%-- 			text="#{facilityReference.emissionUnitReportingDefs.itemDesc[(empty emissionLine.emissionsUnitNumerator ? '' : emissionLine.emissionsUnitNumerator)]}" --%>
<%-- 			id="viewCalc" useWindow="true" windowWidth="1200" --%>
<%-- 			shortDesc="Click to see calculations" windowHeight="700" --%>
<%-- 			action="dialog:viewCalc"> --%>
<%-- 			<t:updateActionListener property="#{reportProfile.calcEmissionRow}" --%>
<%-- 				value="#{emissionLine}" /> --%>
<%-- 		</af:commandButton> --%>
	</af:column>
</af:column>

<af:column sortProperty="explanation" sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}"
	headerText="Explanation of how the emissions or emissions factor is derived"
	formatType="text"
	rendered="#{!reportProfile.renderSum && reportProfile.expandedExp}">
	<af:inputText value="#{(emissionLine.explanation != null)?'XXXXX':''}"
		rendered="#{!reportProfile.renderSum && reportProfile.expandedExp && emissionLine.tradeSecretE && !reportProfile.tradeSecretVisible}"
		inlineStyle="color: orange; font-weight: bold;" readOnly="true" />
	<af:inputText value="#{emissionLine.explanation}"
		rendered="#{reportProfile.editable && !reportProfile.renderSum && reportProfile.expandedExp && (!emissionLine.tradeSecretE || reportProfile.tradeSecretVisible)}"
		maximumLength="2000" columns="#{!reportProfile.editable?120:150}"
		rows="#{reportProfile.expRows}"
		inlineStyle="#{emissionLine.tradeSecretE?'color: orange; font-weight: bold;' : ''}"
		readOnly="false" />
	<af:inputText value="#{emissionLine.explanation}"
		rendered="#{!reportProfile.editable && !reportProfile.renderSum && reportProfile.expandedExp && (!emissionLine.tradeSecretE || reportProfile.tradeSecretVisible)}"
		columns="120" readOnly="true"
		inlineStyle="#{emissionLine.tradeSecretE?'color: orange; font-weight: bold;' : ''}" />
</af:column>
<af:column sortProperty="c20" sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}"
	headerText="Explanation" width="200" formatType="text"
	rendered="#{!reportProfile.renderSum && !reportProfile.expandedExp}">
	<af:commandLink
		shortDesc="Explanation of how the emissions or emissions factor is derived"
		rendered="#{reportProfile.editable && emissionLine.explanation == null}"
		text="add" id="viewEditExp1" useWindow="true" windowWidth="800"
		windowHeight="500" action="#{reportProfile.editViewExp}">
		<t:updateActionListener property="#{reportProfile.secretEmissionRow}"
			value="#{emissionLine}" />
	</af:commandLink>
	<af:outputText truncateAt="25" 
		rendered="#{(reportProfile.tradeSecretVisible || !emissionLine.tradeSecretE) && emissionLine.explanation != null && !reportProfile.editable}"
		value="#{emissionLine.explanation}" id="viewEditExp2"
		inlineStyle="#{emissionLine.tradeSecretE?'color: orange; font-weight: bold;' : ''}"
		shortDesc="#{emissionLine.explanation}"/>
	<af:commandLink
		shortDesc="Explanation of how the emissions or emissions factor is derived"
		rendered="#{(reportProfile.tradeSecretVisible || !emissionLine.tradeSecretE) && emissionLine.explanation != null && reportProfile.editable}"
		text="#{emissionLine.truncatedExp}" id="viewEditExp2Link"
		inlineStyle="#{emissionLine.tradeSecretE?'color: orange; font-weight: bold;' : ''}"
		useWindow="true" windowWidth="800" windowHeight="500"
		action="#{reportProfile.editViewExp}">
		<t:updateActionListener property="#{reportProfile.secretEmissionRow}"
			value="#{emissionLine}" />
	</af:commandLink>
	<af:inputText value="XXXXX"
		rendered="#{(!reportProfile.tradeSecretVisible && emissionLine.tradeSecretE) && emissionLine.explanation != null}"
		shortDesc="Explanation of how the emissions or emissions factor is derived--Trade Secret protected"
		inlineStyle="color: orange; font-weight: bold;" readOnly="true" />
</af:column>
<af:column headerText="Further Validations Required" sortProperty="exceedThresholdQA" sortable="#{!reportProfile.doRptCompare && !reportProfile.editable}" width="14%"
	rendered="#{!reportProfile.expandedExp && reportProfile.renderComponent == 'emissionUnits'}" shortDesc="Reported emissions exceeded limit; additional facility inventory validations are required."
	formatType="icon">
	<af:selectBooleanCheckbox value="#{emissionLine.exceedThresholdQA}"
		readOnly="true" inlineStyle="display:none;" />
	<af:objectImage source="/images/blackCheck.gif"
		rendered="#{emissionLine.exceedThresholdQA}"
		shortDesc="EU Emissions of #{emissionLine.pollutant} exceed #{emissionLine.thresholdQAStr} TPY.  Additional facility inventory validations will be performed." />
</af:column>
<af:column rendered="false" headerText="Trade Secret" sortable="false" sortProperty="tradeSecretE">
	<afh:rowLayout>
		<af:selectBooleanCheckbox readOnly="#{true}"
			value="#{emissionLine.tradeSecretE}" />
		<af:commandLink
			rendered="#{!reportProfile.editable && emissionLine.tradeSecretEText != null}"
			text="why" id="viewEditExplanationTS1" useWindow="true"
			inlineStyle="color: orange; font-weight: bold;" windowWidth="800"
			windowHeight="500" action="#{reportProfile.editViewExpTS}">
			<t:updateActionListener property="#{reportProfile.secretEmissionRow}"
				value="#{emissionLine}" />
		</af:commandLink>
		<af:commandLink
			rendered="#{reportProfile.editable && reportProfile.tradeSecretVisible}"
			text="#{emissionLine.tradeSecretEText == null?' add':' why'}"
			id="viewEditExplanationTS2" useWindow="true" windowWidth="800"
			windowHeight="500" action="#{reportProfile.editViewExpTS}">
			<t:updateActionListener property="#{reportProfile.secretEmissionRow}"
				value="#{emissionLine}" />
		</af:commandLink>
	</afh:rowLayout>
</af:column>
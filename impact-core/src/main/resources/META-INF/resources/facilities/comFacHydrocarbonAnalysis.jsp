<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:objectSpacer height="15" />

<%@ include file="comFacExtendedHydrocarbonAnalysisTable.jsp"%>

<af:objectSpacer height="15" />

<%@ include file="comFacExtendedHydrocarbonAnalysisTable2.jsp"%>

<af:objectSpacer height="15" />

<%@ include file="comFacExtendedHydrocarbonAnalysisTable3.jsp"%>

<af:objectSpacer height="15" />

<af:panelForm rows="1" maxColumns="2" width="780">
	<af:selectOneChoice label="AQD Emission Factor Group:"
		inlineStyle="font-size:10pt;" styleClass=""
		rendered="#{!facilityProfile.publicApp}"
		value="#{facilityProfile.facility.aqdEmissionFactorGroupCd}"
		id="aqdEmissionFactorGrp" unselectedLabel=" " readOnly="#{facilityProfile.emissionFactorGroupUpdatable}">
		<f:selectItems
			value="#{facilityReference.aqdEmissionFactorGroupDefs.items[(empty facilityProfile.facility.aqdEmissionFactorGroupCd ? '' : facilityProfile.facility.aqdEmissionFactorGroupCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:objectSpacer height="15" />

<af:outputFormatted styleUsage="instruction"
	value="<ul>
			<li>Other Hexanes:  The term 'Other Hexanes' refers to the sum of all organic compounds with six carbon atoms that are not specifically requested on this spreadsheet (e.g. 2-methylpentane, 3-methylpentane, etc.).  Normal hexane, Benzene, and cyclohexane are specifically requested and should therefore be omitted from the sum of 'Other Hexanes'.</li>
			<li>Other Heptanes:  The term 'Other Heptanes' refers to the sum of all organic compounds with seven carbon atoms that are not specifically requested on this spreadsheet (e.g. n-heptane, 3-ethylpentane, cycloheptane, etc.).  Methylcyclohexane and Toluene are specifically requested and should therefore be omitted from the sum of 'Other Heptanes'.</li>
			<li>Other Octanes:  The term 'Other Octanes' refers to the sum of all organic compounds with eight carbon atoms that are not specifically requested on this spreadsheet (e.g. n-octane, 2,3-Dimethylhexane, cyclooctane, etc.).  Ethylbenzene, o-xylene, m-xylene, p-xylene, and 2,2,4-Trimethylpentane are specifically requested and should therefore be omitted from the sum of 'Other Octanes'.</li>
			<li>Nonanes:  The term 'Nonanes' refers to the sum of all organic compounds with nine carbon atoms (e.g. n-nonane, 2,2,3-Trimethylhexane, indane, etc.)</li>
			<li>Xylenes:  The term 'Xylenes' refers to the sum of all isomers of xylene (i.e. o-xylene, m-xylene, and p-xylene)</li>
			<li>Decanes+:  The term 'Decanes+' refers to the sum of all organic compounds with ten or more carbon atoms (e.g., decane, undecane, dodecane, 3,4-Diethylhexane, etc.)</li>
		</ul>" />

<af:objectSpacer height="15" />

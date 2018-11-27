<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup>
    <af:outputText
      inlineStyle="font-size:75%;color:#666"
      value="\"Potential to emit\" means the maximum capacity of a source to emit any air pollutant 
      under its physical and operational design. Any physical or operational limitation on the 
      capacity of a source to emit an air pollutant, including air pollution control equipment 
      and restrictions on hours of operation or on the type or amount of material combusted, 
      stored or processed, shall be treated as part of its design if the limitation is enforceable
      by the EPA and the Division. This term does not alter or affect the use of this term for any
       other purposes under the Act, or the term “capacity factor” as used in Title IV of the Act 
       or the regulations promulgated thereunder." />
	<af:showDetailHeader text="Basis for Determination Options:">
		<af:panelForm>
			<af:outputText inlineStyle="font-size:75%;color:#666"
				value="Manufacturer Data" />

			<af:outputText inlineStyle="font-size:75%;color:#666"
				value="Test results for this source" />
			<af:outputText inlineStyle="font-size:75%;color:#666"
				value="Similar source test results" />
			<af:outputText inlineStyle="font-size:75%;color:#666" value="GRICalc" />
			<af:outputText inlineStyle="font-size:75%;color:#666"
				value="Tanks Program" />
			<af:outputText inlineStyle="font-size:75%;color:#666" value="AP-42" />
			<af:outputText inlineStyle="font-size:75%;color:#666"
				value="Other.  If this is selected, attach a document with a description of the method used." />
		</af:panelForm>
	</af:showDetailHeader>
</af:panelGroup>
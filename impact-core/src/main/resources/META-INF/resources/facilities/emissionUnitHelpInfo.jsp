<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document title="Emission Unit Type Selection:">
		<af:page title="Emission Unit Type Selection:">
			<af:table value="#{facilityReference.emissionUnitTypeWrapper}"
				binding="#{facilityReference.emissionUnitTypeWrapper.table}"
				bandingInterval="1" banding="row" var="euType" rows="#{infraDefs.totalEUTypeCount}">
				<af:column formatType="text" headerText=" Abbreviation ">
					<af:inputText value="#{euType.emissionUnitTypeCd}"
						readOnly="true" inlineStyle="padding:5px;" />
				</af:column>
				<af:column formatType="text" 
					headerText="#{facilityProfile.publicApp ? ' Emission Unit Type ' : ' Select This '}">
					<af:inputText value="#{euType.emissionUnitTypeName}"
						readOnly="true" inlineStyle="padding:5px;" />
				</af:column>
				<af:column sortable="false" formatType="text"
					headerText="#{facilityProfile.publicApp ? ' Description ' : ' If you have one of these '}">
					<af:inputText value="#{euType.emissionUnitTypeDesc}"
						readOnly="true" inlineStyle="padding:5px;" />
				</af:column>
			</af:table>
		</af:page>
	</af:document>
</f:view>
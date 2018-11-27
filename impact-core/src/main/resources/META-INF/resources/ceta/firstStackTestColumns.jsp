<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:column sortable="true" sortProperty="id" formatType="text" 
	headerText="Stack Test ID" width="85px">
	<af:commandLink text="#{st.stckId}"
		rendered="#{stackTestDetail.internalApp}"
		action="#{stackTestDetail.submitStackTest}"> 
		<af:setActionListener from="#{st.id}" to="#{stackTestDetail.id}" />
	</af:commandLink>
	<af:commandLink text="#{st.stckId}"
		rendered="#{!stackTestDetail.internalApp}"
		action="#{stackTestDetail.submitStackTestRO}"> 
		<af:setActionListener from="#{st.id}" to="#{stackTestDetail.id}" />
	</af:commandLink>
</af:column>
<af:column rendered="#{!stackTestSearch.fromFacility}" formatType="text"
	headerText="Facility ID" sortable="true" sortProperty="facilityId">
	<af:commandLink text="#{st.facilityId}"
		rendered="#{!stackTestSearch.fromFacility}"
		action="#{facilityProfile.submitProfileById}"
		inlineStyle="white-space: nowrap;">
		<t:updateActionListener property="#{facilityProfile.facilityId}"
			value="#{st.facilityId}" />
		<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{st.fpId}" />
		<t:updateActionListener property="#{facilityProfile.useFpId}"
				value="#{not empty st.fpId}" />	
		<t:updateActionListener property="#{menuItem_facProfile.disabled}"
			value="false" />
	</af:commandLink>
</af:column>
<af:column sortable="true" sortProperty="facilityAfsNumber"
	formatType="text" headerText="AFS ID" width="50px" noWrap="true">
	<af:inputText readOnly="true" value="#{st.facilityAfsNumber}" />
</af:column>
<af:column sortable="true" sortProperty="facilityNm" 
    rendered="#{!stackTestSearch.fromFacility}"
	formatType="text" headerText="Facility Name">
	<af:inputText readOnly="true" value="#{st.facilityNm}" />
	<af:outputText value="#{st.afsExportErrors}" rendered="#{st.afsExportErrors != null}" inlineStyle="color: orange; font-weight: bold;"/>
</af:column>
<af:column rendered="#{!stackTestSearch.fromFacility}" sortProperty="permitClassCd" sortable="true"
		formatType="text" headerText="Facility Class">
		<af:outputText
			value="#{facilityReference.permitClassDefs.itemDesc[(empty st.permitClassCd ? '' : st.permitClassCd)]}" />
	</af:column>

<af:column rendered="#{!stackTestSearch.fromFacility}" sortProperty="facilityTypeCd" sortable="true" noWrap="true"
		formatType="text" headerText="Facility Type">
		<af:outputText
			value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty st.facilityTypeCd ? '' : st.facilityTypeCd)]}" />
</af:column>

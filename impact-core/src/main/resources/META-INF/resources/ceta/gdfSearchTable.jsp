<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
		
<af:table var="gdf" bandingInterval="1" banding="row" width="100%"
  value="#{gdfSearch.gdfs}" rows="#{gdfSearch.pageLimit}">
  
  <af:column sortable="true" sortProperty="month" formatType="text"
    headerText="Month">
    <af:commandLink rendered="true"
      action="#{gdfDetail.startEditGDF}"
      useWindow="true" windowWidth="650" windowHeight="350">
	    <af:selectOneChoice value="#{gdf.month}" readOnly="true">
	    	<f:selectItems 
	    		value="#{compEvalDefs.monthDef.items[(empty gdf.month ? '' : gdf.month)]}" />
	    </af:selectOneChoice>
      <af:setActionListener from="#{gdf.doLaaCd}"
        to="#{gdfDetail.doLaaCd}" /> 
      <af:setActionListener from="#{gdf.gdfId}"
        to="#{gdfDetail.gdfId}" />       
    </af:commandLink>
  </af:column>
  
  <af:column sortable="true" sortProperty="year" formatType="text"
    headerText="Year">
    <af:inputText readOnly="true" value="#{gdf.year}"/>
  </af:column>
  
  <af:column sortable="true" sortProperty="doLaaCd"
    formatType="text" headerText="DO/LAA">
    <af:selectOneChoice value="#{gdf.doLaaCd}" readOnly="true">
    	<f:selectItems value="#{facilitySearch.doLaas}" />
    </af:selectOneChoice>
  </af:column>
  
  <af:column sortable="true" sortProperty="stageOne" formatType="text"
    headerText="Stage I">
    <af:inputText readOnly="true" value="#{gdf.stageOne}"/>
  </af:column>
  
  <af:column sortable="true" sortProperty="stageOneAndTwo" formatType="text"
    headerText="Stage I & II">
    <af:inputText readOnly="true" value="#{gdf.stageOneAndTwo}"/>
  </af:column>
  
  <af:column sortable="true" sortProperty="nonStageOneAndTwo" formatType="text"
    headerText="Non-Stage I & II">
    <af:inputText readOnly="true" value="#{gdf.nonStageOneAndTwo}"/>
  </af:column>
 
  <f:facet name="footer">
    <afh:rowLayout halign="center">
      <af:panelButtonBar>
        <af:commandButton text="Add New Month" useWindow="true"
          windowWidth="675" windowHeight="475"
          action="#{gdfDetail.startNewGDF}">
	      <af:setActionListener from="#{gdfSearch.doLaaCd}"
	        to="#{gdfDetail.doLaaCd}" /> 
        </af:commandButton>
        <af:commandButton actionListener="#{tableExporter.printTable}"
          onclick="#{tableExporter.onClickScript}" text="Printable view" />
        <af:commandButton actionListener="#{tableExporter.excelTable}"
          onclick="#{tableExporter.onClickScript}"
          text="Export to excel" />
      </af:panelButtonBar>
    </afh:rowLayout>
  </f:facet>
</af:table>


<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
		
<af:table var="complaint" bandingInterval="1" banding="row" width="100%"
  value="#{complaintSearch.complaints}" rows="#{complaintSearch.pageLimit}">
  
  <af:column headerText="" bandingShade="light">
  	<af:column sortable="true" sortProperty="month" formatType="text" headerText="Month">
    	<af:commandLink rendered="true"
      		action="#{complaintDetail.startEditComplaint}"
      		useWindow="true" windowWidth="650" windowHeight="400">
	    	<af:selectOneChoice value="#{complaint.month}" readOnly="true">
	    		<f:selectItems value="#{compEvalDefs.monthDef.items[(empty complaint.month ? '' : complaint.month)]}" />
	    	</af:selectOneChoice>
      		<af:setActionListener from="#{complaint.doLaaCd}" to="#{complaintDetail.doLaaCd}" /> 
      		<af:setActionListener from="#{complaint.complaintId}" to="#{complaintDetail.complaintId}" />       
    	</af:commandLink>
  	</af:column>
  
  	<af:column sortable="true" sortProperty="year" formatType="text" headerText="Year">
    	<af:selectOneChoice value="#{complaint.year}" readOnly="true">
    		<f:selectItems 
    			value="#{complianceReportSearch.reportingYearsDef.items[(empty complaint.year ? '' : complaint.year)]}" />
    	</af:selectOneChoice>
  	</af:column>
  
  	<af:column sortable="true" sortProperty="doLaaCd" formatType="text" headerText="DO/LAA">
    	<af:selectOneChoice value="#{complaint.doLaaCd}" readOnly="true">
    		<f:selectItems value="#{facilitySearch.doLaas}" />
    	</af:selectOneChoice>
  	</af:column>
  </af:column>
  
  <af:column headerText="Complaints Inspected" bandingShade="light">
  	<af:column sortable="true" sortProperty="highPriority" formatType="text" headerText="High Priority">
    	<af:inputText readOnly="true" value="#{complaint.highPriority}"/>
  	</af:column>
  
  	<af:column sortable="true" sortProperty="nonHighPriority" formatType="text" headerText="Non-High Priority">
    	<af:inputText readOnly="true" value="#{complaint.nonHighPriority}"/>
  	</af:column>
  
  	<af:column sortable="true" sortProperty="other" formatType="text" headerText="Other">
    	<af:inputText readOnly="true" value="#{complaint.other}"/>
  	</af:column>
  
    <af:column sortable="true" sortProperty="total" formatType="text"
    	headerText="Total (HPV + nonHPV + Other)">
    	<af:inputText readOnly="true" value="#{complaint.total}"/>
  	</af:column>
  	<af:column sortable="true" sortProperty="openBurning" formatType="text" headerText="Open Burning">
    	<af:inputText readOnly="true" value="#{complaint.openBurning}"/>
  	</af:column>
  	<af:column sortable="true" sortProperty="asbestos" formatType="text" headerText="Asbestos">
    	<af:inputText readOnly="true" value="#{complaint.asbestos}"/>
  	</af:column>
  </af:column>
  
  <af:column headerText="Other Inspections" bandingShade="light">
  	<af:column sortable="true" sortProperty="asbestosNonNotifier" formatType="text" headerText="Asbestos Non-Notifier Inspections">
    	<af:inputText readOnly="true" value="#{complaint.asbestosNonNotifier}"/>
  	</af:column>
  	<af:column sortable="true" sortProperty="antiTamperingInspections" formatType="text" headerText="Anti-Tampering Inspections">
    	<af:inputText readOnly="true" value="#{complaint.antiTamperingInspections}"/>
  	</af:column>
 </af:column>
 
  <f:facet name="footer">
    <afh:rowLayout halign="center">
      <af:panelButtonBar>
        <af:commandButton text="Add New Month" useWindow="true"
          windowWidth="675" windowHeight="475"
          action="#{complaintDetail.startNewComplaint}">    
	      <af:setActionListener from="#{complaintSearch.doLaaCd}"
	        to="#{complaintDetail.doLaaCd}" /> 
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


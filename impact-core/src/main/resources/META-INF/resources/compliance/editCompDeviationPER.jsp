<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="New Deviation">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:messages />
      <af:panelForm partialTriggers="reportType" >
      	 <af:inputText required="true" label="AQD EU ID:" maximumLength="500" columns="80" rows="1" value="#{complianceReport.complianceDeviation.identifier}" />
	 	
      	<af:selectInputDate 
      	 		required="true"
				label="Start Date" 
			    value="#{complianceReport.complianceDeviation.startDate}" 
		> <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
		<af:selectInputDate 
				required="true"
				label="End Date" 
			    value="#{complianceReport.complianceDeviation.endDate}" 
		> <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
		<af:inputText readOnly="false" maximumLength="4000" columns="80" rows="2" label="Duration:" value="#{complianceReport.complianceDeviation.perDescription}" />
		<af:inputText readOnly="false" maximumLength="4000" columns="80" rows="4" label="Description of Deviation or Exceedance and Probable Cause:" value="#{complianceReport.complianceDeviation.perProbableCause}" />
	 	<af:inputText readOnly="false" maximumLength="4000" columns="80" rows="4" label="Description of Corrective Actions. If none, describe why not:" value="#{complianceReport.complianceDeviation.perCorrectiveAction}" />
		
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Create" 
              rendered="#{complianceReport.complianceDeviation.deviationId==0}" 
              actionListener="#{complianceReport.createDeviation}" />
            <af:commandButton text="Update" 
              rendered="#{complianceReport.complianceDeviation.deviationId>0}" 
              actionListener="#{complianceReport.updateDeviation}" />
            <af:commandButton text="Cancel" immediate="true"
              actionListener="#{complianceReport.cancelDeviation}" />
            <af:commandButton text="Delete" 
               rendered="#{complianceReport.complianceDeviation.deviationId>0}" 
              actionListener="#{complianceReport.deleteDeviation}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

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
      <af:panelForm labelWidth="250" width="600">
        <af:inputText required="true" label="EU ID:" maximumLength="500"
          columns="50" rows="1"
          value="#{complianceReport.complianceDeviation.identifier}" />

        <af:inputText readOnly="false" maximumLength="4000" columns="50"
          rows="2"
          label="Emission Limitation/Control Measure or Permit Term No.:"
          value="#{complianceReport.complianceDeviation.perDescription}" />
        <af:inputText readOnly="false" maximumLength="4000" columns="50"
          rows="5" label="Compliance Method:"
          value="#{complianceReport.complianceDeviation.tvccComplianceMethod}" />

        <af:panelGroup layout="vertical" rendered="true">
          <af:showDetailHeader text="Excursions/Deviations"
            disclosed="true">
            <af:outputText inlineStyle="font-size:75%;color:#666"
              value="Fill in ONE of the following fields BUT NOT BOTH"></af:outputText>
             <af:selectInputDate id="deviationStartDate"
                label="Report Date of Those Documented Within Excursion/Deviation Reports Submitted to District:"
                rendered="#{complianceReport.complianceDeviation.deviationId > 0}"
                value="#{complianceReport.complianceDeviation.startDate}" />
            <afh:rowLayout halign="center"
                rendered="#{complianceReport.complianceDeviation.deviationId == 0}">
              <af:inputText value="" readOnly="true"
                label="Report Date(s) of Those Documented Within Excursion/Deviation Reports Submitted to District:"/>
              <af:selectInputDate id="deviationStartDate1"
                value="#{complianceReport.complianceDeviation.startDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
              <af:selectInputDate id="deviationStartDate2"
                value="#{complianceReport.deviationStartDate2}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
              <af:selectInputDate id="deviationStartDate3"
                value="#{complianceReport.deviationStartDate3}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
              <af:selectInputDate id="deviationStartDate4"
                value="#{complianceReport.deviationStartDate4}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
            </afh:rowLayout>
            <af:outputText
              inlineStyle="font-size:75%;color:#666;padding:5px"
              value="or..."></af:outputText>
            <af:inputText wrap="true" readOnly="false"
              maximumLength="4000" columns="50" rows="5"
              label="Explain the date, nature, duration, and probable cause of the excursion/deviation, as well as any corrective action taken:"
              value="#{complianceReport.complianceDeviation.tvccExcursionsSubmitted}" />
          </af:showDetailHeader>
        </af:panelGroup>

        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Save"
              rendered="#{complianceReport.complianceDeviation.deviationId==0}"
              actionListener="#{complianceReport.createDeviation}" />
            <af:commandButton text="Save"
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

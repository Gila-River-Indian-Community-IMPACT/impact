<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical" rendered="true">
  <af:showDetailHeader text="Detailed EU Information" disclosed="true"
    id="perDetail">
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="The Emission Unit(s) listed below has been issued a NSR(s). Complete the table below by identifying whether deviations or exceedances of operational restrictions(OR)/emission limitations(EL) and/or monitoring, record keeping or reporting (MRR) occurred by selecting either Yes or No. If any of the date fields do not identify an actual date, you are required to update your Facility Inventory for each applicable emissions unit(s). Failure to update your Facility Inventory may result in an incomplete report submittal."></af:outputText>

    <af:table rows="5"
      value="#{complianceReport.complianceReport.perDetails}"
      bandingInterval="1" width="100%" var="report">
      
      <af:column sortable="true" formatType="text"
        headerText="AQD EU ID">
        <af:outputText value="#{report.epaEmuId}" />
      </af:column>

      <af:column sortable="true" formatType="text"
        headerText="AQD EU Description">
        <af:outputText value="#{report.companyId}" />
      </af:column>

      <af:column sortable="true" formatType="text"
        headerText="Permit Number: Effective Date">
	    <af:selectManyListbox value="#{report.permitNumbers}" readOnly="true">
	       <f:selectItems value="#{report.permitInfoItems}" />
	    </af:selectManyListbox>
      </af:column>

      <af:column headerText="Dates" bandingShade="light">
        <af:column sortable="true" formatType="text"
          headerText="Completion of Initial Installation">
          <af:selectInputDate columns="5" id="initInstall"
            value="#{report.initialInstallComplete}"
            readOnly="#{report.initInstallDtFromFP || (complianceReport.locked || !complianceReport.editMode)}" />
        </af:column>
        <af:column sortable="true" formatType="text"
          headerText="Begin Installation/ Modification">
          <af:selectInputDate columns="5" id="beginInstall"
            value="#{report.modificationBegun}"
            readOnly="#{report.modificationDtFromFP || (complianceReport.locked || !complianceReport.editMode)}" />
        </af:column>
        <af:column sortable="true" formatType="text"
          headerText="Commence Operation after Installation or Latest Modification">
          <af:selectInputDate columns="5" id="operation"
            value="#{report.commencedOperation}"
            readOnly="#{report.commenceDtFromFP || (complianceReport.locked || !complianceReport.editMode)}" />
        </af:column>
      </af:column>

      <af:column headerText="Deviations or Exceedances From:"
        bandingShade="light">
        <af:column sortable="true" formatType="text" headerText="OR/EL">
          <af:selectOneChoice id="deviation"
            readOnly="#{complianceReport.locked || !complianceReport.editMode}"
            value="#{report.deviations}">
            <af:selectItem label="Yes" value="Yes" />
            <af:selectItem label="No" value="No" />
          </af:selectOneChoice>
        </af:column>

        <af:column sortable="true" formatType="text" headerText="MRR">
          <af:selectOneChoice
            readOnly="#{complianceReport.locked || !complianceReport.editMode}"
            value="#{report.deviationsFromMRR}">
            <af:selectItem label="Yes" value="Yes" />
            <af:selectItem label="No" value="No" />
          </af:selectOneChoice>
        </af:column>
      </af:column>

      <af:column sortable="false" formatType="text"
        headerText="Notes">
        <af:inputText columns="70" rows="1" maximumLength="4000"
          shortDesc="Please explain any of the above questions as appropriate"
          readOnly="#{complianceReport.locked || !complianceReport.editMode}"
          value="#{report.comment}" />
      </af:column>

      <f:facet name="footer">
        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton
              actionListener="#{tableExporter.printTable}"
              onclick="#{tableExporter.onClickScript}"
              text="Printable view" />
            <af:commandButton
              actionListener="#{tableExporter.excelTable}"
              onclick="#{tableExporter.onClickScript}"
              text="Export to excel" />
          </af:panelButtonBar>
        </afh:rowLayout>
      </f:facet>

    </af:table>
  </af:showDetailHeader>

</af:panelGroup>

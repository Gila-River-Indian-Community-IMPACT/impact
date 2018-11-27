<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Stars2 Emission Unit Permit History">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <af:inputText label="Facility ID:"
          value="#{facilityProfile.facility.facilityId}" columns="10"
          maximumLength="10" readOnly="true" />
        <af:inputText label="Facility Name:"
          value="#{facilityProfile.facility.name}" columns="60"
          maximumLength="60" readOnly="true" />
        <af:inputText label="AQD Emissions Unit ID:"
          value="#{facilityProfile.emissionUnit.epaEmuId}" columns="9"
          maximumLength="9" readOnly="true" />
      </af:panelForm>
      <af:panelForm>
        <af:table value="#{facilityProfile.allEuPermits}"
          width="99%" var="permit" rows="#{facilityProfile.pageLimit}">
          <af:column headerText="Permit Number" sortable="true"
            sortProperty="permitNumber" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:commandLink text="#{permit.permitNumber}"
                action="#{facilityProfile.loadEuPermit}">
                <af:setActionListener to="#{permitDetail.permitID}"
                  from="#{permit.permitID}" />
              </af:commandLink>
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="EU Id" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{permit.permitEU.fpEU.epaEmuId}" />
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="EU Permit Status" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectOneChoice label="EU Permit Status :" readOnly="true"
                value="#{permit.permitEU.permitStatusCd}">
                <mu:selectItems
                  value="#{permitReference.permitStatusDefs}" />
              </af:selectOneChoice>
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="Application Numbers" sortable="true"
            sortProperty="applicationNumbers">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{permit.applicationNumbers}" />
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="Type" sortable="true"
            sortProperty="permitTypeDsc" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectOneChoice unselectedLabel=" " readOnly="true"
                value="#{permit.permitType}">
                <mu:selectItems value="#{permitReference.permitTypes}" />
              </af:selectOneChoice>
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="Publication Stage" sortable="true"
            sortProperty="permitGlobalStatusCD" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectOneChoice label="Permit status"
                value="#{permit.permitGlobalStatusCD}" readOnly="true"
                id="soc2">
                <mu:selectItems
                  value="#{permitReference.permitGlobalStatusDefs}"
                  id="soc2" />
              </af:selectOneChoice>
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="Reason(s)" sortable="true"
            sortProperty="permitReasons">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectManyCheckbox label="Reason(s) :" valign="top"
                readOnly="true" value="#{permit.permitReasonCDs}"
                inlineStyle="white-space: nowrap;">
                <f:selectItems value="#{permitSearch.allPermitReasons}" />
              </af:selectManyCheckbox>
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="Effective Date" sortable="true"
            sortProperty="effectiveDate" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{permit.effectiveDate}" />
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="Expiration Date" sortable="true"
            sortProperty="expirationDate" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{permit.expirationDate}" />
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="Draft Issuance Date" sortable="true"
            sortProperty="draftIssueDate" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{permit.draftIssueDate}" />
            </af:panelHorizontal>
          </af:column>
          <af:column headerText="Final Issuance Date" sortable="true"
            sortProperty="finalIssueDate" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{permit.finalIssueDate}" />
            </af:panelHorizontal>
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
      </af:panelForm>
      <af:panelForm>
        <f:facet name="footer">
          <afh:rowLayout halign="center">
            <af:panelButtonBar>
              <af:commandButton text="close"
                action="#{facilityProfile.closeDialog}" />
            </af:panelButtonBar>
          </afh:rowLayout>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
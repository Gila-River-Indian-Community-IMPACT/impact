<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Facilities - Bulk Operations">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <f:facet name="messages">
        <af:messages />
      </f:facet>
      <%@ include file="../util/validate.js"%>
      
      <afh:rowLayout halign="center">
        <h:panelGrid border="1" columns="1" width="400">
          <af:panelBorder>
            <af:panelHeader text="New Event" size="0" />
            <af:panelForm>

              <af:selectOneChoice label="Event Type" readOnly="true"
                value="#{bulkOperationsCatalog.bulkOperation.eventLog.eventTypeDefCd}">
                <f:selectItems value="#{eventLog.eventType}" />
              </af:selectOneChoice>
              
              <af:inputText value="#{bulkOperationsCatalog.bulkOperation.eventLog.note}"
                label="Note: " required="true"
                columns="80" rows="6" id="noteTxt" maximumLength="500"
                autoSubmit="true" onkeydown="charsLeft(500);"
                onkeyup="charsLeft(500);" />

              <afh:rowLayout>
                <afh:cellFormat halign="right">
                  <h:outputText style="font-size:12px"
                    value="Characters Left :" />
                </afh:cellFormat>
                <afh:cellFormat halign="left">
                  <h:inputText readonly="true" size="3" value="500"
                    id="messageText" />
                </afh:cellFormat>
              </afh:rowLayout>

              <af:objectSpacer height="10" />
              <afh:rowLayout halign="center">
                <af:panelButtonBar>

                  <af:commandButton text="Apply"
                    actionListener="#{bulkOperationsCatalog.applyFinalAction}" />
                  <af:commandButton text="Cancel" immediate="true"
                    actionListener="#{bulkOperationsCatalog.bulkOperationCancel}" />
                </af:panelButtonBar>
              </afh:rowLayout>
            </af:panelForm>
          </af:panelBorder>
        </h:panelGrid>
      </afh:rowLayout>

      <af:objectSpacer width="100%" height="15" />

      <h:panelGrid border="1"
        rendered="#{bulkOperationsCatalog.hasFacilitySearchResults}">
        <af:panelBorder>
          <af:showDetailHeader text="Selected Facility List"
            disclosed="true">
            <af:table
              value="#{bulkOperationsCatalog.selectedFacilities}"
              rows="#{bulkOperationsCatalog.pageLimit}"
              bandingInterval="1" banding="row" var="facility">

              <af:column sortProperty="#{facility.facilityId}"
                sortable="true" formatType="text"
                headerText="Facility ID">
                <af:outputText value="#{facility.facilityId}" />
              </af:column>

              <af:column sortProperty="#{facility.name}" sortable="true"
                formatType="text" headerText="Facility Name">
                <af:outputText value="#{facility.name}" />
              </af:column>

              <af:column sortable="false" formatType="text"
                headerText="Operating Status">
                <af:outputText
                  value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
              </af:column>
              <af:column sortable="false" formatType="text"
                headerText="Reporting Category">
                <af:outputText
                  value="#{facilityReference.emissionReportsDefs.itemDesc[(empty facility.reportingTypeCd ? '' : facility.reportingTypeCd)]}" />
              </af:column>
              <af:column sortable="false" formatType="text"
                headerText="Permitting Classification">
                <af:outputText
                  value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
              </af:column>
              <af:column sortProperty="#{facility.permitStatusCd}"
                sortable="true" formatType="text"
                headerText="TV Permit Status">
                <af:selectOneChoice value="#{facility.permitStatusCd}"
                  readOnly="true">
                  <mu:selectItems
                    value="#{facilityReference.permitStatusDefs}" />
                </af:selectOneChoice>
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
        </af:panelBorder>
      </h:panelGrid>

    </af:form>
  </af:document>
</f:view>




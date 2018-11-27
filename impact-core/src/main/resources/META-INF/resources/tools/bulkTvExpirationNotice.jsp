<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Facilities - Bulk Operations">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <f:facet name="messages">
        <af:messages />
      </f:facet>
      <h:panelGrid border="1"
        rendered="#{bulkOperationsCatalog.hasSearchResults}">
        <af:panelBorder>
          <af:showDetailHeader text="Selected Facility List"
            disclosed="true">
            <af:table
              value="#{bulkOperationsCatalog.selectedFacilities}"
              rows="#{bulkOperationsCatalog.pageLimit}" bandingInterval="1" banding="row" var="facility">

              <af:column sortProperty="c01" sortable="true"
                formatType="text" headerText="Facility ID">
                <af:outputText value="#{facility.facilityId}" />
              </af:column>

              <af:column sortProperty="c02" sortable="true"
                formatType="text" headerText="Facility Name">
                <af:outputText value="#{facility.name}" />
              </af:column>

              <af:column sortProperty="c07" sortable="true"
                formatType="text" headerText="Operating Status">
                <af:outputText
                  value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
              </af:column>
              <af:column sortProperty="c08" sortable="true"
                formatType="text" headerText="Reporting Category">
                <af:outputText
                  value="#{facilityReference.emissionReportsDefs.itemDesc[(empty facility.reportingTypeCd ? '' : facility.reportingTypeCd)]}" />
              </af:column>
              <af:column sortProperty="c09" sortable="true"
                formatType="text" headerText="Permitting Classification">
                <af:outputText
                  value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
              </af:column>
              <af:column sortProperty="c10" sortable="true"
                formatType="text" headerText="TV Permit Status">
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

      <af:objectSpacer width="100%" height="15" />

      <afh:rowLayout halign="center">
        <h:panelGrid border="1">
          <af:panelBorder>
            <af:panelHeader text="Facility Roles List" size="0" />
            <af:panelForm>
              <af:table value="#{bulkOperationsCatalog.roles}"
                bandingInterval="1" banding="row" var="facRole">
                <af:column sortProperty="c01" sortable="true"
                  formatType="text" headerText="Facility Roles">
                  <af:outputText value="#{facRole.facilityRoleDsc}" />
                </af:column>
                <af:column sortProperty="c02" sortable="true"
                  formatType="text" headerText="Staff">
                  <af:selectOneChoice value="#{facRole.userId}"
                    readOnly="#{!bulkOperationsCatalog.facilityRoleEditable}">
                    <f:selectItems value="#{infraDefs.basicUsersDef.items[(empty facRole.userId?0:facRole.userId)]}" />
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


              <af:objectSpacer height="10" />
              <afh:rowLayout halign="center">
                <af:panelButtonBar>

                  <af:commandButton text="Apply"
                    action="#{bulkOperationsCatalog.saveNewFacilityRoles}" />
                  <af:commandButton text="Cancel" immediate="true"
                    actionListener="#{bulkOperationsCatalog.cancelUpdateFacilityRoles}" />

                </af:panelButtonBar>
              </afh:rowLayout>
            </af:panelForm>
          </af:panelBorder>
        </h:panelGrid>
      </afh:rowLayout>
    </af:form>
  </af:document>
</f:view>


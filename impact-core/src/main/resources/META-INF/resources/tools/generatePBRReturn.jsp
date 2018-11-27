<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Correspondence - Bulk Operations">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <f:facet name="messages">
        <af:messages />
      </f:facet>
      <afh:rowLayout halign="center">
        <h:panelGrid border="1"
          rendered="#{bulkOperationsCatalog.hasCorrSearchResults}">
          <af:panelBorder>
            <af:showDetailHeader text="Facility List" disclosed="true">
              <af:table
                value="#{bulkOperationsCatalog.bulkOperation.results}"
                rows="10" bandingInterval="1" banding="row"
                var="formResult">

                <af:column sortProperty="#{formResult.facilityId}"
                  sortable="true" formatType="text"
                  headerText="Facility ID">
                  <af:outputText value="#{formResult.facilityId}" />
                </af:column>
                <af:column formatType="text"
                  headerText="Correspondence Document">
                  <h:outputLink value="#{formResult.formURL}">
                    <af:outputText
                      value="#{formResult.facilityId}-pbr.docx" />
                  </h:outputLink>
                </af:column>
                <af:column formatType="text" headerText="Notes">
                  <af:outputText value="#{formResult.notes}" />
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
      </afh:rowLayout>
      <af:objectSpacer height="10" />
      <afh:rowLayout>
        <af:commandButton text="Close" immediate="true"
          actionListener="#{bulkOperationsCatalog.bulkOperationCancel}" />
      </afh:rowLayout>
    </af:form>
  </af:document>
</f:view>


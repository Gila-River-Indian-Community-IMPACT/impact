<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Facility - Permit Count Report">
    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" />
    <f:verbatim></script></f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Facility - Permit Count Report">
        <%@ include file="../util/header.jsp"%>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="600">
            <af:panelBorder>
              <af:showDetailHeader text="Facility - Permit Count Report Filter"
                disclosed="true">
                <af:panelForm rows="1">
                  <af:selectInputDate label="To Date"
                    value="#{facilityPermitReport.endDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{facilityPermitReport.submit}" />
                    <af:commandButton text="Reset"
                      action="#{facilityPermitReport.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">

          <h:panelGrid border="1" width="1100"
             rendered="#{facilityPermitReport.hasFacPmtResults}">
            <af:panelBorder>

              <af:showDetailHeader text="Facility - Permit Report" disclosed="true">
                <af:table bandingInterval="2" banding="row" var="types"
                   emptyText='No permits found.' rows="#{facilityPermitReport.pageLimit}" width="99%"
                   value="#{facilityPermitReport.facilityCounts}">

                  <af:column headerText="" bandingShade="light">
                    <af:column sortable="false"
                      noWrap="true">
                      <f:facet name="header">
                        <af:outputText value="Permit Type" />
                      </f:facet>
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{types.permitType}" />
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="" bandingShade="light">
                    <af:column sortable="false" noWrap="true">
                      <f:facet name="header">
                        <af:outputText value="Number of Facilities With Active Permits" />
                      </f:facet>
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{types.active}" />
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="" bandingShade="light">
                    <af:column sortable="false" noWrap="true">
                      <f:facet name="header">
                        <af:outputText value="Number of Facilities With Expired Permits" />
                      </f:facet>
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{types.expired}" />
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="" bandingShade="light">
                    <af:column sortable="false" noWrap="true">
                      <f:facet name="header">
                        <af:outputText value="Number of Facilities With Extended Permits" />
                      </f:facet>
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{types.extended}" />
                      </af:panelHorizontal>
                    </af:column>
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

      </af:page>
    </af:form>
  </af:document>
</f:view>

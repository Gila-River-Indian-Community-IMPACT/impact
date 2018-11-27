<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="PBR Count Report">
    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" />
    <f:verbatim></script></f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="PBR Count Report">
        <%@ include file="../util/header.jsp"%>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="600">
            <af:panelBorder>
              <af:showDetailHeader text="PBR Count Report Filter"
                disclosed="true">
                <af:panelForm rows="1">
                  <af:selectInputDate label="From Date"
                    value="#{pbrReport.startDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                  <af:selectInputDate label="To Date"
                    value="#{pbrReport.endDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{pbrReport.submit}" />
                    <af:commandButton text="Reset"
                      action="#{pbrReport.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">

          <h:panelGrid border="1" width="1100"
             rendered="#{pbrReport.hasPbrResults}">
            <af:panelBorder>

              <af:showDetailHeader text="PBR Report" disclosed="true">
                <af:table bandingInterval="2" banding="row" var="pbrDetails"
                   emptyText='No PBRs found.' rows="#{pbrReport.pageLimit}" width="99%"
                   value="#{pbrReport.pbrCounts}">

                  <af:column headerText="" bandingShade="light">
                    <af:column sortProperty="doLaaName" sortable="true"
                      noWrap="true">
                      <f:facet name="header">
                        <af:outputText value="District" />
                      </f:facet>
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{pbrDetails.doLaaName}" />
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="" bandingShade="light">
                    <af:column sortProperty="totalCount" sortable="true"
                      noWrap="true">
                      <f:facet name="header">
                        <af:outputText value="Total Count (ex. Superseded)" />
                      </f:facet>
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{pbrDetails.totalCount}" />
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="Count By Type" noWrap="true">

                        <af:table bandingInterval="2" banding="row" var="pbrTypeCount"
                           emptyText='No Count found.' rows="25" width="99%"
                           value="#{pbrDetails.counts}">

                          <af:column bandingShade="light">
                            <af:column noWrap="true">
                              <f:facet name="header">
                                <af:outputText value="PBR Type" />
                              </f:facet>
                              <af:panelHorizontal valign="middle" halign="left">
                                <af:inputText readOnly="true"
                                   value="#{pbrTypeCount.pbrTypeDsc}" />
                              </af:panelHorizontal>
                            </af:column>
                          </af:column>

                          <af:column bandingShade="light">
                            <af:column noWrap="true">
                              <f:facet name="header">
                                <af:outputText value="Received" />
                              </f:facet>
                              <af:panelHorizontal valign="middle" halign="left">
                                <af:inputText readOnly="true"
                                  value="#{pbrTypeCount.received}" />
                              </af:panelHorizontal>
                            </af:column>
                          </af:column>

                          <af:column bandingShade="light">
                            <af:column noWrap="true">
                              <f:facet name="header">
                                <af:outputText value="Accepted" />
                              </f:facet>
                              <af:panelHorizontal valign="middle" halign="left">
                                <af:inputText readOnly="true"
                                  value="#{pbrTypeCount.accepted}" />
                              </af:panelHorizontal>
                            </af:column>
                          </af:column>

                          <af:column bandingShade="light">
                            <af:column noWrap="true">
                              <f:facet name="header">
                                <af:outputText value="Denied" />
                              </f:facet>
                              <af:panelHorizontal valign="middle" halign="left">
                                <af:inputText readOnly="true"
                                  value="#{pbrTypeCount.denied}" />
                              </af:panelHorizontal>
                            </af:column>
                          </af:column>

                          <af:column bandingShade="light">
                            <af:column noWrap="true">
                              <f:facet name="header">
                                <af:outputText value="Superseded" />
                              </f:facet>
                              <af:panelHorizontal valign="middle" halign="left">
                                <af:inputText readOnly="true"
                                  value="#{pbrTypeCount.superseded}" />
                              </af:panelHorizontal>
                            </af:column>
                          </af:column>

                          <af:column bandingShade="light">
                            <af:column noWrap="true">
                              <f:facet name="header">
                                <af:outputText value="Average Processing Time (Days)" />
                              </f:facet>
                              <af:panelHorizontal valign="middle" halign="left">
                                <af:inputText readOnly="true"
                                  value="#{pbrTypeCount.avgDays}" />
                              </af:panelHorizontal>
                            </af:column>
                          </af:column>

                        </af:table>
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

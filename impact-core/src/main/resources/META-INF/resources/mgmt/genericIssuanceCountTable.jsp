<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Generic Issuance Count Report">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Generic Issuance Count Report">
        <%@ include file="../util/header.jsp"%>
        
        <afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
            <afh:rowLayout halign="left">
                  	<af:panelHorizontal>
                	<af:outputFormatted value="<b>Data source:</b> generic issuance<br><br>" />
                	<af:objectSpacer width="10" height="5" />
                	<af:commandButton text="Show Explanation" rendered="#{!complianceSearch.showExplain}"
							action="#{complianceSearch.turnOn}"> </af:commandButton>
						<af:commandButton text="Hide Explanation" rendered="#{complianceSearch.showExplain}"
							action="#{complianceSearch.turnOff}"> </af:commandButton>
                      </af:panelHorizontal>
            </afh:rowLayout>
        	<afh:rowLayout rendered="#{complianceSearch.showExplain}" halign="left">
        		<af:outputFormatted value="Select one or more DO/LAAs, the type of issuance and the date range you would like to view counts for.  The results will display the number of each type of issuance process for the DO/LAA selected.<br><br>"/>
        	</afh:rowLayout>
        </afh:tableLayout>
        
        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1000">
            <af:panelBorder>
              <af:showDetailHeader
                text="Generic Issuance Count Report Filter"
                disclosed="true">
                <af:panelForm rows="1">

                  <af:selectManyListbox label="Districts :" size="6"
                    value="#{genericIssuanceReport.doLaaCds}">
                    <f:selectItems value="#{issuedMetrics.doLaas}" />
                  </af:selectManyListbox>

                  <af:selectManyListbox label="Issuance Types :"
                    size="6"
                    value="#{genericIssuanceReport.issuanceTypeCds}">
                    <f:selectItems
                      value="#{generalIssuance.issuanceTypesAll}" />
                  </af:selectManyListbox>

                  <af:panelGroup layout="vertical">
                    <af:outputLabel value="Issue Date" />
                    <af:selectInputDate label="From :" showRequired="true"
                      value="#{genericIssuanceReport.startDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                    <af:selectInputDate label="To :" showRequired="true"
                      value="#{genericIssuanceReport.endDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                  </af:panelGroup>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{genericIssuanceReport.submit}" />
                    <af:commandButton text="Reset" 
                      action="#{genericIssuanceReport.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">

          <h:panelGrid border="1" width="1100"
            rendered="#{genericIssuanceReport.hasResults}">
            <af:panelBorder>

              <af:showDetailHeader text="Generic Issuance Report"
                disclosed="true">
                <af:table bandingInterval="2" banding="row" var="report"
                  emptyText='No data found.'
                  rows="#{genericIssuanceReport.pageLimit}" width="99%"
                  value="#{genericIssuanceReport.counts}">

                  <af:column headerText="Issuance Type"
                    bandingShade="light" sortProperty="issuanceTypeCd"
                    sortable="true" noWrap="true">
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:selectOneChoice readOnly="true"
                        value="#{report.issuanceTypeCd}">
                        <f:selectItems
                          value="#{generalIssuance.issuanceTypesAll}" />
                      </af:selectOneChoice>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column headerText="Count By DoLaa"
                    bandingShade="light" noWrap="true">
                    <af:panelHorizontal valign="middle" halign="left">

                      <af:table bandingInterval="2" banding="row"
                        var="doLaaCount" emptyText='No DoLaa.' rows="25"
                        width="99%" value="#{report.countsList}">

                        <af:column headerText="DoLaa" width="500px"
                          bandingShade="light" noWrap="true">
                          <af:panelHorizontal valign="middle"
                            halign="left">
                            <af:inputText readOnly="true"
                              value="#{doLaaCount.label}" />
                          </af:panelHorizontal>
                        </af:column>

                        <af:column bandingShade="light" noWrap="true">
                          <f:facet name="header">
                            <af:outputText value="Count" />
                          </f:facet>
                          <af:panelHorizontal valign="middle"
                            halign="left">
                            <af:inputText readOnly="true"
                              value="#{doLaaCount.value}" />
                          </af:panelHorizontal>
                        </af:column>

                      </af:table>
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
              </af:showDetailHeader>

            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>

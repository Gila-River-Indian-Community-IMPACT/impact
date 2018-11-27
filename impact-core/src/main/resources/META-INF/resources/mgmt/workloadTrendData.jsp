<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="3000"
    rendered="#{workloadTrendReport.hasSearchResults}">
    <af:panelBorder>
      <af:showDetailHeader text="Workload Trend Report" disclosed="true">
        <af:table bandingInterval="1" banding="row" var="details"
          emptyText='No Data' rows="#{workloadTrendReport.pageLimit}"
          width="99%" value="#{workloadTrendReport.details}">

          <af:column sortProperty="month" sortable="true"
            headerText="Month" width="60px">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.month}" >
                <f:convertDateTime pattern="yyyy MMM" />
              </af:inputText>              
            </af:panelHorizontal>
          </af:column>

          <af:column headerText="TOTAL">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['TOTAL']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['TOTAL']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['TOTAL']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>
          
          <af:column headerText="CDO">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['CDO']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['CDO']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['CDO']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="NEDO">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['NEDO']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['NEDO']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['NEDO']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="NWDO">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['NWDO']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['NWDO']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['NWDO']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="TDES">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['TDES']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['TDES']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['TDES']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="SWDO">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['SWDO']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['SWDO']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['SWDO']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="SEDO">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['SEDO']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['SEDO']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['SEDO']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="PLAA">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['PLAA']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['PLAA']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['PLAA']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="RAPCA">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['RAPCA']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['RAPCA']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['RAPCA']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="CDAQ">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['CDAQ']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['CDAQ']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['CDAQ']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="SWOAQA">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['HCDOES']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['HCDOES']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['HCDOES']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="CCHD">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['CCHD']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['CCHD']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['CCHD']['workflowCount']}" />
              </af:panelHorizontal>
            </af:column>
          </af:column>

          <af:column headerText="ARAQMD">
            <af:column headerText="Apps Received">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['ARAQMD']['appCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Permits Issued">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['ARAQMD']['permitCount']}" />
              </af:panelHorizontal>
            </af:column>
            <af:column headerText="Workflows Outstanding">
              <af:panelHorizontal valign="middle" halign="center">
                <af:inputText readOnly="true"
                  value="#{details.counts['ARAQMD']['workflowCount']}" />
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


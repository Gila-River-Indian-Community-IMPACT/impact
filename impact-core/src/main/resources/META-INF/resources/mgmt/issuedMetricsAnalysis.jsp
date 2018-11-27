<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="#{issuedMetrics.width-300}"
    rendered="#{issuedMetrics.hasSearchResults}">
    <af:panelBorder>
      <af:showDetailHeader text="Issued Metrics Analysis" disclosed="true">
        <af:table bandingInterval="1" banding="row" var="analysisDetails"
          emptyText='No Issued Permits'
          rows="#{issuedMetrics.pageLimit}" width="99%"
          value="#{issuedMetrics.analysisDetails}">

          <af:column sortProperty="permitType" sortable="true"
            headerText="Permit Type">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectOneChoice unselectedLabel=" " readOnly="true"
                value="#{analysisDetails.permitType}">
                <mu:selectItems value="#{issuedMetrics.permitTypes}" />
              </af:selectOneChoice>
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="permitActionType" sortable="true"
            headerText="Action">
            <af:panelHorizontal valign="middle" halign="left">
              <af:outputText value="#{analysisDetails.permitActionType == 1? 'Waiver' : 'Permit'}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="descLabel" sortable="true"
            headerText="Desc">
            <af:panelHorizontal valign="middle" halign="left">
              <af:outputText value="#{analysisDetails.descLabel}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="subjectToPsd" sortable="true"
            headerText="Subject to PSD">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{analysisDetails.subjectToPsd}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="averageAqdDays" sortable="true" formatType="number"
            headerText="Average Days to Final Issuance (AQD Days)"> 
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true" value="#{analysisDetails.averageAqdDaysString}" /> 
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="averageIssuanceDays" sortable="true" formatType="number"
            headerText="Average Days to Final Issuance (Total Days)">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true" value="#{analysisDetails.averageIssuanceDaysString}" /> 
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="permitCount" sortable="true"
            headerText="Number of Permits Issued">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true"
                value="#{analysisDetails.permitCount}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="prelimBenchmarkDays" sortable="true" 
            headerText="Benchmark (AQD Days from Receipt Through Completeness Review)">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true"
                value="#{analysisDetails.prelimBenchmarkDays}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="excessivePreliminaryCount" sortable="true"
            headerText="Number of Permits with AQD Days through Completeness Review Outside Benchmark">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true"
                value="#{analysisDetails.excessivePreliminaryCount}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="benchmarkDays" sortable="true"
            headerText="Benchmark (AQD Days to Final Issuance)">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true"
                value="#{analysisDetails.benchmarkDays}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="excessiveCount" sortable="true"
            headerText="Number of Permits Issued Outside Benchmark">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true"
                value="#{analysisDetails.excessiveCount}" />
            </af:panelHorizontal>
          </af:column>
          
          

          <f:facet name="footer">
            <afh:rowLayout halign="center">
              <af:panelButtonBar>
                <af:commandButton text="#{issuedMetrics.hideShowTitle}"
                  action="#{issuedMetrics.submitChart}" rendered="false"/>
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


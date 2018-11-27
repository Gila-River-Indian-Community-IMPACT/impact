<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="#{issuedMetrics.width}"
    rendered="#{issuedMetrics.hasSearchResults}">
    <af:panelBorder>
      <af:showDetailHeader text="Issued Metrics Report" disclosed="true">
        <af:table bandingInterval="1" banding="row" var="details"
          emptyText='No Issued Permits'
          rows="#{issuedMetrics.pageLimit}" width="99%"
          value="#{issuedMetrics.details}">

          <af:column sortProperty="doLaaName" sortable="true"
            headerText="District">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.doLaaName}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="countyName" sortable="true"
            headerText="County">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.countyName}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="facilityId" sortable="true"
            noWrap="true" formatType="text" headerText="Facility ID">
            <af:commandLink action="#{facilityProfile.submitProfile}"
              text="#{details.facilityId}">
              <t:updateActionListener property="#{facilityProfile.fpId}"
                value="#{details.fpId}" />
              <t:updateActionListener
                property="#{menuItem_facProfile.disabled}" value="false" />
            </af:commandLink>
          </af:column>

          <af:column sortProperty="facilityNm" sortable="true"
            headerText="Facility Name">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.facilityNm}" />
            </af:panelHorizontal>
          </af:column>

          <af:column headerText="Permit Number" sortable="true"
            sortProperty="permitNumber" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:commandLink text="#{details.permitNumber}"
                action="#{permitDetail.loadPermit}">
                <af:setActionListener to="#{permitDetail.permitID}"
                  from="#{details.permitId}" />
                <t:updateActionListener
                  property="#{permitDetail.fromTODOList}" value="false" />
              </af:commandLink>
            </af:panelHorizontal>
          </af:column>
<%--
          <af:column sortProperty="euCount" sortable="true">
            <f:facet name="header">
              <af:outputText value="EU Count" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{details.euCount}" />
            </af:panelHorizontal>
          </af:column>
 --%>         
          <af:column sortProperty="permitType" sortable="true"
            headerText="Permit Type">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectOneChoice unselectedLabel=" " readOnly="true"
                value="#{details.permitType}">
                <mu:selectItems value="#{issuedMetrics.permitTypes}" />
              </af:selectOneChoice>
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="permitActionType" sortable="true"
            headerText="Action">
            <af:panelHorizontal valign="middle" halign="left">
              <af:outputText value="#{details.permitActionType == 1? 'Waiver' : 'Permit'}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="subjectToPsd" sortable="true"
            headerText="Subject to PSD">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.subjectToPsd}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="subjectToNansr" sortable="true"
            headerText="Subject to N-A NSR">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.subjectToNansr}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="reason" sortable="true"
            headerText="Permit Reason">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.reason}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="reviewerNameDOLAA" sortable="true"
            headerText="AQD Technical Reviewer">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.reviewerNameDOLAA}" />
            </af:panelHorizontal>
          </af:column>
<%--

          <af:column headerText="Draft Issuance Date" sortable="true"
            sortProperty="draftIssueDate" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.draftIssueDate}" />
            </af:panelHorizontal>
          </af:column>
 --%>
          <af:column sortProperty="startDt" sortable="true"
            headerText="Receipt Date">
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate value="#{details.startDt}"
                readOnly="true" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column headerText="Completeness Review Start Date" sortable="true"
            sortProperty="completenessReviewStartDt" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.completenessReviewStartDt}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column headerText="Completeness Review End Date" sortable="true"
            sortProperty="completenessReviewEndDt" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.completenessReviewEndDt}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column headerText="Tech Review/Draft Permit/Waiver Start Date" sortable="true"
            sortProperty="techReviewStartDt" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.techReviewStartDt}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column headerText="Manager Supervisor Review Start Date" sortable="true"
            sortProperty="managerReviewStartDt" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.managerReviewStartDt}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column headerText="Public Notice Date" sortable="true"
            sortProperty="publicNoticePublishDate" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.publicNoticePublishDate}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column headerText="Final Issuance Date" sortable="true"
            sortProperty="finalIssueDate" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.finalIssueDate}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column headerText="Workflow End Date" sortable="true"
            sortProperty="endDt" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.endDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="preliminaryDays" sortable="true"
            headerText="Receipt through Compl. Review Days">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true"
                value="#{details.preliminaryDays}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="processId" sortable="true"
            headerText="Workflow ID" rendered="false">
            <af:panelHorizontal valign="middle" halign="center">
              <af:commandLink action="#{workFlow2DDraw.submitProfile}">
                <af:outputText value="#{details.processId}" />
                <t:updateActionListener
                  property="#{workFlow2DDraw.processId}"
                  value="#{details.processId}" />
                <t:updateActionListener
                  property="#{menuItem_workflowProfile.disabled}"
                  value="false" />
              </af:commandLink>
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="totalAgencyDays" sortable="true"
            headerText="AQD Days">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true" value="#{details.totalAgencyDays}" /> 
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="totalNonAgencyDays" sortable="true"
            headerText="Non-AQD Days">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true"
                value="#{details.totalNonAgencyDays}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="totalIssuanceDays" sortable="true"
            headerText="Total Days">
            <af:panelHorizontal valign="middle" halign="right">
              <af:inputText readOnly="true"
                value="#{details.totalIssuanceDays}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="notes" sortable="true"
            headerText="Notes" rendered="#{issuedMetrics.showNotes}">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" rows="3" columns="150"
                value="#{details.notes}" />
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


<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="#{issuedMetrics.width}"
    rendered="#{permitSOP.hasSearchResults}">
    <af:panelBorder>
      <af:showDetailHeader text="Permits Status Report" disclosed="true">
        <af:table bandingInterval="1" banding="row" var="details"
          emptyText='No Issued Permits' rows="#{permitSOP.pageLimit}"
          width="99%" value="#{permitSOP.details}">

          <af:column sortProperty="doLaaShortDsc" sortable="true"
            headerText="District">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.doLaaShortDsc}" />
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
          
          <af:column sortProperty="permitClassCd" sortable="true"
            headerText="Permitting Classification">
	          <af:selectOneChoice
				value="#{details.permitClassCd}"
				id="facPermitClassCd" readOnly="true">
				<f:selectItems
					value="#{facilityReference.permitClassDefs.items[(empty details.permitClassCd ? '' : details.permitClassCd)]}" />
				</af:selectOneChoice>
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

          <af:column sortProperty="permitType" sortable="true"
            headerText="Permit Type">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectOneChoice unselectedLabel=" " readOnly="true"
                value="#{details.permitType}">
                <mu:selectItems value="#{permitReference.permitTypes}" />
              </af:selectOneChoice>
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="reason" sortable="true"
            headerText="Permit Reason">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.reason}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="permitEuCount" sortable="true"
            headerText="Permit EU Count">
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true" value="#{details.permitEuCount}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="issueDraft" sortable="true"
            headerText="Issue Draft">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.issueDraft == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="general" sortable="true"
            headerText="General">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.general == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="express" sortable="true"
            rendered="#{permitSOP.expressSelected}" headerText="Express">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.express == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="rush" sortable="true"
            headerText="Rush">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.rush == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="statusStr" sortable="true"
            rendered="true" headerText="Permit Status">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.statusStr}" />
            </af:panelHorizontal>
          </af:column>

          <af:column headerText="Draft Issuance Date" sortable="true"
            sortProperty="draftIssueDate" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.draftIssueDate}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column headerText="End of Comment Period" sortable="true"
            sortProperty="draftCommentEndDate" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate readOnly="true"
                value="#{details.draftCommentEndDate}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="reviewerName" sortable="true"
            headerText="#{permitSOP.reviewerHeaderText}">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.reviewerName}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="userName" sortable="true"
            headerText="Current Task Assignment">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.userName}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="activityName" sortable="true"
            headerText="Current Task Name">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate value="#{details.activityName}"
                readOnly="true" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="activityStatusDesc" sortable="true"
            headerText="Task State">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.activityStatusDesc}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="activityDuration" sortable="true"
            headerText="Task Days">
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{details.activityDuration}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="processId" sortable="true"
            headerText="Workflow ID">
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

          <af:column sortProperty="startDt" sortable="true"
            headerText="Workflow Start Date">
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate value="#{details.startDt}"
                readOnly="true" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="days" sortable="true"
            headerText="Total Workflow Days">
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true" value="#{details.days}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="epaDays" sortable="true"
            headerText="District/CO Days">
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true" value="#{details.epaDays}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="notes" sortable="true"
            headerText="Notes" rendered="#{permitSOP.showNotes}">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" rows="3" columns="150"
                value="#{details.notes}" />
            </af:panelHorizontal>
          </af:column> 
		  
		  <af:column sortProperty="naics" sortable="true"
            headerText="NAICS" rendered="#{permitSOP.showNaics}">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.naics}" />
            </af:panelHorizontal>
          </af:column>         

          <f:facet name="footer">
            <afh:rowLayout halign="center">
              <af:panelButtonBar>
                <af:commandButton text="#{permitSOP.hideShowTitle}"
                  action="#{permitSOP.submitChart}" />
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


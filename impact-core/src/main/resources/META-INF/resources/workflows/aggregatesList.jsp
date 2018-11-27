<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<afh:rowLayout halign="left">
  <af:showDetailHeader text="Aggregate List" disclosed="true">
    <af:table value="#{activityProfile.aggregates}" bandingInterval="1"
      banding="row" var="aggregates" emptyText=" " rows="25" width="650"
      binding="#{activityProfile.table}" id="aggTable">
      <f:facet name="selection">
        <af:tableSelectMany />
      </f:facet>

      <af:column sortProperty="loopCnt" sortable="true"
        headerText="Task ID">
        <af:commandLink action="#{activityProfile.submitProfile}">
          <t:outputText
            value="#{aggregates.processId}-#{aggregates.activityTemplateId}-#{aggregates.loopCnt}"
            style="#{aggregates.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}"
            rendered="#{aggregates.processId != activityProfile.processId}" />
          <t:outputText
            value="#{aggregates.processId}-#{aggregates.activityTemplateId}-#{aggregates.loopCnt}"
            style="font-weight:bold; color:#FFFFFF; background-color:#000000"
            rendered="#{aggregates.processId == activityProfile.processId}" />
          <t:updateActionListener property="#{workFlow2DDraw.processId}"
            value="#{aggregates.processId}" />
          <t:updateActionListener
            property="#{activityProfile.fromExternal}" value="#{true}" />
          <t:updateActionListener
            property="#{activityProfile.activityTemplateId}"
            value="#{aggregates.activityTemplateId}" />
          <t:updateActionListener property="#{activityProfile.loopCnt}"
            value="#{aggregates.loopCnt}" />
        </af:commandLink>
      </af:column>

      <af:column sortProperty="activityTemplateNm" sortable="true"
        headerText="Task Name">
        <t:outputText value="#{aggregates.activityTemplateNm}"
          style="#{aggregates.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
      </af:column>

      <af:column sortProperty="facilityId" sortable="true" noWrap="true"
        headerText="Facility ID">
        <af:panelHorizontal valign="middle" halign="left">
          <af:commandLink action="#{facilityProfile.submitProfile}"
            inlineStyle="white-space: nowrap;">
            <t:outputText value="#{aggregates.facilityId}"
              style="#{aggregates.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
            <t:updateActionListener property="#{facilityProfile.fpId}"
              value="#{aggregates.fpId}" />
            <t:updateActionListener
              property="#{menuItem_facProfile.disabled}" value="false" />
          </af:commandLink>
        </af:panelHorizontal>
      </af:column>

      <af:column sortProperty="facilityNm" sortable="true"
        headerText="Facility Name">
        <af:outputText value="#{aggregates.facilityNm}"
          inlineStyle="#{aggregates.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
      </af:column>

      <af:column sortProperty="activityStatusCd" sortable="true"
        headerText="Task State">
        <af:selectOneChoice label="Task State" readOnly="true"
          inlineStyle="#{aggregates.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}"
          value="#{aggregates.activityStatusCd}">
          <f:selectItems value="#{workFlowDefs.activityStatus}" />
        </af:selectOneChoice>
      </af:column>

      <f:facet name="footer">
        <afh:rowLayout halign="center">
          <af:panelButtonBar rendered="#{!activityProfile.readOnlyUser}">
            <af:commandButton id="selfReassignSelected"
              rendered="#{!(activityProfile.activity.userId == activityProfile.userId) && !(activityProfile.activity.activityStatusCd == 'CM') && !(activityProfile.activity.activityStatusCd == 'CNC') && !(activityProfile.activity.activityStatusCd == 'URF') && !(activityProfile.activity.activityStatusCd == 'SK')}"
              text="Self Assign Selected"
              returnListener="#{activityProfile.selfReassignSelected}"
              action="#{confirmWindow.confirm}" useWindow="true"
              windowWidth="#{confirmWindow.width}"
              windowHeight="#{confirmWindow.height}">
              <t:updateActionListener property="#{confirmWindow.type}"
                value="#{confirmWindow.yesNo}" />
            </af:commandButton>
            <af:commandButton id="externalBeanDoSelected"
              rendered="#{activityProfile.doSelectedButton}"
              disabled="#{!(activityProfile.activity.userId == activityProfile.userId)}"
              text="#{activityProfile.doSelectedButtonText}"
              returnListener="#{activityProfile.externalBeanDoSelected}"
              action="#{confirmWindow.confirm}" useWindow="true"
              windowWidth="#{confirmWindow.width}"
              windowHeight="#{confirmWindow.height}">
              <t:updateActionListener property="#{confirmWindow.type}"
                value="#{activityProfile.doSelectedConfirmType}" />
              <t:updateActionListener
                property="#{confirmWindow.message}"
                value="#{activityProfile.doSelectedConfirmMsg}" />
            </af:commandButton>
            <af:commandButton
              text="#{activityProfile.checkInNM} Selected Task"
              disabled="#{!(activityProfile.activity.userId == activityProfile.userId)}"
              rendered="#{activityProfile.needAggCheckIn}"
              action="#{activityProfile.checkInSelected}" />
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
</afh:rowLayout>



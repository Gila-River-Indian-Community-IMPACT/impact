<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:showDetailHeader text="Task Information" disclosed="true">
  <h:panelGrid border="0" columns="2">
    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Task ID : " />
    </t:div>
    <af:inputText readOnly="true"
      value="#{activityProfile.activity.processId}-#{activityProfile.activity.activityTemplateId}-#{activityProfile.activity.loopCnt}" />

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Name : " />
    </t:div>
    <af:inputText readOnly="true"
      value="#{activityProfile.activity.activityTemplateNm}" />

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="State : " />
    </t:div>
    <af:selectOneChoice readOnly="true"
      value="#{activityProfile.activity.activityStatusCd}">
      <f:selectItems value="#{workFlowDefs.activityStatus}" />
    </af:selectOneChoice>

    <t:div style="text-align:right; width: 80px;"
      rendered="#{activityProfile.activity.activityStatusCd == 'RF' || activityProfile.activity.activityStatusCd == 'URF'}">
      <af:outputLabel value="Referral Type : " />
    </t:div>
    <af:selectOneChoice readOnly="true"
      value="#{activityProfile.activity.activityReferralTypeId}"
      rendered="#{activityProfile.activity.activityStatusCd == 'RF' || activityProfile.activity.activityStatusCd == 'URF'}">
      <mu:selectItems value="#{workFlowDefs.activityReferralTypes}" />
    </af:selectOneChoice>

    <t:div style="text-align:right; width: 80px;"
      rendered="#{activityProfile.activity.activityStatusCd == 'RF' || activityProfile.activity.activityStatusCd == 'URF'}">
      <af:outputLabel value="Extend Process : " />
    </t:div>
    <af:inputText readOnly="true"
      rendered="#{activityProfile.activity.activityStatusCd == 'RF' || activityProfile.activity.activityStatusCd == 'URF'}"
      value="" />

    <t:div style="text-align:right; width: 80px;"
      rendered="#{activityProfile.activity.activityStatusCd == 'RF' || activityProfile.activity.activityStatusCd == 'URF'}">
      <af:outputLabel value="Due Date : " />
    </t:div>
    <af:inputText readOnly="true"
      rendered="#{activityProfile.activity.activityStatusCd == 'RF' || activityProfile.activity.activityStatusCd == 'URF'}"
      value="#{activityProfile.activity.extendProcessEndDate == 'Y' ? 'Yes': 'No'}" />

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Status : " />
    </t:div>
    <af:selectOneChoice value="#{activityProfile.activity.status}"
      readOnly="true">
      <f:selectItems value="#{workFlowDefs.statusDef}" />
    </af:selectOneChoice>

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Staff : " />
    </t:div>
    <af:selectOneChoice readOnly="true"
      value="#{activityProfile.activity.userId}">
      <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
    </af:selectOneChoice>

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Start Date : " />
    </t:div>
    <af:selectInputDate readOnly="true" value="#{activityProfile.activity.startDt}" />

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Due Date : " />
    </t:div>
    <af:selectInputDate readOnly="true"
      value="#{activityProfile.activity.dueDt}" />

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="End Date : " />
    </t:div>
    <af:selectInputDate readOnly="true" value="#{activityProfile.activity.endDt}" />
  </h:panelGrid>
</af:showDetailHeader>
<af:showDetailHeader text="Workflow Information" disclosed="true">
  <h:panelGrid border="0" columns="2">
    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Facility ID : " />
    </t:div>
    <af:commandLink immediate="true" id="facilityLink"
      action="#{facilityProfile.submitProfile}"
      text="#{activityProfile.process.facilityIdString}">
      <t:updateActionListener property="#{facilityProfile.fpId}"
        value="#{activityProfile.activity.fpId}" />
      <t:updateActionListener property="#{menuItem_facProfile.disabled}"
        value="false" />
    </af:commandLink>

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Facility : " />
    </t:div>
    <af:inputText readOnly="true" columns="20" rows="2"
      value="#{activityProfile.process.facilityNm}"
      styleClass="border-width: 0px; background-color: #ffffff;" />

    <t:div style="text-align:right; width: 80px;"
      rendered="#{activityProfile.process.processCd != 'task'}">
      <af:outputLabel value="Type : " />
    </t:div>
    <af:selectOneChoice readOnly="true"
      rendered="#{activityProfile.process.processCd != 'task'}"
      value="#{activityProfile.process.processCd}">
      <f:selectItems value="#{workFlowDefs.processTypes}" />
    </af:selectOneChoice>

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Name : " />
    </t:div>
    <af:inputText readOnly="true"
      value="#{activityProfile.process.processTemplateNm}" />

    <t:div style="text-align:right; width: 80px;"
      rendered="#{activityProfile.externalNum != null}">
      <af:outputLabel for="numberLink" value="Number : " />
    </t:div>
    <af:commandLink id="numberLink"
      rendered="#{activityProfile.externalNum != null}"
      action="#{activityProfile.toExternal}" immediate="true"
      text="#{activityProfile.externalNum}" />

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Status : " />
    </t:div>
    <af:selectOneChoice value="#{activityProfile.process.status}"
      readOnly="true">
      <f:selectItems value="#{workFlowDefs.statusDef}" />
    </af:selectOneChoice>

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Created By : " />
    </t:div>
    <af:selectOneChoice readOnly="true"
      value="#{activityProfile.process.userId}">
      <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
    </af:selectOneChoice>

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Start Date : " />
    </t:div>
    <af:selectInputDate readOnly="true" value="#{activityProfile.activity.processStartDt}" />

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="Due Date : " />
    </t:div>
    <af:selectInputDate readOnly="true" value="#{activityProfile.activity.processDueDt}" />

    <t:div style="text-align:right; width: 80px;">
      <af:outputLabel value="End Date : " />
    </t:div>
    <af:selectInputDate readOnly="true" value="#{activityProfile.activity.processEndDt}" />
  </h:panelGrid>
</af:showDetailHeader>

<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Task Profile">
    <af:form
      partialTriggers="page:selfReassign page:externalBeanDoSelected page:skip page:aggTable:selfReassignSelected">
      <f:verbatim>
        <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
      </f:verbatim>
      <af:page var="foo" value="#{menuModel.model}" title="Task Profile"
        id="page">
        <jsp:include flush="true" page="../util/header.jsp" />

        <af:panelBorder>
          <mu:setProperty property="#{activityProfile.processId}"
            value="#{param.processId}" />
          <mu:setProperty
            property="#{activityProfile.activityTemplateId}"
            value="#{param.activityTemplateId}" />
          <mu:setProperty property="#{activityProfile.aggregate}"
            value="#{param.aggregate}" />

          <f:facet name="left">
            <h:panelGrid border="0" width="670"
              rendered="#{activityProfile.aggregate == 'Y'}">
              <jsp:include flush="true" page="aggregatesList.jsp" />
            </h:panelGrid>
          </f:facet>

          <f:facet name="innerLeft">
            <h:panelGrid border="1" width="266">
              <af:panelButtonBar
                rendered="#{!activityProfile.readOnlyUser}">
                <af:commandButton text="Detail"
                  action="#{activityProfile.detail}"
                  rendered="#{activityProfile.detailAble && activityProfile.aggregate == 'Y'}" />
                <af:commandButton text="List"
                  action="#{activityProfile.showAggregate}"
                  rendered="#{activityProfile.detailAble && activityProfile.aggregate == 'N'}" />
                <af:commandButton
                  disabled="#{!(activityProfile.activity.userId == activityProfile.userId)}"
                  text="#{activityProfile.checkInNM}"
                  action="#{activityProfile.checkIn}"
                  rendered="#{activityProfile.needCheckIn}" />
                <af:commandButton id="skip" text="Skip"
                  rendered="#{activityProfile.canSkip}"
                  disabled="#{!(activityProfile.activity.userId == activityProfile.userId)}"
                  returnListener="#{activityProfile.skip}"
                  action="#{confirmWindow.confirm}" useWindow="true"
                  windowWidth="#{confirmWindow.width}"
                  windowHeight="#{confirmWindow.height}">
                  <t:updateActionListener
                    property="#{confirmWindow.type}"
                    value="#{confirmWindow.yesNo}" />
                </af:commandButton>
                <af:commandButton text="CheckOut"
                  disabled="#{!(activityProfile.activity.userId == activityProfile.userId)}"
                  action="#{activityProfile.checkOut}"
                  rendered="#{activityProfile.needCheckOut}" />
                <af:commandButton text="End Referral"
                  disabled="#{!(activityProfile.activity.userId == activityProfile.userId)}"
                  action="dialog:endReferDate" useWindow="true"
                  windowWidth="650" windowHeight="300" immediate="true"
                  rendered="#{activityProfile.activity.activityStatusCd == 'RF' && activityProfile.activity.endDt == null}" />
                <af:commandButton id="selfReassign" text="Self Assign"
                  rendered="#{!(activityProfile.activity.userId == activityProfile.userId) && (!(activityProfile.activity.activityStatusCd == 'CM') && !(activityProfile.activity.activityStatusCd == 'CNC') && !(activityProfile.activity.activityStatusCd == 'URF') && !(activityProfile.activity.activityStatusCd == 'SK'))}"
                  returnListener="#{activityProfile.selfReassign}"
                  immediate="true" action="#{confirmWindow.confirm}"
                  useWindow="true" windowWidth="#{confirmWindow.width}"
                  windowHeight="#{confirmWindow.height}">
                  <t:updateActionListener
                    property="#{confirmWindow.type}"
                    value="#{confirmWindow.yesNo}" />
                </af:commandButton>
                <af:commandButton text="#{activityProfile.externalName}"
                  action="#{activityProfile.submitProfile}"
                  immediate="true"
                  rendered="#{activityProfile.externalName != null}">
                  <t:updateActionListener
                    property="#{activityProfile.fromExternal}"
                    value="false" />
                </af:commandButton>
              </af:panelButtonBar>
              
              <jsp:include page="activity.jsp" />
              
            </h:panelGrid>
          </f:facet>

          <h:panelGrid border="0" width="550"
            rendered="#{activityProfile.aggregate == 'N'}">
            <af:panelForm binding="#{activityProfile.data}" />

            <jsp:include flush="true" page="noteListTable.jsp" />
          </h:panelGrid>
        </af:panelBorder>

      </af:page>
      <af:iterator value="#{activityProfile}" var="validationBean"
        id="v">
        <%@ include file="../util/validationComponents.jsp"%>
      </af:iterator>
    </af:form>
  </af:document>
</f:view>


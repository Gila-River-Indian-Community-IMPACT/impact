<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Referral Task">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Referral Task">
        <jsp:include flush="true" page="../util/header.jsp" />

        <af:panelBorder>

          <f:facet name="left">
            <h:panelGrid border="1" width="266">
              <jsp:include page="activity.jsp" />
            </h:panelGrid>
          </f:facet>

          <af:panelForm partialTriggers="referralType">
            <af:selectOneChoice label="Referral Type :"
              id="referralType" value="#{activityProfile.referralType}"
              autoSubmit="TRUE" immediate="true">
              <mu:selectItems
                value="#{workFlowDefs.activityReferralTypes}" />
            </af:selectOneChoice>
            <af:selectOneChoice label="Extend Process Due Date :"
              rendered="FALSE"
              value="#{activityProfile.activity.extendProcessEndDate}">
              <f:selectItem itemValue="Y" itemLabel="Yes" />
              <f:selectItem itemValue="N" itemLabel="No" />
            </af:selectOneChoice>
            <af:selectInputDate label="Referral End Date :" id="red" required="true"
              value="#{activityProfile.referDate}" > 
              <af:validateDateTimeRange minimum="#{infraDefs.todaysDate}"/></af:selectInputDate>
          </af:panelForm>

          <af:objectSpacer width="100%" height="15" />
          <afh:rowLayout halign="center">
            <af:panelButtonBar>
              <af:commandButton text="Submit"
                rendered="#{!activityProfile.readOnlyUser}"
                action="#{activityProfile.referActivity}" />
            </af:panelButtonBar>
          </afh:rowLayout>

        </af:panelBorder>

      </af:page>
    </af:form>
  </af:document>
</f:view>

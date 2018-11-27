<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="Application Search">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>

      <af:inputHidden value="#{applicationSearch.popupRedirect}" />
      <af:page var="foo" value="#{menuModel.model}"
        title="Application Search">

        <%@ include file="../util/header.jsp"%>

        <jsp:useBean id="applicationSearch" scope="session"
          class="us.oh.state.epa.stars2.portal.application.ApplicationSearch" />
        <jsp:setProperty name="applicationSearch"
          property="fromFacility" value="false" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1">
            <af:panelBorder>
              <af:showDetailHeader text="Search Criteria"
                disclosed="true">
                <af:panelForm maxColumns="3" rows="1">
                  <af:inputText label="Facility ID :"
                    tip="0000000000, 0%, %0%, *0*, 0*"
                    value="#{applicationSearch.facilityID}"
                    maximumLength="20" />
                  <af:inputText label="Facility Name :"
                    tip="acme%, %acme% *acme*, acme*"
                    value="#{applicationSearch.facilityName}"
                    maximumLength="60" />

                  <af:selectOneChoice label="DO/LAA"
                  	unselectedLabel=" "
                    value="#{applicationSearch.doLaaCd}">
                    <f:selectItems value="#{applicationSearch.doLaas}" />
                  </af:selectOneChoice>

                  <af:selectOneChoice label="County"
                  	unselectedLabel=" "
                    value="#{applicationSearch.countyCd}">
                    <f:selectItems value="#{infraDefs.counties}" />
                  </af:selectOneChoice>

                  <af:inputText tip="A0000504, A%, %50% *50*, A*"
                    label="Application Number :"
                    value="#{applicationSearch.applicationNumber}"
                    maximumLength="8" />
                  <af:selectOneChoice label="Request type :"
                    unselectedLabel=" "
                    value="#{applicationSearch.applicationType}">
                    <mu:selectItems
                      value="#{applicationReference.applicationTypeDefs}" />
                  </af:selectOneChoice>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{applicationSearch.search}" />
                    <af:commandButton text="Reset"
                      action="#{applicationSearch.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />
        <afh:rowLayout halign="center">
          <h:panelGrid border="1"
            rendered="#{applicationSearch.hasSearchResults}">
            <af:showDetailHeader text="Request List" disclosed="true">
              <jsp:include flush="true"
                page="applicationSearchTable.jsp" />
            </af:showDetailHeader>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>

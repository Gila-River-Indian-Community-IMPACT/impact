<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Delete Alternate or Multiple Operating Scenario">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page>
        <af:messages />
        <af:panelForm>
          <af:objectSpacer width="100%" height="15" />
          <af:panelGroup layout="vertical">
            <af:outputText
              value="All data associated with this alternate or multiple operating
              scenario will be deleted. Would you like to continue?" />
          </af:panelGroup>

          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Yes"
                actionListener="#{applicationDetail.deleteScenario}" />
              <af:commandButton text="No" immediate="true">
                <af:returnActionListener />
              </af:commandButton>
            </af:panelButtonBar>
          </f:facet>
        </af:panelForm>
      </af:page>
    </af:form>
  </af:document>
</f:view>


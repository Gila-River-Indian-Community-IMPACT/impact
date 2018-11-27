<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Confirm Material Change">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page>
        <af:messages />
        <af:panelForm>
          <af:objectSpacer width="100%" height="15" />
          <af:panelGroup layout="vertical">
            <af:outputText
              value="You have selected a different material. All Process Emissions data will be re-initialized. Would you like to continue?" />
          </af:panelGroup>

          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Yes"
                action="#{reportProfile.changeMaterialAndReturn}" />
              <af:commandButton text="No" immediate="true"
                action="#{reportProfile.closeChangeMaterialDialog}" />
            </af:panelButtonBar>

		 </f:facet>
        </af:panelForm>
      </af:page>
    </af:form>
  </af:document>
</f:view>
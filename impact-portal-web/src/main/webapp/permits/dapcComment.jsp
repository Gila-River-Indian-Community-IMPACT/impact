<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="AQD Comment">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />
      <af:panelForm rows="1" maxColumns="2">
        <af:selectInputDate readOnly="true"
          value="#{permitDetail.tempComment.dateEntered}" />
        <af:selectOneChoice value="#{permitDetail.tempComment.userId}"
          readOnly="true">
          <f:selectItems id="users" value="#{infraDefs.basicUsersDef.allItems}" />
        </af:selectOneChoice>
      </af:panelForm>
      <af:panelForm>
        <af:inputText value="#{permitDetail.tempComment.noteTxt}" columns="100"
          rows="5" />
      </af:panelForm>
      <af:panelForm>
          <afh:rowLayout halign="center">
            <af:panelButtonBar>
              <af:commandButton text="Save"
                actionListener="#{permitDetail.saveComment}" />
              <af:commandButton text="Cancel"
                actionListener="#{applicationDetail.closeViewDoc}" />
            </af:panelButtonBar>
          </afh:rowLayout>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

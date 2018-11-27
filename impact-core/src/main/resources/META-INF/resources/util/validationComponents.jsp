<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<af:panelButtonBar>
  <%
  /* Hidden controls used by the validation popup - begin */
  %>
  <af:commandLink id="ValidationDlgAction"
    inlineStyle="visibility: hidden"
    action="#{validationBean.validationDlgAction}" />
  <af:inputHidden id="ValidationDlgReference"
    value="#{validationBean.validationDlgReference}" />
  <af:inputHidden id="ValidationDlgShowDetailClientID"
    value="#{validationBean.validationDlgShowDetailClientID}" />
  <af:inputHidden id="ValidationDlgEditMode"
    value="#{validationBean.editMode}" />
  <af:inputHidden id="getIdMap" value="#{validationBean.idMap}" />
  
  <%
  /* Hidden controls used by the validation popup - end */
  %>
</af:panelButtonBar>


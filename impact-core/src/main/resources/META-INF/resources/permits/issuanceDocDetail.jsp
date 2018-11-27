<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document title="Document Detail">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:messages />
      <af:panelForm>
        <af:objectSpacer height="5" rendered="#{permitDetail.docInfo != null}"/>
        <af:outputText rendered="#{permitDetail.docInfo != null}"
          value="#{permitDetail.docInfo}" />
        <af:objectSpacer height="5" rendered="#{permitDetail.docInfo != null}"/>
        <af:selectOneChoice label="Doc Type"
          readOnly="#{permitDetail.tempDoc.permitDocTypeCD != null}"
          value="#{permitDetail.tempDoc.permitDocTypeCD}">
          <mu:selectItems value="#{permitReference.permitDocTypeDefs}" />
        </af:selectOneChoice>
        <af:inputText label="Description" required="true"
          readOnly="#{permitDetail.docDescReadOnly}" maximumLength="512"
          value="#{permitDetail.tempDoc.description}" />
        <af:inputFile label="File to Upload"
          rendered="#{permitDetail.isDocUpload}"
          value="#{permitDetail.fileToUpload}" />
        <af:objectSpacer width="100%" height="10" />
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Apply"
              actionListener="#{permitDetail.applyEditDoc}" />
            <af:commandButton text="Cancel"
              immediate="true"
              actionListener="#{permitDetail.cancelEditDoc}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>


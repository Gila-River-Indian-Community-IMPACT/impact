<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document title="Attachment Detail">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:messages />
      <af:panelForm>
        <af:objectSpacer height="5"
          rendered="#{permitDetail.docInfo != null}" />
        <af:outputText rendered="#{permitDetail.docInfo != null}"
          value="#{permitDetail.docInfo}" />
        <af:objectSpacer height="5"
          rendered="#{permitDetail.docInfo != null}" />
        <af:selectOneChoice label="Attachment Type :"
           disabled="#{!permitDetail.NSRFeeSummaryEditAllowed}"
           value="#{permitDetail.singleDoc.permitDocTypeCD}">
          <mu:selectItems value="#{permitDetail.permitDynaimDocTypeInitialDefs}" />
        </af:selectOneChoice>
        <af:inputText label="Description :" required="true"
          disabled="#{!permitDetail.NSRFeeSummaryEditAllowed}"	
          readOnly="#{permitDetail.docDescReadOnly}" maximumLength="512"
          value="#{permitDetail.singleDoc.description}" />
        <af:objectSpacer width="100%" height="10" />
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Apply"
              rendered="#{permitDetail.dialogEdit and !permitDetail.readOnlyUser}"
              disabled="#{!permitDetail.NSRFeeSummaryEditAllowed}"
              actionListener="#{permitDetail.applyEditAttachment}" />
            <af:commandButton text="Cancel"
              rendered="#{permitDetail.dialogEdit}" immediate="true"
              disabled="#{!permitDetail.NSRFeeSummaryEditAllowed}"
              actionListener="#{permitDetail.cancelEditDoc}" />
            <af:commandButton text="Delete" id="delButton"
              disabled="#{!permitDetail.attachDocDelAble || !permitDetail.NSRFeeSummaryEditAllowed}"
              rendered="#{!permitDetail.readOnlyUser}"
              returnListener="#{permitDetail.deleteAttachment}"
              action="#{confirmWindow.confirm}" useWindow="true"
              windowWidth="#{confirmWindow.width}"
              windowHeight="#{confirmWindow.height}">
              <t:updateActionListener property="#{confirmWindow.type}"
                value="#{confirmWindow.yesNo}" />
              <t:updateActionListener
                property="#{confirmWindow.message}"
                value="Click Yes to confirm the deletion of #{permitDetail.singleDoc.description}" />
            </af:commandButton>
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>


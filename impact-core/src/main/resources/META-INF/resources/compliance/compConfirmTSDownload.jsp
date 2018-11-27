<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Document Download">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true" partialTriggers="TSDownloadYesBtn">
      <af:messages />

      <af:panelForm>
        <af:objectSpacer width="100%" height="15" />
        <af:outputFormatted id="TSConfirmMessage"
          value="#{attachments.tsConfirmMessage}" />
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:goButton id="TSDownloadYesBtn" text="Yes"
              destination="#{attachments.tradeSecretDocURL}"
              targetFrame="_blank"/>
            <af:commandButton id="TSDownloadCancelBtn" text="Cancel"
              immediate="true">
              <af:returnActionListener />
            </af:commandButton>
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>


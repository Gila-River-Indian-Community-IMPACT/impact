<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Add Note">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
     <af:form usesUpload="true" id="form">
      <af:messages />
        <%@ include file="../util/validate.js"%>
      <af:panelForm>
        <af:inputText value="#{activityProfile.tempNote.note}"
          label="Note: " columns="80" rows="10" id="noteTxt"
          maximumLength="2000" readOnly="#{activityProfile.noteReadOnly}" 
          onkeydown="charsLeft(2000);"
          onkeyup="charsLeft(2000);"/>
        
        <afh:rowLayout>
          <afh:cellFormat halign="right">
            <h:outputText style="font-size:12px" value="Characters Left :"/></afh:cellFormat>
          <afh:cellFormat halign="left">
            <h:inputText readonly="true" value="2000" id="messageText" /></afh:cellFormat>
        </afh:rowLayout>        
      </af:panelForm>
      <af:panelForm>
        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton text="Save"
              rendered="#{!activityProfile.noteReadOnly}"
              actionListener="#{activityProfile.saveComment}" />
            <af:commandButton text="Cancel"
              rendered="#{!activityProfile.noteReadOnly}"
              actionListener="#{activityProfile.closeDialog}" />
            <af:commandButton text="Close"
              rendered="#{activityProfile.noteReadOnly}"
              actionListener="#{activityProfile.closeDialog}" />
          </af:panelButtonBar>
        </afh:rowLayout>
      </af:panelForm>
    </af:form>
		<f:verbatim>
			<script type="text/javascript">
				charsLeft(2000);
			</script>
		</f:verbatim>
  </af:document>
</f:view>

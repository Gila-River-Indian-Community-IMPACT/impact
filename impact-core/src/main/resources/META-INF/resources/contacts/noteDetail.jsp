<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Add Note">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />
        <%@ include file="../util/validate.js"%>
      <af:panelForm>
        <af:inputText id="noteTxt" label="Note: " rows="10"
          value="#{contactDetail.contactNote.noteTxt}" columns="80"
          maximumLength="4000"
          readOnly="#{contactDetail.readOnlyNote}"
          onkeydown="charsLeft(4000);"
          onkeyup="charsLeft(4000);">
        </af:inputText>
        
         <afh:rowLayout>
          <afh:cellFormat halign="right">
            <h:outputText style="font-size:12px" value="Characters Left :"/></afh:cellFormat>
          <afh:cellFormat halign="left">
            <h:inputText size="3" value="4000" id="messageText" /></afh:cellFormat>
        </afh:rowLayout>
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Save"
              disabled="#{contactDetail.disableNoteButton}"
              rendered="#{contactDetail.editable1}"
              actionListener="#{contactDetail.applyEditNote}" />
            <af:commandButton text="Cancel"
              rendered="#{contactDetail.editable1}" immediate="true"
              action="#{contactDetail.cancelEditNote}" />
            <af:commandButton text="Edit"
              disabled="#{contactDetail.disableNoteButton}"
              rendered="#{!contactDetail.editable1}"
              actionListener="#{contactDetail.startEditNote}" />
            <af:commandButton text="Close"
              rendered="#{! contactDetail.editable1}"
              action="#{contactDetail.closeDialog}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
		<f:verbatim>
			<script type="text/javascript">
				charsLeft(4000);
			</script>
		</f:verbatim>
	</af:document>
</f:view>


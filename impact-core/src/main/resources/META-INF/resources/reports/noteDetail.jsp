<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
<af:document title="Add Note">
	<f:verbatim>
		<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
	</f:verbatim>
	<af:form usesUpload="true" id="form1">
		<af:messages />
		  <%@ include file="../util/validate.js"%>
		<af:panelForm rendered="#{!reportSearch.ntvReport}" id="notePanel1">			
			<af:inputText id="noteTxt" label="Note: " rows="10" showRequired="true"
				value="#{reportProfile.reportNote.noteTxt}" columns="80"
				maximumLength="4000" readOnly="#{reportProfile.noteReadOnly}"
				onkeydown="charsLeft(4000);"
                onkeyup="charsLeft(4000);">
			</af:inputText>
			
			<afh:rowLayout>
              <afh:cellFormat halign="right">
                <h:outputText style="font-size:12px" value="Characters Left :"/></afh:cellFormat>
              <afh:cellFormat halign="left">
                <h:inputText readonly="true" size="3" value="4000" id="messageText" /></afh:cellFormat>
            </afh:rowLayout>
			<f:facet name="footer">
				<af:panelButtonBar id="bar1">
					<af:commandButton text="Save" rendered="#{!reportProfile.noteReadOnly}"
						actionListener="#{reportProfile.applyEditNote}" />
					<af:commandButton text="Cancel"
						rendered="#{!reportProfile.noteReadOnly}" immediate="true"
						action="#{reportProfile.cancelEditNote}" />
					<af:commandButton text="Close"
						rendered="#{reportProfile.noteReadOnly}"
						action="#{reportProfile.closeDialog}" />
				</af:panelButtonBar>
			</f:facet>
		</af:panelForm>
		<af:panelForm rendered="#{reportSearch.ntvReport}" id="notePanel2">			
			<af:inputText id="noteTxt" label="Note: " rows="10"
				value="#{erNTVDetail.reportNote.noteTxt}" columns="80"
				maximumLength="4000"
				readOnly="#{! erNTVDetail.editable || erNTVDetail.reportNote.userId != erNTVDetail.userID}"
				onkeydown="charsLeft(4000);"
                onkeyup="charsLeft(4000);">
			</af:inputText>
			
			<afh:rowLayout>
              <afh:cellFormat halign="right">
                <h:outputText style="font-size:12px" value="Characters Left :"/></afh:cellFormat>
              <afh:cellFormat halign="left">
                <h:inputText readonly="true" size="3" value="4000" id="messageText" /></afh:cellFormat>
            </afh:rowLayout>
			<f:facet name="footer">
				<af:panelButtonBar id="bar2">
					<af:commandButton text="Save" rendered="#{erNTVDetail.editable}"
						actionListener="#{erNTVDetail.applyEditNote}" />
					<af:commandButton text="Cancel" rendered="#{erNTVDetail.editable}"
						immediate="true" action="#{erNTVDetail.cancelEditNote}" />
					<af:commandButton text="Edit"
						disabled="#{erNTVDetail.reportNote.userId != erNTVDetail.userID}"
						rendered="#{!erNTVDetail.editable}"
						actionListener="#{erNTVDetail.startEditNote}" />
					<af:commandButton text="Close" rendered="#{! erNTVDetail.editable}"
						action="#{erNTVDetail.closeDialog}" />
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


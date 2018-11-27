<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Add Note">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<%@ include file="../util/validate.js"%>
			<af:panelForm>
				<af:inputText id="noteTxt" label="Note: " rows="10"
					readOnly="#{continuousMonitorDetail.noteReadOnly || !continuousMonitorDetail.continuousMonitorEditAllowed}"
					value="#{continuousMonitorDetail.continuousMonitorNote.noteTxt}"
					columns="80" maximumLength="4000" onkeydown="charsLeft(4000);"
					onkeyup="charsLeft(4000);" />

				<afh:rowLayout>
					<afh:cellFormat halign="right">
						<h:outputText style="font-size:12px" value="Characters Left :" />
					</afh:cellFormat>
					<afh:cellFormat halign="left">
						<h:inputText readonly="true" size="3" value="4000"
							id="messageText" />
					</afh:cellFormat>
				</afh:rowLayout>

				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							disabled="#{continuousMonitorDetail.readOnlyUser}"
							rendered="#{continuousMonitorDetail.noteReadOnly}">
							<t:updateActionListener
								property="#{continuousMonitorDetail.noteReadOnly}" value="false" />
						</af:commandButton>
						<af:commandButton text="Save"
							disabled="#{continuousMonitorDetail.readOnlyUser || !continuousMonitorDetail.continuousMonitorEditAllowed}"
							rendered="#{!continuousMonitorDetail.noteReadOnly}"
							actionListener="#{continuousMonitorDetail.applyEditNote}" />
						<af:commandButton text="Cancel" immediate="true"
							rendered="#{!continuousMonitorDetail.noteReadOnly}"
							action="#{continuousMonitorDetail.cancelEditNote}" />
						<af:commandButton text="Close"
							rendered="#{continuousMonitorDetail.noteReadOnly}"
							actionListener="#{continuousMonitorDetail.closeViewNote}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
		<f:verbatim>
			<script type="text/javascript">
				charsLeft(4000);
			</script>
			<%@ include file="../scripts/jquery-1.9.1.min.js"%>
		</f:verbatim>
	</af:document>
</f:view>


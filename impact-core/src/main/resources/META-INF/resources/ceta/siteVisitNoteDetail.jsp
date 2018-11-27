<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Add Note">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<%@ include file="../util/validate.js"%>
			<af:panelForm>
				<af:inputText value="#{siteVisitDetail.tempComment.noteTxt}"
					label="Note: " readOnly="#{siteVisitDetail.noteReadOnly}" columns="80"
					rows="10" id="noteTxt" maximumLength="4000"
					onkeydown="charsLeft(4000);" onkeyup="charsLeft(4000);" />

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
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Save"
								rendered="#{!siteVisitDetail.noteReadOnly}"
								actionListener="#{siteVisitDetail.saveComment}" />
							<af:commandButton text="Cancel"
								rendered="#{!siteVisitDetail.noteReadOnly}"
								actionListener="#{siteVisitDetail.closeDialog}" />
							<af:commandButton text="Close"
								rendered="#{siteVisitDetail.noteReadOnly}"
								actionListener="#{siteVisitDetail.closeDialog}" />
						</af:panelButtonBar>
					</afh:rowLayout>
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

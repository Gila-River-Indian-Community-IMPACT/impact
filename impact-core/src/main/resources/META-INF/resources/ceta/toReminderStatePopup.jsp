<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="stSubmitBody" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Move Stack Test to Reminder State">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<af:panelHeader>
				<af:messages />
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}" />
				</f:facet>
				<af:panelForm maxColumns="1">
					<af:outputFormatted
						value="Set a reminder date and click <b>Move to Reminder State</b> or <b>Cancel</b> the operation." />
					<af:objectSpacer height="30" width="10"/>
					<af:selectInputDate label="Reminder Date:" id="remDate"
						value="#{stackTestDetail.stackTest.reminderDate}">
						<af:validateDateTimeRange
							minimum="#{stackTestDetail.tomorrowsDate}" />
					</af:selectInputDate>
					<f:facet name="footer">
						<af:panelButtonBar>
							<af:commandButton text="Move to Reminder State"
								action="#{stackTestDetail.moveToReminderState}" />
							<af:objectSpacer width="20" />
							<af:commandButton text="Cancel"
								action="#{stackTestDetail.cancel}" />
						</af:panelButtonBar>
					</f:facet>
				</af:panelForm>
			</af:panelHeader>
		</af:form>
	</af:document>
</f:view>

<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="stSubmitBody" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Stack Test Submit">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<af:panelHeader>
				<af:messages />
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"/>
				</f:facet>
				<af:panelForm>
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="Click <b>Submit Stack Test</b> to submit the stack test or<br><br>Set a reminder date and click <b>Submit Stack Test & Create Reminder</b> to submit the stack test and create a cloned stack test in Reminder State." />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="center">
						<afh:cellFormat halign="left">
							<af:panelForm maxColumns="1">
								<af:commandButton text="Submit Stack Test"
									action="#{stackTestDetail.finishSubmitState}" />
							</af:panelForm>
						</afh:cellFormat>
						<afh:cellFormat halign="right">
							<af:panelForm maxColumns="1">
								<af:selectInputDate label="Reminder Date:" id="remDate"
									value="#{stackTestDetail.reminderDate}" >
									<af:validateDateTimeRange minimum="#{stackTestDetail.tomorrowsDate}"/>
								</af:selectInputDate>
								<af:commandButton text="Submit Stack Test & Create Reminder"
									action="#{stackTestDetail.finishSubmitStateAndCloneRem}" />
							</af:panelForm>
						</afh:cellFormat>
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="center">
						<af:commandButton text="Cancel" action="#{stackTestDetail.cancel}" 
							immediate="true"/>
					</afh:rowLayout>
				</af:panelForm>
			</af:panelHeader>
		</af:form>
	</af:document>
</f:view>

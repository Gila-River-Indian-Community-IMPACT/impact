<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document id="body" title="#{submitTask.title}">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form >
			<afh:rowLayout halign="center">
				<h:panelGrid>
					<af:panelForm>
						<af:outputFormatted
					        inlineStyle="color: red; font-weight: bold;"
					        value="<b>ERROR: Duplicate Application</b>" />
						<af:objectSpacer height="10" />
						<af:outputFormatted
							value="Application #{submitTask.applicationDetail.application.applicationNumber} already exists in the
							system.	This could happen if you have attempted to submit this application before but the system had 
							failed to remove the application task after the submission was completed." />
						<af:objectSpacer height="15" />
						<af:outputFormatted
							value="You can check to see if the application #{submitTask.applicationDetail.application.applicationNumber}
							was	successfully submitted before by navigating to the Applications Menu under the IMPACT Home tab.	If you
							see application #{submitTask.applicationDetail.application.applicationNumber} in the applications list and
							it has a value in the Received Date column, the application has been already submitted. In that case, you
							should delete the task for application #{submitTask.applicationDetail.application.applicationNumber} from
							your In Progress Tasks list. If application #{submitTask.applicationDetail.application.applicationNumber}
							is not present in the applications list or if you believe there is some error with the submitted 
							application, please contact the Air Quality Division." />
						<af:objectSpacer height="30" />
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton text="Close"
									action="#{submitTask.closeSubmitTask}" immediate="true"/>
							</af:panelButtonBar>
						</afh:rowLayout>
					</af:panelForm>
				</h:panelGrid>
			</afh:rowLayout>
		</af:form>
	</af:document>
</f:view>
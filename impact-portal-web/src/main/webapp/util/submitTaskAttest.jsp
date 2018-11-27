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
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page>
				<f:facet name="messages">
				  <af:messages />
				</f:facet>
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<af:outputFormatted value="<b>#{submitTask.title}</b>"
								rendered="#{submitTask.prettyTitle}" />
							<af:outputFormatted value="#{submitTask.message}" />
							<af:objectSpacer height="30" />
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="#{submitTask.button1Text}"
										rendered="#{submitTask.button1Text != null}"
										action="#{submitTask.processConfirmAttest}"
										returnListener="#{submitTask.returnSelected}" useWindow="true"
										windowWidth="512" windowHeight="512">
										<t:updateActionListener property="#{submitTask.selection}"
											value="#{submitTask.button1Text}" />
									</af:commandButton>
									<af:commandButton text="#{submitTask.button2Text}"
										rendered="#{submitTask.button2Text != null}"
										actionListener="#{submitTask.selected}">
										<t:updateActionListener property="#{submitTask.selection}"
											value="#{submitTask.button2Text}" />
									</af:commandButton>
									<af:commandButton text="#{submitTask.button3Text}"
										rendered="#{submitTask.button3Text != null}"
										actionListener="#{submitTask.selected}">
										<t:updateActionListener property="#{submitTask.selection}"
											value="#{submitTask.button3Text}" />
									</af:commandButton>

									<af:commandButton text="View What You are About to Submit"
										id="viewBtnRpt" rendered="#{submitTask.task.report != null}"
										action="#{reportProfile.prepareWhatYouSubmit}" useWindow="true"
										windowWidth="500" windowHeight="300">
									</af:commandButton>
									<af:commandButton text="View What You are About to Submit"
										id="viewBtnNtvRpt"
										rendered="#{submitTask.task.ntvReport != null}"
										shortDesc="Print current public and trade secret emissions information"
										action="#{erNTVDetail.printEmissionReport}" useWindow="true"
										windowWidth="500" windowHeight="300">
										<t:updateActionListener value="false"
											property="#{erNTVDetail.hideTradeSecret}" />
									</af:commandButton>

									<af:commandButton id="viewBtnNtvApp"
										text="View What You are About to Submit"
										rendered="#{submitTask.task.application != null}"
										useWindow="true" windowWidth="500" windowHeight="300"
										action="#{applicationDetail.prepareWhatYouSubmit}">
									</af:commandButton>
									
									<af:commandButton id="viewBtnCompRpt"
										rendered="#{submitTask.task.complianceReport != null}"
										text="View What You are About to Submit" useWindow="true"
										windowWidth="500" windowHeight="300"
										action="#{complianceReport.printComplianceReportAtSubmit}"> 
									</af:commandButton>
									
									<af:commandButton id="viewBtnStk"
										rendered="#{submitTask.task.stackTest != null}"
										text="View What You are About to Submit" useWindow="true"
										windowWidth="500" windowHeight="300"
										action="#{stackTestDetail.printEmissionsTest}">
									</af:commandButton>
									
									<af:commandButton text="View What You are About to Submit"
										action="#{facilityProfile.reportFacilityProfile}"
										useWindow="true" windowWidth="500" windowHeight="300"
										rendered="#{submitTask.task.taskType == 'FC' || submitTask.task.taskType == 'FCH'}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
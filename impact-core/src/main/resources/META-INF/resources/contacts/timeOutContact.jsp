<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Contact - Time Out Current Associations">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:messages />
			<f:subview id="contact">
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" columns="1" width="400">
						<af:panelBorder>
							<af:panelHeader text="Timeout Contact's Current Associations" size="0" />

							<af:panelForm>

								<af:selectInputDate label="End Date:" id="endDate"
									value="#{contactDetail.timeOutEndDate}"
									readOnly="#{!contactDetail.timeOutEditable}"
									showRequired="false">
								</af:selectInputDate>

								<af:objectSpacer height="10" />

								<afh:rowLayout halign="left">
									<af:outputFormatted
										rendered="#{contactDetail.timeOutFinalStep}"
										inlineStyle="color: orange; font-weight: bold;"
										value="#{contactDetail.timeOutMessage}" />
								</afh:rowLayout>

								<af:objectSpacer height="10" />

								<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
									<afh:rowLayout halign="center">
										<af:outputFormatted
											rendered="#{contactDetail.timeOutFinalStep}"
											inlineStyle="color: orange; font-weight: bold;"
											value="Are you sure you would like to continue?" />
									</afh:rowLayout>
								</afh:tableLayout>

								<af:objectSpacer height="10" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>

										<af:commandButton text="Apply"
											action="#{contactDetail.applyTimeOut}"
											rendered="#{!contactDetail.timeOutFinalStep}" />
										<af:commandButton text="Yes"
											action="#{contactDetail.saveTimeOut}"
											rendered="#{contactDetail.timeOutFinalStep}" />
										<af:commandButton text="No"
											action="#{contactDetail.changeTimeOut}"
											rendered="#{contactDetail.timeOutFinalStep}" />
										<af:commandButton text="Cancel" immediate="true"
											action="#{contactDetail.cancelTimeOut}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelForm>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</f:subview>
		</af:form>
	</af:document>
</f:view>




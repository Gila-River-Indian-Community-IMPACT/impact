<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="IMPACT Inprogress Task Delete Operation">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page>
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<af:outputFormatted value="#{myTasks.deleteTaskMsg}" />
							<af:objectSpacer height="10" width="100%" />
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Yes"
										actionListener="#{myTasks.deleteTask}" onclick="return isDeleted();return false;">
									</af:commandButton>
									<af:commandButton text="No"
										actionListener="#{myTasks.canceldelInProgTask}">
									</af:commandButton>
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
    	<f:verbatim>
			<script type="text/javascript">
				var deleted = false;
				function isDeleted() {
					if (deleted) {
						return false;
					} else {
						deleted = true;
						return true;
					}
				}
			</script>
		</f:verbatim>		
	</af:document>
</f:view>

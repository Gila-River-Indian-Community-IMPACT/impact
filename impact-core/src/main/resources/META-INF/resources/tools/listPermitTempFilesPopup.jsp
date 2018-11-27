<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Temporary Permit Files">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" />
    <f:verbatim></script>
		</f:verbatim>
		<af:form>

			<af:page>
				<af:messages />
				<af:panelForm>
					<af:objectSpacer width="100%" height="15" />
					<af:panelBorder>
						<f:facet name="left">
							<af:iterator value="#{listPermitTempFiles.permitTempFiles}" var="file"
								rows="0">
								<af:panelGroup layout="vertical">
									<h:outputLink value="#{file.formURL}">
										<af:outputText value="#{file.fileName}" />
									</h:outputLink>
								</af:panelGroup>
							</af:iterator>
						</f:facet>
					</af:panelBorder>
					<f:facet name="footer">
						<af:panelButtonBar>
							<af:commandButton text="Close" immediate="true">
								<af:returnActionListener />
							</af:commandButton>
						</af:panelButtonBar>
					</f:facet>
				</af:panelForm>
			</af:page>

		</af:form>
	</af:document>
</f:view>

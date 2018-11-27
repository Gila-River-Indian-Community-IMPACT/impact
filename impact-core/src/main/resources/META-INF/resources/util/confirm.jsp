<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="#{confirmWindow.title}">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page>
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<af:outputFormatted value="<b>#{confirmWindow.title}</b>"
								rendered="#{confirmWindow.prettyTitle}" />
							<af:outputFormatted value="#{confirmWindow.message}" />
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="#{confirmWindow.button1Text}"
										rendered="#{confirmWindow.button1Text != null}"
										actionListener="#{confirmWindow.selected}">
										<t:updateActionListener property="#{confirmWindow.selection}"
											value="#{confirmWindow.button1Text}" />
									</af:commandButton>
									<af:commandButton text="#{confirmWindow.button2Text}"
										rendered="#{confirmWindow.button2Text != null}"
										actionListener="#{confirmWindow.selected}">
										<t:updateActionListener property="#{confirmWindow.selection}"
											value="#{confirmWindow.button2Text}" />
									</af:commandButton>
									<af:commandButton text="#{confirmWindow.button3Text}"
										rendered="#{confirmWindow.button3Text != null}"
										actionListener="#{confirmWindow.selected}">
										<t:updateActionListener property="#{confirmWindow.selection}"
											value="#{confirmWindow.button3Text}" />
									</af:commandButton>
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>


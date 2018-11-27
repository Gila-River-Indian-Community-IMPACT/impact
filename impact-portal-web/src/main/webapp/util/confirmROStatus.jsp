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
		<af:form usesUpload="true">
			<af:messages />
			<afh:rowLayout halign="center">
				<h:panelGrid>
					<af:panelForm>
						<af:outputFormatted value="<b>#{submitTask.title}</b>"
							rendered="#{submitTask.prettyTitle}" />
						<af:outputFormatted
							value="I am authorized by law to electronically sign and attest
							to the accuracy of this submission." />
						<af:objectSpacer height="30" />
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
				                <af:commandButton text="Yes"
				                  action="#{submitTask.processConfirmRO}"
				                  returnListener="#{submitTask.returnFirstSelected}"
				                  useWindow="true" windowWidth="#{submitTask.width + 200}"
				              	  windowHeight="#{submitTask.height + 150}"/>
				                <%-- not applicable for WY
				                 <af:commandButton text="No"
				                  action="#{submitTask.processConfirmNonRO}"
				                  returnListener="#{submitTask.returnFirstSelected}"
				                  useWindow="true" windowWidth="#{submitTask.width}"
				              	  windowHeight="#{submitTask.height + 150}"/>--%>
								<af:commandButton text="No"
									action="#{submitTask.cancelGetPin}" immediate="true"/>
							</af:panelButtonBar>
						</afh:rowLayout>
					</af:panelForm>
				</h:panelGrid>
			</afh:rowLayout>
		</af:form>
	</af:document>
</f:view>
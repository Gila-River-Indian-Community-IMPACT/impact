<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Finalized results">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page>
				<%@ include file="../util/branding.jsp"%>
				<afh:rowLayout halign="center">
					<af:panelGroup layout="vertical">
						<af:outputLabel for="issuedPermits"
							value="The following permits have been successfully published/issued:" />
						<af:panelGroup id="issuedPermits" layout="vertical">
							<af:iterator value="#{permitTables}" var="finalizedPermits">
								<%@ include file="finalizedPermitsTable.jsp"%>
								<af:objectSpacer width="100%" height="15" />
							</af:iterator>
						</af:panelGroup>
						<af:commandButton text="Close" onclick="window.close()" />
					</af:panelGroup>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

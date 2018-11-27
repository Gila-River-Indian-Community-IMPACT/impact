<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="IMPACT Company Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:page title="Company Detail">
			<jsp:include page="header.jsp" />
			<f:subview id="comCmpProfile">
				<jsp:include flush="true" page="comCompanyProfile.jsp" />


				<af:objectSpacer height="20" />
				<af:form>
					<af:panelForm>
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton text="Close"
									action="#{facilityProfile.closeDialog}" immediate="true" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</af:panelForm>
				</af:form>
			</f:subview>

		</af:page>
	</af:document>
</f:view>
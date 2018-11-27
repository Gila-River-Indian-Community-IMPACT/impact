<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="fceDelete" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Confirmation">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:page>
			<af:form usesUpload="true" id="form">
				<af:panelForm>
					<af:messages />
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="You are changing the set of included permits. This will affect the list of permit conditions that may be included in this inspection. Click Yes to proceed or No to cancel." />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Yes"
								action="#{fceDetail.saveFcePermitSelection}" />
							<af:commandButton text="No" action="#{fceDetail.close}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</af:form>
		</af:page>
	</af:document>
</f:view>

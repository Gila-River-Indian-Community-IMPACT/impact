<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="unDead" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Restore Permit & Workflow">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
				<af:panelForm>
					<af:messages />
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="CAUTION: Only choose this option if you are absolutely sure you must recover this permit and its workflow to continue work on it." />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Yes"
								action="#{permitDetail.unDeadEndPermit}" />
							<af:commandButton text="No"
								action="#{permitDetail.cancelPopup}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
		</af:form>
	</af:document>
</f:view>

<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="visitDelete" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Delete Site Visit Confirmation">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
				<af:panelForm>
					<af:messages />
					<afh:rowLayout halign="left">
						<af:outputFormatted rendered="#{!siteVisitDetail.disableDelete}"
							value="Click <b>Delete</b> to remove the Site Visit." />
						<af:outputFormatted rendered="#{siteVisitDetail.disableDelete}"
							inlineStyle="color: orange; font-weight: bold;"
							value="#{siteVisitDetail.disableDeleteMsg}" />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="left">
						<af:outputFormatted rendered="#{siteVisitDetail.afsWarning != null}"
							inlineStyle="color: orange; font-weight: bold;"
							value="#{siteVisitDetail.afsWarning}" />
					</afh:rowLayout>
					<af:objectSpacer height="10" rendered="#{siteVisitDetail.afsWarning != null}"/>
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Delete"
							disabled="#{siteVisitDetail.disableDelete}"
								action="#{siteVisitDetail.deleteVisit2}" />
							<af:commandButton text="Cancel"
								action="#{siteVisitDetail.close}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
		</af:form>
	</af:document>
</f:view>

<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Create Another Report">
		    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}">
				<afh:rowLayout halign="center">
					<af:outputFormatted
						value="Select the report you wish to copy data from for the new report you are creating. Select the report by clicking on the emissions inventory number.  Your new report will be associated with the same facility inventory as the report you select.<br><br>After creating the new report you can associate it with a different facility inventory." />
				</afh:rowLayout>
				<af:objectSpacer height="10" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<jsp:include flush="true" page="erCopyTable.jsp" />
					</h:panelGrid>
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="cancel" immediate="true"
							action="#{reportProfile.closeDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

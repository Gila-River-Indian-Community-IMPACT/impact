<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="confirmInspRptStateChange" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Confirm Inspection Report State Change">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
				<af:page>
					<afh:rowLayout halign="center">
						<af:outputFormatted value="#{fceDetail.infoStatement}" />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Yes"   rendered="#{fceDetail.fce.reportStateInitial}"
								disabled="#{fceDetail.readOnlyUser}"
								action="#{fceDetail.fcePrepare}" />
							<af:commandButton text="Yes"   rendered="#{fceDetail.fce.reportStatePrepare}"
								disabled="#{fceDetail.readOnlyUser}"
								action="#{fceDetail.fceComplete}" />
							<af:commandButton text="Yes"   rendered="#{fceDetail.fce.reportStateComplete}"
								disabled="#{fceDetail.readOnlyUser}"
								action="#{fceDetail.fceFinalize}" />
							<af:commandButton text="No"
								action="#{fceDetail.close}" />
						</af:panelButtonBar>
					</afh:rowLayout>	
				</af:page>
		</af:form>
	</af:document>
</f:view>


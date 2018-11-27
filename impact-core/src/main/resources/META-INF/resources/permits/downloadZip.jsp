<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Permit Documents">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" />
				<f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page>

				<%@ include file="../util/branding.jsp"%>

				<afh:rowLayout halign="center">
					<af:panelGroup layout="vertical">
						<af:goLink destination="#{permitDetail.url}"
							text="Zipped Permit Docuuments" />
						<af:objectSpacer width="100%" height="15" />
						<af:commandButton text="Close" onclick="window.close()" />
					</af:panelGroup>
				</afh:rowLayout>

			</af:page>
		</af:form>
	</af:document>
</f:view>
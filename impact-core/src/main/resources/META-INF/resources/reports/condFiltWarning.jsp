<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Warning:  Condensible exceeds Filterable">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page>
				<af:panelForm>
					<afh:rowLayout halign="center">
						<af:outputFormatted value="#{condFiltProb}"
							inlineStyle="color: orange; font-weight: bold;" />
					</afh:rowLayout>
					<af:objectSpacer height="10" width="100%" />
					<afh:rowLayout halign="center">
						<af:commandButton text="Close" onclick="window.close()" />
					</afh:rowLayout>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>

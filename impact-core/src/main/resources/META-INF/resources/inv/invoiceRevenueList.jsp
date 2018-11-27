<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Results">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page>
				<afh:rowLayout halign="left">
					<af:outputText value="#{partialResultMsg}"
						inlineStyle="color: orange; font-weight: bold;" />
				</afh:rowLayout>
				<afh:tableLayout halign="center" borderWidth="0" width="400">
					<afh:rowLayout>
						<afh:cellFormat halign="right">
							<af:outputText value="#{docsZipFile.notes}" />
						</afh:cellFormat>
						<afh:cellFormat halign="center">
							<af:goLink destination="#{docsZipFile.formURL}"
								text="#{docsZipFile.fileName}" />
						</afh:cellFormat>
					</afh:rowLayout>
				</afh:tableLayout>
				<af:objectSpacer height="10" width="100%" />
				<afh:rowLayout halign="center">
					<af:panelGroup layout="vertical">
						<af:iterator value="#{invoiceTables}" var="postedInvoices">
							<%@ include file="postedInvoiceTable.jsp"%>
							<af:objectSpacer height="15" />
						</af:iterator>
					</af:panelGroup>
				</afh:rowLayout>
				<af:objectSpacer height="10" width="100%" />
				<afh:rowLayout halign="center">
					<af:commandButton text="Close" onclick="window.close()" />
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

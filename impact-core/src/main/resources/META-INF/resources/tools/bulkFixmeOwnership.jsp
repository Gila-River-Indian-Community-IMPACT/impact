<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<f:verbatim>
		<script>
			</f:verbatim><h:outputText value="#{infraDefs.js}" />
			<f:verbatim>
		</script>
	</f:verbatim>

	<af:document title="Bulk FIXME Ownership">
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="Bulk FIXME Ownership">
				<%@ include file="../util/header.jsp"%>
				<af:objectSpacer height="10" />
				<af:panelForm width="1000" inlineStyle="margin:auto;"
					labelWidth="300">

					<afh:rowLayout halign="center">
						<af:objectSpacer height="30" />
						<af:commandButton text="Remove FIXME Companies" id="removeButton"
							disabled="#{!bulkFixmeOwnership.needFix}"
							action="#{bulkFixmeOwnership.removeFixmeCompanies}">
						</af:commandButton>
					</afh:rowLayout>
					<afh:rowLayout halign="center">
						<af:objectSpacer height="30" />

						<af:showDetailHeader text="FIXME Facilities List" disclosed="true"
							rendered="#{bulkFixmeOwnership.needFix}">
							<%@ include file="comFixmeCompanyTable.jsp"%></af:showDetailHeader>

						<af:outputFormatted rendered="#{!bulkFixmeOwnership.needFix}"
							value="<b>No FIXME data found.</b>" />
					</afh:rowLayout>

				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
	<af:objectSpacer height="300" />
</f:view>
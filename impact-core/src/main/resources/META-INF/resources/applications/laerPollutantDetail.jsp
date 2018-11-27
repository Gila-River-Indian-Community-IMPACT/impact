<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>


<f:view>
	<af:document id="laerPollutantBody" inlineStyle="width:1000px;">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" id="laerPollutantPage"
				title="LAER Pollutant Detail">
				<af:panelForm rows="5" maxColumns="1" labelWidth="140" width="900">
					<af:selectOneChoice id="pollutant" label="Pollutant:"
						value="#{applicationDetail.selectedLAEREmission.pollutantCd}"
						showRequired="true" readOnly="#{! applicationDetail.editable1}">
						<f:selectItems
							value="#{applicationDetail.nsrLaerPollutantDefs}" />
					</af:selectOneChoice>
					<af:inputText id="proposedLAER" label="Proposed LAER:"
						value="#{applicationDetail.selectedLAEREmission.laer}"
						columns="65" rows="1" maximumLength="500"
						readOnly="#{!applicationDetail.editable1}" showRequired="true" />
				</af:panelForm>

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							rendered="#{!applicationDetail.editable1}"
							disabled="#{!applicationDetail.euEditAllowed}"
							action="#{applicationDetail.editLAEREmission}" />
						<af:commandButton text="Save" id="saveButton"
							rendered="#{applicationDetail.editable1}"
							disabled="#{!applicationDetail.euEditAllowed}"
							action="#{applicationDetail.saveLAEREmission}" />
						<af:commandButton text="Cancel" id="cancelButton"
							rendered="#{applicationDetail.editable1}"
							action="#{applicationDetail.cancelEditLAEREmission}" />
						<af:commandButton text="Delete"
							rendered="#{!applicationDetail.editable1}"
							disabled="#{!applicationDetail.euEditAllowed}"
							action="#{applicationDetail.deleteLAEREmission}" />
						<af:commandButton text="Close"
							rendered="#{!applicationDetail.editable1}"
							action="#{applicationDetail.closeDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

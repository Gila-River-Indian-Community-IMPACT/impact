<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="shapeBody" title="Shape Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:panelHeader text="Shape Detail" size="0" />
			<af:panelForm rows="9" maxColumns="1" labelWidth="140">

				<afh:rowLayout halign="left">
					<af:inputText label="Shape ID:" rows="1"
						value="#{spatialData.modifyShape.shapeId}" id="modifyShapeId"
						readOnly="true">
					</af:inputText>
				</afh:rowLayout>
				
				<afh:rowLayout halign="left">
					<af:inputText label="Label:" columns="32" rows="1" id="modifyLabel"
						readOnly="#{!spatialData.editMode1}" showRequired="true"
						value="#{spatialData.modifyShape.label}" maximumLength="64">
					</af:inputText>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:inputText label="Description:" columns="32" rows="1" id="modifyDescription"
						readOnly="#{!spatialData.editMode1}" showRequired="false"
						value="#{spatialData.modifyShape.description}" maximumLength="128">
					</af:inputText>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:inputText id="modifyArea" label="Area (km2):"
						value="#{spatialData.modifyShape.area}" 
						readOnly="true">
					</af:inputText>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:inputText label="Perimeter (km):" id="modifyPerimeter"
						value="#{spatialData.modifyShape.length}" 
						readOnly="true">
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							rendered="#{!spatialData.editMode1}"
							disabled="#{!spatialData.editAllowed}"
							action="#{spatialData.edit}" />
						<af:commandButton text="Save"
							rendered="#{spatialData.editMode1}"
							action="#{spatialData.save}" />
						<af:commandButton text="Cancel" immediate="true"
							rendered="#{spatialData.editMode1}"
							action="#{spatialData.cancel}" />
						<af:commandButton text="Delete"
							rendered="#{!spatialData.editMode1}"
							disabled="#{!spatialData.deleteAllowed}"
							action="#{spatialData.delete}" />
						<af:commandButton text="Close" immediate="true"
							rendered="#{!spatialData.editMode1}"
							action="#{spatialData.closeDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
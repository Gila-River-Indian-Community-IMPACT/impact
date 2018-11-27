<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="EmissionsFugLeaksOilGas">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />

			<af:panelForm>
				<af:objectSpacer width="100%" height="15" />
				<af:selectOneChoice id="equipmentServiceTypeCd"
					label="Equipment and Service Type :"
					readOnly="#{! applicationDetail.editable1}"
					value="#{applicationDetail.applicationEUFugitiveLeaks.equipmentServiceTypeCd}"
					showRequired="true">
					<f:selectItems
						value="#{applicationDetail.appEUEquipServiceTypeDefs.items[(empty applicationDetail.applicationEUFugitiveLeaks.equipmentServiceTypeCd ? '' : applicationDetail.applicationEUFugitiveLeaks.equipmentServiceTypeCd)]}" />
				</af:selectOneChoice>
				<af:inputText id="equipmentTypeNumber" columns="12"
					label="Number of New or Modified Equipment Types :" maximumLength="12"
					readOnly="#{! applicationDetail.editable1}"
					value="#{applicationDetail.applicationEUFugitiveLeaks.equipmentTypeNumber}"
					showRequired="true" />
				<af:inputText id="leakRate" columns="12" label="Leak Rate (ppm) :"
					maximumLength="12" readOnly="#{! applicationDetail.editable1}"
					value="#{applicationDetail.applicationEUFugitiveLeaks.leakRate}"
					showRequired="true">
					<af:convertNumber pattern=".00" />
				</af:inputText>
				<af:inputText id="percentVoc" columns="12" label="Percent VOC :"
					maximumLength="12" readOnly="#{! applicationDetail.editable1}"
					value="#{applicationDetail.applicationEUFugitiveLeaks.percentVoc}" />
				<f:facet name="footer">
					<af:panelButtonBar>


						<af:commandButton text="Edit"
							rendered="#{!applicationDetail.editable1 && applicationDetail.editAllowed}"
							action="#{applicationDetail.editApplicationEUFugitiveLeaks}" />
						<af:commandButton text="Save" id="saveButton"
							rendered="#{applicationDetail.editable1}"
							action="#{applicationDetail.saveApplicationEUFugitiveLeaks}" />
						<af:commandButton text="Cancel"
							rendered="#{applicationDetail.editable1}" immediate="true"
							action="#{applicationDetail.cancelEditApplicationEUFugitiveLeaks}" />
						<af:commandButton text="Delete"
							rendered="#{!applicationDetail.editable1 && applicationDetail.editAllowed}"
							action="#{applicationDetail.deleteApplicationEUFugitiveLeaks}" />
						<af:commandButton text="Close"
							rendered="#{! applicationDetail.editable1}"
							action="#{applicationDetail.closeEditDialog}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>


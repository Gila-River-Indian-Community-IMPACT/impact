<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="contEquipDescBody" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Control Equipment Description">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<afh:rowLayout halign="center">
				<h:panelGrid columns="1">
					<af:table id="ceTab" emptyText=" "
						rendered="#{stackTestDetail.editable && stackTestDetail.controlEquipsRows}"
						value="#{stackTestDetail.controlEquips}" var="ce"
						bandingInterval="1" banding="row">
						<af:column formatType="icon" 
						headerText="Control Equipment used in (some of) the selected Emissions Units/Processes">
						<af:column formatType="icon">
							<af:commandButton text="AddText"
								action="#{stackTestDetail.addControlDesc}">
								<t:updateActionListener value="#{ce}"
									property="#{stackTestDetail.controlEquip}" />
							</af:commandButton>
						</af:column>
						<af:column sortable="true" sortProperty="controlEquipmentId"
							headerText="Control Equipment ID">
							<af:inputText value="#{ce.controlEquipmentId}" readOnly="true" />
						</af:column>
						<af:column sortable="true" sortProperty="equipmentTypeCd"
							headerText="Control Equipment Type">
							<af:selectOneChoice value="#{ce.equipmentTypeCd}" unselectedLabel="" readOnly="true">
								<f:selectItems
									value="#{infraDefs.contEquipTypes.items[(empty ce.equipmentTypeCd ? '' : ce.equipmentTypeCd)]}" />
							</af:selectOneChoice>
						</af:column>
						<af:column sortable="true" sortProperty="dapcDesc"
							headerText="AQD Description">
							<af:inputText value="#{ce.dapcDesc}" readOnly="true" />
						</af:column>
						<af:column sortable="true" sortProperty="regUserDesc"
							headerText="User Description">
							<af:inputText value="#{ce.regUserDesc}" readOnly="true" />
						</af:column>
						</af:column>
					</af:table>
					<af:objectSpacer height="10" width="10" />
					<af:inputText label="Control Equipment Used:"
						readOnly="#{!stackTestDetail.editable}" immediate="true"
						autoSubmit="true"
						value="#{stackTestDetail.stackTest.controlEquipUsed}"
						maximumLength="4000" columns="133" rows="4" />
					<af:objectSpacer height="10" width="10" />
					<afh:rowLayout halign="center">
						<af:commandButton immediate="true" text="Return"
							action="#{stackTestDetail.returnFromCeCopy}" />
					</afh:rowLayout>
				</h:panelGrid>
			</afh:rowLayout>
		</af:form>
	</af:document>
</f:view>

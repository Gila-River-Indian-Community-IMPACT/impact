<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Select Emissions Unit and/or Process Tested">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<%@ include file="../util/validate.js"%>
			<af:panelHeader text="Select the Emissions Unit(s)/SCC(s) tested." />
			<af:messages />
			<f:facet name="nodeStamp">
				<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
					type="#{foo.type}" disabled="#{foo.disabled}"
					rendered="#{foo.rendered}" icon="#{foo.icon}" />
			</f:facet>
			<af:panelGroup layout="vertical">
				<af:table id="euProcTable" emptyText=" "
					value="#{stackTestDetail.euDetails}" var="euProc"
					bandingInterval="1" banding="row">
					<af:column sortable="false" formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox readOnly="false"
							rendered="#{!euProc.alreadySelected && euProc.sccId!=euProc.noProcess}" value="#{euProc.selected}" />
					</af:column>
					<af:column formatType="text" headerText="Select EU/SCC Pair">
						<af:column sortable="true" sortProperty="emuId" formatType="text"
							headerText="Emissions Unit">
							<af:inputText readOnly="true" value="#{euProc.emuId}" />
						</af:column>
						<af:column sortProperty="sccId" sortable="true"
							headerText="SCC ID" formatType="text">
							<af:inputText readOnly="true" value="#{euProc.sccId}" />
							<af:inputText readOnly="true"
								rendered="#{euProc.sccActiveInfo != null}"
								value="#{euProc.sccActiveInfo}" inlineStyle="color: orange; font-weight: bold;" />
						</af:column>
					</af:column>
					<af:column sortProperty="euOpStatusCd" sortable="true"
						formatType="text" headerText="EU Operating Status">
						<af:outputText
							value="#{facilityReference.euOperatingStatusDefs.itemDesc[(empty euProc.euOpStatusCd ? '' : euProc.euOpStatusCd)]}"
							inlineStyle="#{(euProc.shutdownDate!=null) ? 'color: orange; font-weight: bold;' : '' }" />
						<af:selectInputDate value="#{euProc.shutdownDate}"
							inlineStyle="color: orange; font-weight: bold;" readOnly="true"
							rendered="#{euProc.shutdownDate != null}">
						</af:selectInputDate>
					</af:column>
					<af:column sortable="true" sortProperty="euDesc" formatType="text"
						headerText="Emissions Unit Description">
						<af:inputText readOnly="true" value="#{euProc.euDesc}" />
					</af:column>
					<af:column sortable="true" sortProperty="processDesc"
						formatType="text" headerText="Company Process Description">
						<af:inputText readOnly="true" value="#{euProc.processDesc}" />
					</af:column>
					<af:column sortable="true" sortProperty="controlEquipment"
						formatType="text" headerText="Associated Control Equipment">
						<af:inputText readOnly="true" value="#{euProc.controlEquipment}" />
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>
				<af:objectSpacer height="5" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Include Checked EU/SCCs"
							action="#{stackTestDetail.includeEuSccs}">
						</af:commandButton>
						<af:commandButton action="#{stackTestDetail.cancelAttachment}"
							text="Cancel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelGroup>
		</af:form>
	</af:document>
</f:view>

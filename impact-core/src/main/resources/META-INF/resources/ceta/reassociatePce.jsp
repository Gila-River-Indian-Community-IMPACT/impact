<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Associate Site Visits with FCE #{fceDetail.fceId}">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<af:panelHeader
				text="Associate Site Visits with Inspection #{fceDetail.fce.inspId}">
				<f:facet name="messages">
					<af:messages />
				</f:facet>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}" />
				</f:facet>
				<afh:rowLayout halign="center">
					<af:table id="svTable" emptyText=" "
						value="#{fceDetail.fce.siteVisits}" var="sv" bandingInterval="1"
						banding="row">
						<af:column sortable="true" sortProperty="selected"
							formatType="icon"
							headerText="Select">
							<af:selectBooleanCheckbox value="#{sv.selected}" />
						</af:column>
						<%@ include file="firstPlainVisitColumns.jsp"%>
						<%@ include file="visitColumns.jsp"%>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</afh:rowLayout>
				<af:objectSpacer height="5" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton action="#{fceDetail.saveReassign}"
							shortDesc="Selected visits will be associated with this Inspection"
							text="Save Association Changes" />
						<af:commandButton action="#{fceDetail.cancelReassign}"
							text="Cancel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelHeader>
		</af:form>
	</af:document>
</f:view>

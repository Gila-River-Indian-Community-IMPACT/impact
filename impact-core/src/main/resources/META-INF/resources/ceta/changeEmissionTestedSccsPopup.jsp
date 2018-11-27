<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="changeSccsBody"
		onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Add/Delete Stack Tested SCC IDs">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<%@ include file="../util/validate.js"%>
			<af:panelHeader>
				<af:messages />
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}" />
				</f:facet>
				<af:panelForm>
					<af:table id="sccTable" emptyText=" " width="400"
						value="#{stackTestDetail.testedSccs}" var="scc"
						bandingInterval="1" banding="row">
						<af:column formatType="icon" headerText="Processes Tested">
							<af:column sortable="true" sortProperty="selected"
								rendered="#{stackTestDetail.editable}" formatType="icon"
								headerText="Select">
								<af:selectBooleanCheckbox value="#{scc.selected}"
									readOnly="false" />
							</af:column>
							<af:column sortable="true" sortProperty="sccId" formatType="icon"
								headerText="SCC ID">
								<af:inputText maximumLength="10" columns="10" readOnly="true"
									value="#{scc.sccId}" />
							</af:column>
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center"
								rendered="#{stackTestDetail.editable}">
								<af:inputText label="SCC ID:" readOnly="false"
									shortDesc="Provide an SCC that is tested but is not in the facility inventory."
									value="#{stackTestDetail.providedScc}" maximumLength="8"
									columns="8" />
								<af:objectSpacer width="5" height="5" />
								<af:commandButton text="Add"
									shortDesc="Click to add entered SCC ID."
									action="#{stackTestDetail.enterSccId}" />
								<af:objectSpacer width="10" height="5" />
								<af:commandButton action="#{stackTestDetail.deleteSelectedSCCs}"
									rendered="#{stackTestDetail.editable}"
									shortDesc="Click to remove selected SCC IDs."
									text="Delete Selected SCC ID(s)" />
							</afh:rowLayout>
						</f:facet>
					</af:table>
					<af:objectSpacer height="5" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton action="#{stackTestDetail.sccChangesDone}"
							text="Return" />
					</af:panelButtonBar>
				</afh:rowLayout>
				</af:panelForm>
			</af:panelHeader>
		</af:form>
	</af:document>
</f:view>

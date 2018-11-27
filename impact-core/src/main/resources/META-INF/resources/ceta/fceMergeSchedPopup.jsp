<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="mergeSchedBody" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Merge Scheduled Inspection Information">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<%@ include file="../util/validate.js"%>
			<af:panelHeader messageType="Information" text="Merge Scheduled Inspection Information" >
				<af:messages />
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"/>
				</f:facet>
				<af:panelForm>
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="Select a scheduled inspection to merge into the completed inspection. The chosen scheduled inspection will be deleted after the merge." />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<af:table id="fceTable" emptyText=" "
						binding="#{fceDetail.allFCEs.table}"
						value="#{fceDetail.allFCEs}" var="fce" bandingInterval="1"
						banding="row" width="98%">
						<f:facet name="selection">
							<af:tableSelectOne/>
						</f:facet>
						<af:column sortable="true" sortProperty="fceId" formatType="icon"
							headerText="Inspection ID">
							<af:inputText readOnly="true" value="#{fce.inspId}" />
						</af:column>
						<jsp:include flush="true" page="../ceta/fceList.jsp" />
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
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Merge Scheduled Inspection Information"
								action="#{fceDetail.performSchedMerge}" />
							<af:commandButton text="Cancel"
								action="#{fceDetail.cancelMergeSched}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</af:panelHeader>
		</af:form>
	</af:document>
</f:view>

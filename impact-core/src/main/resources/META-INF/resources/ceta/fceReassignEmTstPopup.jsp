<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="siteVisitBody" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Set/Change Inspection Assignment">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<af:messages />
			<%@ include file="../util/validate.js"%>
			<af:panelHeader>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}" />
				</f:facet>
				<af:panelForm maxColumns="1" rendered ="#{stackTestDetail.stackTest.inspId != null}">
					<afh:rowLayout halign="left">
						<af:inputText label="Associated Inspection ID:" readOnly="true"
							rendered="#{stackTestDetail.stackTest.inspId != null}"
							value="#{stackTestDetail.stackTest.inspId}" />
					</afh:rowLayout>
				</af:panelForm>
				<af:panelForm>
					<afh:rowLayout halign="left">
						<af:outputFormatted rendered="#{stackTestDetail.stackTest.fceId != null}" inlineStyle="font-size:80%; color: rgb(0,0,0);"
							value="Select the Inspection to assign this Stack Test to or click <b>Clear Inspection Association</b> to assign to no Inspection." />
						<af:outputFormatted rendered="#{stackTestDetail.stackTest.fceId == null}" inlineStyle="font-size:80%; color: rgb(0,0,0);"
							value="Select the Inspection to assign this Stack Test to." />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<af:table id="fceTable" emptyText=" "
						binding="#{stackTestDetail.allFCEs.table}"
						value="#{stackTestDetail.allFCEs}" var="fce" bandingInterval="1"
						banding="row">
						<f:facet name="selection">
							<af:tableSelectOne disabled="#{stackTestDetail.stackTest.fceId == fce.id}"/>
						</f:facet>
						<af:column sortable="true" sortProperty="fceId" formatType="icon"
							headerText="Inspection ID">
							<af:inputText readOnly="true" value="#{fce.inspId}" />
						</af:column>
						<jsp:include flush="true" page="../ceta/firstFceList.jsp" />
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
							<af:commandButton text="Save Assignment Change" disabled ="#{stackTestDetail.allFCEsSize == 0}"
								action="#{stackTestDetail.saveChgFceAssign}" />
							<af:commandButton text="Clear Inspection Association" disabled="#{stackTestDetail.stackTest.fceId == null}"
								action="#{stackTestDetail.clearFceAssign}" />
							<af:commandButton text="Cancel"
								action="#{stackTestDetail.cancelChgFceAssign}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</af:panelHeader>
		</af:form>
	</af:document>
</f:view>

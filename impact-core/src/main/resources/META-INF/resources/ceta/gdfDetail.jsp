<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="body" title="GDF Inspections">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:messages />
			<af:panelForm>

				<af:selectOneChoice id="doLaaChoice" label="DO/LAA :" required="true"
					value="#{gdfDetail.gdf.doLaaCd}"
					readOnly="true">
					<f:selectItems value="#{facilitySearch.doLaas}" />
				</af:selectOneChoice>

				<af:selectOneChoice id="yearChoice" label="Year :" required="true"
					value="#{gdfDetail.gdf.year}"
					readOnly="#{gdfDetail.gdf.gdfId != null}">
					<f:selectItems
						value="#{complianceReportSearch.reportingYearsDef.items[(empty gdfDetail.gdf.year ? '' : gdfDetail.gdf.year)]}" />
				</af:selectOneChoice>
				<af:selectOneChoice id="monthChoice" label="Month :" required="true"
					value="#{gdfDetail.gdf.month}"
					readOnly="#{gdfDetail.gdf.gdfId != null}">
					<f:selectItems
						value="#{compEvalDefs.monthDef.items[(empty gdfDetail.gdf.month ? '' : gdfDetail.gdf.month)]}" />
				</af:selectOneChoice>

				<af:inputText id="stageOneText" label="Stage I: "
					readOnly="#{!gdfDetail.editMode}"
					value="#{gdfDetail.gdf.stageOne}" />

				<af:inputText id="stageOneAndTwoText" label="Stage I & II: "
					readOnly="#{!gdfDetail.editMode}"
					value="#{gdfDetail.gdf.stageOneAndTwo}" />

				<af:inputText id="nonStageOneAndTwoText" label="Non-Stage I & II: "
					readOnly="#{!gdfDetail.editMode}"
					value="#{gdfDetail.gdf.nonStageOneAndTwo}" />
				<af:objectSpacer width="100%" height="10" />
				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Edit" rendered="#{!gdfDetail.editMode}"
							actionListener="#{gdfDetail.enterEditGDF}" />
						<af:commandButton text="Save" rendered="#{gdfDetail.editMode}"
							actionListener="#{gdfDetail.applyEditGDF}" />
						<af:commandButton text="#{gdfDetail.editMode?'Cancel':'Close'}"  immediate="true"
							actionListener="#{gdfDetail.cancelEditGDF}" />
						<af:objectSpacer width="20" />
						<af:commandButton text="Delete" rendered="#{!gdfDetail.editMode && gdfDetail.gdf.gdfId != null}"
							actionListener="#{gdfDetail.applyRemoveGDF}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>

<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Title V Operational Restriction">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true" partialTriggers="complianceBox">
			<af:messages />
			<af:panelForm width="700px">
				<af:objectSpacer width="100%" height="15" />
				<af:selectOneChoice id="reqBasis"
					readOnly="#{! applicationDetail.operationalRestrictionModify}"
					label="Requirement Basis :"
					value="#{applicationDetail.operationalRestriction.reqBasisCd}"
					showRequired="true">
					<f:selectItems value="#{applicationReference.tvReqBasisDefs}" />
				</af:selectOneChoice>
				<af:inputText id="ruleCite" label="Permit/Waiver/State Rule/Federal Standard Cite :"
					maximumLength="50" columns="50"
					readOnly="#{! applicationDetail.operationalRestrictionModify}"
					value="#{applicationDetail.operationalRestriction.ruleCite}"
					showRequired="true" />
				<af:selectOneChoice id="restrictionType" label="Restriction Type :"
					unselectedLabel="Please select"
					value="#{applicationDetail.operationalRestriction.restrictionTypeCd}"
					readOnly="#{! applicationDetail.operationalRestrictionModify}"
					showRequired="true">
					<f:selectItems
						value="#{applicationReference.tvRestrictionTypeDefs}" />
				</af:selectOneChoice>
				<af:inputText id="restrictionDesc"
					label="Description of Restriction :" maximumLength="150"
					columns="50" rows="3"
					readOnly="#{! applicationDetail.operationalRestrictionModify}"
					value="#{applicationDetail.operationalRestriction.restrictionDesc}"
					showRequired="true" />
				<af:inputText id="complianceMethod"
					label="Method to Determine Compliance :" maximumLength="50"
					columns="50"
					readOnly="#{! applicationDetail.operationalRestrictionModify}"
					value="#{applicationDetail.operationalRestriction.complianceMethod}"
					tip="Include frequency of monitoring/testing" showRequired="true" />

				<af:objectSpacer width="100%" height="20" />

				<af:selectOneChoice id="complianceBox" label="In compliance? "
					value="#{applicationDetail.operationalRestriction.complianceFlag}"
					readOnly="#{! applicationDetail.operationalRestrictionModify}"
					autoSubmit="true" showRequired="true">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				<af:outputText
					rendered="#{!applicationDetail.operationalRestriction.compliant && applicationDetail.operationalRestriction.complianceFlag != null}"
					value="Describe how compliance will be achieved with a milestone schedule in the Compliance Plan, required to be attached at the facility level in this permit application."
					inlineStyle="font-size:75%;color:#666" />

				<af:objectSpacer width="100%" height="20" />

				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Save"
							rendered="#{applicationDetail.operationalRestrictionModify}"
							actionListener="#{applicationDetail.applyEditOperationalRestriction}" />
						<af:commandButton text="Edit"
							rendered="#{! applicationDetail.operationalRestrictionModify}"
							actionListener="#{applicationDetail.editTVOperationalRestriction}"
							disabled="#{!applicationDetail.euEditAllowed}" />
						<af:commandButton text="Delete"
							rendered="#{! applicationDetail.newOperationalRestriction && ! applicationDetail.operationalRestrictionModify}"
							actionListener="#{applicationDetail.removeTVOperationalRestriction}"
							disabled="#{!applicationDetail.euEditAllowed}" />
						<af:commandButton text="Cancel" immediate="true"
							rendered="#{applicationDetail.operationalRestrictionModify}"
							action="#{applicationDetail.cancelEditTVOperationalRestriction}" />
						<af:commandButton text="Close" immediate="true"
							rendered="#{!applicationDetail.operationalRestrictionModify}"
							actionListener="#{applicationDetail.closeTVOperationalRestriction}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>


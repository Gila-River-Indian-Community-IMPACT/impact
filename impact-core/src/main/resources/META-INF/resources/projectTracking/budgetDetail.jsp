<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Budget Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
		<af:page id="budgetDetail" var="foo" value="#{menuModel.model}">
			<af:panelHeader text="Budget Detail" />
				
				<f:facet name="messages">
					<af:messages />
				</f:facet>

				<af:panelForm rows="1" maxColumns="1" labelWidth="150px">
					<af:selectOneChoice id="BFY" label="BFY: " showRequired="true"
						unselectedLabel=""
						readOnly="#{!projectTrackingDetail.budgetEditMode}"
						value="#{projectTrackingDetail.budget.BFY}">
						<f:selectItems value="#{infraDefs.oddYears}" />
					</af:selectOneChoice>


					<af:selectOneChoice id="budgetFunctionCd" label="Function: "
						showRequired="true"
						unselectedLabel=""
						readOnly="#{!projectTrackingDetail.budgetEditMode}"
						value="#{projectTrackingDetail.budget.budgetFunctionCd}">
						<f:selectItems
							value="#{empty projectTrackingDetail.budget.budgetFunctionCd
											? projectTrackingReference.budgetFunctionDefs.items[0] 
											: projectTrackingReference.budgetFunctionDefs.items[projectTrackingDetail.budget.budgetFunctionCd]}" />
					</af:selectOneChoice>

					<af:inputText id="amountRO" label="Amount: " showRequired="true"
						readOnly="true" rendered="#{!projectTrackingDetail.budgetEditMode}"
						value="#{projectTrackingDetail.budget.amount}">
						<af:convertNumber type='currency' locale="en-US"
							minFractionDigits="2" />
					</af:inputText>
					<af:inputText id="amount" label="Amount: " showRequired="true"
						readOnly="false"
						rendered="#{projectTrackingDetail.budgetEditMode}"
						columns="15" maximumLength="14"
						value="#{projectTrackingDetail.budget.amount}">
						<f:converter
							converterId="us.oh.state.epa.stars2.webcommon.converter.BigDecimalConverter" />
					</af:inputText>

				</af:panelForm>
				
				<af:objectSpacer height="10" />
				
				<afh:rowLayout halign="center">
				<af:switcher
					facetName="#{projectTrackingDetail.budgetEditMode ? 'edit' : 'readOnly'}"
					defaultFacet="readOnly">
					<f:facet name="readOnly">
						<af:panelButtonBar>
							<af:commandButton id="editBtn" text="Edit"
								disabled="#{projectTrackingDetail.readOnlyUser}"
								action="#{projectTrackingDetail.enterBudgetEditMode}" />
								<af:commandButton id="deleteBtn" text="Delete" useWindow="true"
									windowWidth="#{confirmWindow.width}"
									windowHeight="#{confirmWindow.height}"
									returnListener="#{projectTrackingDetail.deleteBudget}"
									rendered="#{infraDefs.stars2Admin}"
									action="#{confirmWindow.confirm}">
									<t:updateActionListener property="#{confirmWindow.type}"
										value="#{confirmWindow.yesNo}" />
									<t:updateActionListener property="#{confirmWindow.message}"
										value="Click Yes to confirm the deletion of the budget" />
								</af:commandButton>
								<af:commandButton id="closeBtn" text="Close"
								action="#{projectTrackingDetail.closeBudgetDialog}" />
						</af:panelButtonBar>
					</f:facet>
					<f:facet name="edit">
						<af:panelButtonBar>
							<af:commandButton id="saveBtn" text="Save" 
								action="#{projectTrackingDetail.saveBudget}"/>
							<af:commandButton id="cancelBtn" text="Cancel"
								immediate="true"
								action="#{projectTrackingDetail.closeBudgetDialog}" />
						</af:panelButtonBar>
					</f:facet>
				</af:switcher>

			</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
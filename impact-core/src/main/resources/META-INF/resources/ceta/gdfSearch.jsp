<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Monthly GDF Inspections">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Monthly GDF Inspections">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000">
						<af:panelForm rows="1" maxColumns="2">
							<afh:rowLayout halign="center">
								<af:outputFormatted inlineStyle="color: rgb(0,0,255);"
									value="Select DO/LAA and click <b>Submit</b>.<br>To add an additional Month click <b>Add New Month</b> button.<br>To edit existing monthly totals, click on the Month in the far left column." />
							</afh:rowLayout>
							<af:panelForm rows="3" maxColumns="1">
								<af:selectOneChoice label="DO/LAA" value="#{gdfSearch.doLaaCd}"
									unselectedLabel="">
									<f:selectItems value="#{facilitySearch.doLaasCeta}" />
								</af:selectOneChoice>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit" action="#{gdfSearch.search}">
										</af:commandButton>
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelForm>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000"
						rendered="#{gdfSearch.hasSearchResults}">
						<af:showDetailHeader text="GDF Inspections List" disclosed="true">
							<jsp:include flush="true" page="gdfSearchTable.jsp" />
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

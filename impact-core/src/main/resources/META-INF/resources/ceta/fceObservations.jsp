<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Observations and Concerns">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">


			<af:page var="foo" value="#{menuModel.model}"
				title="Observations and Concerns" id="observations">
				<%@ include file="../util/branding.jsp"%>

			<f:verbatim>
				<script type="text/javaScript" >
					function charsLeft(maxChars, inputFieldId, charsLeftFieldId)
					{
					  
					  var field = document.getElementById(inputFieldId);
					  var count = document.getElementById(charsLeftFieldId);
					     
					  if(field.value.length > maxChars)
					    field.value = field.value.substring(0, maxChars);
					  else
					    count.textContent = maxChars - field.value.length;  
					}
				</script>
			</f:verbatim>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"
						onclick="if (#{fceDetail.editable}) { alert('Please save or discard your changes'); return false; }" />
				</f:facet>
				<afh:rowLayout halign="center" width="1000"
					rendered="#{!fceDetail.blankOutPage}">
					<h:panelGrid border="1">
						<af:panelBorder>
							<f:facet name="top">
								<f:subview id="fceHeader">
									<jsp:include page="fceHeader.jsp" />
								</f:subview>
							</f:facet>

							<af:objectSpacer height="10" />	
								
							<afh:rowLayout halign="center">
								<af:outputFormatted rendered="#{fceDetail.inspectionReadOnly}" inlineStyle="color: orange; font-weight: bold;" value="#{fceDetail.inspectionReadOnlyStatement}"/>		
							</afh:rowLayout>
							
							<af:objectSpacer height="10" />	
							
							<af:panelForm maxColumns="1">
								
									<af:showDetailHeader text="Inspection Concerns"
										disclosed="true">
										<af:panelForm rows="1" maxColumns="1">
											<af:inputText id="inspectionConcerns" columns="140" rows="15"
												maximumLength="20000"
												readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
												value="#{fceDetail.fce.inspectionConcerns}" 
												onkeydown="charsLeft(20000, 'observations:inspectionConcerns', 'observations:inspectionConcernsCharsLeft');" 
												onkeyup="charsLeft(20000, 'observations:inspectionConcerns', 'observations:inspectionConcernsCharsLeft');" />
										</af:panelForm>

										<afh:rowLayout>
											<afh:cellFormat halign="right">
												<h:outputText style="font-size:12px"
													value="Characters Left : " />
											</afh:cellFormat>
											<afh:cellFormat halign="left">
												<af:inputText id="inspectionConcernsCharsLeft"
													readOnly="true" columns="6"
													inlineStyle="border-style: none; font-size: 12px"
													value="20000" />
											</afh:cellFormat>
										</afh:rowLayout>
									</af:showDetailHeader>
								
									<af:showDetailHeader text="File Review/Records Request"
										disclosed="true">
										<af:panelForm rows="1" maxColumns="1">
											<af:inputText id="fileReview" columns="140" rows="4"
												maximumLength="2000"
												readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
												value="#{fceDetail.fce.fileReview}"
												onkeydown="charsLeft(2000, 'observations:fileReview', 'observations:fileReviewCharsLeft');"
												onkeyup="charsLeft(2000, 'observations:fileReview', 'observations:fileReviewCharsLeft');" />
										</af:panelForm>
										
										<afh:rowLayout>
											<afh:cellFormat halign="right">
												<h:outputText style="font-size:12px"
													value="Characters Left : " />
											</afh:cellFormat>
											<afh:cellFormat halign="left">
												<af:inputText id="fileReviewCharsLeft"
													readOnly="true" columns="6"
													inlineStyle="border-style: none; font-size: 12px"
													value="2000" />
											</afh:cellFormat>
										</afh:rowLayout>
									</af:showDetailHeader>

									<af:showDetailHeader text="Regulatory Discussion"
										disclosed="true">
										<af:panelForm rows="1" maxColumns="1">
											<af:inputText id="regulatoryDiscussion" columns="140"
												rows="4" maximumLength="2000"
												readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
												value="#{fceDetail.fce.regulatoryDiscussion}" 
												onkeydown="charsLeft(2000, 'observations:regulatoryDiscussion', 'observations:regulatoryDiscussionCharsLeft');" 
												onkeyup="charsLeft(2000, 'observations:regulatoryDiscussion', 'observations:regulatoryDiscussionCharsLeft');" />
										</af:panelForm>
										
										<afh:rowLayout>
											<afh:cellFormat halign="right">
												<h:outputText style="font-size:12px"
													value="Characters Left : " />
											</afh:cellFormat>
											<afh:cellFormat halign="left">
												<af:inputText id="regulatoryDiscussionCharsLeft"
													readOnly="true" columns="6"
													inlineStyle="border-style: none; font-size: 12px"
													value="2000" />
											</afh:cellFormat>
										</afh:rowLayout>
									</af:showDetailHeader>

									<af:showDetailHeader text="Physical Inspection of Plant"
										disclosed="true">
										<af:panelForm rows="1" maxColumns="1">
											<af:inputText id="physicalInspectionOfPlant" columns="140"
												rows="4" maximumLength="2000"
												readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
												value="#{fceDetail.fce.physicalInspectionOfPlant}" 
												onkeydown="charsLeft(2000, 'observations:physicalInspectionOfPlant', 'observations:physicalInspectionOfPlantCharsLeft');" 
												onkeyup="charsLeft(2000, 'observations:physicalInspectionOfPlant', 'observations:physicalInspectionOfPlantCharsLeft');" />
										</af:panelForm>
										
										<afh:rowLayout>
											<afh:cellFormat halign="right">
												<h:outputText style="font-size:12px"
													value="Characters Left : " />
											</afh:cellFormat>
											<afh:cellFormat halign="left">
												<af:inputText id="physicalInspectionOfPlantCharsLeft"
													readOnly="true" columns="6"
													inlineStyle="border-style: none; font-size: 12px"
													value="2000" />
											</afh:cellFormat>
										</afh:rowLayout>
									</af:showDetailHeader>

									<af:showDetailHeader text="Ambient Monitoring" disclosed="true">
										<af:panelForm rows="1" maxColumns="1">
											<af:inputText id="ambientMonitoring" columns="140" rows="4"
												maximumLength="2000"
												readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
												value="#{fceDetail.fce.ambientMonitoring}" 
												onkeydown="charsLeft(2000, 'observations:ambientMonitoring', 'observations:ambientMonitoringCharsLeft');" 
												onkeyup="charsLeft(2000, 'observations:ambientMonitoring', 'observations:ambientMonitoringCharsLeft');" />
										</af:panelForm>
										
										<afh:rowLayout>
											<afh:cellFormat halign="right">
												<h:outputText style="font-size:12px"
													value="Characters Left : " />
											</afh:cellFormat>
											<afh:cellFormat halign="left">
												<af:inputText id="ambientMonitoringCharsLeft"
													readOnly="true" columns="6"
													inlineStyle="border-style: none; font-size: 12px"
													value="2000" />
											</afh:cellFormat>
										</afh:rowLayout>
									</af:showDetailHeader>

									<af:showDetailHeader text="Other Information" disclosed="true">
										<af:panelForm rows="1" maxColumns="1">
											<af:inputText id="otherInformation" columns="140" rows="4"
												maximumLength="2000"
												readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
												value="#{fceDetail.fce.otherInformation}" 
												onkeydown="charsLeft(2000, 'observations:otherInformation', 'observations:otherInformationCharsLeft');" 
												onkeyup="charsLeft(2000, 'observations:otherInformation', 'observations:otherInformationCharsLeft');" />
										</af:panelForm>
										
										<afh:rowLayout>
											<afh:cellFormat halign="right">
												<h:outputText style="font-size:12px"
													value="Characters Left : " />
											</afh:cellFormat>
											<afh:cellFormat halign="left">
												<af:inputText id="otherInformationCharsLeft"
													readOnly="true" columns="6"
													inlineStyle="border-style: none; font-size: 12px"
													value="2000" />
											</afh:cellFormat>
										</afh:rowLayout>
									</af:showDetailHeader>

								<af:panelForm rows="3" maxColumns="1">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton text="Edit" action="#{fceDetail.editFce}"
												disabled="#{fceDetail.inspectionReadOnly}"
												rendered="#{!fceDetail.editable}" />
											<af:commandButton text="Save"
												action="#{fceDetail.saveObservationsAndConcerns}"
												rendered="#{fceDetail.editable}" />
											<af:commandButton text="Cancel"
												action="#{fceDetail.cancelObservationsAndConcernsEdit}"
												immediate="true" rendered="#{fceDetail.editable}" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</af:panelForm>

							</af:panelForm>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</af:page>
			<%-- hidden controls for navigation to compliance report from popup --%>
			<%@ include file="../util/hiddenControls.jsp"%>
			
		</af:form>
		
		<f:verbatim>
			<script type="text/javascript" >
				charsLeft(20000, 'observations:inspectionConcerns', 'observations:inspectionConcernsCharsLeft');
				charsLeft(2000, 'observations:fileReview', 'observations:fileReviewCharsLeft');
				charsLeft(2000, 'observations:regulatoryDiscussion', 'observations:regulatoryDiscussionCharsLeft');
				charsLeft(2000, 'observations:physicalInspectionOfPlant', 'observations:physicalInspectionOfPlantCharsLeft');
				charsLeft(2000, 'observations:ambientMonitoring', 'observations:ambientMonitoringCharsLeft');
				charsLeft(2000, 'observations:otherInformation', 'observations:otherInformationCharsLeft');
			</script>
		</f:verbatim>

	</af:document>
</f:view>

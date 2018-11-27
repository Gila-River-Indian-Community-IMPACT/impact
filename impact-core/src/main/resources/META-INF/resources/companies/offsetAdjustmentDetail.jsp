<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="offsetAdjustmentDetail"
		title="Emissions Offset Adjustment Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<af:form>
			<af:panelHeader text="Emissions Offset Adjustment Detail" size="0" />
			<af:panelForm rows="6" maxColumns="1" labelWidth="140"
				partialTriggers="nonAttainmentAreaCd">
				<af:selectOneChoice id="nonAttainmentAreaCd" 
					showRequired="true"
					label="Non-Attainment Area: " unselectedLabel=""
					readOnly="#{!companyProfile.editable1}"
					autoSubmit="true"
					value="#{companyProfile.modifyEmissionsOffsetAdjustment.nonAttainmentAreaCd}">
					<f:selectItems
						value="#{permitReference.offsetTrackingNonAttainmentAreaDefs.items[(empty companyProfile.modifyEmissionsOffsetAdjustment.nonAttainmentAreaCd ? '' : companyProfile.modifyEmissionsOffsetAdjustment.nonAttainmentAreaCd)]}" />
				</af:selectOneChoice>

				<af:selectOneChoice id="pollutantCd" 
					showRequired="true"
					label="Pollutant: " readOnly="#{!companyProfile.editable1}"
					value="#{companyProfile.modifyEmissionsOffsetAdjustment.pollutantCd}">
					<f:selectItems
						value="#{companyProfile.nonAttainmentAreaPollutants}"/>
				</af:selectOneChoice>
				
				<af:selectInputDate id="date"
					showRequired="true"
					label="Date: " readOnly="#{!companyProfile.editable1}"
					value="#{companyProfile.modifyEmissionsOffsetAdjustment.date}">
					<af:validateDateTimeRange
						minimum="1970-01-01"
						maximum="#{companyProfile.maxDate}"/>
				</af:selectInputDate>	
				
				<af:inputText id="amount" 
					columns="11" maximumLength="11"
					showRequired="true"
					label="Amount (Tons): " readOnly="#{!companyProfile.editable1}"
					value="#{companyProfile.modifyEmissionsOffsetAdjustment.amount}" >
					<af:convertNumber type="number" pattern="###,###.###" 
						minFractionDigits="3" maxFractionDigits="3" />
				</af:inputText>	
					
				<af:selectOneChoice id="includeInTotal" 
					showRequired="true"
					label="Include in Total (Y/N): " unselectedLabel=""
					readOnly="#{!companyProfile.editable1}"
					value="#{companyProfile.modifyEmissionsOffsetAdjustment.includeInTotal}">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				
				<af:inputText id="comment"
					showRequired="true"
					label="Comment: " readOnly="#{!companyProfile.editable1}" 
					maximumLength="200" rows="4" columns="70"
					value="#{companyProfile.modifyEmissionsOffsetAdjustment.comment}" />

				<af:objectSpacer height="20" />
				
				<afh:rowLayout halign="center">
				 	<af:switcher 
				 		facetName="#{companyProfile.editable1 ? 'edit' : 'view'}"
				 		defaultFacet="view">
				 		<f:facet name='view'>
				 		<af:panelButtonBar>
						 	<af:commandButton text="Edit"
								rendered="#{infraDefs.stars2Admin || userAttrs.NSRAdminUser}"
								action="#{companyProfile.startToEditEmissionsOffsetAdjustment}" />
							<af:commandButton text="Close"
								action="#{companyProfile.closeDialog}" />
							<af:commandButton text="Delete"
								rendered="#{infraDefs.stars2Admin || userAttrs.NSRAdminUser}"
								useWindow="true"
								windowWidth="#{confirmWindow.width}"  windowHeight="#{confirmWindow.height}"
								returnListener="#{companyProfile.deleteEmissionsOffsetAdjustment}"
								action="#{confirmWindow.confirm}">
				        	    <t:updateActionListener property="#{confirmWindow.type}"
				                	value="#{confirmWindow.yesNo}" />
				             	<t:updateActionListener property="#{confirmWindow.message}"
				                	value="Click Yes to confirm the deletion of emissions offset adjustment"/>
				             </af:commandButton>  
						</af:panelButtonBar>		
				 		</f:facet>
				 		<f:facet name="edit">
				 		<af:panelButtonBar>
				 			<af:commandButton text="Add"
				 				rendered="#{companyProfile.modifyEmissionsOffsetAdjustment.newObject}" 
								action="#{companyProfile.addEmissionsOffsetAdjustment}" />
							<af:commandButton text="Save" 
								rendered="#{!companyProfile.modifyEmissionsOffsetAdjustment.newObject}"
								action="#{companyProfile.saveEmissionsOffsetAdjustment}" />
							<af:commandButton text="Cancel"
								immediate="true"
								action="#{companyProfile.cancelEditEmissionsOffsetAdjustment}" />
						</af:panelButtonBar>			
				 		</f:facet>
				 	</af:switcher>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
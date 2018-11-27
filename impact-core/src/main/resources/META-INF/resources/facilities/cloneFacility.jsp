<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

    <f:view>
      <af:document title="IMPACT Clone Facility">
            <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
          <af:form>
          <af:inputHidden value="#{cloneFacility.popupRedirect}"/>
          <af:page var="foo" value="#{menuModel.model}" id="cloneFacility" title="IMPACT Clone Facility">
    		
    		<jsp:include page="header.jsp" />
   
		<afh:rowLayout halign="center">
           	<af:panelForm rows="1" maxColumns="2" width="100%">
           		<af:inputText label="From Facility ID:" value="#{cloneFacility.facilityId}" 
               		id="facId" columns="10" maximumLength="10"
               		showRequired="true"  autoSubmit="True"
                   	valueChangeListener="#{cloneFacility.loadEu}" onchange="$('.loading').show();" />
			</af:panelForm>
		</afh:rowLayout>
			

		<afh:rowLayout halign="center" partialTriggers="facId">
			<t:div style="display:none;" styleClass="loading">
				<af:objectImage source="/images/loading.gif" />
			</t:div>
		</afh:rowLayout>

		<afh:rowLayout halign="center">
			<af:panelGroup partialTriggers="facId">
				<h:panelGrid columns="1" border="0" width="950"
					rendered="#{cloneFacility.emusWrapper.rowCount != 0}"
					style="margin-left:auto;margin-right:auto;">
					<af:objectSpacer width="100%" height="15" />
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="<b>Select Emission Units to clone</b>" />
					</afh:rowLayout>
					<af:table id="emusTable" value="#{cloneFacility.emusWrapper}" width="98%"
						bandingInterval="1" binding="#{cloneFacility.emusWrapper.table}"
						banding="row" var="emissionUnit"
						selectionListener="#{cloneFacility.selectionEu}">
						<f:facet name="selection">
							<af:tableSelectMany autoSubmit="True" shortDesc="Select" />
						</f:facet>
						<af:column sortProperty="epaEmuId" sortable="true"
							formatType="text">
							<f:facet name="header">
								<af:outputText value="AQD ID" />
							</f:facet>
							<af:outputText value="#{emissionUnit.epaEmuId}" />
						</af:column>
						<af:column sortProperty="euDesc" sortable="true"
							formatType="text">
							<f:facet name="header">
								<af:outputText value="AQD Description" />
							</f:facet>
							<af:outputText value="#{emissionUnit.euDesc}" truncateAt="80" 
							shortDesc="#{emissionUnit.euDesc}"/>
						</af:column>
						<af:column sortProperty="companyId" sortable="true"
							formatType="text">
							<f:facet name="header">
								<af:outputText value="Company ID" />
							</f:facet>
							<af:outputText value="#{emissionUnit.companyId}" />
						</af:column>
						<af:column sortProperty="regulatedUserDsc" sortable="true"
							formatType="text">
							<f:facet name="header">
								<af:outputText value="Company Description" />
							</f:facet>
							<af:outputText value="#{emissionUnit.regulatedUserDsc}"
								truncateAt="80" 
								shortDesc="#{emissionUnit.regulatedUserDsc}"/>
						</af:column>
						<af:column sortProperty="emissionUnitTypeName" sortable="true"
							formatType="text">
							<f:facet name="header">
								<af:outputText value="Emissions Unit Type" />
							</f:facet>
							<af:outputText value="#{emissionUnit.emissionUnitTypeName}"
								truncateAt="80" 
								shortDesc="#{emissionUnit.emissionUnitTypeName}"/>
						</af:column>
						<af:column sortProperty="operatingStatusCd" sortable="true"
							formatType="text">
							<f:facet name="header">
								<af:outputText value="Operating Status" />
							</f:facet>
							<af:outputText
								value="#{facilityReference.euOperatingStatusDefs.itemDesc[(empty emissionUnit.operatingStatusCd ? '' : emissionUnit.operatingStatusCd)]}" />
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton
										actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Printable view" />
									<af:commandButton
										actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</h:panelGrid>
			</af:panelGroup>
		</afh:rowLayout>
			<!-- Control Equipment Table -->
		<afh:rowLayout halign="center">
			<af:panelGroup partialTriggers="facId emusTable">
				<h:panelGrid columns="1" border="0" width="950"
					rendered="#{cloneFacility.cesWrapper.rowCount!=0}"
					style="margin-left:auto;margin-right:auto;">
					<af:objectSpacer width="100%" height="15" />
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="<b>Selected Control Equipment to clone</b>" />
					</afh:rowLayout>
					<af:table value="#{cloneFacility.cesWrapper}" width="98%"
						bandingInterval="1" binding="#{cloneFacility.cesWrapper.table}"
						banding="row" var="controlEquip">
						<af:column sortProperty="c01" sortable="true" formatType="text"
							headerText="AQD ID" noWrap="true">
							<af:outputText value="#{controlEquip.controlEquipmentId}" />
						</af:column>
						<af:column sortProperty="c02" sortable="true" formatType="text"
							headerText="AQD Description">
							<af:outputText value="#{controlEquip.dapcDesc}" />
						</af:column>
						<af:column sortProperty="c03" sortable="true" formatType="text"
							headerText="Company ID">
							<af:outputText value="#{controlEquip.companyId}" />
						</af:column>
						<af:column sortProperty="c04" sortable="true" formatType="text"
							headerText="Company Description">
							<af:outputText value="#{controlEquip.regUserDesc}" />
						</af:column>
						<af:column sortProperty="c05" sortable="true" formatType="text"
							headerText="Control Equipment Type">
							<af:outputText
								value="#{facilityReference.contEquipTypeDefs.itemDesc[(empty controlEquip.equipmentTypeCd ? '' : controlEquip.equipmentTypeCd)]}" />
						</af:column>
						<af:column sortProperty="c06" sortable="true" formatType="text"
							headerText="Operating Status">
							<af:outputText
								value="#{facilityReference.ceOperatingStatusDefs.itemDesc[(empty controlEquip.operatingStatusCd ? '' : controlEquip.operatingStatusCd)]}" />
						</af:column>
						<af:column sortProperty="c07" sortable="true" formatType="text"
							headerText="Initial Installation Date">
							<af:selectInputDate
								value="#{controlEquip.contEquipInstallDate}" readOnly="true" />
						</af:column>
						<af:column sortProperty="c08" sortable="true" formatType="text"
							headerText="Associated AQD Emissions Unit IDs">
							<af:outputText value="#{controlEquip.associatedEpaEuIds}" />
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton
										actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Printable view" />
									<af:commandButton
										actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</h:panelGrid>
			</af:panelGroup>
		</afh:rowLayout>
			<!-- Release Points Table -->
		<afh:rowLayout halign="center">
			<af:panelGroup partialTriggers="facId emusTable">
				<h:panelGrid columns="1" border="0" width="950"
					rendered="#{cloneFacility.egpsWrapper.rowCount!=0}"
					style="margin-left:auto;margin-right:auto;">
					<af:objectSpacer width="100%" height="15" />
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="<b>Selected Release Points to clone</b>" />
					</afh:rowLayout>
					<af:table value="#{cloneFacility.egpsWrapper}" width="98%"
						bandingInterval="1" binding="#{cloneFacility.egpsWrapper.table}"
						banding="row" var="egressPoint">
						<af:column sortProperty="c01" sortable="true" formatType="text"
							headerText="AQD ID">
							<af:outputText value="#{egressPoint.releasePointId}" />
						</af:column>
						<af:column sortProperty="c02" sortable="true" formatType="text"
							headerText="AQD Description">
							<af:outputText value="#{egressPoint.dapcDesc}" />
						</af:column>
						<af:column sortProperty="c03" sortable="true" formatType="text"
							headerText="Company ID" noWrap="true">
							<af:outputText value="#{egressPoint.egressPointId}" />
						</af:column>
						<af:column sortProperty="c04" sortable="true" formatType="text"
							headerText="Company Description">
							<af:outputText value="#{egressPoint.regulatedUserDsc}" />
						</af:column>
						<af:column sortProperty="c05" sortable="true" formatType="text"
							headerText="Release Point Type">
							<af:outputText
								value="#{facilityReference.egrPntTypeDefs.itemDesc[(empty egressPoint.egressPointTypeCd ? '' : egressPoint.egressPointTypeCd)]}" />
						</af:column>
						<af:column sortProperty="c06" sortable="true" formatType="text"
							headerText="Operating Status">
							<af:outputText
								value="#{facilityReference.egOperatingStatusDefs.itemDesc[(empty egressPoint.operatingStatusCd ? '' : egressPoint.operatingStatusCd)]}" />
						</af:column>
						<af:column sortProperty="c07" sortable="true" formatType="icon"
							headerText="CEM(s) Present" rendered="false">
							<af:selectBooleanCheckbox value="#{egressPoint.cemPresent}"
								readOnly="true" />
						</af:column>
						<af:column sortProperty="c08" sortable="true" formatType="text"
							headerText="Associated AQD Emissions Unit IDs">
							<af:outputText value="#{egressPoint.associatedEpaEuIds}" />
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton
										actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Printable view" />
									<af:commandButton
										actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</h:panelGrid>
			</af:panelGroup>
		</afh:rowLayout>
	<af:objectSpacer width="100%" height="5" />
  <af:panelHeader text="Facility" size="0" />
		
			<af:panelForm rows="1" maxColumns="2" width="650">
            	<af:inputText label="New Facility Name:" value="#{cloneFacility.facilityName}" 
                	id="facName" columns="55" maximumLength="55"
                    showRequired="true"/>
		
				<af:selectOneChoice label="Company Name:"
								value="#{cloneFacility.ownerCompanyId}"
								id="companyName"
								unselectedLabel="" showRequired="true">
								<f:selectItems value="#{infraDefs.companies}" />
							</af:selectOneChoice>
        	</af:panelForm>
	                                                                                                                    
	<af:objectSpacer width="100%" height="5" />		
			  <af:panelHeader text="New Facility Location" size="0" />
						<af:panelForm rows="1" maxColumns="5" labelWidth="200" width="650">
							<af:inputText label="Latitude:" styleClass="latitude"
								value="#{cloneFacility.phyAddr.latitude}" columns="15"
								maximumLength="10" id="latitude" showRequired="true"
								shortDesc="The value must be between #{infraDefs.minLatitude} ~ #{infraDefs.maxLatitude}."/>
							<af:inputText label="Longitude:" styleClass="longitude"
								value="#{cloneFacility.phyAddr.longitude}"
								columns="15" maximumLength="11" id="longitude"
								showRequired="true" autoSubmit="true"
								shortDesc="The value must be between #{infraDefs.minLongitude} ~ #{infraDefs.maxLongitude}." />
							<af:commandButton text="Generate Location Data" id="genButton1"
								inlineStyle="outline: none;"
								onmouseover="document.getElementById('cloneFacility:genButton1').focus();"
								action="#{cloneFacility.phyAddr.calculatePlss}" />
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="11" labelWidth="200"
							width="650">
							<af:selectOneChoice label="Quarter Quarter:"
								value="#{cloneFacility.phyAddr.quarterQuarter}"
								id="quarterQuarter">
								<f:selectItems value="#{infraDefs.quarterQuarters}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Quarter:"
								value="#{cloneFacility.phyAddr.quarter}" id="quarter">
								<f:selectItems value="#{infraDefs.quarters}" />
							</af:selectOneChoice>
							<af:inputText label="Section:" styleClass="section"
								value="#{cloneFacility.phyAddr.section}" columns="3"
								maximumLength="2" id="section" showRequired="true"
								autoSubmit="true" shortDesc="The value must be between 1 ~ 36." />
							<af:inputText label="Township:" styleClass="township"
								value="#{cloneFacility.phyAddr.township}" columns="3"
								maximumLength="3" id="township" showRequired="true"
								autoSubmit="true"
								shortDesc="The value must be of the format D followed by N or S, where D is a positive number. E.g., 45N." />
							<af:inputText label="Range:" styleClass="range"
								value="#{cloneFacility.phyAddr.range}" columns="3"
								maximumLength="4" id="range" showRequired="true"
								autoSubmit="true"
								shortDesc="The value must be of the format D followed by W or E, where D is a positive number. E.g., 6W." />
							<af:commandButton text="Generate Location Data" id="genButton2"
								inlineStyle="outline: none;"
								onmouseover="document.getElementById('cloneFacility:genButton2').focus();"
								action="#{cloneFacility.phyAddr.calculateLatLong}" />
						</af:panelForm>

						<af:panelForm rows="1" maxColumns="6" labelWidth="200" width="650">
							<af:selectOneChoice label="County:" autoSubmit="true"
								styleClass="county"
								value="#{cloneFacility.phyAddr.countyCd}"
								id="countyCd" showRequired="true">
								<f:selectItems value="#{infraDefs.counties}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="State:" autoSubmit="true"
								value="#{cloneFacility.phyAddr.state}" id="state"
								showRequired="true">
								<f:selectItems value="#{infraDefs.states}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="District:" autoSubmit="true" rendered="#{infraDefs.districtVisible}"
								styleClass="district"
								value="#{cloneFacility.phyAddr.districtCd}"
								id="districtCd" showRequired="true">
								<f:selectItems value="#{infraDefs.districts}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Indian Reservation:" autoSubmit="true"
								value="#{cloneFacility.phyAddr.indianReservationCd}" unselectedLabel=" "
								id="indianReservationCd">
								<f:selectItems value="#{infraDefs.indianReservations.items[(empty cloneFacility.phyAddr.indianReservationCd ? '' : cloneFacility.phyAddr.indianReservationCd)]}" />
							</af:selectOneChoice>
						</af:panelForm>

						<af:panelForm rows="1" maxColumns="4" labelWidth="200" width="650"
							partialTriggers="loadAddress">
							<af:inputText label="Physical Address 1:"
								tip="Facility's physical location address. Do not enter a PO box number."
								value="#{cloneFacility.phyAddr.addressLine1}" showRequired="#{!infraDefs.plssAutoReplication}"
								styleClass="address1" id="addressLine1" columns="65"
								maximumLength="100" />
							<af:inputText label="Physical Address 2:"
								tip="Facility's physical location address. Do not enter a PO box number."
								value="#{cloneFacility.phyAddr.addressLine2}"
								id="addressLine2" columns="65" maximumLength="100" />
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="6" labelWidth="200" width="650"
							partialTriggers="loadAddress">
							<af:inputText label="City:" columns="15" styleClass="city"
								inlineStyle="font-size:10pt;" showRequired="#{!infraDefs.plssAutoReplication}"
								value="#{cloneFacility.phyAddr.cityName}"
								maximumLength="50" id="cityName" />
							<af:inputText label="Zip:" columns="15" styleClass="zip"
								inlineStyle="font-size:10pt;" showRequired="#{!infraDefs.plssAutoReplication}"
								value="#{cloneFacility.phyAddr.zipCode}" id="zipCode"
								maximumLength="10" />
							<af:selectInputDate id="effectiveDate" label="Effective Date: "
								value="#{cloneFacility.phyAddr.beginDate}"
								showRequired="true">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:panelForm>
					
					
				<af:objectSpacer width="100%" height="15" />

				<af:inputText styleClass="ismatch" inlineStyle="visibility: hidden"
					partialTriggers="latitude longitude section township range countyCd state districtCd"
					value="#{cloneFacility.phyAddr.isMatch}" readOnly="True" />
			
			<af:objectSpacer height="20" />
			<afh:rowLayout halign="center">
			  <af:panelForm width="100%">
				<af:panelButtonBar >
					  <af:commandButton id="subButn" text="Submit Clone Facility"
					  		onclick="return showSavingConfirmation();return false;"
					  		onmouseover="document.getElementById('createLocation:saveButton').focus();"
					  		disabled="#{facilityProfile.readOnlyUser || cloneFacility.buttonClicked}"
            				action="#{cloneFacility.checkEUsSelectedBeforeCloning}" useWindow="true"
            				windowWidth="600"
            				windowHeight="200">
          			  </af:commandButton>
                      <af:commandButton text="Cancel/Reset"
                      		 action="#{cloneFacility.resetCloneFacility}" immediate="true"/>                    
				</af:panelButtonBar>
			  </af:panelForm>
			</afh:rowLayout>
			
          </af:page>
        </af:form>
        
        <f:verbatim><%@ include
				file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/FacilityType-Option.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/wording-filter.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/facility-detail-location.js"%></f:verbatim>
				
      </af:document>
    </f:view>

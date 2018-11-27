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
		  <af:messages />
    		
		<afh:rowLayout halign="center">
           	<af:panelForm rows="1" maxColumns="2" width="100%">
                  <af:selectOneChoice label="From Facility ID:" id="facId" 
                      value="#{cloneFacility.facilityId}"
                      autoSubmit="true" valueChangeListener="#{cloneFacility.loadEu}" 
                      valuePassThru="true" onchange="$('.loading').show();">
                     <f:selectItems value="#{cloneFacility.companyFacilities}" />
                  </af:selectOneChoice>
					<afh:rowLayout partialTriggers="facId">
						<t:div style="display:none;" styleClass="loading">
							<af:objectImage source="/images/loading.gif" />
						</t:div>
					</afh:rowLayout>
			</af:panelForm>
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
	
	
	
			<afh:rowLayout halign="center">
			  <af:panelForm width="100%">
				<af:panelButtonBar >
					  <af:commandButton partialTriggers="emusTable" id="subButn" text="Submit Clone Facility"
            				action="#{cloneFacility.checkEUsSelectedBeforeCloning}" useWindow="true"
            				returnListener="#{cloneFacility.returnFromDialog}"
			                disabled="#{!cloneFacility.submitReady}"
            				windowWidth="600"
            				windowHeight="200">
          			  </af:commandButton>
                      <af:commandButton text="Cancel/Reset"
                      		 action="#{cloneFacility.cancelResetCloneFacility}" immediate="true"/>                    
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

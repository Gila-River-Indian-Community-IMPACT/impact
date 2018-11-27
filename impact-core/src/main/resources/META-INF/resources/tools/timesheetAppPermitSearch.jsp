<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="timesheetAppPermitSearchBody"
		title="App/Permit Search">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<af:form partialTriggers="applySelected selected">
			<af:panelHeader text="App/Permit Search" size="0" />
			<af:panelForm rows="9" maxColumns="1" labelWidth="140">


        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1100">
            <af:panelBorder>
              <af:showDetailHeader text="Search Criteria"
                disclosed="true">
                <af:panelForm rows="3" maxColumns="1"
                  partialTriggers="appPermitSearchCompany appPermitSearchFacility">
                  
                    <af:selectOneRadio value="#{timesheetEntry.appPermitSearchAppPermitType}" required="yes"
                    	label="App/Permit Type:" rendered="false">
					  <f:selectItem itemLabel="NSR" itemValue="1"/>
					  <f:selectItem itemLabel="Title V" itemValue="2"/>
					  <f:selectItem itemLabel="Both" itemValue="3"/>
					</af:selectOneRadio>
                  
                    <af:selectOneRadio value="#{timesheetEntry.appPermitSearchType}" required="yes"
                    	label="Search Type:">
					  <f:selectItem itemLabel="Applications" itemValue="1"/>
					  <f:selectItem itemLabel="Permits" itemValue="2"/>
					  <f:selectItem itemLabel="Both" itemValue="3"/>
					</af:selectOneRadio>
                  
					<af:selectOneChoice label="Company:" autoSubmit="true"
						value="#{timesheetEntry.appPermitSearchCmpId}"
						id="appPermitSearchCompany" unselectedLabel=""
						valueChangeListener="#{timesheetEntry.appPermitSearchCompanySelected}">
						<f:selectItems value="#{companySearch.allCompanies}"/>
					</af:selectOneChoice>
									 

					<af:selectOneChoice label="Facility:" autoSubmit="true"
						rendered="#{not empty timesheetEntry.facilitiesByCompany}"
						value="#{timesheetEntry.appPermitSearchFacilityId}"
						id="appPermitSearchFacility" unselectedLabel=""
	                    valueChangeListener="#{timesheetEntry.appPermitSearchFacilitySelected}">
						<f:selectItems value="#{timesheetEntry.facilitiesByCompany}" />
					</af:selectOneChoice>


                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{timesheetEntry.submitAppPermitSearch}" />
                    <af:commandButton text="Reset"
                      action="#{timesheetEntry.resetAppPermitSearch}" />
                    <af:commandButton text="Cancel" 
                    	action="#{timesheetEntry.cancelAppPermitSearch}" 
                    	rendered="#{!timesheetEntry.appPermitSearchHasResults}" />
                   </af:panelButtonBar>
                </afh:rowLayout>
                <af:outputText value="NSR results include only non-legacy, billable NSR applications/permits that do not yet have a final invoice." />
                <af:outputText value="Title V results include all Title V applications/permits." />
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>


        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout rendered="#{timesheetEntry.appPermitSearchHasResults}"
            halign="center">
          <h:panelGrid border="1" width="1000">
            <af:panelBorder>
              <af:showDetailHeader text="App/Permit Results" disclosed="true">
                <af:table bandingInterval="1" banding="row"
                  var="result" binding="#{timesheetEntry.appPermitSearchResults.table}"
                  rows="#{timesheetEntry.pageLimit}" width="98%" 
                  value="#{timesheetEntry.appPermitSearchResults}">

				<af:column sortable="true" sortProperty="selected" formatType="icon"
					headerText="Select">
					<af:selectBooleanRadio group="RadioButtons" rendered="true"
						autoSubmit="true"
						valueChangeListener="#{timesheetEntry.appPermitSearchResultSelected}"
						value="#{result.selected}">
					</af:selectBooleanRadio>
				</af:column>

                  <af:column sortProperty="id" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="App/Permit ID" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText value="#{result.id}" readOnly="true" />
                    </af:panelHorizontal>
                  </af:column>
                  
                  <af:column sortProperty="id" sortable="true" rendered="false">
                    <f:facet name="header">
                      <af:outputText value="Type" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText value="#{result.appPermitType}" readOnly="true" />
                    </af:panelHorizontal>
                  </af:column>
                  

                  <af:column sortProperty="company" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Company" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText value="#{result.company}" readOnly="true" />
                    </af:panelHorizontal>
                  </af:column>
                  

                  <af:column sortProperty="facility_id" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Facility ID" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText value="#{result.facilityId}" readOnly="true" />
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="facility" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Facility" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText value="#{result.facility}" readOnly="true" />
                    </af:panelHorizontal>
                  </af:column>
                  

                  <af:column sortProperty="description" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Description" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText value="#{result.description}" readOnly="true"/>
                    </af:panelHorizontal>
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
              </af:showDetailHeader>
            </af:panelBorder>

          </h:panelGrid>
        </afh:rowLayout>

                <af:objectSpacer width="100%" height="15" />

                <afh:rowLayout halign="center" rendered="#{timesheetEntry.appPermitSearchHasResults}">
                  <af:panelButtonBar>
                    <af:commandButton text="Apply Selected" id="applySelected"
                      action="#{timesheetEntry.applyAppPermitSearchSelection}" />
                    <af:commandButton text="Cancel"
                      action="#{timesheetEntry.cancelAppPermitSearch}" />
                  </af:panelButtonBar>
                </afh:rowLayout>

			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
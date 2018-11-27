<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Modeling Data">
      <f:verbatim>
      <script>
      	</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
      </script>
      </f:verbatim>
      
      <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Modeling Data">
        <%@ include file="../util/header.jsp"%>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1">

            <af:panelBorder>
              <af:showDetailHeader text="Search Criteria"
                disclosed="true">
                
                <af:panelForm rows="2" width="1000" maxColumns="3" >

				<af:panelGroup>
	   				<af:panelGroup>
	   					<af:outputLabel value="Search Type"/>
					   <af:selectBooleanRadio group="searchType"
					   				id="searchTypeRadial"
					   				value="#{modelingData.searchTypeRadial}"
					   				autoSubmit="true"
					                text="Radial"
					                selected="true"/>
					   <af:selectBooleanRadio group="searchType"
					   				id="searchTypePolygon"
					   				autoSubmit="true"
					   				value="#{modelingData.searchTypePolygon}"
					                text="Polygon"/>
	                </af:panelGroup>
	
					<af:panelGroup
						partialTriggers="searchTypeRadial">
	                  <af:inputText tip="" id = "latitude"
	                  	rendered="#{modelingData.searchTypeRadial == true}"
	                    columns="12" label="Latitude (dd)" styleClass="latitude"
	                    value="#{modelingData.latitudeDegrees}"
	                    valign="top" autoSubmit="true"/>
	                  <af:inputText tip="" id="longitude"
	                  	rendered="#{modelingData.searchTypeRadial == true}"
	                    columns="12" label="Longitude (dd)" styleClass="longitude"
	                    value="#{modelingData.longitudeDegrees}"
	                    valign="top" autoSubmit="true"/>
	                  <af:inputText tip=""
	                  	rendered="#{modelingData.searchTypeRadial == true}"
	                    columns="12" label="Distance (km)"
	                    value="#{modelingData.distanceKm}"
	                    valign="top">
							<af:convertNumber pattern="###,##0.##" minFractionDigits="2" />
						</af:inputText>
					</af:panelGroup>
	
					<af:panelGroup
						partialTriggers="searchTypePolygon">
	                  <af:selectOneChoice label="Polygon" id="polygon"
	                  	rendered="#{modelingData.searchTypePolygon == true}"
	                    value="#{modelingData.polygon}">
	                    <f:selectItems value="#{infraDefs.geoPolygons}"/>
	                  </af:selectOneChoice>				  
					</af:panelGroup>
				</af:panelGroup>                

				<af:panelGroup>
				<af:outputLabel value="Excluded Facility Types"/>
				<af:selectManyListbox
					valuePassThru="true"
					styleClass="FacilityTypeClass x6"
					value="#{modelingData.excludedFacilityTypes}">
					<f:selectItems
						value="#{facilityReference.facilityTypeDefs.allItems}" />
				</af:selectManyListbox>
				</af:panelGroup>
				<f:verbatim><%@ include
						file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
				<f:verbatim><%@ include file="../scripts/wording-filter.js"%></f:verbatim>
				<f:verbatim><%@ include
						file="../scripts/FacilityType-Option.js"%></f:verbatim>
				<f:verbatim><%@ include
						file="../scripts/facility-detail.js"%></f:verbatim>				

				<af:panelGroup>
				<af:outputLabel value="Pollutants"/>
				<af:selectManyShuttle label="Pollutants"
					valuePassThru="true"
					value="#{modelingData.pollutants}">
					<f:selectItems
						value="#{facilityReference.pollutantDefs.allItems}" />
				</af:selectManyShuttle>
				</af:panelGroup>
									  
                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:objectSpacer width="100%" height="20" />
                    <af:commandButton text="Submit"
                      action="#{modelingData.submitSearch}">
                    </af:commandButton>

                    <af:commandButton text="Reset" immediate="true"
                      action="#{modelingData.reset}" />

                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1"
            rendered="#{modelingData.hasSearchResults}">
            <af:panelBorder>
              <af:showDetailHeader text="Search Results"
                disclosed="true">

                <af:table value="#{modelingData.resultsWrapper}"
                	binding="#{modelingData.resultsWrapper.table}"
                  bandingInterval="1" banding="row" var="result"
                  rows="#{modelingData.pageLimit}">
                  
                  
                  <af:column sortProperty="companyName" sortable="true"
                    formatType="text" headerText="Company Name">
                    <af:outputText value="#{result.companyName}" />
                  </af:column>
                  
                  <af:column sortProperty="facilityName" sortable="true"
                    formatType="text" headerText="Facility Name">
                    <af:outputText value="#{result.facilityName}" />
                  </af:column>
                  
                  <af:column sortProperty="facilityType" sortable="true"
                    formatType="text" headerText="Facility Type">
                    <af:outputText value="#{result.facilityType}" />
                  </af:column>
                  
                  <af:column sortProperty="distanceToFacility" sortable="true"
                    formatType="text" headerText="Facility Distance (km)">
                    <af:outputText value="#{result.distanceToFacility}" >
	                    <af:convertNumber minFractionDigits="2"/>
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="distanceToReleasePoint" sortable="true"
                    formatType="text" headerText="Release Point Distance (km)">
                    <af:outputText value="#{result.distanceToReleasePoint}">
	                    <af:convertNumber minFractionDigits="2"/>
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="modelingSourceId" sortable="true"
                    formatType="text" headerText="Modeling Source Id">
                    <af:outputText value="#{result.modelingSourceId}">
	                    <af:convertNumber/>
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="aqdSourceId" sortable="true"
                    formatType="text" headerText="AQD Source Id">
                    <af:outputText value="#{result.aqdSourceId}" />
                  </af:column>
                  
                  <af:column sortProperty="sourceDesc" sortable="true"
                    formatType="text" headerText="Source Description">
                    <af:outputText value="#{result.sourceDesc}" />
                  </af:column>
                  
                  <af:column sortProperty="utmDatum" sortable="true"
                    formatType="text" headerText="UTM Datum">
                    <af:outputText value="#{result.utmDatum}" />
                  </af:column>
                  
                  <af:column sortProperty="utmZone" sortable="true"
                    formatType="text" headerText="UTM Zone">
                    <af:outputText value="#{result.utmZone}">
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="utmEasting" sortable="true"
                    formatType="text" headerText="UTM Easting">
                    <af:outputText value="#{result.utmEasting}">
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="utmNorthing" sortable="true"
                    formatType="text" headerText="UTM Northing">
                    <af:outputText value="#{result.utmNorthing}">
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="releasePointId" sortable="true"
                    formatType="text" headerText="Release Point Id">
                    <af:outputText value="#{result.releasePointId}">
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="releasePointBaseElevation" sortable="true"
                    formatType="text" headerText="Base Elevation (ft)">
                    <af:outputText value="#{result.releasePointBaseElevation}">
	                    <af:convertNumber minFractionDigits="2"/>
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="releasePointStackHeight" sortable="true"
                    formatType="text" headerText="Stack Height (ft)">
                    <af:outputText value="#{result.releasePointStackHeight}">
	                    <af:convertNumber minFractionDigits="2"/>
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="releasePointTemp" sortable="true"
                    formatType="text" headerText="Temperature (F)">
                    <af:outputText value="#{result.releasePointTemp}">
	                    <af:convertNumber minFractionDigits="2"/>
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="releasePointExitVelocity" sortable="true"
                    formatType="text" headerText="Exit Velocity (ft/sec)">
                    <af:outputText value="#{result.releasePointExitVelocity}">
	                    <af:convertNumber minFractionDigits="2"/>
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="releasePointStackDiameter" sortable="true"
                    formatType="text" headerText="Stack Diameter (ft)">
                    <af:outputText value="#{result.releasePointStackDiameter}">
	                    <af:convertNumber minFractionDigits="2"/>
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="pollutantCd" sortable="true"
                    formatType="text" headerText="Pollutant">
                    <af:outputText value="#{result.pollutantCd}" />
                  </af:column>
                  
                  <af:column sortProperty="pollutantShortTermLimit" sortable="true"
                    formatType="text" headerText="Short-term Limit (lbs/hr)">
                    <af:outputText value="#{result.pollutantShortTermLimit}">
	                    <af:convertNumber minFractionDigits="2"/>
                    </af:outputText>
                  </af:column>
                  
                  <af:column sortProperty="pollutantShortTermEmissionsLimitType" sortable="true"
                    formatType="text" headerText="Short-term Limit Type">
                    <af:outputText value="#{result.pollutantShortTermEmissionsLimitType.label}"/>
                  </af:column>
                  
                  <af:column sortProperty="pollutantLongTermLimit" sortable="true"
                    formatType="text" headerText="Long-term Limit (tons/yr)">
                    <af:outputText value="#{result.pollutantLongTermLimit}">
	                    <af:convertNumber minFractionDigits="2"/>
                    </af:outputText>
                  </af:column>
                  		          
                  <af:column sortProperty="pollutantLongTermEmissionsLimitType" sortable="true"
                    formatType="text" headerText="Long-term Limit Type">
                    <af:outputText value="#{result.pollutantLongTermEmissionsLimitType.label}"/>
                  </af:column>
                  		          
                  <f:facet name="footer">
                    <afh:rowLayout halign="center">
                      <af:panelButtonBar>
                        <af:commandButton
                          actionListener="#{tableExporter.printTable}"
                          onclick="#{tableExporter.onClickScriptTS}"
                          text="Printable view" />
                        <af:commandButton
                          actionListener="#{tableExporter.excelTable}"
                          onclick="#{tableExporter.onClickScriptTS}"
                          text="Export to excel" />
                      </af:panelButtonBar>
                    </afh:rowLayout>
                  </f:facet>
                </af:table>

              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
  <f:verbatim><%@ include
			file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
	<f:verbatim><%@ include
			file="../scripts/wording-filter.js"%></f:verbatim>
	<f:verbatim><%@ include
			file="../scripts/facility-detail-location.js"%></f:verbatim>
</f:view>

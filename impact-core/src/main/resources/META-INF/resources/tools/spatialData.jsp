<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Spatial Data">
		<f:verbatim>
			<script>
				</f:verbatim><af:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Spatial Data">
				<%@ include file="../util/header.jsp"%>
				<af:panelGroup layout="vertical" rendered="true">
					<afh:rowLayout halign="center">
						<t:div id="map" style="width: 1200px; height: 600px"/>
							<afh:script text="
							  var map;
							  var callbacks = [];
							  var polygons = [];

							  var centerLat = #{spatialData.defaultMapOptions.centerLat};
							  var centerLng = #{spatialData.defaultMapOptions.centerLng};
							  var zoomLevel = #{spatialData.defaultMapOptions.zoomLevel};
							  var mapCenter = {lat: centerLat, lng: centerLng};
							  function CenterControl(controlDiv, map){
							  	var controlUI = document.createElement('div');
								controlUI.style.backgroundColor = '#fff';
								controlUI.style.border = '2px solid #fff';
								controlUI.style.borderRadius = '3px';
								controlUI.style.boxShadow = '0 2px 6px rgba(0,0,0,.3)';
								controlUI.style.cursor = 'pointer';
								controlUI.style.marginBottom = '22px';
								controlUI.style.textAlign = 'center';
								controlUI.title = 'Click to recenter the map';
								controlDiv.appendChild(controlUI);
								
								var controlText = document.createElement('div');
								controlText.style.color = 'rgb(25,25,25)';
								controlText.style.fontFamily = 'Roboto,Arial,sans-serif';
						        controlText.style.fontSize = '16px';
						        controlText.style.lineHeight = '38px';
						        controlText.style.paddingLeft = '5px';
						        controlText.style.paddingRight = '5px';
						        controlText.innerHTML = 'Center Map';
						        controlUI.appendChild(controlText);
						        
						        controlUI.addEventListener('click', function() {
						            map.setCenter(mapCenter);
						            map.setZoom(zoomLevel);
						        });
							  }
							  
							  function initMap() {
							    map = new google.maps.Map($(\"[id$='map']\")[0], {
							      center: mapCenter,
							      zoom: zoomLevel,
							    });
							    
							    var centerControlDiv = document.createElement('div');
						        var centerControl = new CenterControl(centerControlDiv, map);
						
						        centerControlDiv.index = 1;
						        map.controls[google.maps.ControlPosition.TOP_CENTER].push(centerControlDiv);
							    
								callbacks.forEach(function(callback) {
									callback();
								});
							  }
						      function attachPolygonInfoWindow(polygon, html, latLng)
							  {
								polygon.infoWindow = new google.maps.InfoWindow({
									content: html,
								});
								google.maps.event.addListener(polygon, 'mouseover', function(e) {
									polygon.infoWindow.setPosition(latLng);
									polygon.infoWindow.open(map);
								});
								google.maps.event.addListener(polygon, 'mouseout', function() {
									polygon.infoWindow.close();
								});
							  }
							  function addCallback(callback) {
								callbacks.push(callback);
							  }
							  function togglePolygon(id) {
                                var i = 0;
                                while (p = polygons[i++]) {
                                       if (p.shapeId === id) {
                                           p.getMap() ?  p.setMap(null) : p.setMap(map);
                                     }
                                }
                  			 }
							"/>
					</afh:rowLayout>
					<af:objectSpacer height="35" />
					<afh:rowLayout halign="center">
						<h:panelGrid columns="1" border="1" width="1200px">
							<afh:rowLayout halign="left">
								<af:panelButtonBar>
									<af:commandLink  id="selectAllLnk"
										text="Select All"
										action="#{spatialData.selectAllShapes}" />
									<af:outputText value="|" />	
									<af:commandLink  id="selectNoneLnk"
										text="Select None"
										action="#{spatialData.unSelectAllShapes}" />
								</af:panelButtonBar>	
							</afh:rowLayout>
							<af:panelGroup layout="vertical" rendered="true">
								<afh:rowLayout halign="center">

									<af:table id="importedShapes" width="1200px"
										value="#{spatialData.importedShapes}"
										bandingInterval="1" banding="row" var="importedShape"
										rows="#{spatialData.pageLimit}" emptyText=" ">

										<af:column id="selected" headerText="Display on Map" 
											width="5%" 
											sortable="true" sortProperty="selected" formatType="icon">
											<af:selectBooleanCheckbox id="displayOnMap" 
												label="Display on Map"
												autoSubmit="true"
												onchange="togglePolygon(#{importedShape.shapeId})"
												value="#{importedShape.selected}" />
										</af:column>
										
										<af:column id="shapeId" headerText="Shape ID" width="5%"
											sortable="true" sortProperty="shapeId" formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:commandLink text="#{importedShape.shapeId}" id="viewReport" useWindow="true"
													windowWidth="650" windowHeight="300" disabled="false"
													returnListener="#{spatialData.dialogDone}" rendered="#{!facilityProfile.publicApp}"
													action="#{spatialData.startToEditImportedShape}">
													<t:updateActionListener
														property="#{spatialData.modifyShape}"
														value="#{importedShape}" />
												</af:commandLink>
												<af:outputText value="#{importedShape.shapeId}" rendered="#{facilityProfile.publicApp}" />
												
												<afh:script text="
												function addPolygon_#{importedShape.shapeId}() {
												  var coordinates = #{importedShape.pathJson};
												  var polygon = new google.maps.Polygon({
												    paths: coordinates,
												    strokeColor: '#FF0000',
												    strokeOpacity: 0.8,
												    strokeWeight: 2,
												    fillColor: '#FF0000',
												    fillOpacity: 0.35
												  });
												  attachPolygonInfoWindow(
												  	polygon, 
												  	'#{importedShape.infoWindowContent}',
												  	#{importedShape.centerJson}
												  );
												  #{importedShape.selected} ? polygon.setMap(map) : polygon.setMap(null);
												  polygon.shapeId = #{importedShape.shapeId}
                                                  polygons.push(polygon);
											  	}
											  	addCallback(addPolygon_#{importedShape.shapeId});
												"/>
												  
											</af:panelHorizontal>
										</af:column>

										<af:column id="label" headerText="Label" width="15%"
											sortable="true"	sortProperty="label"	formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:inputText readOnly="true"
													value="#{importedShape.label}" />
											</af:panelHorizontal>
										</af:column>

										<af:column id="description" headerText="Description" width="25%"
											sortable="true"	sortProperty="description"	formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:inputText readOnly="true"
													value="#{importedShape.description}" />
											</af:panelHorizontal>
										</af:column>

										<af:column id="area" width="5%" 
											sortable="true"	sortProperty="area"	formatType="number">
										      <f:facet name="header">
										        <af:outputText escape="false" value="Area&nbsp;(km<sup>2</sup>)"/>
										      </f:facet>											
												<af:inputText readOnly="true"
													value="#{importedShape.area}" />
										</af:column>

										<af:column id="length" width="5%" 
											sortable="true"	sortProperty="length"	formatType="number">
										      <f:facet name="header">
										        <af:outputText escape="false" value="Perimeter&nbsp;(km)"/>
										      </f:facet>											
												<af:inputText readOnly="true"
													value="#{importedShape.length}" />
										</af:column>
										
										<af:column id="nonAttainmentAreas" headerText="Non-Attainment Area" 
											width="20%" 
											sortable="true" formatType="text">
											<af:outputText value="#{importedShape.nonAttainmentAreas}" />
										</af:column>
										
										<af:column id="projectIds" headerText="Project Tracking Area" 
											width="20%" 
											sortable="true" formatType="text"
											rendered="#{spatialData.internalApp}">
											<af:outputText value="#{importedShape.projects}" />
										</af:column>
										
										<af:column id="isCounty" headerText="County?" noWrap="true" 
											sortable="true" sortProperty="countyShape" formatType="text">
											<af:outputText value="#{importedShape.countyShape ? 'Yes': 'No'}" />
										</af:column>
										
										<af:column id="isIndianReservation" headerText="Indian Reservation?" noWrap="true" 
											sortable="true" sortProperty="indianReservationShape" formatType="text">
											<af:outputText value="#{importedShape.indianReservationShape? 'Yes': 'No'}" />
										</af:column>
											
									</af:table>


								</afh:rowLayout>
							</af:panelGroup>
						</h:panelGrid>
					</afh:rowLayout>
				</af:panelGroup>
			</af:page>
		</af:form>
        <f:verbatim><%@ include
				file="../scripts/jquery-2.2.3.min.js"%></f:verbatim>
        <f:verbatim><%@ include
				file="../scripts/google-maps-api.js"%></f:verbatim>
	</af:document>
</f:view>
    
    

<afh:script text="
  var map;
  function initMap() {
    map = new google.maps.Map($(\"[id$='map']\")[0], {
      center: #{importedShape.centerJson},
      zoom: 5,
      disableDefaultUI: true,
      scrollwheel: false,
      draggable: false,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    });
  
  // Define the LatLng coordinates for the polygon's path.
  var coordinates = #{importedShape.pathJson};

  // Construct the polygon.
  var polygon = new google.maps.Polygon({
    paths: coordinates,
    strokeColor: '#FF0000',
    strokeOpacity: 0.8,
    strokeWeight: 2,
    fillColor: '#FF0000',
    fillOpacity: 0.35
  });
  polygon.setMap(map);
  }
"/>
  

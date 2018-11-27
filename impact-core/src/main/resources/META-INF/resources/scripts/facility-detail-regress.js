<script type="text/javascript">
/*
 * Facility Detail Location page javascript, Tim Wu, 2013-02-25
 */


var isContinueSaving = false;

function getRelLatitude() {
    return $("input.facLatitudeDec");
}

function getRelLongitude() {
    return $("input.facLongitudeDec");
}

function getRelTownship() {
    return $("input.township");
}

function getRelRange() {
    return $("input.range");
}

function checkRelLatitudeValue() {
    var $control = getLatitude();
    var latitude = $control.val();
    if (latitude == "") {
    	return;
    }
    
    // Latitude must be between 41~45
    if (latitude < 0 ) {
    	latitude = -latitude;
    }
   
    $control.val(latitude);
}

function checkRelLongtitudeValue() {
    var $control = getLongitude();
    var longitude = $control.val();
    if (longitude == "") {
    	return;
    }
    
    if (longitude > 0 ) {
    	longitude = -longitude;
    }
    
    $control.val(longitude);
}

function checkRelTownshipValue() {
    var $control = getTownship();
    var township = $control.val();

    // Check the first char whether is 0 or not
    township = removeFirstCharWhenZero(township);
    
    // If the township is start with digits and the length is 2 then append 'W'
	// after the number
    regex = new RegExp("^[1-9][\\d]{0,1}$");
    if (township.match(regex)) {
        township = township + "N";
    }
    
    if (township != "")
    	$control.val(township.toUpperCase());
}

function checkRelRangeValue() {
    var $control = getRange();
    var range = $control.val();

    // Check the first char whether is 0 or not
    range = removeFirstCharWhenZero(range);

    // If the range is start with digits and the length is 1~2 then append 'W'
	// after the number
    regex = new RegExp("^[1-9][\\d]{0,2}$");
    if (range.match(regex)) {
        range = range + "W";
    }
    
    if (range != "")
    	$control.val(range.toUpperCase());
}

function removeRelFirstCharWhenZero(source) {
	var regex = new RegExp("^[0][\\w]+$");
	
    if (source.match(regex)) {
    	source = source.substr(1, range.length);
    }
    
    return source;
}

function showRelSavingConfirmation() {
	
	var ismatch = $("span.ismatch").text();
	// alert(ismatch);
	if(ismatch=="false")
		{
			var title = "Saving Confirmation";
		    var content = "Caution: there is a mismatch in the entered location data. " +
		    	"Please check that the Latitude/Longitude, Section/Township/Range, and County/District values are correct." +
		    	"\r\nClick OK to save the data else click cancel.";
		    var result = confirm(content, title);
		
		    if(!result)
		    	return false;
		    
		    title = "Saving Confirmation";
		    content = "Click OK to confirm that you want to proceed with the data mismatch else click Cancel.";
		    result = confirm(content, title);
		    if(!result)
		    	return false;
		    	
		}
	
	var address1 = $("input.address1").val();
	var city = $("input.city").val();
	var zip = $("input.zip").val();

	if (address1 != "" && city != "" && zip != "") {
		return true; 
	}
	
	var latitude = $("input.latitude").val();
	var longitude = $("input.longitude").val();
	var section = $("input.section").val();
	var township = $("input.township").val();
	var range = $("input.range").val();
	var county = $("input.county").val();
	var state = $("input.state").val();
	
    var isContinueSaving = false;
	if ((latitude=="" || longitude=="") || (section== "" || township== "" || range== "" || county=="" || state=="")) {
		var addressError ="Enter Latitude, Longitude, Section, Township, Range, County, State";
		alert(addressError);
	}else{
	
	    var title = "Saving Confirmation";
	    var content =  "Enter the Physical Address, City, Zip Code of the Facility " + 
	        "or the system will automatically populate the field with the system default value" + 
	        "\r\nClick OK to have the system automatically populate the system default value else click Cancel to return to manual entry.";
	    var result = confirm(content, title);
	    
	    if (result) {
	    	isContinueSaving =  true;
	    } else {
	    	isContinueSaving =  false;
	    }
	} 
    return isContinueSaving; 
}

// Initialize
$(document).ready(function(){
	var $latitude = getRelLatitude();
	var $longitude = getRelLongitude();
	var $township = getRelTownship();
	var $range = getRange();
	$latitude.change(function() { checkRelLatitudeValue(); });
	$longitude.change(function() { checkRelLongtitudeValue(); });
	$township.change(function() { checkRelTownshipValue(); });
	$range.change(function() { checkRelRangeValue(); });
	
	// See the file - /scripts/wording-filter.js for detail
	SetNumericFilter($latitude);
	SetNumericFilter($longitude);
});

</script>
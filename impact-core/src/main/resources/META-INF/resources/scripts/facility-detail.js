<script type="text/javascript">
/*
* Facility Detail page javascript, Tim Wu, 2013-04-16
*/

// Initialize
$(document).ready(function(){
	var afs = $("input.afs");
	
	SetDigitFilter($afs);
});

function showConfirmation() {
	var title = "Saving Confirmation";
	var content = "Click OK to save the data else click cancel.";
	var result = confirm(content, title);
	
	if(!result)
		return false;
	
	if (result) {
		isContinueSaving =  true;
	} else {
		isContinueSaving =  false;
	} 
	return isContinueSaving; 
}

</script>
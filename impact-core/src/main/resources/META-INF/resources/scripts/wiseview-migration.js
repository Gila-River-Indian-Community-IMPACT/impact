<script type="text/javascript">
/*
* WISEView Migration page javascript, Tim Wu, 2013-07-25
*/

function disableMigrationButton(){
	var result = confirm("The migration operation might take 20 ~ 30 minutes. Are you sure you want to continue?");
	if (!result)
		return false;
	
	
	alert("The migration operation is going to start. Please don't close the browser and click the refresh page button.");
	$(".buttonContainer").hide();
	
	return true;
}
</script>
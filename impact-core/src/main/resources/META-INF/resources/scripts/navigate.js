<script type="text/javascript">

	var parentWindow =  window.opener;
	
	if (parentWindow.opener != null){
		parentWindow = parentWindow.opener;
	}
	
	function setFocus(actionClassName)
	{
		var actionElement = getActionElement(actionClassName);
		parentWindow.setTimeout(actionElement.onclick, 1000);
		parentWindow.focus();
	}
	
	/* get the first element of the hiddenControls class */
	function getActionElement(actionClassName) {
		if (actionClassName === undefined) {
			actionClassName = "hiddenControls"
		}
      var elements = parentWindow.document.getElementsByClassName(actionClassName);
	  if(elements.length > 0) {
		  return elements[0];
	  }
	}
</script>






<script type="text/javascript">
/*
* Facility type option group javascript, Chuan-Hui Yang, 2013-02-19
*/
$(document).ready(function() {
	
	changeFacilityTypeOption();
   
});

function changeFacilityTypeOption() {
	 var select = $('.FacilityTypeClass');
	    var groupName, currentGroupName;
	    var regex = new RegExp("\&");
	    
	    $('option', select).each(function(i) {
	    	if ($(this).val() != ''){
	    		currentGroupName = $(this).text().split('_')[0];
	    		$(this).text($(this).text().split('_')[1]);
	    		$(this).addClass(currentGroupName.replace(/[ ]/g,"").replace(regex, "-and-"));	
	    		
	    		if(groupName == null){groupName=currentGroupName;}
	    		
	    		if(groupName != currentGroupName)
	    		{
	                var optgroup = $('<optgroup/>');
	                optgroup.attr('label', groupName);
	                groupName= '.' + groupName.replace(/[ ]/g,"");
	                
	                var className = groupName;
	    		    className = className.replace(regex, "-and-");
	    		    
	                $(className).wrapAll(optgroup);  
	    	   		groupName= currentGroupName;
	    		} 
	    	}
	    });
	        
	    if(groupName != null){
		    var optgroup = $('<optgroup/>');
		    optgroup.attr('label', groupName);
		    groupName= '.' + groupName.replace(/[ ]/g,"");
		    
		    var className = groupName;
		    className = className.replace(regex, "-and-");
		    
		    $(groupName).wrapAll(optgroup); 
	    }else	{
	    	select.eq(1).text(select.eq(1).text().split('_')[1]);
	    }
	
	
}
</script>
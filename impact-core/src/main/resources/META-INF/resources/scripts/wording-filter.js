<script type="text/javascript">
/*
* The methods for filtering the entry value in the input element, Tim Wu, 2012-03-30
*/
function SetIntegerFilter($ele) {
    $ele.keypress(function (e) {
        if ((e.shiftKey && e.which == 45) || e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
            return false;
        }
    });
}

function SetAlphanumericNumericFilter($ele) {
    $ele.keypress(function (e) {
        if((e.shiftKey && e.keyCode == 45) || 
            e.which!=8 && e.which!=0 && !(
            (e.which>=48 && e.which<=57) || 
            (e.which>64 && e.which<91)  || 
            (e.which>=97 && e.which<=122))){
            return false;
        }
    });
}

function SetDecimalFilter($ele) {
    $ele.keypress(function (e) {
        if ((e.shiftKey && e.which == 45) || e.which != 8 && e.which != 0 && 
        		(e.which < 48 || e.which > 57) && e.which != 46) {
            return false;
        }
    });
}

function SetNumericFilter($ele) {
    $ele.keypress(function (e) {
        if ((e.shiftKey && e.which == 45) || e.which != 8 && e.which != 0 && 
        		(e.which < 48 || e.which > 57) && e.which != 46 && e.which != 45) {
            return false;
        }
    });
}

function SetDigitFilter($ele) {
    $ele.keypress(function (e) {
        if (e.which < 48 || e.which > 57) {
            return false;
        }
    });
}

//Initialize
$(document).ready(function(){
	$(".integer-filter").each(function(){
		SetNumericFilter($(this));
	});
	
	$(".alphanumeric-numeric-filter").each(function(){
		SetAlphanumericNumericFilter($(this));
	});
	
	$(".decimal-filter").each(function(){
		SetDecimalFilter($(this));
	});
	
	$(".numeric-filter").each(function(){
		SetNumericFilter($(this));
	});
	
	$(".digit-filter").each(function(){
		SetDigitFilter($(this));
	});
});
</script>
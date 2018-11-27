<script language="JavaScript">

function charsLeft(max)
{
  var field = document.getElementById('noteTxt');
  var count = document.getElementById('messageText');
     
  if(field.value.length > max)
    field.value = field.value.substring(0, max);
  else
    count.value = max - field.value.length;  
}

</script>

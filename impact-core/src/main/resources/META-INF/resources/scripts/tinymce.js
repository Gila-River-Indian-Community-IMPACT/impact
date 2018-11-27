<script type="text/javascript" src="../scripts/tinymce/tinymce.min.js"></script>

<script type"text/javascript">
	tinymce.init({
	  branding: false,
	  selector: 'textArea#mceEditor',
	  height: 500,
	  width: 800,
	  theme: 'modern',
	  statusbar: false,
	  image_advtab: true,
	  fontsize_formats: '8pt 10pt 12pt 14pt 18pt 24pt 36pt',
	  browser_spellcheck: true,
	  menu: {
		    file: {title: 'File', items: 'newdocument print'},
		    edit: {title: 'Edit', items: 'undo redo | cut copy paste | selectall | searchreplace'},
		    insert: {title: 'Insert', items: 'visualchars hr anchor pagebreak insertdatetime nonbreaking'},
		    view: {title: 'View', items: 'visualblocks visualaid preview fullscreen'},
		    format: {title: 'Format', items: 'bold italic underline strikethrough superscript subscript | formats | removeformat'},
		    table: {title: 'Table', items: 'inserttable tableprops deletetable | cell row column'}
		  },
	  toolbar1: 'undo redo | styleselect | bold italic underline | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent',
	  toolbar2: 'print preview | fontsizeselect forecolor backcolor',
	  plugins: [
	    'advlist autolink lists link image charmap print preview hr anchor pagebreak',
	    'searchreplace wordcount visualblocks visualchars fullscreen directionality',
	    'insertdatetime nonbreaking save table contextmenu powerpaste',
	    'textcolor colorpicker textpattern imagetools'
	  ],
	  init_instance_callback: function (editor) {
	      <!-- in the future move this outside of the script -->  
		  editor.setMode('${permitConditionDetail.tinyMCEMode}');
	  }      
	});
</script>
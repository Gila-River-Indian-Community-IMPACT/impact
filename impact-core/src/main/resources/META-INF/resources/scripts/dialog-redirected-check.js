$(document).ready(function() {
	if (window.opener && window.location.href.endsWith('home.jsf')) {
		window.close();
	}
});


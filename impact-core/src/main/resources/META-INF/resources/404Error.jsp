<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
//String realmName = "IMPACT";
//response.setHeader("WWW-Authenticate","FORM realm=\"" + realmName + "\"");
response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
%>
<!DOCTYPE html>
<html>
<head>
<title>IMPACT Error</title>
<style>
body {
	background-color: #194C81;
}

h1 {
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
	font-size: 30px;
	letter-spacing: 0px;
}

p {
	font-family: Arial, Helvetica, sans-serif;
	font-weight: normal;
	font-size: 20px;
}

#wrapper {
	margin-top: 30px;
	text-align: center;
	background-color: #FFFFFF;
	padding-bottom: 20px;
	padding-left: 50px;
	padding-right: 50px;
	border: 2px solid;
	border-radius: 20px;
	display: inline-block;
}
</style>
</head>
<body>
	<div style="text-align: center;">
		<span id="wrapper">
			<h1>404 Error-The requested resource (file/URL) was not found in the server!Please go to the main screen</h1>
		</span>
	</div>
</body>
</html>


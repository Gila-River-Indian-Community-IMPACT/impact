<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%
    response.setContentType("text/javascript");
    response.addHeader("Content-Disposition",
            "attachment; filename=opendoc.js");
%>

var docType = '<c:out value="${sessionScope.webdavOpen.first}"/>';
var docURL = '<c:out value="${sessionScope.webdavOpen.second}"/>';
var readOnly = <c:out value="${sessionScope.webdavOpen.third}"/>;
<c:remove var="webdavOpen" scope="session" />

if (docType == "doc" || docType == "docx" || docType == 'xml' || docType == 'wpd')
{
    openWord(docURL, readOnly);
}
else
{
    WScript.Echo("Cannot open document with type=" + docType);
}
    
function openWord(docURL, readOnly)
{
    var app = null;

    try
    {
        app = new ActiveXObject('Word.Application');
        app.Visible = true;
        app.Documents.Open(docURL, false, readOnly);
    }
    catch(ex)
    {
        if (app != null)
        {
            app.Quit();
        }
        WScript.Echo("Cannot load document: " + ex.description);
    }
}

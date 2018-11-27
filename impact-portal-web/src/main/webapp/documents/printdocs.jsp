<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

var docs = new Array();

<%
    response.setContentType("application/unknown");
    response.addHeader("Content-Disposition",
            "attachment; filename=printdocs.js");
%>

<c:forEach var="pair" varStatus="status" items="${sessionScope.webdavPrint}">
docs[<c:out value="${status.index}"/>] = new Object();
docs[<c:out value="${status.index}"/>].type = '<c:out value="${pair.first}"/>';
docs[<c:out value="${status.index}"/>].url = '<c:out value="${pair.second}"/>';
</c:forEach>
<c:remove var="webdavPrint" scope="session" />

var idx;
for (idx = 0; idx < docs.length; idx++)
{
    var docType = docs[idx].type;
    var docURL = docs[idx].url;

    if (docType == "pdf")
    {
        WScript.Echo("Printing pdf document");
        printPDF(docURL);
    }
    else if (docType == "doc" || docType == 'xml' || docType == 'wpd')
    {
        WScript.Echo("Printing MS Word document");
        printWord(docURL);
    }
    else if (docType == "xls")
    {
        WScript.Echo("Printing Excel document");
        printExcel(docURL);
    }
    else
    {
        WScript.Echo("Cannot print document with type=" + docType);
    }
    WScript.sleep(3000);
}

function printPDF(docURL)
{
    var adTypeBinary = 1;
    var adSaveCreateOverWrite = 2;
    var tempFile = "c:\\temp\\urk.pdf";
    var http, binStream, fso, acroRdPath;

    acroRdPath = getAcroRdPath();
    http = new ActiveXObject("Microsoft.XMLHTTP");
    binStream  = new ActiveXObject("ADODB.Stream");
    http.open("GET", docURL, false);
    http.send();
    pdfData = http.responseBody;
    binStream.type = adTypeBinary;
    binStream.open();
    binStream.write(pdfData);
    binStream.saveToFile(tempFile, adSaveCreateOverWrite);

    runCommand(acroRdPath + " " + "/p /h" + " " + tempFile);
    WScript.sleep(1000);
    fso = new ActiveXObject("Scripting.FileSystemObject");
    for (;;)
    {
        try
        {
            fso.deleteFile(tempFile);
            break;
        }
        catch (ex)
        {
            WScript.sleep(1000);
        }
    }
}

function runCommand(commandStr)
{
    var wmiLocator, processService;

    wmiLocator = new ActiveXObject("WbemScripting.SWbemLocator");
    processService = wmiLocator.ConnectServer().Get("Win32_Process");
    processService.Create(commandStr);
}

function getAcroRdPath()
{
    var HKEY_LOCAL_MACHINE = 0x80000002;
    var acroKeyPath = "SOFTWARE\\Classes\\Software\\Adobe\\Acrobat\\Exe";
    var wmiLocator, regService, refMethod, refInParam, refOutParam;

    wmiLocator = new ActiveXObject("WbemScripting.SWbemLocator");
    regService = wmiLocator.ConnectServer(null, "root\\default").Get(
        "StdRegProv");
    refMethod = regService.Methods_.Item("GetStringValue");
    refInParam = refMethod.InParameters.SpawnInstance_();
    refInParam.hDefKey = HKEY_LOCAL_MACHINE;
    refInParam.sSubKeyName = acroKeyPath;
    refInParam.sValueName = null;
    // To avoid antivirus paranoia...
    var methodName = "ExecMethod_";
    refOutParam = eval("regService." + methodName +
        "(refMethod.Name, refInParam)");
    return refOutParam.sValue;
}

function printWord(docURL)
{
    var word;
    var wordPrintTimeMillisecs = 3 * 1000;

    try
    {
        word = new ActiveXObject('Word.Application');
        word.Documents.Open(docURL);
        word.Documents(1).PrintOut();
        WScript.Sleep(wordPrintTimeMillisecs);
        word.Quit(false);
    }
    catch (ex)
    {
        if (word != null)
        {
            word.Quit(false);
        }
        WScript.Echo("Cannot print Word document: " + ex.description);
    }
}

function printExcel(docURL)
{
    var xl;
    var wb;
    var xlPrintTimeMillisecs = 3 * 1000;
    
    try
    {
        xl = new ActiveXObject("Excel.Application");
        wb = xl.Workbooks.Open(docURL);
        wb.Worksheets(1).PrintOut();
        wb.Close();
        WScript.Sleep(xlPrintTimeMillisecs);
        xl.Quit();
    }
    catch (ex)
    {
        if (xl != null)
        {
            xl.Quit();
        }
        WScript.Echo("Cannot print Excel document: " + ex.description);
    }
}

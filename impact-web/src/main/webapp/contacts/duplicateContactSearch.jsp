<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:panelGroup layout="vertical"
	rendered="#{mergeContact.baseContact != null}">

	<afh:rowLayout halign="center"
		rendered="#{mergeContact.hasDuplicateSearchResults}">
		<h:panelGrid border="1">
			<jsp:include flush="true" page="duplicateContactSearchResults.jsp" />
		</h:panelGrid>
	</afh:rowLayout>
	<af:objectSpacer width="100%" height="15" />
	<afh:rowLayout halign="center">
		<jsp:include flush="true" page="duplicateContactSearchCriteria.jsp" />
	</afh:rowLayout>

</af:panelGroup>
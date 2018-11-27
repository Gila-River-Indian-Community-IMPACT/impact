<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table emptyText=" " var="app" width="99%"
  value="#{applicationSearch.applications}">
  <%
      // Note: the actionListener below sets the "applicationNumber" attribute
          // of the ApplicationDetail class. This will cause the application to
          // be loaded from the READONLY schema in the portal application, which
          // is the desired behavior since the search table is used to display
          // applications that have already been submitted.
  %>
  <af:column sortable="true" sortProperty="applicationNumber"
    formatType="text" headerText="Request Number" width="110px" noWrap="true">
    <af:commandLink text="#{app.applicationNumber}"
      action="home.applications.applicationDetail"  >
      <af:setActionListener from="#{app.applicationNumber}"
        to="#{applicationDetail.applicationNumber}" />
    </af:commandLink>
  </af:column>
  <af:column sortable="true" sortProperty="applicationTypeCd"
    formatType="text" headerText="Request Type" width="90px" noWrap="true">
    <af:selectOneChoice value="#{app.applicationTypeCd}" readOnly="true">
      <f:selectItems value="#{applicationReference.applicationTypeDefs}" />
    </af:selectOneChoice>
  </af:column>
  <af:column sortable="true" sortProperty="receivedDate"
    formatType="text" headerText="Received Date" width="90px" noWrap="true">
    <af:switcher defaultFacet="NoDate"
      facetName="#{empty app.receivedDate? 'NoDate' : 'Date'}">
      <f:facet name="NoDate">
        <af:outputText value="Not Available" />
      </f:facet>
      <f:facet name="Date">
        <af:selectInputDate readOnly="true" value="#{app.receivedDate}" />
      </f:facet>
    </af:switcher>
  </af:column>
  <af:column sortable="true" sortProperty="previousApplicationNumber"
    formatType="text" headerText="Previous Application Number" width="130px">
    <af:outputText
      value="#{empty app.previousApplicationNumber ? 'N/A' : app.previousApplicationNumber}" />
  </af:column>
  <%-- General Permit not valid for WY
	 <af:column sortable="true" sortProperty="generalPermit"
    formatType="text" headerText="General Permit">
    <af:outputText value="#{app.generalPermit}" />
  </af:column> --%>
  <af:column sortable="true" sortProperty="permitNumbers"
    formatType="text" headerText="Permit Number(s)">
    <af:outputText value="#{app.permitNumbers}" />
  </af:column>
  <f:facet name="footer">
    <afh:rowLayout halign="center">
      <af:panelButtonBar>
        <af:commandButton actionListener="#{tableExporter.printTable}"
          onclick="#{tableExporter.onClickScript}" text="Printable view" />
        <af:commandButton actionListener="#{tableExporter.excelTable}"
          onclick="#{tableExporter.onClickScript}"
          text="Export to excel" />
      </af:panelButtonBar>
    </afh:rowLayout>
  </f:facet>
</af:table>


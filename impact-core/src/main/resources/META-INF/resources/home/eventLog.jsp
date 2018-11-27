<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Event Log">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}" title="Event Log">
        <%@ include file="../util/header.jsp"%>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="800">
            <af:panelBorder>
              <af:showDetailHeader text="Event Log Filter"
                disclosed="true">
                <af:panelForm rows="2" maxColumns="4" width="800">

                  <af:inputText columns="25" label="Facility ID"
                    value="#{eventLog.facilityId}"
                    tip="0000000000, 0%, %0%, *0*, 0*" />

                  <af:inputText columns="25" label="Facility Name"
                    value="#{eventLog.facilityName}"
                    tip="acme%, %acme% *acme*, acme*" />

                  <af:inputText label="Description" columns="30"
                    value="#{eventLog.note}" tip="*address*, *facility name*" />

                  <af:selectOneChoice label="Event Type"
                    unselectedLabel=""
                    value="#{eventLog.eventTypeDefCd}">
                    <f:selectItems value="#{eventLog.eventType}" />
                  </af:selectOneChoice>

                  <af:selectInputDate label="Date from"
                    value="#{eventLog.date}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                  <af:selectInputDate label="Date to"
                    value="#{eventLog.dateTo}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                  <af:selectOneChoice label="Staff"
                    unselectedLabel="" value="#{eventLog.userId}">
                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                  </af:selectOneChoice>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{eventLog.submit}" />
                    <af:commandButton text="Reset"
                      action="#{eventLog.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="800"
            rendered="#{eventLog.hasSearchResults}">
            <af:panelBorder>
              <af:showDetailHeader text="Event Log List"
                disclosed="true">
                <af:table bandingInterval="1" banding="row" var="els"
                  emptyText=" " rows="#{eventLog.pageLimit}" width="98%"
                  value="#{eventLog.els}">

                  <af:column headerText="Facility ID"
                    sortProperty="facilityId" sortable="true"
                    noWrap="true">
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:commandLink text="#{els.facilityId}"
                        action="#{facilityProfile.submitProfile}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener property="#{facilityProfile.fpId}"
                          value="#{els.fpId}" />
                        <t:updateActionListener
                          property="#{menuItem_facProfile.disabled}"
                          value="false" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column headerText="Facility Name"
                    sortProperty="facilityName" sortable="true">
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText readOnly="true"
                        value="#{els.facilityName}" />
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="date" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Date" />
                    </f:facet>
                   <af:selectInputDate  value="#{els.date}" readOnly="true" >
                          <f:convertDateTime dateStyle="full" pattern="MM/dd/yyyy HH:mm:ss"/>
                   </af:selectInputDate>
                  </af:column>

                  <af:column sortProperty="userId" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Staff" />
                    </f:facet>
                    <af:selectOneChoice readOnly="true"
                      value="#{els.userId}">
                      <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                    </af:selectOneChoice>
                  </af:column>

                  <af:column sortProperty="eventTypeDefCd" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Event Type" />
                    </f:facet>
                    <af:selectOneChoice label="Event Type"
                      readOnly="true" value="#{els.eventTypeDefCd}">
                      <f:selectItems value="#{eventLog.eventType}" />
                    </af:selectOneChoice>
                  </af:column>

                  <af:column sortProperty="note" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Description" />
                    </f:facet>
                    <af:inputText readOnly="true" columns="40"
                      value="#{els.note}" />
                  </af:column>

				  <f:facet name="footer">
            		<afh:rowLayout halign="center">
              			<af:panelButtonBar>
                			<af:commandButton
                  				actionListener="#{tableExporter.printTable}"
                  				onclick="#{tableExporter.onClickScriptTS}"
                  				text="Printable view" />
                		<af:commandButton
                  				actionListener="#{tableExporter.excelTable}"
                  				onclick="#{tableExporter.onClickScriptTS}"
                  				text="Export to excel" />
              			</af:panelButtonBar>
            		</afh:rowLayout>
          		  </f:facet>
                </af:table>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>


      </af:page>
    </af:form>
  </af:document>
</f:view>

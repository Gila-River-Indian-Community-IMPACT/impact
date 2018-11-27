<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="New Event">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}" title="New Event">
        <%@ include file="../util/header.jsp"%>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1">
            <af:panelBorder>
              <af:showDetailHeader text="New Event" disclosed="true">
                <af:panelForm width="200">
                
                  <af:inputText label="Facility"  required="true" columns="10"
          				value="#{eventLog.facilityId}" />

                  <af:selectOneChoice label="Staff" required="true"
                    value="#{eventLog.userId}"
                    unselectedLabel="-- Select one --">
                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                  </af:selectOneChoice>

                  <af:selectOneChoice label="Event Type" required="true"
                    unselectedLabel="-- Select one --"
                    value="#{eventLog.eventTypeDefCd}">
                    <f:selectItems value="#{eventLog.eventType}" />
                  </af:selectOneChoice>

                  <af:inputText label="Note" required="true"
                    value="#{eventLog.note}" />

                  <af:selectInputDate label="Date" required="true"
                    value="#{eventLog.date}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                  <f:facet name="footer">
                    <af:panelButtonBar>
                      <af:objectSpacer width="100%" height="20" />
                      <af:commandButton text="Submit"
                        disabled="#{eventLog.readOnlyUser}"
                        action="#{eventLog.createEvent}" />
                      <af:commandButton text="Reset"
                        action="#{eventLog.reset}" />

                    </af:panelButtonBar>
                  </f:facet>

                </af:panelForm>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>


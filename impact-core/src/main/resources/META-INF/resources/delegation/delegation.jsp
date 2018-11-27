<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document title="Stars2 Relocation">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Relocations for Facility">

        <jsp:include page="header.jsp" />

        <h:panelGrid border="1">
          <af:panelBorder>

            <f:facet name="top">
              <f:subview id="facilityHeader">
                <jsp:include page="comFacilityHeader.jsp" />
              </f:subview>
            </f:facet>

            <f:facet name="right">
              <h:panelGrid columns="1" border="1" width="950">
                <af:panelGroup layout="vertical">
                  <af:objectSpacer height="10" />

                  <af:panelForm>
                    <af:table value="#{undeliveredMail.facilityRUMs}"
                      bandingInterval="1" banding="row" var="rum"
                      width="98%" emptyText='No History'>
                      <af:column sortable="true" formatType="text"
                        headerText="ID #">
                        <af:commandLink text="#{rum.rumId}" id="viewRUM"
                          useWindow="true" windowWidth="700"
                          windowHeight="350"
                          returnListener="#{undeliveredMail.dialogDone}"
                          action="#{undeliveredMail.startViewRUM}">
                          <t:updateActionListener
                            property="#{undeliveredMail.facilityRUM}"
                            value="#{rum}" />
                        </af:commandLink>
                      </af:column>
                      <af:column sortable="true">
                        <f:facet name="header">
                          <af:outputText value="Mailing Date" />
                        </f:facet>
                        <af:selectInputDate readOnly="true" value="#{rum.originalMailDt}" />
                      </af:column>
                      <af:column sortable="true">
                        <f:facet name="header">
                          <af:outputText value="Category" />
                        </f:facet>
                        <af:outputText
                          value="#{rum.correspondenceCategory}" />
                      </af:column>
                      <af:column sortable="true">
                        <f:facet name="header">
                          <af:outputText value="Disposition" />
                        </f:facet>
                        <af:outputText value="#{rum.dispositionDesc}" />
                      </af:column>
                      <af:column sortable="true">
                        <f:facet name="header">
                          <af:outputText
                            value="Undeliverable Contact/Address" />
                        </f:facet>
                        <af:outputText
                          value="#{rum.undeliverableAddress}" />
                      </af:column>
                      <af:column sortable="true">
                        <f:facet name="header">
                          <af:outputText value="Reason for Return" />
                        </f:facet>
                        <af:outputText value="#{rum.reasonDesc}" />
                      </af:column>
                      <af:column sortable="true">
                        <f:facet name="header">
                          <af:outputText value="Submitted By" />
                        </f:facet>
                        <af:outputText value="#{rum.userName}" />
                      </af:column>
                      <af:column sortable="true">
                        <f:facet name="header">
                          <af:outputText value="Comments" />
                        </f:facet>
                        <af:outputText value="#{rum.dapcNote}" />
                      </af:column>
                      <af:column sortable="true">
                        <f:facet name="header">
                          <af:outputText value="Date Submitted" />
                        </f:facet>
                        <af:selectInputDate readOnly="true" value="#{rum.createdDt}" />
                      </af:column>
                      <f:facet name="footer">
                        <afh:rowLayout halign="center">
                          <af:panelButtonBar>
                            <af:commandButton text="Add Record"
                              id="AddRUMButton" useWindow="true"
                              windowWidth="700" windowHeight="350"
                              returnListener="#{undeliveredMail.dialogDone}"
                              disabled="#{undeliveredMail.disabledUpdateButton}"
                              rendered="#{!undeliveredMail.editable}"
                              action="#{undeliveredMail.startAddRUM}">
                            </af:commandButton>
                            <af:commandButton
                              actionListener="#{tableExporter.printTable}"
                              onclick="#{tableExporter.onClickScript}"
                              text="Printable view" />
                            <af:commandButton
                              actionListener="#{tableExporter.excelTable}"
                              onclick="#{tableExporter.onClickScript}"
                              text="Export to excel" />
                          </af:panelButtonBar>
                        </afh:rowLayout>
                      </f:facet>
                    </af:table>
                  </af:panelForm>

                </af:panelGroup>
              </h:panelGrid>
            </f:facet>
          </af:panelBorder>
        </h:panelGrid>
      </af:page>
    </af:form>
  </af:document>
</f:view>


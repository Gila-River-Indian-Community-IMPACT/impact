<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document title="BAT Summary">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}"
        title="BAT Summary">
        <%@ include file="../permits/header.jsp"%>
        
        <h:panelGrid border="1">
          <af:panelBorder>

            <f:facet name="top">
              <f:subview id="permitDetailTop">
                <jsp:include page="permitDetailTop.jsp" />
              </f:subview>
            </f:facet>
            <%
            /* Top end */
            %>

            <%
            /* Content begin */
            %>
            <h:panelGrid columns="1" border="1"
              width="#{permitDetail.permitWidth}">
              <af:panelGroup>
                <afh:rowLayout halign="center">
                  <af:table value="#{permitDetail.selectedEU.euFees}"
                    id="eufTab" width="#{permitDetail.permitTableWidth}"
                    var="eufs" emptyText=" ">
                    <af:column headerText="EU id">
                      <af:commandLink text="#{eufs.feeId}"
                        action="#{permitDetail.openDoc}" />
                    </af:column>
                    <af:column headerText="Pollutant">
                    </af:column>
                    <af:column headerText="Air Quality Description">
                    </af:column>
                    <af:column
                      headerText="Estimated Actual Short Term Rate">
                    </af:column>
                    <af:column headerText="Rate Basis Code">
                    </af:column>
                    <af:column
                      headerText="Estimated Actual Tons/Yr Rate">
                    </af:column>
                    <af:column
                      headerText="Permit Allowable Short Term Rate">
                    </af:column>
                    <af:column
                      headerText="Permit Allowable Tons/Yr Rate">
                    </af:column>
                    <f:facet name="footer">
                      <afh:rowLayout halign="center">
                        <af:panelButtonBar>
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
                </afh:rowLayout>
              </af:panelGroup>
            </h:panelGrid>
            <%
            /* Content end */
            %>

          </af:panelBorder>
        </h:panelGrid>
      </af:page>
    </af:form>
  </af:document>
</f:view>

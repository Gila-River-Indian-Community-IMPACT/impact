<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="BAT Summary">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}"
        title="BAT Summary">
        <f:facet name="messages">
          <af:messages />
        </f:facet>
        <f:facet name="branding">
          <af:objectImage source="/images/stars2.png" />
        </f:facet>
        <f:facet name="nodeStamp">
          <af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
            disabled="#{foo.disabled}" type="#{foo.type}" rendered="#{foo.rendered}"
            onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }" />
        </f:facet>
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
            <h:panelGrid columns="1" border="1" width="995">
              <af:panelGroup>
                <af:panelHeader text="BAT Table">
                  <afh:rowLayout halign="center">
                    <af:table value="#{permitDetail.selectedEU.euFees}"
                      id="eufTab" width="900" var="eufs" emptyText=" ">
                      <af:column headerText="EU id">
                        <af:commandLink text="#{eufs.feeId}"
                          action="#{permitDetail.openDoc}" />
                      </af:column>
                      <af:column headerText="Pollutant">
                      </af:column>
                      <af:column headerText="Air Quality Description">
                      </af:column>
                      <af:column headerText="Estimated Actual Short Term Rate">
                      </af:column>
                      <af:column headerText="Rate Basis Code">
                      </af:column>
                      <af:column headerText="Estimated Actual Tons/Yr Rate">
                      </af:column>
                      <af:column headerText="Permit Allowable Short Term Rate">
                      </af:column>
                      <af:column headerText="Permit Allowable Tons/Yr Rate">
                      </af:column>
                    </af:table>
                  </afh:rowLayout>
                </af:panelHeader>
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
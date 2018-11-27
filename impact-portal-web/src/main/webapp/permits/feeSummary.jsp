<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Draft Issuance">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}"
        title="Draft Issuance">
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
                <%
                /* TODO Fees begin */
                %>
                <af:panelHeader text="Fee">
                  <af:panelForm maxColumns="2" rows="1" labelWidth="44%"
                    fieldWidth="56%">
                    <af:selectBooleanCheckbox label="Full Cost Recovery:"
                      readOnly="#{! permitDetail.editMode}"
                      value="#{permitDetail.permit.fullCostRecovery}" />
                    <af:inputText label="Invoice Number :" columns="20"
                      value="#{permitDetail.permit.invoice.invoiceId}"
                      readOnly="#{! permitDetail.editMode}" />
                  </af:panelForm>
                </af:panelHeader>
                <%
                /* Fees end */
                %>

                <af:showDetailHeader text="EU Fees" disclosed="true">
                  <afh:rowLayout halign="center">
                    <af:table value="#{permitDetail.selectedEU.euFees}"
                      id="eufTab" width="900" var="eufs" emptyText=" ">
                      <af:column headerText="EU id">
                        <af:commandLink text="#{eufs.feeId}"
                          action="#{permitDetail.openDoc}" />
                      </af:column>
                      <af:column headerText="Category">
                        <af:outputText value="#{eufs.feeCategoryCd}" />
                      </af:column>
                      <af:column headerText="Base amount">
                      </af:column>
                      <af:column headerText="Adjustment">
                        <af:outputText value="#{eufs.adjustmentCd}" />
                      </af:column>
                      <af:column headerText="Adjusted amount">
                        <af:outputText value="#{eufs.adjustedAmount}" />
                      </af:column>
                    </af:table>
                  </afh:rowLayout>
                  <af:panelForm maxColumns="2" rows="1" labelWidth="44%"
                    fieldWidth="56%">
                    <af:inputText label="Other Adjustment :" columns="10"
                      value="#{permitDetail.permit.otherAdjustment}"
                      readOnly="#{! permitDetail.editMode}" />
                    <af:inputText label="Invoice Amount :" columns="10"
                      value="#{permitDetail.permit.invoice.origAmount}"
                      readOnly="#{! permitDetail.editMode}" />
                  </af:panelForm>
                </af:showDetailHeader>

                <af:objectSpacer height="10" />

                <%
                /* Buttons begin */
                %>
                <f:subview id="permitDetailButtons">
                  <jsp:include page="permitDetailButtons.jsp" />
                </f:subview>
                <%
                /* Buttons end */
                %>
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
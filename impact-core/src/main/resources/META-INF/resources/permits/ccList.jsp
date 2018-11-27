<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document title="Carbon Copy">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}"
        title="Carbon Copy">
        <%@ include file="../permits/header.jsp"%>

        <h:panelGrid border="1">
          <af:panelBorder>

            <f:facet name="top">
              <f:subview id="permitDetailTop">
                <jsp:include page="permitDetailTop.jsp" />
              </f:subview>
            </f:facet>

            <%
            /* Content begin */
            %>
            <h:panelGrid columns="1" border="1"
              width="#{permitDetail.permitWidth}">
              <af:panelGroup>
                <afh:rowLayout halign="center">
                  <af:table value="#{permitDetail.permit.permitCCList}"
                    id="contactsTab" rows="50"
                    width="#{permitDetail.permitTableWidth}"
                    var="contact" partialTriggers="addCC" emptyText=" ">
                    <f:facet name="selection">
                      <af:tableSelectMany
                        rendered="#{permitDetail.editMode}" />
                    </f:facet>
                    <af:column headerText="Name" width="15%"
                      formatType="text" sortProperty="name"
                      sortable="true">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText label="Name" simple="true" columns="15"
                          readOnly="#{!permitDetail.editMode}" maximumLength="256"
                          required="true" value="#{contact.name}" />
                      </af:panelHorizontal>
                    </af:column>
                    <af:column headerText="Address 1" formatType="text"
                      sortProperty="address1" sortable="true">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText label="Address 1" simple="true"
                          readOnly="#{!permitDetail.editMode}" maximumLength="100"
                          required="true" value="#{contact.address1}" />
                      </af:panelHorizontal>
                    </af:column>
                    <af:column headerText="Address 2" formatType="text"
                      sortProperty="address2" sortable="true">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText maximumLength="100"
                          readOnly="#{!permitDetail.editMode}"
                          value="#{contact.address2}" />
                      </af:panelHorizontal>
                    </af:column>
                    <af:column headerText="City" width="10%"
                      sortProperty="city" sortable="true"
                      formatType="text">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText label="City" simple="true"
                          readOnly="#{!permitDetail.editMode}" maximumLength="50"
                          required="true" value="#{contact.city}"
                          columns="15" />
                      </af:panelHorizontal>
                    </af:column>
                    <af:column headerText="State" width="10%"
                      sortProperty="state" sortable="true"
                      formatType="text">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:selectOneChoice value="#{contact.state}"
                          readOnly="#{!permitDetail.editMode}"
                          required="true">
                          <f:selectItems value="#{infraDefs.states}" />
                        </af:selectOneChoice>
                      </af:panelHorizontal>
                    </af:column>
                    <af:column headerText="ZIP" width="5%"
                      sortProperty="zipCode" sortable="true"
                      formatType="text">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText label="ZIP" simple="true" maximumLength="9"
                          readOnly="#{!permitDetail.editMode}"
                          required="true" value="#{contact.zipCode}"
                          columns="5" />
                      </af:panelHorizontal>
                    </af:column>

                    <f:facet name="footer">
                      <afh:rowLayout halign="center">
                        <af:panelGroup layout="horizontal">
                          <af:commandButton text="Add" id="addCC"
                            action="#{permitDetail.addCC}"
                            rendered="#{permitDetail.editMode}" />
                          <af:commandButton text="Delete selected"
                            actionListener="#{permitDetail.deleteCC}"
                            rendered="#{permitDetail.editMode}" />
                          <af:commandButton
                            actionListener="#{tableExporter.printTable}"
                            onclick="#{tableExporter.onClickScript}"
                            text="Printable view" />
                          <af:commandButton
                            actionListener="#{tableExporter.excelTable}"
                            onclick="#{tableExporter.onClickScript}"
                            text="Export to excel" />
                        </af:panelGroup>
                      </afh:rowLayout>
                    </f:facet>
                  </af:table>
                  <%
                  /* Carbon Copy list end */
                  %>
                </afh:rowLayout>


                <af:objectSpacer height="10" />

                <f:subview id="permitIssuanceButtons">
                  <jsp:include page="editOnlyButtons.jsp" />
                </f:subview>
              </af:panelGroup>
            </h:panelGrid>
            <%
            /* Content end */
            %>

          </af:panelBorder>
        </h:panelGrid>
      </af:page>
      <af:iterator value="#{permitDetail}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
	  </af:iterator>
    </af:form>
  </af:document>
</f:view>

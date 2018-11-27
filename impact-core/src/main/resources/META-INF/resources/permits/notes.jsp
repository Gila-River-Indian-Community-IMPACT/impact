<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document title="Notes">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}"
        title="Notes">
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
              <afh:rowLayout halign="left">
                <af:table id="DAPCComments"
                  width="#{permitDetail.permitTableWidth}" rows="50"
                  emptyText=" " var="dc"
                  partialTriggers="ThePage:DAPCComments:AddButton"
                  value="#{permitDetail.permit.dapcComments}">

                  <af:column headerText="Note ID" sortable="true"
                    width="10%" sortProperty="noteId">
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:commandLink text="#{dc.noteId}" id="viewNote"
                        useWindow="true" windowWidth="650"
                        windowHeight="300"
                        returnListener="#{permitDetail.commentDialogDone}"
                        action="#{permitDetail.startEditComment}">
                        <t:updateActionListener
                          property="#{permitDetail.modifyComment}"
                          value="#{dc}" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column headerText="Note" sortable="true"
                    width="60%" sortProperty="noteTxt">
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:outputText value="#{dc.noteTxt}"
                        truncateAt="150" 
                        shortDesc="#{dc.noteTxt}"/>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="userId" sortable="true"
                    width="15%" headerText="User Name">
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:selectOneChoice value="#{dc.userId}"
                        readOnly="true">
                        <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                      </af:selectOneChoice>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="dateEntered" sortable="true"
                    width="10%" headerText="Date">
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:selectInputDate readOnly="true"
                        value="#{dc.dateEntered}" />
                    </af:panelHorizontal>
                  </af:column>

                  <f:facet name="footer">
                    <afh:rowLayout halign="center">
                      <af:panelButtonBar>
                        <af:commandButton text="Add Note" id="AddButton"
                          useWindow="true" windowWidth="650"
                          windowHeight="300" rendered="#{!permitDetail.readOnlyUser}"
                          returnListener="#{permitDetail.commentDialogDone}"
                          action="#{permitDetail.startAddComment}" />
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

<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Final Issuance">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}"
        title="Final Issuance">
        <f:facet name="messages">
          <af:messages />
        </f:facet>
        <f:facet name="branding">
          <af:objectImage source="/images/stars2.png" />
        </f:facet>
        <f:facet name="nodeStamp">
          <af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
            disabled="#{foo.disabled}" type="#{foo.type}"
            rendered="#{foo.rendered}"
            onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }" />
        </f:facet>

        <mu:setProperty property="#{permitDetail.currentIssuanceAction}"
          value="F" />

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
                <af:panelHeader text="Final">
                  <af:panelGroup>
                    <af:panelForm labelWidth="44%" fieldWidth="56%" rows="1"
                      maxColumns="2">
                      <af:selectOneChoice label="Issuance status :"
                        readOnly="true"
                        value="#{permitDetail.permit.finalIssuanceStatusCd}">
                        <mu:selectItems id="issuanceStatus"
                          value="#{permitReference.issuanceStatusDefs}" />
                      </af:selectOneChoice>
                      <af:selectInputDate label="Issuance date :"
                        readOnly="#{! permitDetail.editMode}"
                        value="#{permitDetail.permit.finalIssueDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                    </af:panelForm>

                    <afh:tableLayout>
                      <afh:rowLayout rendered="#{permitDetail.installation}">
                        <afh:cellFormat width="50%" halign="left" valign="top">
                          <afh:rowLayout>
                            <afh:cellFormat width="220" halign="right"
                              valign="top">
                              <af:inputText label="Installation certificates :"
                                readOnly="true" />
                            </afh:cellFormat>
                            <afh:cellFormat width="190">
                              <af:switcher
                                facetName="#{empty permitDetail.docsMap[permitReference.installCertCD][permitReference.finalFlag]? 'noDoc': 'doc'}"
                                defaultFacet="noDoc">
                                <f:facet name="noDoc">
                                  <af:commandButton text="Upload Document"
                                    useWindow="true" windowWidth="500"
                                    windowHeight="300" id="UploadDDocButton"
                                    returnListener="#{permitDetail.docDialogDone}"
                                    action="#{permitDetail.uploadDoc}" />
                                </f:facet>
                                <f:facet name="doc">
                                  <af:commandLink text="Open Notice"
                                    action="#{permitDetail.openDoc}">
                                    <af:setActionListener
                                      from="#{permitDetail.docsMap[permitReference.installCertCD][permitReference.finalFlag]}"
                                      to="#{requestScope.doc}" />
                                  </af:commandLink>
                                </f:facet>
                              </af:switcher>
                            </afh:cellFormat>
                          </afh:rowLayout>
                        </afh:cellFormat>
                      </afh:rowLayout>
                      <afh:rowLayout>
                        <afh:cellFormat width="50%" halign="left" valign="top">
                          <afh:rowLayout>
                            <afh:cellFormat width="220" halign="right"
                              valign="top">
                              <af:inputText label="Introductory package :"
                                readOnly="true" />
                            </afh:cellFormat>
                            <afh:cellFormat width="190">
                              <af:switcher
                                facetName="#{empty permitDetail.docsMap[permitReference.introPackageCD][permitReference.finalFlag]? 'noDoc': 'doc'}"
                                defaultFacet="noDoc">
                                <f:facet name="noDoc">
                                  <af:commandButton text="Upload Document"
                                    useWindow="true" windowWidth="500"
                                    windowHeight="300" id="UploadDDocButton"
                                    returnListener="#{permitDetail.docDialogDone}"
                                    action="#{permitDetail.uploadDoc}" />
                                </f:facet>
                                <f:facet name="doc">
                                  <af:commandLink text="Open Notice"
                                    action="#{permitDetail.openDoc}">
                                    <af:setActionListener
                                      from="#{permitDetail.docsMap[permitReference.introPackageCD][permitReference.finalFlag]}"
                                      to="#{requestScope.doc}" />
                                  </af:commandLink>
                                </f:facet>
                              </af:switcher>
                            </afh:cellFormat>
                          </afh:rowLayout>
                        </afh:cellFormat>
                        <afh:cellFormat width="50%" halign="left" valign="top">
                          <afh:rowLayout>
                            <afh:cellFormat width="220" halign="right"
                              valign="top">
                              <af:inputText label="Issuance Document :"
                                readOnly="true" />
                            </afh:cellFormat>
                            <afh:cellFormat>
                              <af:switcher
                                facetName="#{empty permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.finalFlag]? 'noDoc': 'doc'}"
                                defaultFacet="noDoc">
                                <f:facet name="noDoc">
                                  <af:commandButton text="Upload Document"
                                    useWindow="true" windowWidth="500"
                                    windowHeight="300" id="UploadDDocButton"
                                    returnListener="#{permitDetail.docDialogDone}"
                                    action="#{permitDetail.uploadDoc}" />
                                </f:facet>
                                <f:facet name="doc">
                                  <af:commandLink text="Open Document"
                                    action="#{permitDetail.openDoc}">
                                    <af:setActionListener
                                      from="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.finalFlag]}"
                                      to="#{requestScope.doc}" />
                                  </af:commandLink>
                                </f:facet>
                              </af:switcher>
                            </afh:cellFormat>
                          </afh:rowLayout>
                        </afh:cellFormat>
                      </afh:rowLayout>

                    </afh:tableLayout>
                    <afh:rowLayout halign="center"
                      rendered="#{permitDetail.editMode}">
                      <af:panelButtonBar>
                        <af:commandButton text="Upload Document"
                          useWindow="true" windowWidth="500" windowHeight="300"
                          id="UploadFDocButton"
                          returnListener="#{permitDetail.docDialogDone}"
                          action="#{permitDetail.uploadDoc}" />
                      </af:panelButtonBar>
                    </afh:rowLayout>

                    <af:objectSpacer height="10" />

                    <afh:rowLayout halign="center"
                      rendered="#{!permitDetail.editMode && permitDetail.permit.finalIssuanceStatusCd != permitReference.issuanceStatusIssued}">
                      <af:panelButtonBar>
                        <af:commandButton text="Validate for issuance"
                          action="#{permitDetail.validateIssuance}">
                        </af:commandButton>
                        <af:commandButton text="Mark Ready Issuance"
                          rendered="#{permitDetail.permit.finalIssuanceStatusCd == permitReference.issuanceStatusNotReady}"
                          action="#{permitDetail.prepareIssuance}">
                        </af:commandButton>
                        <af:commandButton text="UnMark Ready Issuance"
                          rendered="#{permitDetail.permit.finalIssuanceStatusCd == permitReference.issuanceStatusReady}"
                          action="#{permitDetail.unprepareIssuance}">
                        </af:commandButton>
                        <af:commandButton text="Skip Issuance"
                          action="#{permitDetail.skipIssuance}">
                        </af:commandButton>
                        <af:commandButton text="Finalize issuance"
                          rendered="#{permitDetail.permit.finalIssuanceStatusCd == permitReference.issuanceStatusReady}"
                          action="#{permitDetail.finalizeIssuance}">
                        </af:commandButton>
                      </af:panelButtonBar>
                    </afh:rowLayout>
                  </af:panelGroup>
                </af:panelHeader>
                <%
                /* Final end */
                %>

                <af:objectSpacer height="10" />

                <f:subview id="permitIssuanceButtons">
                  <jsp:include page="permitIssuanceButtons.jsp" />
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

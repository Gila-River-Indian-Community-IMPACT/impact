<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="External References">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="External References">
        <jsp:include flush="true" page="../util/header.jsp" />
        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="800">
            <af:panelBorder>
              <f:facet name="left">
                <af:iterator value="#{externalReferences.sections}"
                  var="section">
                  <af:panelGroup layout="vertical">
                    <af:panelHeader text="#{section.name}" />
                    <af:panelForm>
                      <af:iterator value="#{section.references}"
                        var="reference">
                        <af:panelGroup layout="vertical">
                          <af:goLink text="#{reference.name}"
                            destination="#{reference.value}"
                            targetFrame="new" />
                        </af:panelGroup>
                      </af:iterator>
                    </af:panelForm>
                  </af:panelGroup>
                </af:iterator>
              </f:facet>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>
      </af:page>
    </af:form>
  </af:document>
</f:view>

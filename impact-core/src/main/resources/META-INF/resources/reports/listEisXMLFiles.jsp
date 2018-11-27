<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="EIS XML Files">
    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" />
    <f:verbatim></script></f:verbatim>
    <af:form>

      <af:page var="foo" value="#{menuModel.model}"
        title="EIS XML Files">
        <%@ include file="../util/header.jsp"%>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1">
            <t:div style="overflow:auto;width:800px;height:480px">

              <af:panelBorder>
                <f:facet name="left">

                  <af:iterator value="#{listEisXMLFiles.userTempFiles}"
                    var="file" rows="0">
                    <af:panelGroup layout="vertical">
                    	<af:goLink text="#{file.fileName}" 
							destination="#{file.formURL}"
							targetFrame="_blank"/>
                    </af:panelGroup>
                  </af:iterator>

                </f:facet>
              </af:panelBorder>

            </t:div>
          </h:panelGrid>
        </afh:rowLayout>
      </af:page>

    </af:form>
  </af:document>
</f:view>

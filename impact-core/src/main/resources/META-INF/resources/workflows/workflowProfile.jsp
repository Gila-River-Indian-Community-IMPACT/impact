<%@ page session="true" contentType="text/html;charset=utf-8"
  autoFlush="true"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document title="Workflow Diagram">
    <f:facet name="metaContainer">
      <f:verbatim>
<!--         <META HTTP-EQUIV="refresh" CONTENT="60" /> -->
        <META HTTP-EQUIV="Pragma" CONTENT="no-cache" />
        <META HTTP-EQUIV="Expires" CONTENT="-1" />
        <META HTTP-EQUIV="Cache-Control"
          CONTENT="no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0" />
      </f:verbatim>
    </f:facet>

    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Workflow Diagram">
        <jsp:include flush="true" page="../util/header.jsp" />

        <mu:setProperty property="#{workFlow2DDraw.processId}"
          value="#{param.processId}" />
        <mu:setProperty property="#{workFlow2DDraw.command}"
          value="reload" />

        <afh:rowLayout halign="center">
          <t:graphicImage
            url="/util/imageView.jsf?beanName=workFlow2DDraw&time=#{workFlow2DDraw.time}"
            usemap="#workFlow2DDraw" border="0"
            width="#{workFlow2DDraw.width}"
            height="#{workFlow2DDraw.height}" />
          <mu:areaMap beanName="workFlow2DDraw" />
        </afh:rowLayout>

        <af:panelButtonBar>
          <af:commandButton text="Reload"
            action="#{workFlow2DDraw.reload}" />
        </af:panelButtonBar>

      </af:page>
    </af:form>
  </af:document>
</f:view>

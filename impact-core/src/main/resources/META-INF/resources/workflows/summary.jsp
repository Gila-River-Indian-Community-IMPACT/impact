<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Summary Charts">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}" title="Summary Charts">
        <jsp:include flush="true" page="../util/header.jsp"/>

        <afh:rowLayout halign="center">
          <af:panelGroup>
            <t:graphicImage url="/util/imageView.jsf?beanName=processStatusPie&time=#{processStatusPie.time}"
              usemap="#processStatusPie" border="0"
              width="#{processStatusPie.width}" height="#{processStatusPie.height}" />
            <mu:areaMap beanName="processStatusPie" />
          </af:panelGroup>
          <af:panelGroup>
            <t:graphicImage url="/util/imageView.jsf?beanName=reportingPie&time=#{reportingPie.time}"
              usemap="#reportingPie" border="0" width="#{processStatusPie.width}"
              height="#{processStatusPie.height}" />
            <mu:areaMap beanName="reportingPie" />
          </af:panelGroup>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>


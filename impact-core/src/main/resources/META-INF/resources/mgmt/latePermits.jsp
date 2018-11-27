<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Late Permits Report">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Late Permits Report">
        <%@ include file="../util/header.jsp"%>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="800">
            <af:panelBorder>
              <af:showDetailHeader text="Late Permits Report Filter"
                disclosed="true">
                <af:panelForm rows="1" width="800" maxColumns="4">

                  <af:selectManyListbox label="Districts :" size="6"
                    value="#{latePermits.selectedDoLaas}">
                    <f:selectItems value="#{latePermits.doLaas}" />
                  </af:selectManyListbox>

                  <af:selectManyListbox label="Types :" size="6"
                    value="#{latePermits.selectedPermitTypes}">
                    <f:selectItems value="#{latePermits.permitTypes}" />
                  </af:selectManyListbox>

                  <af:selectManyListbox label="Reasons :" size="6"
                    value="#{latePermits.selectedReasonCds}">
                    <f:selectItems value="#{latePermits.reasonCds}" />
                  </af:selectManyListbox>

                  <af:panelGroup layout="vertical">
                  <%-- General Permit not valid for WY
                  	<af:selectBooleanCheckbox 
                      value="#{latePermits.general}"
                      label="General Permit:"/> --%>
                    <af:selectBooleanCheckbox 
                      value="#{latePermits.express}"
                      label="Express Status:"/>
                    <af:selectBooleanCheckbox 
                      value="#{latePermits.showAll}"
                      label="All Status:"/>
                  </af:panelGroup>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{latePermits.submit}" />
                    <af:commandButton text="Reset"
                      action="#{latePermits.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <af:panelGroup layout="horizontal"
            rendered="#{latePermits.hasSearchResults}">
            <af:panelBorder>
              <afh:rowLayout halign="center">
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{latePermits.showChartResults}"
                    url="/util/imageView.jsf?beanName=latePermitsDolaaSummary&time=#{latePermitsDolaaSummary.time}"
                    usemap="#latePermitsDolaaSummary" border="0" />
                  <mu:areaMap beanName="latePermitsDolaaSummary" />
                </af:panelGroup>
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{latePermits.showChartResults}"
                    url="/util/imageView.jsf?beanName=latePermitsPermitTypeSummary&time=#{latePermitsPermitTypeSummary.time}"
                    usemap="#latePermitsPermitTypeSummary" border="0" />
                  <mu:areaMap beanName="latePermitsPermitTypeSummary" />
                </af:panelGroup>
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{latePermits.showChartResults}"
                    url="/util/imageView.jsf?beanName=latePermitsPermitStatusSummary&time=#{latePermitsPermitStatusSummary.time}"
                    usemap="#sopPermitStatusSummary" border="0" />
                  <mu:areaMap beanName="latePermitsPermitStatusSummary" />
                </af:panelGroup>
              </afh:rowLayout>
            </af:panelBorder>
          </af:panelGroup>
        </afh:rowLayout>

        <jsp:include flush="true" page="latePermitsData.jsp" />

      </af:page>
    </af:form>
  </af:document>
</f:view>

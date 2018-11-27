<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Permits Status Report">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Permits Status Report">
        <%@ include file="../util/header.jsp"%>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="800">
            <af:panelBorder>
              <af:showDetailHeader text="Permits Status Report Filter"
                disclosed="true">
                <af:panelForm rows="1" width="800" maxColumns="4" partialTriggers="permitReasonCds">

                  <af:selectManyListbox label="Districts :" size="6"
                    value="#{permitSOP.selectedDoLaas}">
                    <f:selectItems value="#{permitSOP.doLaas}" />
                  </af:selectManyListbox>
				
				  <af:panelHeader text="Display Only">
	                  <af:panelForm rows="1" maxColumns="2">
	                    <af:panelForm>
		                    <af:selectBooleanCheckbox 
		                      value="#{permitSOP.general}"
		                      label="General:"/>
		                    <af:selectBooleanCheckbox 
		                      value="#{permitSOP.express}"
		                      label="Express:"/>
		                    <af:selectBooleanCheckbox 
		                      value="#{permitSOP.backlogged}"
	                      	  shortDesc="Backlogged only applies to Renewal permits."
		                      label="Backlogged:" disabled="#{!permitSOP.backloggedEnabled}"/>
	                    </af:panelForm>
	                    <af:panelGroup layout="vertical">
		                    <af:selectOneRadio value="#{permitSOP.type}">
		                      <f:selectItems value="#{permitSOP.types}" />
		                    </af:selectOneRadio>
	                    </af:panelGroup>
	                  </af:panelForm>
                   </af:panelHeader>

                  <af:selectManyListbox label="Types :" size="6"
                    value="#{permitSOP.selectedPermitTypes}">
                    <f:selectItems value="#{permitSOP.permitTypes}" />
                  </af:selectManyListbox>
                  
                 <% /* 
                 	* The Dead-ended choice is redundant since this report already excludes
                 	* dead-ended permits, but we'll leave it here in case there is a change later on.
                 	*/ %>
				  <af:panelHeader text="Exclude">
	                  <af:panelForm maxColumns="1">
	                    <af:selectBooleanCheckbox 
	                      value="#{permitSOP.hideShutdownFacility}"
	                      label="Shutdown Facility:"/>
	                    <af:selectBooleanCheckbox 
	                      rendered="false"
	                      value="#{permitSOP.hideDeadEndedPermit}"
	                      label="Dead-ended Permit:"/>
	                    <af:selectBooleanCheckbox 
	                      value="#{permitSOP.hideShutDownInvalidEU}"
	                      label="Shutdown/Invalid EUs:"/>
	                  </af:panelForm>
                   </af:panelHeader>

                  <af:selectManyListbox label="Reasons :" size="6" id="permitReasonCds"
                    value="#{permitSOP.selectedReasonCds}" autoSubmit="true">
                    <f:selectItems value="#{permitSOP.reasonCds}" />
                  </af:selectManyListbox>
                  
				  <af:panelHeader text="Include Column">
	                  <af:panelGroup layout="vertical">
	                    <af:selectBooleanCheckbox 
	                      shortDesc="Enable show notes may slow down the search with large quantities result."
	                      value="#{permitSOP.showNotes}"
	                      label="Notes:"/>
	                    <af:selectBooleanCheckbox 
	                      shortDesc="Add the NAICS column to the report table."
	                      value="#{permitSOP.showNaics}"
	                      label="NAICS:"/>
	                  </af:panelGroup>
                   </af:panelHeader>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{permitSOP.submit}" />
                    <af:commandButton text="Reset"
                      action="#{permitSOP.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <af:panelGroup layout="horizontal"
            rendered="#{permitSOP.hasSearchResults}">
            <af:panelBorder>
              <afh:rowLayout halign="center">
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{permitSOP.showChartResults}"
                    url="/util/imageView.jsf?beanName=sopDolaaSummary&time=#{sopDolaaSummary.time}"
                    usemap="#sopDolaaSummary" border="0" />
                  <mu:areaMap beanName="sopDolaaSummary" />
                </af:panelGroup>
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{permitSOP.showChartResults}"
                    url="/util/imageView.jsf?beanName=sopPermitTypeSummary&time=#{sopPermitTypeSummary.time}"
                    usemap="#sopPermitTypeSummary" border="0" />
                  <mu:areaMap beanName="sopPermitTypeSummary" />
                </af:panelGroup>
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{permitSOP.showChartResults}"
                    url="/util/imageView.jsf?beanName=sopPermitStatusSummary&time=#{sopPermitStatusSummary.time}"
                    usemap="#sopPermitStatusSummary" border="0" />
                  <mu:areaMap beanName="sopPermitStatusSummary" />
                </af:panelGroup>
              </afh:rowLayout>
            </af:panelBorder>
          </af:panelGroup>
        </afh:rowLayout>

        <jsp:include flush="true" page="permitSOPData.jsp" />

      </af:page>
    </af:form>
  </af:document>
</f:view>

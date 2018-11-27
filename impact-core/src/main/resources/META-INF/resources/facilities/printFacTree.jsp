<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Printable Facility Tree">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:panelForm>

        <af:objectSpacer width="100%" height="15" />
		
		<%--
        <af:outputText value="Please use your browser print functionality to print the Facility Tree" />
        <af:objectSpacer width="100%" height="15" />
        --%>

        <t:tree2 id="facilityProfileTree" rendered="#{facilityProfile.facility != null}"
                    value="#{facilityProfile.printTreeData}" var="node" 
                    clientSideToggle="false">
                      <f:facet name="facility">
                        <h:panelGroup>
                            <t:graphicImage value="/images/facility.gif" 
                            	border="0" rendered="#{node.userObject.operatingStatusCd != 'sd'}" />
                            	<t:graphicImage value="/images/ShutdownFacility.gif" 
                            	border="0" rendered="#{node.userObject.operatingStatusCd == 'sd'}" />                            
                            	<t:outputText value="#{node.description}" />
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="moreNodes">
                        <h:panelGroup>
                            <t:graphicImage value="/images/vellipsis.gif" border="0" />
                            	<t:outputText value="#{node.description}" />
                        </h:panelGroup>
                      </f:facet>                      
                      <f:facet name="emissionUnits">
                        <h:panelGroup> 
                            <t:graphicImage value="#{facilityProfile.facility.permitClassCd == 'tv' &&
	                              !(empty node.userObject.tvClassCd) && 
	                              (node.userObject.tvClassCd == 'ins' || node.userObject.tvClassCd == 'trv' || node.userObject.tvClassCd == 'innr') ? 
	                              	((node.userObject.tvClassCd == 'ins' || node.userObject.tvClassCd == 'trv') ? 
	                              	(node.userObject.tvClassCd == 'ins' ? '/images/insignificantEU.gif' : '/images/trivialEU.gif') : 
	                              	'/images/insignificantEUNoAppReqs.gif')
	                              : '/images/EU.gif'}"
                            	rendered="#{node.userObject.operatingStatusCd != 'sd'}" border="0"/>
                            <t:graphicImage value="/images/ShutdownEU.gif" 
                            	rendered="#{node.userObject.operatingStatusCd == 'sd'}" border="0"/>                            
                            	<t:outputText value="#{node.description} #{node.userObject.facPrintEuDesc}" />
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="emissionProcesses">
                        <h:panelGroup> 
                            <t:graphicImage value="/images/process.gif" border="0" />
                            	<t:outputText value="#{node.description}" />
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="controlEquipment">
                        <h:panelGroup> 
                            <t:graphicImage value="/images/CE.gif" border="0" />
                            	<t:outputText value="#{node.description}" />
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="egressPoints">
                        <h:panelGroup>
                            <t:graphicImage value="/images/Stack.gif" border="0" />
                            	<t:outputText value="#{node.description}" />
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="InvalidEUs">
                        <h:panelGroup> 
                            <t:graphicImage value="/images/Caution.gif" 
                            	border="0" />
                            	<t:outputText value="#{node.description}" />
                        </h:panelGroup>
                      </f:facet>                      
                      <f:facet name="UnassignedCEs">
                        <h:panelGroup> 
                            <t:graphicImage value="/images/Caution.gif" 
                            	border="0" />
                            	<t:outputText value="#{node.description}" />
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="UnassignedEgrPnts">
                        <h:panelGroup> 
                            <t:graphicImage value="/images/Caution.gif" 
                            	border="0" />
                            	<t:outputText value="#{node.description}" />
                        </h:panelGroup>
                      </f:facet>
                    </t:tree2>

      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

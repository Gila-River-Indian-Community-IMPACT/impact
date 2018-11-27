<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

            <h:panelGrid border="1">
              <af:panelBorder>
              
                <f:facet name="top">
				  <f:subview id="facilityHeader">
				  	<jsp:include page="comFacilityHeader.jsp"/>
                  </f:subview>
                </f:facet>
                
                <f:facet name="left">
                  <t:div style="overflow:auto;width:250px;height:600px;">
                   <afh:rowLayout halign="center">
                     <af:commandButton text="Expand Facility Tree" action="#{facilityProfile.expandFacilityTree}" 
                      		rendered="#{facilityProfile.facility != null && !facilityProfile.expandTree}" />
                      <af:commandButton text="Collapse Facility Tree" action="#{facilityProfile.collaspeFacilityTree}" 
                      		rendered="#{facilityProfile.facility != null && facilityProfile.expandTree}" />
                    </afh:rowLayout>  	 		 
                    <t:tree2 id="facilityProfileTree" rendered="#{facilityProfile.facility != null}"
                    value="#{facilityProfile.treeData}" var="node" 
                    clientSideToggle="false">
                      <f:facet name="facility">
                        <h:panelGroup>
                          <af:commandMenuItem action="#{facilityProfile.nodeClickedByUser}"
                            onclick="if (#{facilityProfile.editable}) { alert('Please save or discard your changes'); return false; }" >
                            <t:graphicImage value="/images/facility.gif" 
                            border="0" title="Facility" rendered="#{node.userObject.operatingStatusCd != 'sd'}" />
                            <t:graphicImage value="/images/ShutdownFacility.gif" 
                            border="0" title="Facility" rendered="#{node.userObject.operatingStatusCd == 'sd'}" />                            
                            <t:outputText value="#{node.description}" 
                            style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}"
                            title="Facility" />
                            <t:updateActionListener 
                            property="#{facilityProfile.selectedTreeNode}" 
                            value="#{node}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.current}" 
                            value="#{node.identifier}" />
                          </af:commandMenuItem>
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="moreNodes">
                        <h:panelGroup>
                          <af:commandMenuItem action="#{facilityProfile.nodeClickedByUser}" 
                            onclick="if (#{facilityProfile.editable}) { alert('Please save or discard your changes'); return false; }" >
                            <t:graphicImage value="/images/vellipsis.gif" border="0" 
                            title="Collapsed Emissions Units, click to expand"/>
                            <t:outputText value="#{node.description}" 
                            title="Collapsed Emissions Units, click to expand"
                            style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.selectedTreeNode}" 
                            value="#{node}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.current}" 
                            value="#{node.identifier}" />
                          </af:commandMenuItem>
                        </h:panelGroup>
                      </f:facet>                      
                      <f:facet name="emissionUnits">
                        <h:panelGroup> 
                          <af:commandMenuItem action="#{facilityProfile.nodeClickedByUser}" 
                            onclick="if (#{facilityProfile.editable}) { alert('Please save or discard your changes'); return false; }" >
                            <t:graphicImage value="#{facilityProfile.facility.permitClassCd == 'tv' &&
                              !(empty node.userObject.tvClassCd) && 
                              (node.userObject.tvClassCd == 'ins' || node.userObject.tvClassCd == 'trv' || node.userObject.tvClassCd == 'innr') ? 
                              	((node.userObject.tvClassCd == 'ins' || node.userObject.tvClassCd == 'trv') ? 
                              	(node.userObject.tvClassCd == 'ins' ? '/images/insignificantEU.gif' : '/images/trivialEU.gif') : 
                              	'/images/insignificantEUNoAppReqs.gif')
                              : '/images/EU.gif'}"
                            rendered="#{node.userObject.operatingStatusCd != 'sd'}"
                            border="0" title="#{node.userObject.mouseOverTip}"/>
                            <t:graphicImage value="/images/ShutdownEU.gif"
                            rendered="#{node.userObject.operatingStatusCd == 'sd'}"
                            border="0" title="#{node.userObject.mouseOverTip}"/>                            
                            <t:outputText value="#{node.description}" 
                            style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}"
                            title="#{node.userObject.mouseOverTip}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.selectedTreeNode}" 
                            value="#{node}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.current}" 
                            value="#{node.identifier}" />
                          </af:commandMenuItem>
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="emissionProcesses">
                        <h:panelGroup> 
                          <af:commandMenuItem action="#{facilityProfile.nodeClickedByUser}" 
                            onclick="if (#{facilityProfile.editable}) { alert('Please save or discard your changes'); return false; }" >
                            <t:graphicImage value="/images/process.gif" 
                            border="0" title="#{node.userObject.mouseOverTip}"/>
                            <t:outputText value="#{node.description}" 
                            style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}"
                            title="#{node.userObject.mouseOverTip}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.selectedTreeNode}" 
                            value="#{node}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.current}" 
                            value="#{node.identifier}" />
                          </af:commandMenuItem>
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="controlEquipment">
                        <h:panelGroup> 
                          <af:commandMenuItem action="#{facilityProfile.nodeClickedByUser}" 
                            onclick="if (#{facilityProfile.editable}) { alert('Please save or discard your changes'); return false; }" >
                            <t:graphicImage value="/images/CE.gif" 
                            border="0" title="#{node.userObject.mouseOverTip}"/>
                            <t:outputText value="#{node.description}" 
                            title="#{node.userObject.mouseOverTip}"
                            style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.selectedTreeNode}" 
                            value="#{node}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.current}" 
                            value="#{node.identifier}" />
                          </af:commandMenuItem>
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="egressPoints">
                        <h:panelGroup>
                          <af:commandMenuItem action="#{facilityProfile.nodeClickedByUser}" 
                            onclick="if (#{facilityProfile.editable}) { alert('Please save or discard your changes'); return false; }" >
                            <t:graphicImage value="/images/Stack.gif" border="0" 
                            title="#{node.userObject.mouseOverTip}"/>
                            <t:outputText value="#{node.description}" 
                            title="#{node.userObject.mouseOverTip}"
                            style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.selectedTreeNode}" 
                            value="#{node}" />
                            <t:updateActionListener 
                            property="#{facilityProfile.current}" 
                            value="#{node.identifier}" />
                          </af:commandMenuItem>
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="InvalidEUs">
                        <h:panelGroup> 
                            <t:graphicImage value="/images/Caution.gif" 
                            	border="0" title="Invalid Emission Units"/>
                            <t:outputText value="#{node.description}"  
                            	style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}"/>
                        </h:panelGroup>
                      </f:facet>   
                      <f:facet name="ShutdownEUs">
                        <h:panelGroup> 
                            <t:graphicImage value="/images/Caution.gif" 
                              border="0" title="Shutdown Emission Units"/>
                            <t:outputText value="#{node.description}"  
                              style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}"/>
                        </h:panelGroup>
                      </f:facet>                   
                      <f:facet name="UnassignedCEs">
                        <h:panelGroup> 
                            <t:graphicImage value="/images/Caution.gif" 
                            border="0" title="Unassigned Control Equipments"/>
                            <t:outputText value="#{node.description}" 
                            style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}"/>
                        </h:panelGroup>
                      </f:facet>
                      <f:facet name="UnassignedEgrPnts">
                        <h:panelGroup> 
                            <t:graphicImage value="/images/Caution.gif" 
                            border="0" title="Unassigned Release Points"/>
                            <t:outputText value="#{node.description}" 
                            style="#{facilityProfile.current == node.identifier ? 'color:#FFFFFF; background-color:#000000;' : 'font-size:14px;'}"/>
                        </h:panelGroup>
                      </f:facet>
                    </t:tree2>
                  </t:div>
                </f:facet>

				<f:facet name="right">
				<h:panelGrid columns="1" border="1" width="750" rendered="#{facilityProfile.facility != null}">
				                                
                  <f:subview id="facility">
                    <jsp:include flush="true" page="../facilities/comFacility.jsp"/>
                  </f:subview>
                                
                  <f:subview id="emissionUnit">
                    <jsp:include flush="true" page="../facilities/comEmissionUnit.jsp"/>
                  </f:subview>
 
                  <f:subview id="emissionProc">
                     <jsp:include flush="true" page="../facilities/comEmissionProc.jsp"/>
                  </f:subview>
                
                  <f:subview id="cntEquip">
                     <jsp:include flush="true" page="../facilities/comCntEquip.jsp"/>
                  </f:subview> 
                 
                  <f:subview id="egressPoint">
                     <jsp:include flush="true" page="../facilities/comEgressPoint.jsp"/>
                  </f:subview>
                </h:panelGrid>
                </f:facet>

                <f:facet name="bottom">
                </f:facet>

            </af:panelBorder>
          </h:panelGrid>        
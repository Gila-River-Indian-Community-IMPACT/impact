<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
 
  <af:panelGroup layout="vertical" 
                   rendered="true" >
                  <af:showDetailHeader text="Identification of Intermittent Compliance (IC)" disclosed="true">
					<af:outputText inlineStyle="font-size:75%;color:#666" value="Intermittent compliance may be identified through either the following table or by attaching the information below. When attaching a document in lieu of using the table below, the document must meet the required format and content requirements for annual certifications. You can download a 'Title V Compliance Certification' form that meets these requirements along with instructions and examples from the system's "></af:outputText>
					<af:goLink text="reference page. " 
                   				rendered="true"
                   				targetFrame="_new"
                            	destination="../util/externalReferences.jsf" />
                    <af:outputText inlineStyle="font-size:75%;color:#666" value="Except as indicated in this section, the Material Information section below, or any attachments submitted in lieu of using these sections, submittal of this report shall indicate all emissions units subject to one or more applicable requirements operated in continuous compliance with all federally enforceable permit terms and conditions throughout the reporting period identified above."></af:outputText>
                    <af:table value="#{complianceReport.complianceReport.deviationReports}"
                      bandingInterval="1" banding="row" width="100%" var="report">

                	  <af:column 
                        sortable="true" noWrap="true"  
                        formatType="text" headerNoWrap="true" headerText="IC ID">
                        <af:commandLink  useWindow="true" rendered="true" windowWidth="810" windowHeight="480"
                          action="#{complianceReport.startEditDeviation}"
                          disabled="#{complianceReport.complianceReport.reportStatus=='sbmt' && !complianceReport.admin}"
                          text="#{report.deviationId}"> 
                          <t:updateActionListener property="#{complianceReport.complianceDeviation}"
                            value="#{report}" />                           
                        </af:commandLink>
                      </af:column>
                      			
                      <af:column sortable="false" 
                        formatType="text" headerText="EU ID">
                        <af:outputText value="#{report.identifier}" />
                      </af:column>
                      
                      <af:column sortable="false" 
    				    rendered="#{complianceReport.complianceReport.reportType =='tvcc'}"
                        formatType="text" headerText="Emission Limitation/Control Measure or Permit Term No.">
                        <af:outputText value="#{report.perDescription}" />
                      </af:column>
                      
                      <af:column sortable="false" width="40%"
                        formatType="text" headerText="Compliance Method">
                        <af:outputText shortDesc="Provide a brief description of the deviation or exceedance. If visible emissions were observed during a required observation, please note if the color of the visible emissions were normal of representative operations." 
                        value="#{report.tvccComplianceMethod}" />
                      </af:column>
                      
                      <af:column headerText="Excursions/Deviations (Fill in ONLY ONE of the following fields but NOT BOTH):" bandingShade="light">
                      <af:column width="10%" 
                        sortable="true" formatType="text" headerText="Report Date of Those Documented Within Excursion/Deviation Reports Submitted to DO/LAA:">
                        <af:selectInputDate readOnly="true" value="#{report.startDate}" />
                      </af:column>
                      
                     <af:column sortable="false" width="40%" 
                        formatType="text" headerText="Explain the Date, Nature, Duration, and Probable Cause of the Excursion/Deviation, as well as any Corrective Action Taken">
                        <af:outputText shortDesc="Description of the corrective action(s) taken to resolve the deviation, exceedance or visible emission. Examples of corrective actions include changing a fabric filter, performing maintenance on equipment, and retraining of staff, etc.  Please note that not all deviations or exceedances require a corrective action to be taken." 
                        value="#{report.tvccExcursionsSubmitted}" />
                      </af:column>
                      </af:column>
                      <af:column sortable="false" 
                      rendered="#{complianceReport.complianceReport.reportType =='per'}" 
                        formatType="text" headerText="Other">
                        <af:outputText  value="#{report.controlPermit}" />
                      </af:column>
                      
                      <f:facet name="footer">
                       <afh:rowLayout halign="center">
                        <af:panelButtonBar>     
                         <af:commandButton text="New Deviation" useWindow="true"
                				windowWidth="810" windowHeight="480" 
                				disabled="#{complianceReport.locked || !complianceReport.editMode}"
                				rendered="#{(complianceReport.complianceReport.reportStatus=='drft' && !complianceReport.internalApp) || (complianceReport.internalApp && complianceReport.admin)}" 
                				action="#{complianceReport.startNewDeviation}" /> 
                  			  <af:commandButton actionListener="#{tableExporter.printTable}"
                    			onclick="#{tableExporter.onClickScript}" text="Printable view" />
                  			  <af:commandButton actionListener="#{tableExporter.excelTable}"
                    			onclick="#{tableExporter.onClickScript}" text="Export to excel" />                                                               			                        
                        </af:panelButtonBar>
                       </afh:rowLayout>
                     </f:facet>                        
                    </af:table>

                  </af:showDetailHeader>
   
  </af:panelGroup>
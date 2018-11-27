<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
 
  <af:panelGroup layout="vertical" 
                   rendered="true" >
                  <af:showDetailHeader text="Deviation, Exceedance, or Visible Emissions Detail" disclosed="true">
					<af:outputText inlineStyle="font-size:75%;color:#666" 
					value="Additional information is required for each deviation or exceedance that 
					prompted a 'yes' answer in the Detailed EU Information table above and for any visible 
					emissions (VE) incident that occurred during the reporting period. This information may 
					be identified through either completing a row for each occurrence or by adding a row 
					and indicating the required information is included in an attachment. When attaching a 
					document in lieu of using the table below, the document must meet the content 
					requirements identified by the columns in this table." />
                    <af:table value="#{complianceReport.complianceReport.deviationReports}"
                      bandingInterval="1" banding="row" width="100%" var="report">

                	  <af:column 
                        sortable="true" noWrap="true"  
                        formatType="text" headerNoWrap="false" headerText="Dev/Exc/VE ID">
                        <af:commandLink  useWindow="true" rendered="true" windowWidth="1000" windowHeight="400"
                          action="#{complianceReport.startEditDeviation}"
                          disabled="#{complianceReport.complianceReport.reportStatus=='sbmt'}"
                          text="#{report.deviationId}"> 
                          <t:updateActionListener property="#{complianceReport.complianceDeviation}"
                            value="#{report}" />                           
                        </af:commandLink>
                      </af:column>
                      			
                      <af:column sortable="false" 
                        formatType="text" headerText="AQD EU ID">
                        <af:outputText shortDesc="The “Identifier” is either the emissions unit ID, control equipment ID or appropriate description." value="#{report.identifier}" />
                      </af:column>
                      
                      <af:column 
                        sortable="true" formatType="text" headerText="Start Date">
                        <af:selectInputDate readOnly="true" value="#{report.startDate}" />
                      </af:column>
                      
                      <af:column 
                        sortable="true" formatType="text" headerText="End Date">
                        <af:selectInputDate readOnly="true" value="#{report.endDate}" />
                      </af:column>
                      
                      <af:column sortable="false" 
                        formatType="text" headerText="Duration">
                        <af:outputText shortDesc="Describe the duration of the deviation or exceedance." 
                        value="#{report.perDescription}" />
                      </af:column>
                      
                      <af:column sortable="false" 
                        formatType="text" headerText="Description of Deviation or Exceedance and Probable Cause.">
                        <af:outputText shortDesc="Provide a description of the probable cause of the deviation, exceedance or visible emission." 
                        value="#{report.perProbableCause}" />
                      </af:column>
                      
                     <af:column sortable="false" 
                        formatType="text" headerText="Description of Corrective Actions. If none, describe why not.">
                        <af:outputText shortDesc="Description of the corrective action(s) taken to resolve the deviation, exceedance or visible emission. Examples of corrective actions include changing a fabric filter, performing maintenance on equipment, and retraining of staff, etc.  Please note that not all deviations or exceedances require a corrective action to be taken." 
                        value="#{report.perCorrectiveAction}" />
                      </af:column>
                      
                      <f:facet name="footer">
                       <afh:rowLayout halign="center">
                        <af:panelButtonBar>     
                         <af:commandButton text="New Deviation" useWindow="true"
                				windowWidth="1000" windowHeight="400" 
                				rendered="#{(complianceReport.complianceReport.reportStatus=='drft' && complianceReport.staging) || !complianceReport.staging}" 
                				disabled="#{complianceReport.locked || !complianceReport.editMode}" 
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
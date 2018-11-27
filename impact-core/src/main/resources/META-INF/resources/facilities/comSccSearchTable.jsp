<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="SCC Codes Search">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages/>
                      
      <af:panelForm>
      		
		<af:table value="#{facilityProfile.sccCodes}" bandingInterval="1"
			id="SccTab" rows="20"
			rendered="#{facilityProfile.editable && facilityProfile.selectSccMethod == 'searchSCC'}"
			binding="#{facilityProfile.sccCodesTable}" banding="row" var="scc"
			width="98%">
			<f:facet name="selection">
				<af:tableSelectOne />
			</f:facet>
			<af:column sortProperty="sccId" sortable="true" formatType="icon"
				noWrap="true" headerText="SCC Code">
				<af:outputText value="#{scc.sccId}" />
			</af:column>
			<af:column sortProperty="sccLevel1Desc" sortable="true"
				formatType="icon" headerText="Level 1 Description">
				<af:outputText value="#{scc.sccLevel1Desc}" />
			</af:column>
			<af:column sortProperty="sccLevel2Desc" sortable="true"
				formatType="icon" headerText="Level 2 Description">
				<af:outputText value="#{scc.sccLevel2Desc}" />
			</af:column>
			<af:column sortProperty="sccLevel3Desc" sortable="true"
				formatType="icon" headerText="Level 3 Description">
				<af:outputText value="#{scc.sccLevel3Desc}" />
			</af:column>
			<af:column sortProperty="sccLevel4Desc" sortable="true"
				formatType="icon" headerText="Level 4 Description">
				<af:outputText value="#{scc.sccLevel4Desc}" />
			</af:column>
			<f:facet name="footer">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton actionListener="#{tableExporter.printTable}"
							onclick="#{tableExporter.onClickScript}" text="Printable view" />
						<af:commandButton actionListener="#{tableExporter.excelTable}"
							onclick="#{tableExporter.onClickScript}" text="Export to excel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>
		</af:table>
		<af:objectSpacer height="20" /> 
		<afh:rowLayout halign="center">     
            <af:panelButtonBar>
              <af:commandButton text="Apply Selected SCC"
                                action="#{facilityProfile.applySelectedSccCode}"/>
              <af:commandButton text="Cancel"
                                immediate="true"
                                action="#{facilityProfile.cancelSelectSccCode}"/>
            </af:panelButtonBar>
        </afh:rowLayout>           
	 </af:panelForm>
    </af:form>
  </af:document>
</f:view>  
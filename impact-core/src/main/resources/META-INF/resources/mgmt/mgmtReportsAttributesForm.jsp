<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical">
  <af:panelHeader text="Report Definition" />
  <af:panelForm>
    <af:inputText label="Report Name: "
      value="#{reportTree.report.name}" showRequired="true"
      readOnly="#{!(reportTree.addingReport || reportTree.editingReport)}"
      rendered="#{reportTree.addingReport || reportTree.selectedTreeNode.type == 'report'}" />
    <af:inputText label="Group Name: "
      value="#{reportTree.report.groupNm}" showRequired="true"
      readOnly="#{!(reportTree.addingReport || reportTree.editingReport)}"
      rendered="#{reportTree.addingReport || reportTree.selectedTreeNode.type == 'report'}" />

	    <af:inputText label="Report File: " showRequired="true"
	      value="#{reportTree.report.reportFileName}"
	      readOnly="true"
	      rendered="#{!(reportTree.addingReport) && reportTree.selectedTreeNode.type == 'report'}" />

         <af:inputFile id="publicFile" showRequired="#{reportTree.addingReport}"
          label="#{reportTree.editingReport? '' : 'Report File:' }"
    	  rendered="#{reportTree.addingReport || reportTree.editingReport}"
          value="#{reportTree.report.reportFile}"/>



      
      
      
    <af:panelHeader text="Report Attributes : "
      rendered="false" />
    <af:panelForm>
      <af:table value="#{reportTree.report.attributes}"
        bandingInterval="1" banding="row" var="attribute" width="80%"
        rendered="#{reportTree.addingReport || reportTree.editingReport}">
        <f:facet name="selection">
          <af:tableSelectMany
            disabled="#{!reportTree.addingReport && !reportTree.editingReport}" />
        </f:facet>
        <af:column sortProperty="type" sortable="true" id="type"
          formatType="text" headerText="Variable Type">
          <af:selectOneChoice label="Variable Type"
            readOnly="#{!reportTree.addingReport && !reportTree.editingReport}"
            value="#{attribute.type}">
            <f:selectItems value="#{reportTree.attributeTypes}" />
          </af:selectOneChoice>
        </af:column>
        <af:column sortProperty="code" sortable="true" id="code"
          formatType="text" headerText="Data Element">
          <af:inputText columns="20" required="true"
            readOnly="#{!reportTree.addingReport && !reportTree.editingReport}"
            value="#{attribute.code}" />
        </af:column>
        <af:column sortProperty="description" sortable="true"
          formatType="text" headerText="Label">
          <af:inputText columns="40" required="true"
            readOnly="#{!reportTree.addingReport && !reportTree.editingReport}"
            value="#{attribute.description}" />
        </af:column>
        <af:column sortProperty="value" sortable="true"
          formatType="text" headerText="Default Value">
          <af:inputText columns="10"
            readOnly="#{!reportTree.addingReport && !reportTree.editingReport}"
            value="#{attribute.value}" />
        </af:column>
        <f:facet name="footer">
          <afh:rowLayout halign="center">
            <af:panelButtonBar>
              <af:commandButton text="Add Attribute"
                rendered="#{(reportTree.addingReport || reportTree.editingReport)}"
                actionListener="#{reportTree.initActionTable}"
                action="#{reportTree.addActionTableRow}">
                <t:updateActionListener
                  property="#{reportTree.actionTableNewObject}"
                  value="#{reportTree.newReportAttributeObject}" />
              </af:commandButton>
              <af:commandButton text="Delete Selected Attributes"
                actionListener="#{reportTree.initActionTable}"
                action="#{reportTree.deleteActionTableRows}"
                rendered="#{(reportTree.addingReport || reportTree.editingReport)}">
              </af:commandButton>
              <af:commandButton
                actionListener="#{tableExporter.printTable}"
                onclick="#{tableExporter.onClickScript}"
                text="Printable view" />
              <af:commandButton
                actionListener="#{tableExporter.excelTable}"
                onclick="#{tableExporter.onClickScript}"
                text="Export to excel" />
            </af:panelButtonBar>
          </afh:rowLayout>
        </f:facet>
      </af:table>
    </af:panelForm>
  </af:panelForm>
  
  <afh:rowLayout halign="center">
    <af:panelButtonBar rendered="#{!reportTree.readOnlyUser}">
      <af:commandButton
        rendered="#{reportTree.reportEditor && !reportTree.editingReport && reportTree.selectedTreeNode.type == 'report'}"
        id="delReport" text="Delete Report"
        action="#{confirmWindow.confirm}" useWindow="true"
        windowWidth="#{confirmWindow.width}"
        windowHeight="#{confirmWindow.height}">
        <t:updateActionListener property="#{confirmWindow.type}"
          value="#{confirmWindow.yesNo}" />
        <t:updateActionListener value="reportTree.deleteReport"
          property="#{confirmWindow.method}" />
      </af:commandButton>
      <af:commandButton text="Edit Report" immediate="true"
        action="#{reportTree.editReport}"
        rendered="#{reportTree.reportEditor && !reportTree.editingReport && reportTree.selectedTreeNode.type == 'report'}" />
      <af:commandButton text="Clone Report"
        action="#{reportTree.cloneReport}"
        rendered="#{reportTree.reportEditor && !reportTree.editingReport && reportTree.selectedTreeNode.type == 'report'}" />
      <af:commandButton text="Add Report"
        action="#{reportTree.addReport}"
        rendered="#{reportTree.reportEditor && !reportTree.addingReport && reportTree.selectedTreeNode.type != 'report'}" />
      <af:commandButton text="Save" action="#{reportTree.saveReport}"
        rendered="#{reportTree.reportEditor && reportTree.addingReport || reportTree.editingReport}" />
      <af:commandButton text="Cancel" immediate="true"
        action="#{reportTree.reset}"
        rendered="#{(reportTree.reportEditor && reportTree.addingReport || reportTree.editingReport)}" />
    </af:panelButtonBar>
  </afh:rowLayout>

</af:panelGroup>

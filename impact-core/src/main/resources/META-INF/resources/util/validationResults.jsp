<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Validation results">
    <f:verbatim>
      <script>
      var parentWindow = window.opener;
      var referenceClientID = 'v:0:ValidationDlgReference';
      var editModeClientID = 'v:0:ValidationDlgEditMode';
      var actionClientID = 'v:0:ValidationDlgAction';
      var showDetailClientIDClientID = 'v:0:ValidationDlgShowDetailClientID';
      var quotes = "'";
      var time = 0;
      var waitingTime = 30;
      
      function setFocus(reference, clientID, showDetailClientID)
      {
          if (parentWindow.document.getElementById(referenceClientID) != null && parentWindow.document.getElementById(referenceClientID).value == reference)
          {
              /*
               * If the element to set focus to is not rendered, it's probably
               * inside an ADF showDetail that's currently undisclosed. Refresh
               * the parent window with a disclosed ADF showDetail.
               */
              if (parentWindow.document.getElementById(clientID) == null)
              {
                  setFocusAfterAction(reference, clientID, showDetailClientID);
              }
              else
              {
                  setFocusToElement(clientID);
              }
          }
          else if (parentWindow.document.getElementById(editModeClientID) != null && parentWindow.document.getElementById(editModeClientID).value == 'true')
          {
              alert("Please save or discard your changes");
          }
          else
          {
             /*
              * The parent window is currently displaying a different node. Load
              * the node into the parent window.
              */
              setFocusAfterAction(reference, clientID, showDetailClientID);
          }
          // alert("Check main window.");
      }
      
      function setFocusAfterAction(reference, clientID, showDetailClientID)
      {
          var actionElement = parentWindow.document.getElementById(actionClientID);
          var referenceElement = parentWindow.document.getElementById(
            referenceClientID);
          var showDetailClientIDElement = parentWindow.document.getElementById(
            showDetailClientIDClientID);
         
          referenceElement.value = reference;
          showDetailClientIDElement.value = showDetailClientID;
          parentWindow.setTimeout(actionElement.onclick, 0);
          startTimer(clientID);
      }
      
      function startTimer(clientID)
      {
          var timeoutHandlerCode = 'timerHandler(' + quotes + clientID +
            quotes + ')';
          setTimeout(timeoutHandlerCode, 500);
      }

      function timerHandler(clientID)
      {
          if (setFocusToElement(clientID))
          {
              time = time + 1;
              if (time <= waitingTime)
              {
                  startTimer(clientID);
              }
              else
              {
                  alert("Close validation window and re-validate again. Something wrong with client ID: " + clientIDElement.value);
              }
          }
      }
      
      function setFocusToElement(clientID)
      {
          if (clientID == '')
          {
              parentWindow.focus();
          }
          else
          {
              var focusElement = parentWindow.document.getElementById(clientID);   
              if (focusElement == null)
              {
                  return(true);
              }

              /*
               * If we're dealing with an input element that's not disabled, call
               * focus() method on it. Else, just bring into view by
               * calling scrollIntoView()
               */
              if ((focusElement.tagName == 'INPUT' || focusElement.tagName == 'input' ||
                focusElement.tagName == 'SELECT' || focusElement.tagName == 'select' ||
                focusElement.tagName == 'TEXTAREA' || focusElement.tagName == 'textarea') &&
                !focusElement.disabled)
              {
                  focusElement.focus();
              }
              else
              {
                  focusElement.scrollIntoView(true);  
              }
          }
      }
    </script>
    </f:verbatim>
    <af:form>
      <af:panelGroup layout="vertical">
        <af:table value="#{validationMsgs}" var="msg" rows="15">
        
          <af:column headerText="Severity" sortable="true" sortProperty="severity">
            <af:outputText value="#{msg.severity}" />
          </af:column>
          
          <af:column headerText="EU ID" sortable="true" sortProperty="euId">
            <af:outputText value="#{msg.euId}" />
          </af:column>
          
          <af:column headerText="Message" sortable="true" sortProperty="message">
            <af:commandLink text="#{msg.message}" disabled="#{msg.referenceID == null || disableLinks}"
              rendered = "#{permitDetail != null}"	
              onclick="setFocus('#{msg.referenceID}', '#{msg.clientID}', '#{msg.showDetailClientID}')" actionListener = "#{permitDetail.loadPermit}">
              <f:param name="permitID" value="#{msg.permitId}" />
            </af:commandLink>
            <af:commandLink text="#{msg.message}" disabled="#{msg.referenceID == null}"
              rendered = "#{permitDetail == null}"
              onclick="setFocus('#{msg.referenceID}', '#{msg.clientID}', '#{msg.showDetailClientID}')">
             </af:commandLink>
          </af:column>
          
          <f:facet name="footer">
            <afh:rowLayout halign="center">
              <af:panelButtonBar>
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
        <af:objectSpacer height="10" />
        <afh:rowLayout halign="center">
        	<af:commandButton text="Close" 
        	  actionListener="#{tableExporter.removeCloseValidationDialogSession}" 
        	  onclick="window.close()" />
        </afh:rowLayout>
      </af:panelGroup>
    </af:form>
  </af:document>
</f:view>

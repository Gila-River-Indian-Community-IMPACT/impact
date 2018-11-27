<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<f:view>
      <af:document id="body" title="Delete permit">
            <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form >
          <af:panelPage title="Delete Permit">
            <f:facet name="branding">
              <af:objectImage source="/images/stars2.png" />
            </f:facet>

            <af:panelForm>          
                  <af:inputText label="Permit Number"
                        value="#{permitProfile.permit.permitNumber}?"
                        disabled="true"/>
                  <f:facet name="footer">
                    <af:panelGroup layout="horizontal">
                      <af:commandButton text="Delete permit"
                        action="#{permitProfile.removePermit}"/>
                      <af:commandButton text="Cancel"
                        action="cancelRemovePermit"/>
                    </af:panelGroup>
                  </f:facet>
            </af:panelForm>

          </af:panelPage>
        </af:form>
      </af:document>
</f:view>


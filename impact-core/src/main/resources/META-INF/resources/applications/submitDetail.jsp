<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Application Submission">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page>
        <af:messages />

        <af:panelForm>
          <af:objectSpacer width="100%" height="15" />
          <af:switcher defaultFacet="submitAllowed"
            facetName="#{applicationDetail.submitAllowed ? 'submitAllowed' : 'submitNotAllowed'}">
            <f:facet name="submitAllowed">
              <af:switcher defaultFacet="appNotCorrected"
                facetName="#{applicationDetail.application.applicationCorrected ? 'appCorrected' : 'appNotCorrected' }">
                <f:facet name="appNotCorrected">
                  <af:switcher defaultFacet="withPermit"
                    facetName="#{applicationDetail.pbrRprRpe ? 'notWithPermit' : 'withPermit' }">
                    <f:facet name="withPermit">
                      <af:panelGroup layout="vertical">
                        <af:outputText
                          value="You have requested to submit the application.
                    This will generate a corresponding permit/workflow instance for this application." />
                        <af:outputText
                          value="Click Yes to proceed or No to cancel." />
                      </af:panelGroup>
                    </f:facet>
                    <f:facet name="notWithPermit">
                      <af:panelGroup layout="vertical">
                        <af:outputText
                        value="You have requested to submit the notification/request.
                    This will generate a corresponding workflow instance for this notification/request." />
                        <af:outputText
                          value="Click Yes to proceed or No to cancel." />
                      </af:panelGroup>
                    </f:facet>
                  </af:switcher>
                </f:facet>
                <f:facet name="appCorrected">
                  <af:switcher defaultFacet="corrected"
                    facetName="#{applicationDetail.application.applicationAmended ? 'amended' : 'corrected' }">
                    <f:facet name="corrected">
                      <af:outputText
                        value="You have requested to submit a correction to an application.
                        This will generate a To Do list item as a reminder to update
                        the corresponding permit(s), but will not generate a permit/workflow instance.
                        Click Yes to proceed or No to cancel." />
                    </f:facet>
                    <f:facet name="amended">
                      <af:outputText
                        value="You have requested to submit an amendment to an application.
                        This will generate a To Do list item as a reminder to update
                        the corresponding permit(s), but will not generate a permit/workflow instance.
                        Click Yes to proceed or No to cancel." />
                    </f:facet>
                  </af:switcher>
                </f:facet>
              </af:switcher>
            </f:facet>
            <f:facet name="submitNotAllowed">
              <af:outputText
                value="You have requested to generate a corresponding 
                permit/workflow instance for this application. 
                One such instance was generated when the application was submitted. 
                Use this capability only in cases where you are splitting this 
                application into multiple permits (not applicable to Title V permits), 
                or the original permit instance was mistakenly closed. 
                Click Yes to proceed or No to cancel." />
            </f:facet>
          </af:switcher>
          <f:facet name="footer">
            <af:panelButtonBar>
              <af:switcher defaultFacet="internal"
                facetName="#{applicationDetail.internalApp ? 'internal' : 'external'}">
                <f:facet name="internal">
                  <af:commandButton text="Yes"
                    actionListener="#{applicationDetail.applySubmit}" />
                </f:facet>
                <f:facet name="external">
                  <af:commandButton text="Yes"
                    action="#{applicationDetail.applySubmitFromPortal}" />
                </f:facet>
              </af:switcher>
              <af:commandButton text="No" immediate="true"
                action="#{applicationDetail.cancelSubmit}" />
            </af:panelButtonBar>
          </f:facet>
        </af:panelForm>
      </af:page>
    </af:form>
  </af:document>
</f:view>


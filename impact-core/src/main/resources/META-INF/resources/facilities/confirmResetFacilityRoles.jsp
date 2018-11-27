<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Confirm Reset Facility Roles">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page>
        <afh:rowLayout halign="center">
          <h:panelGrid>
            <af:panelForm>
              <af:outputFormatted value="The facility is going to be reset the workflow roles. Would you like to continue?" />
              <af:panelButtonBar>
                <af:commandButton text="Yes"
                  actionListener="#{facilityProfile.resetFacilityRoles}">
                </af:commandButton>
                <af:commandButton text="No"
                  actionListener="#{facilityProfile.noConfirm}">      
                </af:commandButton>
              </af:panelButtonBar>
            </af:panelForm>
          </h:panelGrid>
        </afh:rowLayout>
      </af:page>
    </af:form>
  </af:document>
</f:view>
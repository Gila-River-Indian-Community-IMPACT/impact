<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Role Administration">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page>
        <afh:rowLayout halign="center">
          <h:panelGrid>
            <af:panelForm>
              <af:outputFormatted
                value="Are you sure you want to delete role: #{roleTree.role.securityGroupName}" />
              <af:panelButtonBar>
                <af:commandButton text="Yes"
                  action="#{roleTree.deleteRole}">
                  <af:returnActionListener />
                </af:commandButton>
                <af:commandButton text="No" action="#{roleTree.reset}">
                  <af:returnActionListener />
                </af:commandButton>
              </af:panelButtonBar>
            </af:panelForm>
          </h:panelGrid>
        </afh:rowLayout>
      </af:page>
    </af:form>
  </af:document>
</f:view>


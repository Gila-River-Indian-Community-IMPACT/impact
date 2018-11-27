<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Facility Roles Bulk Update">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <afh:rowLayout halign="center">
          <af:table value="#{facilityBulk.facilityRoles}"
            bandingInterval="1" banding="row" var="facRole"
            rows="#{facilityBulk.pageLimit}">
            <af:column headerText="Facility Roles">
              <af:outputText value="#{facRole.facilityRoleDsc}" />
            </af:column>
            <af:column headerText="Staff">
              <af:selectOneChoice value="#{facRole.userId}">
                <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
              </af:selectOneChoice>
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
        </afh:rowLayout>
      </af:panelForm>
      <af:panelForm>
        <af:objectSpacer width="100%" height="15" />
        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton text="Apply"
              actionListener="#{facilityBulk.applyUpdateFacilityRoles}" />
            <af:commandButton text="Cancel" immediate="true"
              actionListener="#{facilityBulk.cancelUpdateFacilityRoles}" />
          </af:panelButtonBar>
        </afh:rowLayout>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

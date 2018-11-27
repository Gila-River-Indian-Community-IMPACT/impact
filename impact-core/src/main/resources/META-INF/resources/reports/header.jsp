<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<%@ include file="../util/branding.jsp"%>
<f:facet name="nodeStamp">
  <af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
    type="#{foo.type}" disabled="#{foo.disabled}"
    rendered="#{foo.rendered}" icon="#{foo.icon}"
	onclick="if (#{reportDetail.disallowClick}) { alert('Please save or discard your changes'); return false; } else {if (#{reportDetail.openedForEdit}) { alert('Please click Validate and Recompute Totals/Save at the emissions inventory level before leaving the emissions inventory'); return false; }} if (#{reportProfile.doRptCompare}) { alert('Please click Exit Comparison Mode at inventory level before leaving the inventory comparison'); return false; }" />
</f:facet>

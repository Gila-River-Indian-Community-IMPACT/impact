<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Air Emissions Inventory">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:switcher defaultFacet="noMetaContainer" facetName="#{reportProfile.doRptCompare? 'metaContainer': 'noMetaContainer'}">
		  <f:facet name="metaContainer">
		    <f:verbatim>
				<style>
				.af_column_cell-text-band,.x2m,.portlet-section-alternate,.x73,.portlet-table-alternate,.x7a,.OraHGridNavCell,.x8d {
					background-color: #EEEDDD !important;
				}
				.af_column_cell-number-band,.x2o {
					background-color: #EEEDDD !important;
				}
				.af_column_cell-icon-format-band,.x2s,.af_tableSelectOne_cell-icon-format-band,.x2t,.af_tableSelectMany_cell-icon-format-band,.x2u,.OraTableCellSelectBand,.x65 {
					background-color: #EEEDDD !important;
				}
				</style>
		    </f:verbatim>		
		  </f:facet>
		  <f:facet name="noMetaContainer">
		  </f:facet>
		</af:switcher>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Emissions Inventory Detail" id="detailPage">
				<af:inputHidden value="#{submitTask.popupRedirect}"
					rendered="#{reportDetail.portalApp}" />
				<af:inputHidden value="#{reportSearch.popupRedirect}"
					rendered="#{reportDetail.internalApp}" />
				<af:inputHidden value="#{submitTask.logUserOff}"
					rendered="#{facilityProfile.portalApp}" />
				<jsp:include flush="true" page="header.jsp" />
				<f:subview id="comRptDetails">
					<jsp:include page="erNTVDetail.jsp" />
					<jsp:include page="reportProfile.jsp" />
				</f:subview>
			</af:page>
			<af:iterator value="#{reportDetail}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
			</af:iterator>
		</af:form>
	</af:document>
</f:view>



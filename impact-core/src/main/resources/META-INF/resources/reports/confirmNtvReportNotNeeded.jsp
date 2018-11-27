<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Confirm Emissions Inventory is Prior/Invalid">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Confirm Emissions Inventory is Prior/Invalid">
				<af:panelForm maxColumns="1">
				<af:outputFormatted
						    inlineStyle="color: orange; font-weight: bold;"
						    value="&lt;br&gt;&lt;b&gt;USER CAUTION!&lt;/b&gt;"/>
						<af:outputFormatted
							value="&lt;br&gt;The following VALID Report Scenarios should &lt;b&gt;NEVER&lt;/b&gt; be marked as &lt;b&gt;'Report Not Needed'&lt;/b&gt;.  These cases should &lt;u&gt;always&lt;/u&gt; be marked as &lt;b&gt; 'Approved'&lt;/b&gt; or &lt;b&gt; 'Approve/Revision Requested':&lt;/b&gt;&lt;ul&gt;&lt;li&gt;'ZERO' emissions inventories,&lt;/li&gt;&lt;li&gt;REVISED reports and&lt;/li&gt;&lt;li&gt;Reports showing emissions that indicate 'Less than Reporting Requirement' (Which DO assess a fee). &lt;/li&gt;&lt;/ul&gt;&lt;br&gt;If you have two reports for the same year requiring comparison, the most accurate report should be marked &lt;b&gt;'Approved' &lt;/b&gt;, with the invalid report marked as &lt;b&gt;'Report Not Needed'&lt;/b&gt; (if still in the &lt;b&gt;Submitted&lt;/b&gt; State).  See Stars2 help. &lt;br&gt;&lt;br&gt;Do not mark a report as &lt;b&gt;'Report Not Needed' &lt;/b&gt; until a valid, accurate report is submitted." />
				</af:panelForm>
				<af:objectSpacer width="100%" height="20" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Mark Report Not Needed"
							action="#{erNTVDetail.toStateNN}"/>
						<af:commandButton
							text="Cancel"
							action="#{erNTVDetail.cancelAddContact}" immediate="true" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

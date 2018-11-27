<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="body" title="IMPACT Home">
		<af:form>
			<af:page>
				
				<jsp:include flush="true" page="util/header.jsp" />
				
				<af:objectSpacer height="50" />
				
				<afh:tableLayout>
					<afh:rowLayout halign="center">
						<af:panelBox text="The WDEQ Air Quality IMPACT Public Website Disclaimer:" background="light" width="50%">
							<af:outputFormatted
								value="<b><br>This tool allows you to search the Wyoming Air Quality Division’s IMPACT database. Improper or incorrect use of this tool may cause unexpected or inaccurate results. Although every attempt is made to ensure that the information contained in the database is accurate, the Wyoming Department of Environmental Quality (DEQ) is not responsible for any loss, consequence, or damage resulting directly or indirectly from reliance on the accuracy, reliability, or timeliness of the information that is contained in this database. Users assume the entire risk of using the data contained in IMPACT.  DEQ is providing this data &quot;as is;&quot; and no warranty expressed or implied is made, including (without limitation) any implied warranties of merchantability or fitness for a particular purpose. In no event will DEQ be liable to any user or third party for any direct, indirect, incidental, consequential, special, or exemplary damages or lost profit resulting from any use or misuse of this data. Additional information concerning the accuracy and appropriate uses of this data may be obtained from the point of contact for the IMPACT database. The point of contact can be found at: </b>" />
							<af:goLink text="http://deq.wyoming.gov/aqd/impact-system/"
              					destination="http://deq.wyoming.gov/aqd/impact-system/"
              					id="deqContact"  targetFrame="_blank"
              					inlineStyle="font-family:arial; font-size:16px; font-weight:bold"/>
              				<af:outputFormatted value="<br>" />
						</af:panelBox>
					</afh:rowLayout>
				</afh:tableLayout>
				
				<af:objectSpacer height="10" />
					
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton id="agreeBtn" text="Agree" action="#{facilityProfile.refreshSearchFacilities}" >
							<t:updateActionListener
								property="#{appAccessAgreement.hasAgreed}" value="true" />
						</af:commandButton>
					</af:panelButtonBar>
				</afh:rowLayout>
				
			</af:page>
		</af:form>
	</af:document>
</f:view>
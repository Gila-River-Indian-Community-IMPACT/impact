<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:showDetailHeader text="Explanation" disclosed="false">
	<af:outputFormatted styleUsage="instruction"
		value="<ul>
								<li>The Issued Metrics and Not Issued Metrics reports currently fully support NSR permits only.  Comprehensive support for Title V permits can be added in a future release of IMPACT.</li>
								<li>For each Permit, AQD Days and Non-AQD Days are counted and totaled. Whenever a workflow task is referred to the company/facility that requested the permit, the time is counted as Non-AQD effort.  Benchmark comparisons are done against AQD Days only.</li>
								<li>A permit and associated workflow is created whenever the user submits a permit application from IMPACT. Any permit that is manually entered into IMPACT (from the internal IMPACT web application) will not have an associated workflow. These permits are referred to as ‘legacy permits’. This report only includes permits that have an associated workflow.</li>
								<li>Issued Metrics Report and Not Issued Metrics Report tables:
									<ul>
										<li>Issued Metrics Report – only includes permits that are Issued Final and have a completed workflow.</li>
											<ul>
												<li>To generate a report of the permits Issued Final with completed workflow during a certain date range, select Date Field = Workflow end Date and specify the first and/or last day of the desired reporting period.  If no values are specified for either From or To, all permits that have been Issued Final and have a completed workflow will be included in the Issued Metrics Report and Analysis tables.</li>
											</ul>
										<li>Not Issued Metrics Report – includes permits for which the associated workflow is not completed yet OR the permit is either Issued Withdrawal or Dead-Ended</li>
											<ul>
												<li>Permits where Publication Stage = Issued Final but the associated Workflow is not completed yet will be displayed in the Not Issued Metrics Report table.</li>
												<li>Using the checkboxes in the Search Criteria, the user can exclude Permits that are Dead-Ended and/or Issued Withdrawal.</li>
												<li>Since permits that are in progress do not have a workflow end date, no results will be returned in the Not Issued Metrics Report and Analysis tables when Date Field=Workflow End Date and a value is entered for either From or To.</li>
												<li>To generate a report of all the permits that are still in progress, do not specify values for the From and To date fields. (Note: the value in the Date Field dropdown doesn't matter when no From or To date values are entered).</li>
												<li>To generate a report of all the permits that were received during a date range but are still in progress, select a Date Field value of Receipt Date and choose From and/or To date values to define the date range.</li>
											</ul>
										<li>For the following columns, if there are multiple loops through the respective workflow steps, this column is populated with the start date for the first loop. The first two fields represent the time it takes to get ready for public notice.  Additional loops are typically administrative. So, for the purpose of populating these columns, the dates are those from the first loop.
											<ul>
												<li>Tech Review/Draft Permit/Waiver Start Date</li>
												<li>Manager/Supervisor Review Start Date</li>
												<li>Completeness Review Start Date</li>
											</ul>
										</li>
										<li>The Completeness Review End Date column is populated with the end date of the last iteration of the Completeness Review step.</li>
										<li>Final Issuance Date is manually entered by an AQD user on the Final Issuance Permit Detail page and may not be the same as the date the Final Issuance workflow task was completed.  The end date of the Final Issuance workflow task is used to compute the number of Days to Final Issuance. Using this date ensures that a permit is not counted as Issued Final until all associated workflow tasks are finished.</li>
									</ul>
								</li>
								<li>Analysis Tables:
									<ul>
										<li>The Issued Metrics Analysis and Not Issued Metrics Analysis tables each have a row that summarizes the following:
											<ul>
												<li>Waivers</li>
												<li>PSD</li>
												<li>Minor NSR (non-PSD)</li>
											</ul>
										</li>
										<li>Benchmark (AQD Days from Receipt Through Completeness Review) is configurable by the Admin user in a Definition list (Admin->Reports->Issued Metrics Report).</li>
										<li>Number of Permits with AQD Days Through Completeness Review Outside Benchmark - For each permit, AQD Days from Receipt through Completeness Review is compared with the associated benchmark. If the number of AQD Days is greater than the benchmark, this counter is incremented.</li>
										<li>Benchmark (AQD Days to Final Issuance) is configurable by the Admin user in a Definition list (Admin->Reports->Issued Metrics Report). A different benchmark value can be configured for Waivers, PSD, and Minor NSR.</li>
										<li>Number of Permits Issued Outside Benchmark – For each permit, AQD Days is compared with the associated benchmark. If the number of AQD Days is greater than the benchmark, this counter is incremented.</li>
										</li>
									</ul>
								</li>
								<li>The Not Issued Metrics Report and Analysis tables represent the applications that have been submitted for which permits have not yet been issued final OR for which Publication Stage is either Issued Withdrawal or Dead-Ended. Note: For this count, permits that have multiple applications only count as one.  Usually, there is a 1:1 relationship between permit and application, at least in the minds of those tracking this metric.</li>
					  </ul>" />
</af:showDetailHeader>
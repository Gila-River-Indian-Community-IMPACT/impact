Select 
    ff.fp_id, ff.transitional_status_cd, ff.emissions_rpt_cd, ff.permit_classification_cd, 
    ff.facility_id, ff.do_laa_cd, ff.version_id, ff.facility_nm, ff.facility_desc, ff.start_dt, ff.end_dt, 
    ff.operating_status_cd, ff.long_deg, ff.long_min, ff.long_sec, ff.lat_deg, ff.lat_min, ff.lat_sec, 
    ff.portable, ff.portable_group_cd, ff.last_shutdown_date, ff.copy_on_change, ff.per_due_date_cd, 
    ff.mact_ind, ff.neshaps_ind, ff.nsps_ind, ff.psd_ind, ff.nonattainment_nsr_ind, ff.permit_status_cd, 
    ff.sec112_ind, ff.federal_SCSC_ID, ff.intra_state_voucher_flag, ff.core_place_id, ff.tv_cert_report_due_date,
    ff.tiv_ind, ff.valid, ff.submitted, ff.portable_group_type_cd, ff.last_modified AS facility_lm, 
    ca.address1, ca.address2, ca.city, osd.operating_status_dsc, pcd.permit_classification_dsc, ca.zip5,
    pdd.per_due_date_dsc, pgd.portable_group_dsc, pgt.portable_group_type_dsc, sd.state_nm, cd.county_nm,
    cdd.county_nm as sec_county_nm, tsd.transitional_status_dsc, psd.permit_status_dsc
from 
    fp_facility ff 
      LEFT OUTER JOIN pa_per_due_date_def pdd ON (ff.per_due_date_cd=pdd.per_due_date_cd)
      LEFT OUTER JOIN fp_permit_status_def psd ON (ff.permit_status_cd=psd.permit_status_cd)
      LEFT OUTER JOIN fp_portable_group_def pgd ON (ff.portable_group_cd=pgd.portable_group_cd)
      LEFT OUTER JOIN fp_portable_group_type pgt ON (ff.portable_group_type_cd=pgt.portable_group_type_cd)
      LEFT OUTER JOIN fp_transitional_status_def tsd ON (ff.transitional_status_cd=tsd.transitional_status_cd)
      LEFT OUTER JOIN fp_county_notification cn ON (ff.fp_id=cn.fp_id) 
      LEFT OUTER JOIN cm_county_def cdd  ON (cdd.county_cd=cn.county_cd),
    cm_county_def cd, cm_address ca, fp_facility_address_xref ax, fp_operating_status_def osd, 
    cm_state_def sd,  fp_permit_classification_def pcd
where 
    ff.fp_id = $P{FP_ID} and ff.fp_id=ax.fp_id and ca.address_id=ax.address_id and
    ff.operating_status_cd=osd.operating_status_cd and ca.state_cd=sd.state_cd and ca.county_cd=cd.county_cd and
    ff.permit_classification_cd=pcd.permit_classification_cd
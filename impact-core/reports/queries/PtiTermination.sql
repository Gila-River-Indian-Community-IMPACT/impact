SELECT fp.facility_nm, fp.facility_id, pt.permit_id, pe.corr_epa_emu_id, pe.permit_eu_id
FROM pt_permit pt, pt_eu pe, pt_eu_group pg, pt_ptio_permit po, fp_facility fp,
     pt_permit_reason_xref px, fp_emissions_unit fu
WHERE pt.permit_id=pg.permit_id and 
fp.version_id = -1 and
pg.permit_eu_group_id=pe.permit_eu_group_id and 
pt.permit_id=po.permit_id and
pt.permit_id=px.permit_id and
fu.corr_epa_emu_id = pe.corr_epa_emu_id and
fu.initial_installation_dt is null and
(px.reason_cd='II' or px.reason_cd='C') and
(pt.effective_date > (sysdate + 180) or
pe.extension_dt > sysdate)
SELECT ff.fp_id, ff.facility_id, ff.version_id, nd.naics_cd, nd.naics_dsc, 
       facility_nm, ff.operating_status_cd, ff.emissions_rpt_cd, ff.permit_status_cd,
       ff.permit_classification_cd, ff.last_modified AS facility_lm, 
       ca.address_id, ca.address1,
       ca.address2, ca.city, ca.state_cd, ca.zip5, ca.zip4, ca.country_cd,
       ca.county_cd, 
       (SELECT county_nm FROM Stars2.cm_county_def ccd WHERE ca.county_cd = ccd.county_cd), 
       ca.start_dt, ca.end_dt, ca.last_modified AS address_lm, 
       ccd.county_cd, ccd.county_nm, ccd.state_cd, ccd.do_laa_cd, 
       ccd.last_modified AS county_lm, 
       fpdo.do_laa_cd AS code, fpdo.do_laa_dsc AS description, fpdo.last_modified
  FROM Stars2.fp_facility ff, Stars2.cm_address ca, Stars2.fp_facility_address_xref faxr,
       Stars2.cm_county_def ccd, Stars2.cm_state_def csd, Stars2.cm_do_laa_def fpdo, 
       Stars2.fp_facility_naics_xref nx, Stars2.fp_naics_def nd
    WHERE ff.version_id = -1
      AND ff.fp_id = faxr.fp_id
      AND faxr.address_id = ca.address_id
      AND ca.end_dt is null 
      AND ca.county_cd = ccd.county_cd
      AND ccd.state_cd = csd.state_cd
      AND ff.do_laa_cd = fpdo.do_laa_cd 
      AND ff.fp_id = nx.fp_id
      AND nx.naics_cd = nd.naics_cd 
      AND ccd.DO_LAA_CD LIKE $P{DOLAA}
      AND nd.NAICS_CD LIKE $P{NAICS}
SELECT ff.fp_id, ff.facility_id, ff.version_id, sd.sic_cd, sd.sic_dsc, 
    facility_nm, ff.operating_status_cd, ff.emissions_rpt_cd, ff.permit_status_cd,
    ff.permit_classification_cd, ff.last_modified AS facility_lm, 
   ca.address_id, ca.address_type_cd, ca.address1,
      ca.address2, ca.city, ca.state_cd, ca.zip5, ca.zip4, ca.country_cd,
      ca.county_cd, 
      (SELECT county_nm FROM cm_county_def ccd WHERE ca.county_cd = ccd.county_cd), 
      ca.start_dt, ca.end_dt, ca.last_modified AS
      address_lm, 
   ccd.county_cd, ccd.county_nm, ccd.state_cd, ccd.do_laa_cd, 
      ccd.last_modified AS county_lm, 
      fpdo.do_laa_cd AS code, fpdo.do_laa_dsc AS description, fpdo.last_modified
      FROM fp_facility ff, cm_address ca, fp_facility_address_xref faxr,
      cm_county_def ccd, cm_state_def csd, cm_address_type_def cat, fp_facility_sic_xref sx, 
      cm_do_laa_def fpdo, fp_sic_def sd 
      WHERE ff.version_id = -1
	  AND ff.fp_id = faxr.fp_id
	  AND faxr.address_id = ca.address_id
	  AND ca.end_dt is null 
	  AND ca.address_type_cd = cat.address_type_cd
	  AND ca.county_cd = ccd.county_cd
	  AND ccd.state_cd = csd.state_cd
	  AND ff.do_laa_cd = fpdo.do_laa_cd  
          AND sx.sic_cd = sd.sic_cd
          AND ff.fp_id = sx.fp_id 
          AND ff.FACILITY_ID LIKE $P{FACILITY_ID}
          AND LOWER(FACILITY_NM) LIKE LOWER($P{FACILITY_NM})
          AND LOWER(OPERATING_STATUS_CD) LIKE LOWER($P{OPERATING_STATUS_CD})
          AND LOWER(ca.COUNTY_CD) LIKE LOWER($P{COUNTY})
          AND LOWER(ccd.DO_LAA_CD) LIKE LOWER($P{DO_LAA_CD})
          AND LOWER(sd.SIC_CD) LIKE LOWER($P{SIC_CD})
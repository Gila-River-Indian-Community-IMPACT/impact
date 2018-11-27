package us.oh.state.epa.stars2.migration.tandc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class TvPermitFormatter {

    private static String HEADER = 
            MicrosoftTags.HTML_TAG
            + "<head>\n"
            + "    <title>Reproduction of Permit Terms and Conditions</title>\n"
            + "     <STYLE TYPE=\"text/css\">\n"
            + "       TABLE.header { text-align: left;\n"
            + "         page-break-before: always;\n"
            + "         }\n"
            + "       TABLE.firstheader { text-align: left;\n"
            + "         }\n"
            + "       TD.disclaimer { color: red; }\n"
            + "     </STYLE>\n"
            + MicrosoftTags.HEAD_TAGS
            + MicrosoftTags.STYLE_TAGS
            + "</head>\n";
    
            private static String BEGINNINGSECTION = 
            "   <body>"
            + "        <a name=\"TOP\"/>"
            + "  <p class=MsoNormal text-indent:0in;margin-left:0in;'>\n"
            + "      Facility ID:&nbsp;_FACID_&nbsp;Issuance type:&nbsp;_ISSUANCETYPE_\n"
            + "  </p>\n"
            + "  <br>\n"
            + "  <p class=MsoNormal style='color:orange;text-indent:0in;margin-left:0in;'>\n"
            + "This version of facility specific terms and conditions was converted\n" +
              "from a database format to an HTML file during an upgrade of the Ohio EPA,\n" +
              "Division of Air Pollution Control's permitting software.  Every attempt has\n" +
              "been made to convert the terms and conditions to look and substantively\n" +
              "conform to the permit issued or being drafted in STARS. However, the\n" +
              "format of the terms may vary slightly from the original. In addition,\n" +
              "although it is not expected, there is a slight possibility that a term\n" +
              "and condition may have been inadvertently \"left out\" of this reproduction\n" +
              "during the conversion process. Therefore, if this version is to be used\n" +
              "as a starting point in drafting a new version of a permit, it is imperative\n" +
              "that the entire set of terms and conditions be reviewed to ensure they\n" +
              "substantively mimic the issued permit.  The official version of any permit\n" +
              "issued final by Ohio EPA is kept in the Agency's Legal section.  The Legal\n" +
              "section may be contacted at (614) 644-3037. \n"
            + "  </p>"
            + "  <br>\n"
            + "  <p class=MsoNormal style='color:orange;text-indent:0in;margin-left:0in;'>\n"
            + "In addition to the terms and conditions, hyperlinks have been inserted\n" +
              "into the document so you may more readily access the section of the document\n" +
              "you wish to review.\n"
            + "  </p>\n"
            + "  <br>\n"
            + "  <p class=MsoNormal style='color:orange;text-indent:0in;margin-left:0in;'>\n"
            + "Finally, the term language under \"Part III\" and before\n" +
              "\"I. Applicable Emissions Limitations...\" has been added to aid in\n" +
              "document conversion, and was not part of the original issued permit.\n"
            + "  </p>\n"
            + "  <br>\n"
            + ""
            + "        <a name=\"PartII\"/>"
            + "  <p class=MsoNormal style='color:orange;text-indent:0in;margin-left:0in;'>\n"
            + "    <br>\n"
            + "      ***THIS IS NOT AN OFFICIAL VERSION OF THE PERMIT.  SEE PAGE 1 FOR ADDITIONAL INFORMATION***\n"
            + "  </p>\n"
            + "  <br>\n"
            + "  <p class=MsoNormal text-indent:0in;margin-left:0in;'>\n"
            + "      Facility ID:&nbsp;_FACID_&nbsp;Issuance type:&nbsp;_ISSUANCETYPE_\n"
            + "  </p>\n"
            + "  <br>\n"
            + ""
            + ""
            + "<p class=MsoNormal style='text-indent:0in;margin-left:0in;'>\n"
            + "       <b>Part II - Specific Facility Terms and Conditions</b>\n"
            + "</p>\n"
            + "<br>\n"
            + ""
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "a")
            + "       <b>State and Federally Enforceable Section</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"2\" cellspacing=\"0\">"
            + "        <tr>"
            + "       <td width=\"10px\">&nbsp;</td>"
            + "                <td>"
            + "                   <table>"
            + "                   <tbody>"
            + "                       _II_A_FA_TERMSANDCONDITIONS_"
            + "                   </tbody>"
            + "                   </table>"
            + "               </td>"
            + "        </tr>"
            + "   </table>"
            + ""
            + ""
            + "        <!-- -->"
            + "        <!-- 2nd page -->"
            + "        <!-- -->"
            + "  <p class=MsoNormal style='color:orange;text-indent:0in;margin-left:0in;'>\n"
            + "    <br>\n"
            + "      ***THIS IS NOT AN OFFICIAL VERSION OF THE PERMIT.  SEE PAGE 1 FOR ADDITIONAL INFORMATION***\n"
            + "  </p>\n"
            + "  <br>\n"
            + "  <p class=MsoNormal text-indent:0in;margin-left:0in;'>\n"
            + "      Facility ID:&nbsp;_FACID_&nbsp;Issuance type:&nbsp;_ISSUANCETYPE_\n"
            + "  </p>\n"
            + "  <br>\n"
            + "        "
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "b")
            + "       <b>State Only Enforceable Section</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"2\" cellspacing=\"0\">\n"
            + "        <tr>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "                <td>\n"
            + "                   <table>\n"
            + "                   <tbody>"
            + "                       _II_B_FB_TERMSANDCONDITIONS_\n"
            + "                   </tbody>\n"
            + "                   </table>\n"
            + "               </td>\n"
            + "        </tr>\n"
            + "   </table>\n"
            + ""
            + "   <table class=\"header\" width=\"850px\" cellpadding=\"2\" cellspacing=\"0\">\n"
            + "   <tbody>\n" + "       _EULINKS_\n" + "   </tbody>\n" + "   </table>\n";

    private static String EUSECTION = "        <a name=\"PartIII_EMUID_\"/>"
            + "  <p class=MsoNormal style='color:orange;text-indent:0in;margin-left:0in;'>\n"
            + "    <br>\n"
            + "      ***THIS IS NOT AN OFFICIAL VERSION OF THE PERMIT.  SEE PAGE 1 FOR ADDITIONAL INFORMATION***\n"
            + "  </p>\n"
            + "  <br>\n"
            + "  <p class=\"MsoNormal\" style=\"text-indent:0in;margin-left:0in;font-weight: bold;\">\n"
            + "      Facility ID:&nbsp;_FACID_&nbsp;Issuance type:&nbsp;_ISSUANCETYPE_\n"
            + "  </p>\n"
            + "  <br>\n"
            + "<p class=MsoNormal style='text-indent:0in;margin-left:0in;'>\n"
            + "       <b>_PARTIIITITLE_</b>\n"
            + "</p>\n"
            + "<br>\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">"
            + "   <tr><td>&nbsp;</td></tr>"
            + "        <tr><td><a href=\"#PartII\">Go to the top of this document</a></td></tr>"
            + "        </table>"
            + "  <br>\n"
            + ""
            + "  <table>\n"
            + "    <tr style=\"font-weight: bold;\">\n"
            + "       <td style=\"text-align: right\">Facility ID:&nbsp;</td>\n"
            + "       <td style=\"text-align: left;\">_FACID_</td>\n"
            + "       <td style=\"text-align: right\">Emissions Unit ID:&nbsp;</td>\n"
            + "       <td style=\"text-align: left;\">_EMUID_</td>\n"
            + "       <td style=\"text-align: right\">Issuance type:&nbsp;</td>\n"
            + "       <td style=\"text-align: left\">_ISSUANCETYPE_</td>\n"
            + "     </tr>\n"
            + "     <tr><td>&nbsp;</td></tr>\n"
            + "   </table>\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr style=\"font-weight: bold;\">\n"
            + "       <td>_ENFORCEABLE_</td>\n"
            + "   </tr>\n"
            + "   </table>\n"
            + ""
            + "\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "")
            + "       The following emissions unit terms and conditions are federally\n" +
              "       enforceable with the exception of those listed below which are\n" +
              "       enforceable under state law only.\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "<br>\n"
            +  MicrosoftTags.LEVEL3_P.replaceFirst("_L3_X_", "1.")
            + "       None."
            +  MicrosoftTags.LEVEL3_P_CLOSE
            + "<br>\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "I.")
            + "       <b>Applicable Emissions Limitations and/or Control Requirements</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "<br>\n"
            + ""
            +  MicrosoftTags.LEVEL3_P.replaceFirst("_L3_X_", "1.") +
            		"The specific operation(s), property, and/or equipment which constitute\n" +
            		"this emissions unit are listed in the following table along with the\n" +
            		"applicable rules and/or requirements and with the applicable emissions\n" +
            		"limitations and/or control measures. Emissions from this unit shall not\n" +
            		"exceed the listed limitations, and the listed control measures shall be\n" +
            		"employed. Additional applicable emissions limitations and/or control measures\n" +
            		"(if any) may be specified in narrative form following the table.\n"
                    +  MicrosoftTags.LEVEL3_P_CLOSE
            + "    <table  width=\"900px\" style=\"margin-left:1.2in\">"
            + "       <thead>"
            + "         <tr style=\"font-weight: bold; text-align: left; text-decoration: underline;\">"
            + "            <td width=\"33%\">Operations, Property, and/or Equipment</td>"
            + "            <td width=\"27%\">Applicable Rules/Requirements</td>"
            + "            <td width=\"40%\">Applicable Emissions Limitations/Control Measures</td>"
            + "         </tr>"
            + "      </thead>"
            + "      <tbody>"
            + "        _EU_1A_"
            + "      </tbody>"
            + "   </table>"
            + ""
            +  MicrosoftTags.LEVEL3_P.replaceFirst("_L3_X_", "2.")
            + "     <b>Additional Terms and Conditions</b>\n"
            + MicrosoftTags.LEVEL3_P_CLOSE
            + "\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "                <td>&nbsp;</td>\n"
            + "                <td>\n"
            + "                  <table>\n"
            + "                  <tbody>\n"
            + "                    _EU_A_I_2_\n"
            + "                  </tbody>\n"
            + "                  </table>\n"
            + "                </td>\n"
            + "   </tr>\n"
            + "   </table>\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr><td>&nbsp;</td></tr>\n"
            + "        <tr><td><a href=\"#PartII\">Go to the top of this document</a></td></tr>\n"
            + "        <tr><td><a href=\"#PartIII_EMUID_\">Go to the top of Part III for this Emissions Unit</a></td></tr>\n"
            + "   <tr>\n"
            + "       <td class=\"disclaimer\">***THIS IS NOT AN OFFICIAL VERSION OF THE PERMIT.  SEE PAGE 1 FOR ADDITIONAL INFORMATION.***</td>\n"
            + "        </tr>\n"
            + "        </table>\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "II.")
            + "       <b>Operational Restrictions</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "                <td>&nbsp;</td>\n"
            + "                <td>\n"
            + "                  <table>\n"
            + "                  <tbody>\n"
            + "                    _EU_A_II_1_\n"
            + "                  </tbody>\n"
            + "                  </table>\n"
            + "                </td>\n"
            + "   </tr>\n"
            + "   </table>\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr><td>&nbsp;</td></tr>\n"
            + "        <tr><td><a href=\"#PartII\">Go to the top of this document</a></td></tr>\n"
            + "        <tr><td><a href=\"#PartIII_EMUID_\">Go to the top of Part III for this Emissions Unit</a></td></tr>\n"
            + "   <tr>\n"
            + "       <td class=\"disclaimer\">***THIS IS NOT AN OFFICIAL VERSION OF THE PERMIT.  SEE PAGE 1 FOR ADDITIONAL INFORMATION.***</td>\n"
            + "        </tr>\n"
            + "        </table>\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "III.")
            + "   <b>Monitoring and/or Record Keeping Requirements</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "                <td>&nbsp;</td>\n"
            + "                <td>\n"
            + "                  <table>\n"
            + "                  <tbody>\n"
            + "                    _EU_A_III_1_\n"
            + "                  </tbody>\n"
            + "                  </table>\n"
            + "                </td>\n"
            + "   </tr>\n"
            + "   </table>\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr><td>&nbsp;</td></tr>\n"
            + "        <tr><td><a href=\"#PartII\">Go to the top of this document</a></td></tr>\n"
            + "        <tr><td><a href=\"#PartIII_EMUID_\">Go to the top of Part III for this Emissions Unit</a></td></tr>\n"
            + "   <tr>\n"
            + "       <td class=\"disclaimer\">***THIS IS NOT AN OFFICIAL VERSION OF THE PERMIT.  SEE PAGE 1 FOR ADDITIONAL INFORMATION.***</td>\n"
            + "        </tr>\n"
            + "        </table>\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "IV.")
            + "       <b>Reporting Requirements</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "                <td>&nbsp;</td>\n"
            + "                <td>\n"
            + "                  <table>\n"
            + "                  <tbody>\n"
            + "                    _EU_A_IV_1_\n"
            + "                  </tbody>\n"
            + "                  </table>\n"
            + "                </td>\n"
            + "   </tr>\n"
            + "   </table>\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr><td>&nbsp;</td></tr>\n"
            + "        <tr><td><a href=\"#PartII\">Go to the top of this document</a></td></tr>\n"
            + "        <tr><td><a href=\"#PartIII_EMUID_\">Go to the top of Part III for this Emissions Unit</a></td></tr>\n"
            + "   <tr>\n"
            + "       <td class=\"disclaimer\">***THIS IS NOT AN OFFICIAL VERSION OF THE PERMIT.  SEE PAGE 1 FOR ADDITIONAL INFORMATION.***</td>\n"
            + "        </tr>\n"
            + "        </table>\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "V.")
            + "       <b>Testing Requirements</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "                <td>&nbsp;</td>\n"
            + "                <td>\n"
            + "                  <table>\n"
            + "                  <tbody>\n"
            + "                    _EU_A_V_1_\n"
            + "                  </tbody>\n"
            + "                  </table>\n"
            + "                </td>\n"
            + "   </tr>\n"
            + "   </table>\n"
            + ""
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr><td>&nbsp;</td></tr>\n"
            + "        <tr><td><a href=\"#PartII\">Go to the top of this document</a></td></tr>\n"
            + "        <tr><td><a href=\"#PartIII_EMUID_\">Go to the top of Part III for this Emissions Unit</a></td></tr>\n"
            + "   <tr>\n"
            + "       <td class=\"disclaimer\">***THIS IS NOT AN OFFICIAL VERSION OF THE PERMIT.  SEE PAGE 1 FOR ADDITIONAL INFORMATION.***</td>\n"
            + "        </tr>\n"
            + "        </table>\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "VI.")
            + "       <b>Miscellaneous Requirements</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr>" + "       <td width=\"10px\">&nbsp;</td>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "                <td>&nbsp;</td>\n" + "                <td>\n"
            + "                  <table>\n" + "                  <tbody>\n"
            + "                    _EU_A_VI_1_\n" + "                  </tbody>\n"
            + "                  </table>\n" + "                </td>\n"
            + "   </tr>\n" + "   </table>\n";

    private static String ENDSECTION = "</body></html>\n";

    public TvPermitFormatter() {
        // null constructor
    }

    public void writeData(TvPermitInfo permitInfo, BufferedWriter outFile) throws IOException {

        int levelNo = 1;
        String beginning = BEGINNINGSECTION.replaceAll("_LFO_NUM_", new Integer(levelNo).toString());

        // TODO
        // ret = ret.replaceFirst("_FACNAME_",
        // FormatUtil.cleanChars(classData.facilityName));

        String issuanceType = FormatUtil.getIssuanceType(permitInfo.sect_id);

        beginning = beginning.replaceAll("_FACID_", FormatUtil
                .cleanChars(permitInfo.facility_id));
        beginning = beginning.replaceAll("_ISSUANCETYPE_", FormatUtil
                .cleanChars(issuanceType));

        StringBuffer sect1 = new StringBuffer();
        TvEuLevel dtls = permitInfo.euList.get("FAC0");
        if (dtls != null) {
            FormatUtil.addDtlRows(sect1, dtls.faPart, 3, false);
            beginning = beginning.replaceFirst("_II_A_FA_TERMSANDCONDITIONS_",
                    FormatUtil.cleanChars(sect1.toString()));
        }

        StringBuffer sect2 = new StringBuffer();
        TvEuLevel dtls_b = permitInfo.euList.get("FAC0");
        if (dtls_b != null) {
            FormatUtil.addDtlRows(sect2, dtls_b.fbPart, 3, false);
            beginning = beginning.replaceFirst("_II_B_FB_TERMSANDCONDITIONS_",
                    FormatUtil.cleanChars(sect2.toString()));
        }

        StringBuffer sect3 = new StringBuffer();
        Collection<TvEuLevel> c = permitInfo.euList.values();
        Iterator<TvEuLevel> itr = c.iterator();
        while (itr.hasNext()) {
            TvEuLevel euL = itr.next();
            if (euL.faPart.size() > 0) {
                /*
                 * The facility level Terms and Conditions have already been
                 * handled above. Skip over them.
                 */
                continue;
            }
            sect3.append(FormatUtil.SECTIONROWBEGIN);
            String hlink = "<a href=\"#PartIII" + euL.euId
                    + "\">Go to Part III for Emissions Unit " + euL.euId
                    + "</a>";
            FormatUtil.addTableCell(sect3, hlink);
            sect3.append(FormatUtil.SECTIONROWEND);

        }

        beginning = beginning.replaceFirst("_EULINKS_", FormatUtil
                .cleanChars(sect3.toString()));
        
        outFile.write(HEADER + beginning);
        outFile.flush();

        /*
         * Now process all of the EUs
         */
        c = permitInfo.euList.values();
        itr = c.iterator();
        while (itr.hasNext()) {
            TvEuLevel euL = itr.next();
            if (euL.faPart.size() > 0) {
                /*
                 * The facility level Terms and Conditions have already been
                 * handled above. Skip over them.
                 */
                continue;
            }

            /*
             * State and Federally Enforceable section
             */
            String s = new String(EUSECTION.replaceAll("_LFO_NUM_", new Integer(++levelNo).toString()));
            s = s.replaceAll("_PARTIIITITLE_",
                    "Part III - Terms and Conditions for Emissions Units");
            s = s.replaceAll("_ENFORCEABLE_",
                    "A. State and Federally Enforceable Section");
            s = s.replaceAll("_FACID_", FormatUtil
                    .cleanChars(permitInfo.facility_id));
            s = s.replaceAll("_EMUID_", FormatUtil.cleanChars(euL.euId));
            s = s.replaceAll("_ISSUANCETYPE_", FormatUtil
                    .cleanChars(issuanceType));

            StringBuffer sect = new StringBuffer();
            for (permit_1aInputData p1aData : euL.stateAndFederally_1a) {
                sect.append(FormatUtil.SECTIONROWBEGIN);
                FormatUtil.addTableCell(sect, p1aData.ops_props_equip);
                FormatUtil.addTableCell(sect, p1aData.reqt_text);
                FormatUtil.addTableCell(sect, p1aData.limit_ctrl);
                sect.append(FormatUtil.SECTIONROWEND);
            }
            s = s.replaceFirst("_EU_1A_", FormatUtil
                    .cleanChars(sect.toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_AX, 4, false);
            s = s.replaceFirst("_EU_A_I_2_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_A2, 3, false);
            s = s.replaceFirst("_EU_A_II_1_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_A3, 3, false);
            s = s.replaceFirst("_EU_A_III_1_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_A4, 3, false);
            s = s.replaceFirst("_EU_A_IV_1_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_A5, 3, false);
            s = s.replaceFirst("_EU_A_V_1_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_A6, 3, false);
            s = s.replaceFirst("_EU_A_VI_1_", FormatUtil.cleanChars(sect
                    .toString()));

            outFile.write(s);
            outFile.flush();

            /*
             * State Enforceable section
             */
            s = new String(EUSECTION.replaceAll("_LFO_NUM_", new Integer(++levelNo).toString()));
            s = s.replaceAll("_PARTIIITITLE_", "&nbsp;"); // Make it blank
            s = s.replaceAll("_ENFORCEABLE_", "B. State Enforceable Section");
            s = s.replaceAll("_FACID_", FormatUtil
                    .cleanChars(permitInfo.facility_id));
            s = s.replaceAll("_EMUID_", FormatUtil.cleanChars(euL.euId));
            s = s.replaceAll("_ISSUANCETYPE_", FormatUtil
                    .cleanChars(issuanceType));

            sect = new StringBuffer();
            for (permit_1aInputData p1aData : euL.stateOnly_1a) {
                sect.append(FormatUtil.SECTIONROWBEGIN);
                FormatUtil.addTableCell(sect, p1aData.ops_props_equip);
                FormatUtil.addTableCell(sect, p1aData.reqt_text);
                FormatUtil.addTableCell(sect, p1aData.limit_ctrl);
                sect.append(FormatUtil.SECTIONROWEND);
            }
            s = s.replaceFirst("_EU_1A_", FormatUtil
                    .cleanChars(sect.toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_BX, 3, false);
            s = s.replaceFirst("_EU_A_I_2_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_B2, 3, false);
            s = s.replaceFirst("_EU_A_II_1_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_B3, 3, false);
            s = s.replaceFirst("_EU_A_III_1_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_B4, 3, false);
            s = s.replaceFirst("_EU_A_IV_1_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_B5, 3, false);
            s = s.replaceFirst("_EU_A_V_1_", FormatUtil.cleanChars(sect
                    .toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dtl_B6, 3, false);
            s = s.replaceFirst("_EU_A_VI_1_", FormatUtil.cleanChars(sect
                    .toString()));

            outFile.write(s);
            outFile.flush();
        }

        outFile.write(ENDSECTION);
    }

}

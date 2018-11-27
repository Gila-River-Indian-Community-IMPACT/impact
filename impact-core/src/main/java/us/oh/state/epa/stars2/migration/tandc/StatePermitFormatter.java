package us.oh.state.epa.stars2.migration.tandc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class StatePermitFormatter {
    
    private static String HEADER = 
        MicrosoftTags.HTML_TAG
        + "<head>\n"
        + "    <title>Reproduction of Permit Terms and Conditions</title>\n"
        + "     <STYLE TYPE=\"text/css\">\n"
        + "       TABLE.header { text-align: left;\n"
        + "         page-break-before: always;\n"
        + "         }\n"
        + "     </STYLE>\n"
        + MicrosoftTags.HEAD_TAGS
        + MicrosoftTags.STYLE_TAGS
        + "</head>\n"
        + "\n";

    private static String BEGINNINGSECTION = 
              "<body>\n"
            + "  <a name=\"TOP\"/>\n"
            + "  <p class=MsoNormal text-indent:0in;margin-left:0in;'>\n"
            + "      Facility ID:&nbsp;_FACID_&nbsp;Issuance type:&nbsp;_ISSUANCETYPE_\n"
            + "  </p>\n"
            + "  <br>\n"
            + "\n"
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
              "into the document so you may more readily access the section of the document " +
              "you wish to review.\n"
            + "  </p>\n"
            + "  <br>\n"
            + "  <p class=MsoNormal style='color:orange;text-indent:0in;margin-left:0in;'>\n"
            + "Finally, the term language under \"Part II\" and before\n" +
              "\"A. Applicable Emissions Limitations...\" has been added to aid in\n" +
              "document conversion, and was not part of the original issued permit.\n"
            + "  </p>\n"
            + "  <br>\n"
            + "\n"
            + "  <table class=\"header\" width=\"850px\" cellpadding=\"2\" cellspacing=\"0\">\n"
            + "    <tbody>\n"
            + "_EULINKS_\n" 
            + "    </tbody>" 
            + "  </table>\n"
            + "  <br>\n";

    private static String EUSECTION = 
            "    <a name=\"PartII_EMUID_\"/>\n"
            + "  <p class=MsoNormal style='color:orange;text-indent:0in;margin-left:0in;'>\n"
            + "    <br>\n"
            + "      ***THIS IS NOT AN OFFICIAL VERSION OF THE PERMIT.  SEE PAGE 1 FOR ADDITIONAL INFORMATION***\n"
            + "  </p>\n"
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
            + "  <br>\n"
            + "\n"
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "     <tr><td>&nbsp;</td></tr>\n"
            + "     <tr><td><a href=\"#TOP\">Go to the top of this document</a></td></tr>\n"
            + "     <tr><td>&nbsp;</td></tr>\n"
            + "   </table>\n"
            + "\n"
            + "<p class=MsoNormal style='text-indent:0in;margin-left:0in;'>\n"
            + "       <b>Part II - Special Terms and Conditions</b>\n"
            + "</p>\n"
            + "<br>\n"
            + "\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "")
            + "       This permit document constitutes a permit-to-install issued in\n" +
              "       accordance with ORC 3704.03(F) and a permit-to-operate issued in\n" +
              "       accordance with ORC 3704.03(G).\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "\n"
            +  MicrosoftTags.LEVEL3_P.replaceFirst("_L3_X_", "1.")
            + "\n"
            + "       For the purpose of a permit-to-install document, the emissions unit\n"
            + "       terms and conditions identified below are federally enforceable with\n"
            + "       the exception of those listed below which are enforceable under state\n"
            + "       law only.\n"
            +  MicrosoftTags.LEVEL3_P_CLOSE
            + "\n"
            +  MicrosoftTags.LEVEL4_P.replaceFirst("_L4_X_", "(a)")
            + "        None."
            +  MicrosoftTags.LEVEL4_P_CLOSE
            + "\n"
            +  MicrosoftTags.LEVEL3_P.replaceFirst("_L3_X_", "2.")
            + "\n"
            + "       For the purpose of a permit-to-operate document, the emissions unit\n"
            + "       terms and conditions identified below are enforceable under state law\n"
            + "       only with the exception of those listed below which are federally\n"
            + "       enforceable.\n"
            +  MicrosoftTags.LEVEL3_P_CLOSE
            + "\n"
            +  MicrosoftTags.LEVEL4_P.replaceFirst("_L4_X_", "(a)")
            + "        None."
            +  MicrosoftTags.LEVEL4_P_CLOSE
            + "<br>\n"
            + "\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "A.")
            + "       <b>Applicable Emissions Limitations and/or Control Requirements</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "\n"
            +  MicrosoftTags.LEVEL3_P.replaceFirst("_L3_X_", "1.")
            +		"The specific operation(s), property, and/or equipment which constitute this\n" +
            		"emissions unit are listed in the following table along with the applicable\n" +
            		"rules and/or requirements and with the applicable emissions limitations and/or\n" +
            		"control measures. Emissions from this unit shall not exceed the listed limitations,\n" +
            		"and the listed control measures shall be employed. Additional applicable emissions\n" +
            		"limitations and/or control measures (if any) may be specified in narrative form\n" +
            		"following the table.\n"
            +  MicrosoftTags.LEVEL3_P_CLOSE
            + "                  <table width=\"900px\" style=\"margin-left:1.2in\">\n"
            + "                    <thead>\n"
            + "               <tr style=\"font-weight: bold; text-align: left; text-decoration: underline;\">\n"
            + "                      <td width=\"33%\">Operations, Property, and/or Equipment</td>\n"
            + "                      <td width=\"27%\">Applicable Rules/Requirements</td>\n"
            + "                      <td width=\"40%\">Applicable Emissions Limitations/Control Measures</td>\n"
            + "                    </tr>\n"
            + "                    </thead>\n"
            + "                    <tbody>\n"
            + "                      _EU_1A_\n"
            + "                    </tbody>\n"
            + "                  </table>\n"
            + "\n"
            +  MicrosoftTags.LEVEL3_P.replaceFirst("_L3_X_", "2.")
            + "     <b>Additional Terms and Conditions</b>\n"
            + MicrosoftTags.LEVEL3_P_CLOSE
            + "\n"
            + "   <table width=\"850px\" cellpadding=\"0\" cellspacing=\"0\">\n"
            + "   <tr>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "       <td width=\"10px\">&nbsp;</td>\n"
            + "                <td>&nbsp;</td>\n"
            + "                <td>\n"
            + "                  <table>\n"
            + "                  <tbody>\n"
            + "               _II_A_TERMSANDCONDITIONS_\n"
            + "                  </tbody>\n"
            + "                  </table>\n"
            + "                </td>\n"
            + "   </tr>\n"
            + "   </table>\n"
            + "\n"
            + "\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "B.")
            + "       <b>Operational Restrictions</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "\n"
            + "   <table width=\"850px\" cellpadding=\"2\" cellspacing=\"0\">\n"
            + "     <tbody>\n"
            + "       _II_B_TERMSANDCONDITIONS_\n"
            + "     </tbody>\n"
            + "   </table>\n"
            + "\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "C.")
            + "   <b>Monitoring and/or Record Keeping Requirements</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "\n"
            + "   <table width=\"850px\" cellpadding=\"2\" cellspacing=\"0\">\n"
            + "     <tbody>\n"
            + "       _II_C_TERMSANDCONDITIONS_\n"
            + "     </tbody>\n"
            + "   </table>" + "\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "D.")
            + "       <b>Reporting Requirements</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "   <table width=\"850px\" cellpadding=\"2\" cellspacing=\"0\">\n"
            + "     <tbody>\n"
            + "        _II_D_TERMSANDCONDITIONS_\n"
            + "     </tbody>\n"
            + "   </table>" + "\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "E.")
            + "       <b>Testing Requirements</b>"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "   <table width=\"850px\" cellpadding=\"2\" cellspacing=\"0\">\n"
            + "     <tbody>\n"
            + "        _II_E_TERMSANDCONDITIONS_\n"
            + "     </tbody>\n"
            + "   </table>\n"
            +  MicrosoftTags.LEVEL2_P.replaceFirst("_L2_X_", "F.")
            + "       <b>Miscellaneous Requirements</b>\n"
            +  MicrosoftTags.LEVEL2_P_CLOSE
            + "   <table width=\"850px\" cellpadding=\"2\" cellspacing=\"0\">\n"
            + "     <tbody>\n"
            + "       _II_F_TERMSANDCONDITIONS_\n"
            + "     </tbody>\n"
            + "   </table>\n";

    private static String ENDSECTION = "</body></html>";

    public StatePermitFormatter() {
        // null constructor
    }

    public void writeData(StatePermitInfo permitInfo, BufferedWriter outFile) throws IOException {

        String beginning = BEGINNINGSECTION;

        // TODO
        // ret = ret.replaceFirst("_FACNAME_",
        // FormatUtil.cleanChars(classData.facilityName));

        String issuanceType = FormatUtil.getIssuanceType(permitInfo.sect_id);

        beginning = beginning.replaceAll("_FACID_", FormatUtil
                .cleanChars(permitInfo.facility_id));
        beginning = beginning.replaceAll("_ISSUANCETYPE_", FormatUtil
                .cleanChars(issuanceType));

        StringBuffer sect = new StringBuffer();
        Collection<StateEuLevel> c = permitInfo.euList.values();
        Iterator<StateEuLevel> itr = c.iterator();
        while (itr.hasNext()) {
            StateEuLevel euL = itr.next();
            sect.append(FormatUtil.SECTIONROWBEGIN);
            String hlink = "<a href=\"#PartII" + euL.euId
                    + "\">Go to Part II for Emissions Unit " + euL.euId
                    + "</a>";
            FormatUtil.addTableCell(sect, hlink);
            sect.append(FormatUtil.SECTIONROWEND);
        }

        if (c.size() > 1) {
            beginning = beginning.replaceFirst("_EULINKS_", FormatUtil
                    .cleanChars(sect.toString()));
        } else {
            beginning = beginning.replaceFirst("_EULINKS_",
                    FormatUtil.HTMLSPACE);
        }
        
        outFile.write(HEADER + beginning);
        outFile.flush();

        /*
         * Now process all of the EUs
         */
        c = permitInfo.euList.values();
        itr = c.iterator();
        int levelNo = 0;
        while (itr.hasNext()) {
            StateEuLevel euL = itr.next();
            levelNo++;

            String s = new String(EUSECTION.replaceAll("_LFO_NUM_", new Integer(levelNo).toString()));
            s = s.replaceAll("_FACID_", FormatUtil
                    .cleanChars(permitInfo.facility_id));
            s = s.replaceAll("_EMUID_", FormatUtil.cleanChars(euL.euId));
            s = s.replaceAll("_ISSUANCETYPE_", FormatUtil
                    .cleanChars(issuanceType));

            sect = new StringBuffer();
            for (permit_1aInputData p1aData : euL.state_1a) {
                sect.append(FormatUtil.SECTIONROWBEGIN);
                FormatUtil.addTableCell(sect, p1aData.ops_props_equip);
                FormatUtil.addTableCell(sect, p1aData.reqt_text);
                FormatUtil.addTableCell(sect, p1aData.limit_ctrl);
                sect.append(FormatUtil.SECTIONROWEND);
            }
            s = s.replaceFirst("_EU_1A_", FormatUtil
                    .cleanChars(sect.toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.aPart, 4, true);
            s = s.replaceFirst("_II_A_TERMSANDCONDITIONS_", FormatUtil
                    .cleanChars(sect.toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.bPart, 3, true);
            s = s.replaceFirst("_II_B_TERMSANDCONDITIONS_", FormatUtil
                    .cleanChars(sect.toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.cPart, 3, true);
            s = s.replaceFirst("_II_C_TERMSANDCONDITIONS_", FormatUtil
                    .cleanChars(sect.toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.dPart, 3, true);
            s = s.replaceFirst("_II_D_TERMSANDCONDITIONS_", FormatUtil
                    .cleanChars(sect.toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.ePart, 3, true);
            s = s.replaceFirst("_II_E_TERMSANDCONDITIONS_", FormatUtil
                    .cleanChars(sect.toString()));

            sect = new StringBuffer();
            FormatUtil.addDtlRows(sect, euL.fPart, 3, true);
            s = s.replaceFirst("_II_F_TERMSANDCONDITIONS_", FormatUtil
                    .cleanChars(sect.toString()));

            outFile.write(s);
            outFile.flush();

        }

        outFile.write(ENDSECTION);
        outFile.flush();
    }

}

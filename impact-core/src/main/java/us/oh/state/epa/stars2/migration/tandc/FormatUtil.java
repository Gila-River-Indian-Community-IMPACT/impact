package us.oh.state.epa.stars2.migration.tandc;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {

    public final static String SECTIONROWBEGIN = "<tr style=\"text-align: left; vertical-align: top;\">\n";
    public final static String SECTIONROWEND = "</tr>\n";
    public final static String CELLBEGIN = "<td>\n";
    public final static String CELLEND = "</td>\n";
    
    public final static String HTMLSPACE = "&nbsp;";
    public final static String H_DOLLARSIGN = "&nbsp;&#36;";
    public final static String H_BACKSLASH = "&nbsp;&#92;";

    FormatUtil() {
        // null constructor
    }

    /*
     * Helper method for converting Strings to HTML output when using
     * String.replaceFirst(). The replaceFirst method has a 'feature' whereby
     * dollar signs and backslashes in the replacement string causes
     * counterintuitive behavior. This method converts instances of those two
     * characters to their equivalent HTML form.
     */
    public static String cleanChars(String s) {
        int idx;

        while ((idx = s.indexOf('$')) >= 0) {
            String tmp = s.substring(0, (idx)) + H_DOLLARSIGN
                    + s.substring((idx + 1));
            s = tmp;
        }

        while ((idx = s.indexOf('\\')) >= 0) {
            String tmp = s.substring(0, (idx)) + H_BACKSLASH
                    + s.substring((idx + 1));
            s = tmp;
        }

        return (s);
    }
    
    public static void addBoldTableCell(StringBuffer sb, String val) {
        if (val.length() > 0) {
            val = "<b>" + val + "</b>";
        }
        addTableCell(sb, val);
    }

    public static void addTableCell(StringBuffer sb, String val) {
        String s;
        if (val.length() > 0) {
//            int idx;
//            while ((idx = val.indexOf('\n')) >= 0) {
//                String tmp = val.substring(0, (idx)) + "<br>\n"
//                        + val.substring((idx + 1));
//                val = tmp;
//            }
            s = val.replaceAll("\n", "<br>\n");
        } else {
            s = HTMLSPACE;
        }
        sb.append(CELLBEGIN + s + CELLEND);
    }
    
    public static void addLevel4Entry(StringBuffer sb, String row_num, String val) {
        String s;
        if (val.length() > 0) {
            int idx;
            while ((idx = val.indexOf('\n')) >= 0) {
                String tmp = val.substring(0, (idx)) + "<br>"
                        + val.substring((idx + 1));
                val = tmp;
            }
            s = MicrosoftTags.LEVEL4_P.replaceFirst("_L4_X_", row_num) + val +
                MicrosoftTags.LEVEL4_P_CLOSE;
        } else {
            s = HTMLSPACE;
        }
        sb.append(CELLBEGIN + s + CELLEND);
    }
    
    public static String formatVal(String val) {
        if (val.length() > 0) {
            int idx;
            while ((idx = val.indexOf('\n')) >= 0) {
                String tmp = val.substring(0, (idx)) + "<br>"
                        + val.substring((idx + 1));
                val = tmp;
            }
        }
        return val + "\n";
    }
    
    public static String getIssuanceType(String sect_id) {
        String ret = "Unknown";
        if (sect_id.equalsIgnoreCase("STPDR")) {
            ret = "Draft State Permit To Operate";
        } else if (sect_id.equalsIgnoreCase("STPFN")) {
            ret = "Final State Permit To Operate";
        } else if (sect_id.equalsIgnoreCase("TVPDR")) {
            ret = "Title V Draft Permit";
        } else if (sect_id.equalsIgnoreCase("TVPPR")) {
            ret = "Title V Proposed Permit";
        } else if (sect_id.equalsIgnoreCase("TVPPP")) {
            ret = "Title V Preliminary Proposed Permit";
        } else if (sect_id.equalsIgnoreCase("TVPFN")) {
            ret = "Title V Final Permit";
        }

        return (ret);
    }
    
    public static String formatSubSection(String val, String rowNum, int startLevel,
            boolean gotLevel4) {
        if (val == null || val.trim().length() == 0) {
            return "None\n";
        }
        Pattern level5Pattern = Pattern.compile("^\\s+([a-z]\\.)");
        Pattern level6Pattern = Pattern.compile("^\\s+([iv]+\\.)");
        Pattern level7Pattern = Pattern.compile("^\\s*(\\([a-z]+\\))");
        Pattern level8Pattern = Pattern.compile("^\\s*(\\([iv]+\\))");
        StringBuffer result = new StringBuffer();
        val = val.replaceAll("\\\n", "<br>\n");
        String[] lines = val.split("\\\n");
       
        for (String line : lines) {
            if (line.trim().length() == 0) {
                continue;
            }
            Matcher level6Matcher = level6Pattern.matcher(line);
            if (level6Matcher.find()) {
                result.append(MicrosoftTags.getLevelStart(startLevel + 2, level6Matcher.group(1)));
                result.append(line.trim().substring(level6Matcher.start() + level6Matcher.group(1).length() + 1) + "\n");
                result.append(MicrosoftTags.getLevelClose(startLevel + 2));
            } else {
                Matcher level8Matcher = level8Pattern.matcher(line);
                if (level8Matcher.find()) {
                    result.append(MicrosoftTags.getLevelStart(startLevel + 4, level8Matcher.group(1)));
                    result.append(line.trim().substring(level8Matcher.start() + level8Matcher.group(1).length()) + "\n");
                    result.append(MicrosoftTags.getLevelClose(startLevel + 4));
                } else {
                    Matcher level7Matcher = level7Pattern.matcher(line);
                    if (level7Matcher.find()) {
                        result.append(MicrosoftTags.getLevelStart(startLevel + 3, level7Matcher.group(1)));
                        result.append(line.trim().substring(level7Matcher.start() + level7Matcher.group(1).length()) + "\n");
                        result.append(MicrosoftTags.getLevelClose(startLevel + 3));
                    } else {
                        Matcher level5Matcher = level5Pattern.matcher(line);
                        if (level5Matcher.find()) {
                            result.append(MicrosoftTags.getLevelStart(startLevel + 1, level5Matcher.group(1)));
                            result.append(line.trim().substring(level5Matcher.start() + level5Matcher.group(1).length()) + "\n");
                            result.append(MicrosoftTags.getLevelClose(startLevel + 1));
                        } else if (!gotLevel4){
                            result.append(MicrosoftTags.getLevelStart(startLevel, getLevelId(startLevel, rowNum)));
                            result.append(line.trim() + "\n");
                            result.append(MicrosoftTags.getLevelClose(startLevel));
                            gotLevel4 = true;
                        } else {
                            // need to insert text before last close
                            int insertIdx = result.toString().lastIndexOf(MicrosoftTags.getLevelClose(startLevel)) - 1;
                            if (insertIdx >= 0) {
                                result.insert(insertIdx, "\n" + line + "\n");
                            } else {
                                result.append(line + "\n");
                            }
                        }
                    }
                }
            }
        }
        return result.toString();
    }
    
    public static void addDtlRows(StringBuffer sect,
            ArrayList<permit_dtlInputData> dtls, int levelNo, boolean isState) {

        /*
         * If there are no entries in the dtl table - print 'None'
         */
        if (dtls.size() == 0) {
            permit_dtlInputData n = new permit_dtlInputData();
            n.row_num = "None";
            n.sub_part = FormatUtil.HTMLSPACE;
            n.reqt_text = FormatUtil.HTMLSPACE;
            dtls.add(n);
        }
        /*
         * STARS legacy data may have multiple elements in the dtl table with
         * the same row_number and sub_part, but different sequence numbers.
         * They had a 2K limit on these text attributes so they had to
         * accommodate long T&C text by inserting multiple rows. Don't print the
         * row_num and sub_part if they are repeated.
         */
        StringBuffer meat = new StringBuffer();
        String prevRow = "";
        String prevSubPart = "";
        for (permit_dtlInputData dtlData : dtls) {
            String val = dtlData.reqt_text;
            if (!isState && dtlData.sub_part.matches("[a-z]+\\.?")) {
                String subPart = dtlData.sub_part;
                if (!subPart.endsWith(".")) {
                    subPart = subPart + ".";
                }
                val = " " + subPart +  val;
            }
            if (!dtlData.row_num.equals("None")) {
                if (dtlData.row_num.equals(prevRow) && dtlData.section.equals(prevSubPart)) {
                    String line = formatSubSection(val, dtlData.row_num, levelNo,
                            dtlData.row_num.equals(prevRow));
                    // need to insert text before last close
                    int insertIdx = meat.toString().lastIndexOf(MicrosoftTags.LEVEL5_P_CLOSE) - 1;
                    if (insertIdx >= 0) {
                        meat.insert(insertIdx, "<br>\n" + line + "\n");
                    } else {
                        meat.append(line + "\n");
                    }
                } else {
                    String rowNum = dtlData.row_num;
                    // kludge to fix bad row num
                    if (dtlData.sub_part.matches("a\\.?")) {
                        rowNum = "1";
                    }
                    meat.append(formatSubSection(val, rowNum, levelNo,
                            dtlData.row_num.equals(prevRow)));
                }
            } else {
                meat.append(MicrosoftTags.getLevelStart(levelNo, getLevelId(levelNo, "1")));
                meat.append("None\n");
                meat.append(MicrosoftTags.getLevelClose(levelNo));
            }
            prevRow = dtlData.row_num;
            prevSubPart = dtlData.section;
        }
        sect.append(SECTIONROWBEGIN + CELLBEGIN);
        sect.append(meat);
        sect.append(CELLEND + SECTIONROWEND);
    }

    private static String getLevelId(int levelNo, String rowNum) {
        String levelId = "a)";
        int row = 1;
        try {
            row = Integer.parseInt(rowNum);
        } catch (NumberFormatException e) {
            // keep default value (1)
            row = 1;
        }
        switch (levelNo) {
        case 3:
            levelId = row + ".";
            break;
        case 4:
            levelId = "(" + (char)('a' + (row - 1)) + ")";
            break;
        default:
            levelId = (char)('a' + (row - 1)) + ")";
            break;
        }
        return levelId;
    }
}
package us.oh.state.epa.stars2.util;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

public class CetaGetUserId {
    static HashMap<String, String> nameMap = new HashMap<String, String>();
    static HashMap<String, String> missingNames = new HashMap<String, String>();
    protected static Logger logger = Logger.getLogger(CetaGetUserId.class);
    static FullComplianceEvalService fceBO = null;

    
    static public List<Integer> getId(String name, String doLaa) {
        ArrayList<Integer> rtn = new ArrayList<Integer>();
        boolean debug = false;
//        if(name!=null && (name.contains("Roland Lacy") || name.contains("ROLAND LACY") || name.toLowerCase().contains("roland lacy"))) {
//            debug = true;
//        }
        if(debug) {
            logger.error("Found >" + name + "<; map size=" + nameMap.size());
        }
        boolean foundUnknown = false;
        if(name == null || name.length() == 0) return rtn;

        StringBuffer nameBuf = new StringBuffer(name);
      // normalize names since that is what we will look for
      int nextNdx = 0;
      while(true) { // normalize the names by removing all extra blanks. If punctuation then remove all blanks.
          boolean foundWork = false;
          int i = nameBuf.indexOf(" ", nextNdx);
          if(i == -1) break;
          if(i > 0) {
              // check for non-lettter to left
              if('.' == nameBuf.charAt(i - 1) ||
                      '\'' == nameBuf.charAt(i - 1) ||
                      '/' == nameBuf.charAt(i - 1) ||
                      '(' == nameBuf.charAt(i - 1) ||
                      ')' == nameBuf.charAt(i - 1)){
                  nameBuf.deleteCharAt(i);
                  foundWork = true;
                  nextNdx = i;
                  continue;
              }
          }
          if(i < nameBuf.length() - 2) {
              // check for non-lettter to right
              if(' ' == nameBuf.charAt(i + 1) ||  //blank
                      '.' == nameBuf.charAt(i + 1) ||
                      '\'' == nameBuf.charAt(i + 1) ||
                      '/' == nameBuf.charAt(i + 1) ||
                      '(' == nameBuf.charAt(i + 1) ||
                      ')' == nameBuf.charAt(i + 1)){
                  nameBuf.deleteCharAt(i);
                  foundWork = true;
                  nextNdx = i;
                  continue;
              }
          }
          if(!foundWork) nextNdx = i + 1;
      }
        
        
        
        // See if translation needed before parsing for names.
        String translation = nameMap.get(nameBuf.toString().toLowerCase());
        if(translation != null && translation.length() > 1 && translation.charAt(0) == '#') {
            nameBuf = new StringBuffer(translation.substring(1));
            if(debug) {
                logger.error("Translated to >" + nameBuf.toString() + "<");
            }
        }

        StringBuffer buf = new StringBuffer(nameBuf.toString().toLowerCase().replaceAll(" and ", "/").replaceAll("\\.and ", "./").trim());
        if(debug) {
            logger.error("buf is >" + buf.toString() + "<");
        }
        while(true) {
            if(buf.length() == 0) break;
            // Find name separator and look for each name
            int sep1 = buf.indexOf("/");
            int sep2 = buf.indexOf(";");
            int sep3 = buf.indexOf(",");
            int sep4 = buf.indexOf("&");
            int sep = minimum(sep1, sep2);
            sep = minimum(sep, sep3);
            sep = minimum(sep, sep4);
            StringBuffer nextName;
            if(sep < 0) {
                nextName = new StringBuffer(buf);
                buf.setLength(0);
            } else {
                nextName = new StringBuffer(buf.substring(0, sep));
                buf.delete(0, sep + 1);
            }

//            boolean foundWork = true;
//            nextNdx = 0;
//            while(true) { // normalize the name by removing all extra blanks. If punctuation then remove all blanks.
//                foundWork = false;
//                int i = nextName.indexOf(" ", nextNdx);
//                if(i == -1) break;
//                if(i > 0) {
//                    // check for non-lettter to left
//                    if('.' == nextName.charAt(i - 1) ||
//                            '\'' == nextName.charAt(i - 1) ||
//                            '/' == nextName.charAt(i - 1) ||
//                            '(' == nextName.charAt(i - 1) ||
//                            ')' == nextName.charAt(i - 1)){
//                        nextName.deleteCharAt(i);
//                        foundWork = true;
//                        nextNdx = i;
//                        continue;
//                    }
//                }
//                if(i < nextName.length() - 2) {
//                    // check for non-lettter to right
//                    if(' ' == nextName.charAt(i + 1) ||  //blank
//                            '.' == nextName.charAt(i + 1) ||
//                            '\'' == nextName.charAt(i + 1) ||
//                            '/' == nextName.charAt(i + 1) ||
//                            '(' == nextName.charAt(i + 1) ||
//                            ')' == nextName.charAt(i + 1)){
//                        nextName.deleteCharAt(i);
//                        foundWork = true;
//                        nextNdx = i;
//                        continue;
//                    }
//                }
//                if(!foundWork) nextNdx = i + 1;
//            }
            if(debug) {
                logger.error("nextName is >" + nextName.toString() + "<");
            }
            // Delete strings ,MTAPCA (MTAPCA) (MTAPCA MTAPCA
            String normalizedName = nextName.toString().replaceAll(",MTAPCA", "").replaceAll("\\(MTAPCA\\)", "").replaceAll("\\(MTAPCA", "").replaceAll("MTAPCA", "").trim(); 
            if(debug) {
                logger.error("normalizedName is >" + normalizedName + "<");
            }
            String stars2Name = nameMap.get(normalizedName.toLowerCase());
            if(debug) {
                logger.error("stars2Name is >" + stars2Name + "<");
            }
            if(stars2Name == null) {
                String n = normalizedName.toLowerCase();
                for(Entry<String, String> s : nameMap.entrySet()) {
                    String k = s.getKey();
                    if(k.equals(n)) {
                        stars2Name = s.getValue();
                        break;
                    }
                }
            }
            if(debug) {
                logger.error("stars2Name2 is >" + stars2Name + "<");
            }
            
            if(stars2Name == null) {
                stars2Name = normalizedName.trim();
            }
            if(debug) {
                logger.error("stars2Name3 is >" + stars2Name + "<");
            }
            if(stars2Name != null && stars2Name.equals("#")) {
                // Skip because this indicated no witness
            } else if(stars2Name.length() > 0 && stars2Name.charAt(0) == '#'){
                // not a valid mapping.
                if(debug) {
                    logger.error("stars2Name >" + stars2Name + "< added to missing names.  Original was >" + name + "<");
                }
                missingNames.put(stars2Name, doLaa + "(mapped to substitution: " + stars2Name + " (Original field is: " + name + ")");
                foundUnknown = true;
                continue;
            } else if(stars2Name.length() > 0){
                Integer userId = null;
                try {
                    if(fceBO == null) {
                        fceBO = ServiceFactory.getInstance().getFullComplianceEvalService(); 
                    }
                    userId = fceBO.getUserId(stars2Name);
                    if(debug) {
                        int uId = -1;
                        if(userId != null) uId = userId;
                        logger.error("user ID for >" + stars2Name + "< is " + uId);
                    }
                    if(userId == null) {
                        if(debug) {
                            logger.error("stars2Name >" + stars2Name + "< added to missing names.  Original was >" + name + "<");
                        }
                        missingNames.put(stars2Name, doLaa + ": " + stars2Name + " (Original field is: " + name + ")");
                        foundUnknown = true;
                        continue;
                    }
                    rtn.add(userId);
                } catch(ServiceFactoryException sfe) {
                    logger.error(sfe.getMessage(), sfe);
                } catch(RemoteException re) {
                    logger.error(re.getMessage(), re);
                }
            }
        }
        if(rtn.size() == 0 && foundUnknown) {
            rtn.add(CommonConst.UNKNOWN_USER_ID);
        }
        return rtn;
    }

    static public void loadTable(String dir) {
        FileReader inFile;
        try {
            inFile = new FileReader(new File(dir + File.separator + "migrationData" + File.separator, "cetaNameMappings.txt"));     
            int charRd = 0;
            while(charRd != -1) {
                // Read CETA name
                StringBuffer cetaName = new StringBuffer();
                while(charRd != -1 && (char)charRd != ':') {
                    charRd = inFile.read();
                    if(charRd != -1 && (char)charRd != ':') {
                        cetaName.append((char)charRd);
                    }
                }
                // Read Stars2 name
                StringBuffer stars2Name = new StringBuffer();
                while(charRd != -1 && (char)charRd != '\n') {
                    charRd = inFile.read();
                    if(charRd != -1 && (char)charRd != '\n') {
                        stars2Name.append((char)charRd);
                    }
                }
                // Normalize name before puttng it in.
                StringBuffer cetaNameLower = new StringBuffer(cetaName.toString().trim().toLowerCase());
                int nextNdx = 0;
                boolean foundWork;
                while(true) { // normalize the name by removing all extra blanks. If punctuation then remove all blanks.
                    foundWork = false;
                    int i = cetaNameLower.indexOf(" ", nextNdx);
                    if(i == -1) break;
                    if(i > 0) {
                        // check for non-lettter to left
                        if('.' == cetaNameLower.charAt(i - 1) ||
                                '\'' == cetaNameLower.charAt(i - 1) ||
                                '/' == cetaNameLower.charAt(i - 1) ||
                                '(' == cetaNameLower.charAt(i - 1) ||
                                ')' == cetaNameLower.charAt(i - 1)){
                            cetaNameLower.deleteCharAt(i);
                            foundWork = true;
                            nextNdx = i;
                            continue;
                        }
                    }
                    if(i < cetaNameLower.length() - 2) {
                        // check for non-lettter to right
                        if(' ' == cetaNameLower.charAt(i + 1) ||  //blank
                                '.' == cetaNameLower.charAt(i + 1) ||
                                '\'' == cetaNameLower.charAt(i + 1) ||
                                '/' == cetaNameLower.charAt(i + 1) ||
                                '(' == cetaNameLower.charAt(i + 1) ||
                                ')' == cetaNameLower.charAt(i + 1)){
                            cetaNameLower.deleteCharAt(i);
                            foundWork = true;
                            nextNdx = i;
                            continue;
                        }
                    }
                    if(!foundWork) nextNdx = i + 1;
                }
                
                StringBuffer stars2NameLower = new StringBuffer(stars2Name.toString().trim().toLowerCase());
                nextNdx = 0;
                while(true) { // normalize the name by removing all extra blanks. If punctuation then remove all blanks.
                    foundWork = false;
                    int i = stars2NameLower.indexOf(" ", nextNdx);
                    if(i == -1) break;
                    if(i > 0) {
                        // check for non-lettter to left
                        if('.' == stars2NameLower.charAt(i - 1) ||
                                '\'' == stars2NameLower.charAt(i - 1) ||
                                '/' == stars2NameLower.charAt(i - 1) ||
                                '(' == stars2NameLower.charAt(i - 1) ||
                                ')' == stars2NameLower.charAt(i - 1)){
                            stars2NameLower.deleteCharAt(i);
                            foundWork = true;
                            nextNdx = i;
                            continue;
                        }
                    }
                    if(i < stars2NameLower.length() - 2) {
                        // check for non-lettter to right
                        if(' ' == stars2NameLower.charAt(i + 1) ||  //blank
                                '.' == stars2NameLower.charAt(i + 1) ||
                                '\'' == stars2NameLower.charAt(i + 1) ||
                                '/' == stars2NameLower.charAt(i + 1) ||
                                '(' == stars2NameLower.charAt(i + 1) ||
                                ')' == stars2NameLower.charAt(i + 1)){
                            stars2NameLower.deleteCharAt(i);
                            foundWork = true;
                            nextNdx = i;
                            continue;
                        }
                    }
                    if(!foundWork) nextNdx = i + 1;
                }
                nameMap.put(cetaNameLower.toString(), stars2NameLower.toString());
            }
        } catch(IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            logger.error("The name mappings are expected to be at " + dir + File.separator + "migrationData" + File.separator + "cetaNameMappings.txt" +
                    " and be of the format \"CETANAME:STARS2NAM\", one per line.\n");
        }
        logger.error("name map size=" + nameMap.size());
    }
    
    static public void dumpMissingNames(String dir) {
        FileWriter outStream;
        try {
            outStream = new FileWriter(new File(dir + File.separator + "migrationData" + File.separator, "missingCetaNames.txt"));     
            for(String s : missingNames.values()) {
                logger.error("CETA Name not known: " + s);
                outStream.write("CETA Name not known: " + s + "\n");
            }
            outStream.flush();
        } catch(IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            logger.error("The unknown names are written to " + dir + File.separator + "migrationData" + File.separator + "missingCetaNames.txt\n");
        }
    }
    
    static int minimum(int i1, int i2) {
        int min = i1;
        if(i1 < 0) {
            min = i2;
        } else {
            if(i2 >= 0) {
                if(i2 < i1) min = i2;
            }
        }
        return min;
    }
}
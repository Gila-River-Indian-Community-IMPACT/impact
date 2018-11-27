package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class FilterCharacters {

    static void filter(FileWriter log, Logger logger, File file, File cleanFile) {
        // the first character is a horizontal tab
        String goodChars = "    ±°`² abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*()_-+={[}]|\\:;<,>.?/'\"";
        HashSet<Integer> good = new HashSet<Integer>(100);
        for(int i = 0; i < goodChars.length(); i++) {
            good.add(new Integer(goodChars.codePointAt(i)));
            if(Character.charCount(goodChars.codePointAt(i)) > 1) {
                try {
                    log.write("Character " + i + " requires " + Character.charCount(goodChars.codePointAt(i)) + 
                    " characters");
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage(), ioe);
                    try{log.write(ioe.getMessage()); } catch(IOException ioe2) {}
                }
            }
        }
        FileWriter outStream = null;
        FileReader inStream = null;
        int numSeparators = 0;
        try {
            outStream = new FileWriter(cleanFile);
            inStream = new FileReader(file);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            try{log.write(ioe.getMessage()); } catch(IOException ioe2) {}
        }
        try {
            int c = 0;
            while(true) {
                c = inStream.read();
                if(c == -1) break;
                if(c == '|') numSeparators++;
 
                if(c == 8220 || c == 8221) c = '"';  // slanted double quotes are illegal
                if(c == 8217) c = '\'';              // one of the slanted single quotes is illegal
                if(!good.contains(new Integer(c)) && c != 13 && c != 10) {
/*
                	char cc = (char)c;
                    log.write("at " + numSeparators + " v=" + c + " '"  
                            + cc +  "'\n");
                    log.flush();
*/
                } else {
                    outStream.write(c);
                }
            }
            outStream.flush();
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            try{log.write(ioe.getMessage()); } catch(IOException ioe2) {}
        }
        try {
            outStream.close();
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            try{log.write(ioe.getMessage()); } catch(IOException ioe2) {}
        }
    }

}
package us.oh.state.epa.stars2.framework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;


/**
 * Static utility methods.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.0
 * @author Tom Dixon
 */
public class Utility {
	

	public static Timestamp formatEndOfDay(Timestamp date) {
		Calendar now = new GregorianCalendar();
		now.setTimeInMillis(date.getTime());
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		now.add(Calendar.DAY_OF_MONTH, 1);
		now.add(Calendar.MILLISECOND, -1);
		return new Timestamp(now.getTimeInMillis());
	}

	public static java.util.Date formatEndOfDay(java.util.Date date) {
		Calendar now = new GregorianCalendar();
		now.setTimeInMillis(date.getTime());
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		now.add(Calendar.DAY_OF_MONTH, 1);
		now.add(Calendar.MILLISECOND, -1);
		return new java.util.Date(now.getTimeInMillis());
	}

	public static Timestamp formatBeginOfDay(Timestamp date) {
		Calendar now = new GregorianCalendar();
		now.setTimeInMillis(date.getTime());
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return new Timestamp(now.getTimeInMillis());
	}

	public static java.util.Date formatBeginOfDay(java.util.Date date) {
		Calendar now = new GregorianCalendar();
		now.setTimeInMillis(date.getTime());
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return new java.util.Date(now.getTimeInMillis());
	}


	/**
	 * Makes an ArrayList out of "newStuff". The order of objects in "newStuff"
	 * is maintained. Returns an empty ArrayList if "newStuff" is null or empty.
	 * 
	 * @param newStuff
	 *            Object[]
	 * 
	 * @return ArrayList An ArrayList of objects.
	 */
	static public final <T extends Object> ArrayList<T> createArrayList(
			T[] newStuff) {
		ArrayList<T> ret = new ArrayList<T>();
		if ((newStuff != null) && (newStuff.length > 0)) {
			for (T obj : newStuff) {
				ret.add(obj);
			}
		}

		return ret;
	}

	static public final boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	static public final boolean isNullOrEmpty(List<String> strs) {
		return strs == null || strs.isEmpty();
	}

	static public final boolean isNullOrZero(Integer integer) {
		return integer == null || integer == 0;
	}

	public static boolean isNullOrZero(Float val) {
		return val == null || val == 0.0f;
	}
	
	public static boolean isNullOrZero(BigDecimal val) {
		return val == null || val.compareTo(new BigDecimal("0")) == 0;
	}

	static public Integer tryParseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	static public Long tryParseLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	static public final Timestamp getToday() {
		Date date = new Date();
		SimpleDateFormat sdformat = new SimpleDateFormat("MM/dd/yyyy");

		return new Timestamp(new Date(sdformat.format(date)).getTime());
	}

	static public List<String> unZipIt(InputStream zipFile, String outputFolder) {
		List<String> unzipFilePaths = new ArrayList<String>();

		byte[] buffer = new byte[1024];

		try {

			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(zipFile);

			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				String fileName = ze.getName();
				String filePath = outputFolder + File.separator + fileName;

				// delete the older file if the file is exists in the output
				// folder
				File olderFile = new File(filePath);
				if (olderFile.exists()) {
					deleteFolder(olderFile);
					olderFile = null;
				}

				File newFile = new File(filePath);

				System.out.println("newFile.getParent()"+newFile.getParent());
				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();
				//newFile.mkdir();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				unzipFilePaths.add(filePath);
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return unzipFilePaths;
	}

	static public void deleteFolder(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					File fileDelete = new File(file, temp);

					deleteFolder(fileDelete);
				}

				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			file.delete();
		}
	}

	static public String toString(String[] source, String split) {
		if (source == null)
			return "";

		StringBuffer sb = new StringBuffer();
		for (String str : source) {
			sb.append(str + split);
		}

		return sb.toString();
	}
	
	static public <T> boolean hasDuplicate(Iterable<T> iterableContainer) {
		boolean ret = false;
		
		Set<T> set = new HashSet<T>();
		for(T item : iterableContainer) {
			if(!set.add(item)) {
				ret = true;
			}
		}
		
		return ret;
	}
	
	static public boolean copyIt(InputStream inputFile, String outputFolder){
		
		FileOutputStream output = null;
		try{
		// create output directory is not exists
		File folder = new File(outputFolder);
		if (!folder.exists()) {
			folder.mkdir();
		}
		String outputFilePath = outputFolder + File.separator + "FacilityIds.csv";
	
		File f = new File(outputFilePath);
		
		output = new FileOutputStream(f);
		
		byte[] buf = new byte[1024];
		
		int bytesRead;
		
		while ((bytesRead = inputFile.read(buf)) > 0) {
		
		            output.write(buf, 0, bytesRead);
		
		}
		
		inputFile.close();
		
		output.close();
		
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}

					
		return true;
	}
	
static public boolean copyApplication(InputStream inputFile, String outputFolder){
		
		FileOutputStream output = null;
		try{
		// create output directory is not exists
		File folder = new File(outputFolder);
		if (!folder.exists()) {
			folder.mkdir();
		}
		String outputFilePath = outputFolder + File.separator + "NSRApplication.csv";
	
		File f = new File(outputFilePath);
		
		output = new FileOutputStream(f);
		
		byte[] buf = new byte[1024];
		
		int bytesRead;
		
		while ((bytesRead = inputFile.read(buf)) > 0) {
		
		            output.write(buf, 0, bytesRead);
		
		}
		
		inputFile.close();
		
		output.close();
		
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}

					
		return true;
	}

	/**
	 * Returns a string comprising n copies of a given string delimited with
	 * a given delimit character 
	 * @param n
	 * @param s
	 * @param delimiter
	 * @return
	 */
	public static String nCopyAndJoin(int n, String s, char delimiter) {
		String ret = null;
		
		if(n > 0 && !isNullOrEmpty(s)) {
			ret = StringUtils.join(
					Collections.nCopies(n, s).toArray(new String[0]),
					delimiter);
		}
		
		return ret;
	}
	
	/**
	 * Returns a String representing the given object
	 * 
	 * @param object
	 * @return String
	 */
	
	public static <T> String stringValueOf(T object) {

		String value = null;

		if (null != object) {

			if (object instanceof String) {
				value = (String) object;
			} else if (object instanceof Integer) {
				Integer i = (Integer) object;
				value = Integer.toString(i);
			} else if (object instanceof Float) {
				Float f = (Float) object;
				value = Float.toString(f);
			} else if (object instanceof Double) {
				Double d = (Double) object;
				value = Double.toString(d);
			} else if (object instanceof BigDecimal) {
				BigDecimal bd = (BigDecimal) object;
				value = bd.toPlainString();
			} else if (object instanceof Boolean) {
				Boolean b = (Boolean) object;
				value = b ? "True" : "False";
			} else if (object instanceof Timestamp) {
				Timestamp ts = (Timestamp) object;
				value = ts.toString();
			} else if (object instanceof Date) {
				Date d = (Date) object;
				value = d.toString();
			} else {
				 // unsupported data type
			}
		}

		return value;
	}
	
	/**
	 * Takes a numeric string and a scale as argument, and returns a string
	 * rounded up to the given scale
	 * @param s
	 * @param scale
	 * @return rounded value
	 */
	public static String roundUp(final String s, final int scale) {

		String roundedValue = null;

		if (isNullOrEmpty(s)) {
			throw new IllegalArgumentException("Input parameter s is null");
		}
		
		if (0 > scale) {
			throw new IllegalArgumentException("Invalid scale " + scale);
		}
		

		BigDecimal d = new BigDecimal(s);
		
		if (s.contains("E") || s.contains("e")) {
			
			if (d.toString().contains("E") || d.toString().contains("e")) {
				throw new RuntimeException("Value " + s + " too large to convert to numeric string");
			}
			
			return roundUp(d.toString(), scale);
			
		} 

		if (-1 != s.indexOf(".")) {
			if (s.substring(s.indexOf(".") + 1).length() > scale) {
				String format = "%." + scale + "f";
				roundedValue = String.format(format, d);
			} else {
				roundedValue = s;
			}
		} else {
			roundedValue = s;
		}

		return roundedValue;
	}

}

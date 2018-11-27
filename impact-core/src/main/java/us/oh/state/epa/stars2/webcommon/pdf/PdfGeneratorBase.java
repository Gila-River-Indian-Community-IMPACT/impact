package us.oh.state.epa.stars2.webcommon.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import us.oh.state.epa.common.util.DateUtils;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Base class for classes generating PDF documents. This class defines a set of
 * fonts and methods that are useful for rendering data in a PDF document.
 *
 */
public class PdfGeneratorBase implements java.io.Serializable {

	private static final long serialVersionUID = -8734320334354146212L;

	protected int defaultLeading = 10;
    protected int titleLeading = 15;
    
    protected int titleFontSize = 12;
    protected int defaultFontSize = 10;
    protected int italicFontSize = 10;
    protected int smallerFontSize = 8;
    protected int smallFontSize = 6;
    
    protected String defaultFontFamily = FontFactory.HELVETICA;
    protected String dataFontFamily = FontFactory.COURIER;

    protected Font normalFont = FontFactory.getFont(defaultFontFamily, defaultFontSize, Font.NORMAL);
    protected Font boldFont = FontFactory.getFont(defaultFontFamily, defaultFontSize, Font.BOLD);
    protected Font italicFont = FontFactory.getFont(defaultFontFamily, italicFontSize, Font.ITALIC);
    protected Font underlineFont = FontFactory.getFont(defaultFontFamily, defaultFontSize, Font.UNDERLINE);
    
    protected Font titleFont = FontFactory.getFont(defaultFontFamily, titleFontSize, Font.BOLD);
    protected Font dataFont = FontFactory.getFont(dataFontFamily, defaultFontSize, Font.NORMAL);
    
    protected Font smallBold = FontFactory.getFont(defaultFontFamily, smallFontSize, Font.BOLD);
    protected Font smallNormal = FontFactory.getFont(defaultFontFamily, smallFontSize, Font.NORMAL);
    protected Font smallerNormal = FontFactory.getFont(defaultFontFamily, smallerFontSize, Font.NORMAL);
    protected Font smallerBold = FontFactory.getFont(defaultFontFamily, smallerFontSize, Font.BOLD);
    protected Font smallData = FontFactory.getFont(dataFontFamily, smallFontSize, Font.NORMAL);
    protected Font smallerUnderline = FontFactory.getFont(defaultFontFamily, smallerFontSize, Font.UNDERLINE);
    
    protected String userName;
    protected Timestamp submitDate;
    private boolean submittedPDFDoc;
    private boolean includeAllAttachments;
	private boolean attestationOnly;
	private boolean attestationAttached;

	protected String certificationNote;
	
	private static Logger logger = Logger.getLogger(PdfGeneratorBase.class);

	/**
     * Create a table that is suitable to be displayed as the title for a document.
     * @param title array of String objects to be displayed as the title. Each string
     * will be displayed in a separate row.
     * @return
     * @throws DocumentException 
     */
    protected Table createTitleTable(String[] title) throws DocumentException {
        
        Table titleTable = new Table(1);
        titleTable.setAlignment(Element.ALIGN_CENTER);
        titleTable.setBorder(0);
        
        for (String str : title) {
            Paragraph para = new Paragraph(titleLeading, str, titleFont);
            para.setAlignment(Element.ALIGN_CENTER);
            Cell cell = new Cell(para);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleTable.addCell(cell);
        }
        
        if (getUserName() != null || attestationOnly) {
            Paragraph blankParagraph = new Paragraph(" ");
            Paragraph padParagraph = new Paragraph();
            for (int i=0; i<3; i++) {
                padParagraph.add(blankParagraph);
            }
            Cell cell = new Cell(padParagraph);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleTable.addCell(cell);
            
            Timestamp submitDate = getSubmitDate();
            if (submitDate == null) {
                submitDate = new Timestamp(System.currentTimeMillis());
            }
            
            cell = new Cell(createCertificationTable(submitDate));
            cell.setBorder(0);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            titleTable.addCell(cell);
        }
        
        return titleTable;
    }
    
    /**
     * Create a table that is suitable to be displayed as the title for a document.
     * @param title array of String objects to be displayed as the title. Each string
     * will be displayed in a separate row.
     * @return
     * @throws BadElementException
     */
    protected Table createTitleTable(String title,String subTitle) throws BadElementException {
        
        Table titleTable = new Table(3);
        titleTable.setWidth(98);
        float[] widths = {1,3,1};
        titleTable.setWidths(widths);
        titleTable.setAlignment(Element.ALIGN_CENTER);
        titleTable.setBorder(0);
        
        
        //ROW 1
        titleTable.addCell(createHeaderCellValue(""));
        
        Paragraph para = new Paragraph(titleLeading, title, titleFont);
        para.setAlignment(Element.ALIGN_CENTER);
        Cell cell = new Cell(para);
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleTable.addCell(cell);
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d yyyy, HH:mm:ss");
        Paragraph para2 = new Paragraph(titleLeading, sdf.format(Calendar.getInstance().getTime()), smallerNormal);
        para2.setAlignment(Element.ALIGN_RIGHT);
        Cell cell2 = new Cell(para2);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        titleTable.addCell(cell2);
        
        //ROW 2 (only create if there is a subtitle
        if (subTitle != null && subTitle.length()>0) {
            titleTable.addCell(createHeaderCellValue(""));
            
            Paragraph para3 = new Paragraph(titleLeading, subTitle, boldFont);
            para3.setAlignment(Element.ALIGN_CENTER);
            Cell cell3 = new Cell(para3);
            cell3.setBorder(0);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleTable.addCell(cell3);
            
            titleTable.addCell(createHeaderCellValue(""));
        }
        return titleTable;
    }
    
    /**
     * Create a table to display <code>text</code> in a single column
     * with no borders. Text will be displayed defaultLeading with
     * dataFont.
     * @param text the text to be displayed.
     * @return
     * @throws BadElementException
     */
    protected Table createTextTable(String text) throws BadElementException {
        return createTextTable(defaultLeading, text, dataFont);
    }
    
    /**
     * Create a table to display <code>text</code> in a single column
     * with no borders.
     * @param leading leading value to use for text.
     * @param text the text to be displayed.
     * @param font the font.
     * @return
     * @throws BadElementException
     */
    protected Table createTextTable(int leading, String text, Font font) throws BadElementException {
        Table table = new Table(1);
        table.setBorder(0);
        table.setWidth(90);
        Cell cell = new Cell();
        cell.setBorder(0);
        if (text != null) {
            cell.add(new Phrase(leading, text, font));
        }
        table.addCell(cell);
        return table;
    }
    
    /**
     * Create a cell displaying <code>headerText</code> centered and rendered
     * in bold.
     * @param headerText text to be displayed.
     * @return
     */
    protected Cell createHeaderCell(String headerText) {
        Cell header = createCell(headerText, boldFont);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        return header;
    }
    
    /**
     * Create a cell displaying <code>dataText</code> with the
     * <code>dataFont</code> font.
     * @param dataText text to be displayed.
     * @return
     */
    protected Cell createDataCell(String... dataText) {
        return createCell(dataFont, dataText);
    }
    
    /**
     * Create a cell displaying <code>text</code> with the normalFont font.
     * @param text text to be displayed.
     * @return
     */
    public Cell createCell(String... text) {
        return createCell(defaultLeading, normalFont, text);
    }
    
    /**
     * Create a cell displaying <code>text</code> with the bold font, right justified, with no border.
     * @param text text to be displayed.
     * @return
     */
    protected Cell createHeaderCellLabel(String text) {
        Cell cell = createCell(text,getBoldFont());
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }
    
    /**
     * Create a cell displaying <code>text</code> with the normalFont font, left-justified, with no border.
     * @param text text to be displayed.
     * @return
     */
    protected Cell createHeaderCellValue(String... text) {
        Cell cell = createCell(text);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }


    
    /**
     * Create a cell displaying <code>cellText</code> with the cellFont font.
     * @param cellText text to be displayed.
     * @param cellFont font in which text will be displayed.
     * @return
     */
    protected Cell createCell(String cellText, Font cellFont) {
        return createCell(defaultLeading, cellText, cellFont);
    }
    
    protected Cell createCell(Font cellFont, String... cellText) {
        return createCell(defaultLeading, cellFont, cellText);
    }
    
    /**
     * Create a cell with the specified attributes.
     * @param leading leading value for the Phrase in the cell.
     * @param cellText text to be displayed.
     * @param cellFont font in which text will be displayed.
     * @return
     */
    protected Cell createCell (int leading, String cellText, Font cellFont) {
    	return createCell(leading,cellFont,cellText);
    }
    
    protected Cell createCell (int leading, Font cellFont, String... cellText) {
        Cell cell = new Cell();
        for (String s : cellText) {
            cell.add(new Phrase(leading, s, cellFont));
            cell.add(Chunk.NEWLINE);
        }
        return cell;
    }
    
    /**
     * Create a cell displaying an 'x' if <code>checked</code>
     * is true. Note: this is a simple attempt at providing checkboxes.
     * Better alternatives are welcome!
     * @param checked
     * @return
     */
    protected Cell createCheckCell(boolean checked) {
        return createCheckCell(9, checked, normalFont);
    }
    
    /**
     * Create a check cell with the specified attributes.
     * @param leading leading to be used for Phrase.
     * @param checked is cell checked?
     * @param cellFont font used for displaying 'x'.
     * @see us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase.createCheckCell
     * @return
     */
    protected Cell createCheckCell(int leading, boolean checked, Font cellFont) {
        return createCell(leading, checked ? "x" : " ", cellFont);
    }
    

    /**
     * Create a paragraph that is suitable for use as the title for a ListItem
     * with the first argument displayed in bold and the second aregument displayed
     * in the normal font.
     * @param boldText text to be displayed in <code>boldFont</code>.
     * @param normText text to be displayed in <code>normalFont</code>.
     * @return
     */
    protected Paragraph createItemTitle(String boldText, String normText) {
        Paragraph itemTitle = new Paragraph(defaultLeading);
        itemTitle.add(new Phrase(defaultLeading, boldText, boldFont));
        itemTitle.add(new Phrase(defaultLeading, normText, normalFont));
        return itemTitle;
    }
    
    /**
     * Create a table to display the name/value pairs in <code>nameValueMap</code>.
     * Names will be displayed using <code>normalFont</code> and values will be
     * displayed using <code>dataFont</code>.
     * @param nameValueMap map of name/value pairs.
     * @param columns number of columns in which name/value pairs should be displayed.
     * @return
     * @throws BadElementException
     */
    protected Table createNameValueTable(LinkedHashMap<String, String> nameValueMap, int columns) throws BadElementException {
        return createNameValueTable(nameValueMap, columns, normalFont, dataFont);
    }
    
    /**
     * Create a name/value table with the specified attributes.
     * @param nameValueMap map of name/value pairs.
     * @param columns number of columns in which name/value pairs should be displayed.
     * @param nameFont font used for displaying names.
     * @param valueFont font used for displaying values.
     * @return
     * @throws BadElementException
     */
    protected Table createNameValueTable(LinkedHashMap<String, String> nameValueMap, 
            int columns, Font nameFont, Font valueFont) throws BadElementException {
        Table table = new Table(columns * 2);
        table.setBorder(0);
        table.setPadding(2);

        for (String name : nameValueMap.keySet()) {
            String value = nameValueMap.get(name);

            Cell nameCell = new Cell();
            nameCell.setBorder(0);
            nameCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            nameCell.add(new Phrase(defaultLeading, name, nameFont));

            Cell valueCell = new Cell();
            valueCell.setBorder(0);
            if (value != null) {
                valueCell.add(new Phrase(defaultLeading, value, valueFont));
            }
            table.addCell(nameCell);
            table.addCell(valueCell);
        }

        return table;
    }
    
    protected Table createCertificationTable(Timestamp submitDate) throws DocumentException {
    	int columnCount = attestationOnly ? 4 : 2;
        Table table = new Table(columnCount);
        table.setBorder(0);
        table.setPadding(5);

        Cell certificationLanguageCell = new Cell();
        certificationLanguageCell.setBorder(0);
        certificationLanguageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        if (!attestationAttached) {
        	certificationLanguageCell.add(getCertificationLanguage());
        } else {
        	certificationLanguageCell.add(getCROMERRCertificationLanguage());
        }
        certificationLanguageCell.setColspan(columnCount);
        table.addCell(certificationLanguageCell);
        
        Paragraph blankParagraph = new Paragraph(" ");
        Paragraph padParagraph = new Paragraph();
        padParagraph.add(blankParagraph);
        Cell padCell = new Cell(padParagraph);
        padCell.setColspan(columnCount);
        padCell.setBorder(0);
        padCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        if (!attestationOnly) {
            float[] widths = {1,2};
            table.setWidths(widths);
	        Cell userCell = new Cell(new Phrase(titleLeading, "Account: " + userName, smallerBold));
	        userCell.setBorder(0);
	        userCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("MMM d yyyy, HH:mm:ss");
	        Cell dateCell = new Cell(new Phrase(titleLeading, "Date/time submitted: " + sdf.format(submitDate), smallerBold));
	        dateCell.setBorder(0);
	        dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        
	        table.addCell(userCell);
	        table.addCell(dateCell);
        } else {
            float[] widths = {6, 6, 1, 3};
            table.setWidths(widths);
            table.setWidth(95);
            
            table.addCell(padCell);
        	
        	Cell nameTitleCell = new Cell(new Phrase(titleLeading, "Responsible Official Name (please print): ", smallerBold));
        	nameTitleCell.setBorder(0);
        	nameTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        	
        	Cell nameCell = new Cell(new Phrase(titleLeading, "                               ", smallerBold));
        	nameCell.setBorderWidthBottom(1f);
        	nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        	
        	Cell titleTitleCell = new Cell(new Phrase(titleLeading, "Title: ", smallerBold));
        	titleTitleCell.setBorder(0);
        	titleTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        	
        	Cell titleCell = new Cell(new Phrase(titleLeading, "                               ", smallerBold));
        	titleCell.setBorderWidthBottom(1f);
        	titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        	
        	table.addCell(nameTitleCell);
	        table.addCell(nameCell);
        	table.addCell(titleTitleCell);
	        table.addCell(titleCell);
	        
	        table.addCell(padCell);
        	
        	Cell signatureTitleCell = new Cell(new Phrase(titleLeading, "Responsible Official Signature: ", smallerBold));
        	signatureTitleCell.setBorder(0);
        	signatureTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        	
        	Cell signatureCell = new Cell(new Phrase(titleLeading, "                          ", smallerBold));
        	signatureCell.setBorderWidthBottom(1f);
        	signatureCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        	
        	Cell dateTitleCell = new Cell(new Phrase(titleLeading, "Date: ", smallerBold));
        	dateTitleCell.setBorder(0);
        	dateTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        	Cell dateCell = new Cell(new Phrase(titleLeading, "                     ", smallerBold));
        	dateCell.setBorderWidthBottom(1f);
        	dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        
        	table.addCell(signatureTitleCell);
	        table.addCell(signatureCell);
	        table.addCell(dateTitleCell);
	        table.addCell(dateCell);
        }
        
        if (certificationNote != null) {
            table.addCell(padCell);
            table.addCell(padCell);
        	Cell noteCell = new Cell(new Paragraph(defaultLeading, formatCertificationLanguage(certificationNote), smallerBold));
        	noteCell.setBorder(0);
        	noteCell.setColspan(columnCount);
        	table.addCell(noteCell);
        }

        return table;
    }
    
    protected Phrase getPageNumberPhrase() {
        return new Phrase("Page ");
    }
    
    public final Font getBoldFont() {
        return boldFont;
    }

    public final void setBoldFont(Font boldFont) {
        this.boldFont = boldFont;
    }

    public final Font getDataFont() {
        return dataFont;
    }

    public final void setDataFont(Font dataFont) {
        this.dataFont = dataFont;
    }

    public final String getDataFontFamily() {
        return dataFontFamily;
    }

    public final void setDataFontFamily(String dataFontFamily) {
        this.dataFontFamily = dataFontFamily;
    }

    public final String getDefaultFontFamily() {
        return defaultFontFamily;
    }

    public final void setDefaultFontFamily(String defaultFontFamily) {
        this.defaultFontFamily = defaultFontFamily;
    }

    public final int getDefaultFontSize() {
        return defaultFontSize;
    }

    public final void setDefaultFontSize(int defaultFontSize) {
        this.defaultFontSize = defaultFontSize;
    }

    public final int getDefaultLeading() {
        return defaultLeading;
    }

    public final void setDefaultLeading(int defaultLeading) {
        this.defaultLeading = defaultLeading;
    }

    public final Font getItalicFont() {
        return italicFont;
    }

    public final void setItalicFont(Font italicFont) {
        this.italicFont = italicFont;
    }

    public final int getItalicFontSize() {
        return italicFontSize;
    }

    public final void setItalicFontSize(int italicFontSize) {
        this.italicFontSize = italicFontSize;
    }

    public final Font getNormalFont() {
        return normalFont;
    }

    public final void setNormalFont(Font normalFont) {
        this.normalFont = normalFont;
    }

    public final Font getSmallBold() {
        return smallBold;
    }

    public final void setSmallBold(Font smallBold) {
        this.smallBold = smallBold;
    }

    public final Font getSmallData() {
        return smallData;
    }

    public final void setSmallData(Font smallData) {
        this.smallData = smallData;
    }

    public final int getSmallFontSize() {
        return smallFontSize;
    }

    public final void setSmallFontSize(int smallFontSize) {
        this.smallFontSize = smallFontSize;
    }

    public final Font getSmallNormal() {
        return smallNormal;
    }

    public final void setSmallNormal(Font smallNormal) {
        this.smallNormal = smallNormal;
    }

    public final Font getTitleFont() {
        return titleFont;
    }

    public final void setTitleFont(Font titleFont) {
        this.titleFont = titleFont;
    }

    public final int getTitleFontSize() {
        return titleFontSize;
    }

    public final void setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    public final int getTitleLeading() {
        return titleLeading;
    }

    public final void setTitleLeading(int titleLeading) {
        this.titleLeading = titleLeading;
    }

    public final Font getUnderlineFont() {
        return underlineFont;
    }

    public final void setUnderlineFont(Font underlineFont) {
        this.underlineFont = underlineFont;
    }
    
    protected Paragraph getCertificationLanguage()  throws DocumentException {
        String str = SystemPropertyDef.getSystemPropertyValue("DAPCAttestationMessage", null);
        // format the certification language better
        Paragraph attestation = new Paragraph(defaultLeading, formatCertificationLanguage(str), smallerBold);

        return attestation;
    }
    
    protected Paragraph getCROMERRCertificationLanguage()  throws DocumentException {
		String message = "";
		InfrastructureService infraBO;
		try {
			infraBO = ServiceFactory.getInstance().getInfrastructureService();
			message = infraBO.getCROMERRAttestationMessage(userName);
		} catch (ServiceFactoryException e) {
			logger.error("Exception accessing infrastructure service", e);
		} catch (RemoteException e) {
			logger.error("Exception retrieving CROMERR attestation language", e);
		}
        Paragraph attestation = new Paragraph(defaultLeading, formatCertificationLanguage(message), smallerBold);

        return attestation;
    }
    
    protected String formatCertificationLanguage(String certificationLanguage) {
        String language = certificationLanguage.replaceAll("<br>", "\n");
        language = language.replaceAll("<p>", "\n");
        language = language.replaceAll("<hr>", "\n");
        language = language.replaceAll("<[^>]+>", "");
        return language;
    }

    public final String getUserName() {
        return userName;
    }

    public final void setUserName(String userName) {
        this.userName = userName;
    }
    
    public final Timestamp getSubmitDate() {
        return submitDate;
    }

    public final void setSubmitDate(Timestamp submitDate) {
        this.submitDate = submitDate;
    }

    public final boolean isSubmittedPDFDoc() {
		return submittedPDFDoc;
	}

	public final void setSubmittedPDFDoc(boolean submittedPDFDoc) {
		this.submittedPDFDoc = submittedPDFDoc;
	}
	
	public final boolean isIncludeAllAttachments() {
		return includeAllAttachments;
	}

	public final void setIncludeAllAttachments(boolean includeAllAttachments) {
		this.includeAllAttachments = includeAllAttachments;
	}
	
	public final boolean isAttestationOnly() {
		return attestationOnly;
	}

	public final void setAttestationOnly(boolean attestationOnly) {
		this.attestationOnly = attestationOnly;
	}
    
    public final String getCertificationNote() {
		return certificationNote;
	}

	public final void setCertificationNote(String certificationNote) {
		this.certificationNote = certificationNote;
	}
	public final boolean isAttestationAttached() {
		return attestationAttached;
	}

	public final void setAttestationAttached(boolean attestationAttached) {
		this.attestationAttached = attestationAttached;
		if (this.attestationAttached) {
			setCertificationNote(
					"Note: An attestation document signed by the Responsible Official" +
					" for this facility is included in the attachments section of this request.");
		}
	}
    
    public static final void addTradeSecretWatermarkHorizontal(String filePath) throws IOException, DocumentException {
        String fullFilePath = filePath;
        String fullParentPath = null;
        String relativeParentPath = null;
        if (filePath.endsWith(".pdf")) {
            if (!filePath.startsWith(DocumentUtil.getFileStoreRootPath())) {
                fullFilePath = DocumentUtil.getFileStoreRootPath() + File.separator + filePath;
            }
            File origFile = new File(fullFilePath);
            fullParentPath = origFile.getParent();
            relativeParentPath = fullParentPath.substring(DocumentUtil.getFileStoreRootPath().length());

            String tmpFileName = origFile.getName().replace(".pdf", "_tmp.pdf");
            File tmpFile = new File(fullParentPath + File.separator + tmpFileName);
            
            // copy document to temporary location so we can work on it
            DocumentUtil.copyDocument(relativeParentPath + File.separator + origFile.getName(), 
                    relativeParentPath + File.separator + tmpFile.getName());
            
            // overwrite original File with watermarked version
            PdfReader pdfReader = new PdfReader(tmpFile.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(origFile);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            java.awt.Image i = new java.awt.image.BufferedImage(700, 100,
                    java.awt.image.BufferedImage.TYPE_USHORT_GRAY);
            java.awt.Font bigF = new java.awt.Font("COURIER", java.awt.Font.BOLD,
                    70);
            java.awt.Graphics g = i.getGraphics();
            g.setFont(bigF);
            g.setColor(new Color(10, 10, 10));
            g.drawString("TRADE SECRET", 20, 65);
            com.lowagie.text.Image iTextImage = com.lowagie.text.Image.getInstance(
                    i, new Color(255, 255, 255));
            iTextImage.setAbsolutePosition(220, 45);
            iTextImage.setRotationDegrees(60);
            iTextImage.setInverted(true);
 
            PdfContentByte contentByte = null;
            for (int p=1; p<=pdfReader.getNumberOfPages(); p++) {
                contentByte = pdfStamper.getUnderContent(p);
                if (contentByte != null) {
                    contentByte.addImage(iTextImage);
                } else {
                    logger.error("No content byte for page " + p);
                }
            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();
            
            // remove temp file once we're done
            tmpFile.delete();
        } else {
            logger.error("No pdf file found at path " + filePath);
        }
    }
	
	public static final void addTradeSecretWatermark(String filePath) throws IOException, DocumentException {
		String fullFilePath = filePath;
		String fullParentPath = null;
		String relativeParentPath = null;
		if (filePath.endsWith(".pdf")) {
			if (!filePath.startsWith(DocumentUtil.getFileStoreRootPath())) {
				fullFilePath = DocumentUtil.getFileStoreRootPath() + File.separator + filePath;
			}
			File origFile = new File(fullFilePath);
			fullParentPath = origFile.getParent();
			relativeParentPath = fullParentPath.substring(DocumentUtil.getFileStoreRootPath().length());

			String tmpFileName = origFile.getName().replace(".pdf", "_tmp.pdf");
			File tmpFile = new File(fullParentPath + File.separator + tmpFileName);
			
			// copy document to temporary location so we can work on it
			DocumentUtil.copyDocument(relativeParentPath + File.separator + origFile.getName(), 
					relativeParentPath + File.separator + tmpFile.getName());
			
			// overwrite original File with watermarked version
			PdfReader pdfReader = new PdfReader(tmpFile.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(origFile);
			PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
			java.awt.Image i = new java.awt.image.BufferedImage(700, 100,
					java.awt.image.BufferedImage.TYPE_USHORT_GRAY);
			java.awt.Font bigF = new java.awt.Font("COURIER", java.awt.Font.BOLD,
					70);
			java.awt.Graphics g = i.getGraphics();
			g.setFont(bigF);
			g.setColor(new Color(10, 10, 10));
			g.drawString("TRADE SECRET", 20, 65);
			com.lowagie.text.Image iTextImage = com.lowagie.text.Image.getInstance(
					i, new Color(255, 255, 255));
			iTextImage.setAbsolutePosition(80, 120);
			iTextImage.setRotationDegrees(60);
			iTextImage.setInverted(true);
 
			PdfContentByte contentByte = null;
			for (int p=1; p<=pdfReader.getNumberOfPages(); p++) {
				contentByte = pdfStamper.getUnderContent(p);
				if (contentByte != null) {
					contentByte.addImage(iTextImage);
				} else {
					logger.error("No content byte for page " + p);
				}
			}
			pdfStamper.close();
			pdfReader.close();
			fos.close();
			
			// remove temp file once we're done
			tmpFile.delete();
		} else {
			logger.error("No pdf file found at path " + filePath);
		}
	}
	
	protected String formatDate(Timestamp date) {
		String s = null;
		if (null != date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			s = DateUtils.toDateString(cal);
		}
		return s;
	}

}

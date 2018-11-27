package us.oh.state.epa.stars2.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Stack;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class WordParser {

    // Our SAX parser.
    protected XMLReader parser;
    
    // Starting (and ending) ContentHandler
    protected WordElementHandler handler;
    
    // Current element list.
    protected Stack<WordElementHandler> wordElementStack = new Stack<WordElementHandler>();
    protected static Logger logger = Logger.getLogger(WordParser.class);
    
    protected Exception ex = null;

    public void parseWordDoc(InputStream in, OutputStream out, 
                             boolean doSub, DocumentGenerationBean data) 
        throws DocumentGenerationException {
        
        try {
            
            final PipedInputStream pis = new PipedInputStream();
            PipedOutputStream pos = new PipedOutputStream(pis);
                        
            Thread parserThread = new Thread("WordParser.parserThread") {
                    
                    public void run() {
                        try {
                            parser = XMLReaderFactory.createXMLReader();
                            parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                            parser.setFeature("http://xml.org/sax/features/validation", false);
                            parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
                            handler = new WordElementHandler();
                            parser.setContentHandler(handler);
                            InputStreamReader isr = new InputStreamReader(pis, "UTF-8");
                            parser.parse(new InputSource(isr));
                        }
                        catch (Exception e) {
                            logger.error("Problem parsing word document:" + e.getMessage(), e);
                            ex = e;
                            try {
                                pis.close();
                            }
                            catch (Exception ei) {
                            }
                        }
                    }
                };

            parserThread.setDaemon(true);
            parserThread.start();

            int len;
            while ((len = in.read()) > 0) {
                pos.write(len);
            }
            pos.flush();
            pos.close();
            parserThread.join();
            if (ex != null) {
                throw new DocumentGenerationException("Problem with XML parse of input template: " 
                                                      + ex.getMessage(), ex);
            }

            out.write(new String("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n").getBytes());
            logger.debug("Parsed data: " + handler.getParsedData().toString());

            if (doSub) {
                DocumentGenerationUtil dgu = new DocumentGenerationUtil();
                dgu.generate(handler.getParsedData().toString(), out, data);
            }
            else {
                out.write(handler.getParsedData().toString().getBytes());
            }
        }
        catch (Exception e) {
            throw new DocumentGenerationException("Could not complete document substitutions: " + e.getMessage(), e);
        }

    }

    public static void main(String[] args) throws Exception {

        String inputZipFile = null;
        FileInputStream fis = null;
        String outputZipFile = null;
        FileOutputStream fos = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-i")) {
                inputZipFile = args[++i];
            }
            else if (args[i].equals("-o")) {
                outputZipFile = args[++i];
            }
        }
        if (inputZipFile == null || outputZipFile == null) {
            System.err.println("Usage: java -cp %STARS2HOME%\\classes us.oh.state.epa.stars2.util.WordParser "
                               + "-i inputFileName -o outputFileName");
        }
        try {
            fis = new FileInputStream(inputZipFile);
            fos = new FileOutputStream(outputZipFile);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Usage: java -cp %STARS2HOME%\\classes us.oh.state.epa.stars2.util.WordParser "
                               + "-i inputFileName -o outputFileName");
        }

        ZipInputStream zis = new ZipInputStream(fis);
        ZipOutputStream zos = new ZipOutputStream(fos);
        zos.setLevel (Deflater.BEST_COMPRESSION);

        ZipEntry ze = null;
        while ((ze = zis.getNextEntry()) != null) {

            if (ze.getName().equals("word/document.xml")) {
                // Skip it! The bloody parser will close the inputStream
                // so we will need to handle this seperately.
                continue;
            }

            ZipEntry newEntry = new ZipEntry(ze.getName());
            zos.putNextEntry(newEntry);

            int i = -1;
            while ((i = zis.read()) != -1) {
                zos.write(i);
            }
            zis.closeEntry();
            zos.closeEntry();
            zos.flush();
        }
        zis.close();
        fis.close();

        fis = new FileInputStream(inputZipFile);
        zis = new ZipInputStream(fis);
        ze = null;
        while ((ze = zis.getNextEntry()) != null) {

            if (ze.getName().equals("word/document.xml")) {
                ZipEntry newEntry = new ZipEntry(ze.getName());
                zos.putNextEntry(newEntry);
                WordParser wp = new WordParser();
                wp.parseWordDoc(zis, zos, false, null);
                zos.closeEntry();
                zos.flush();
                break;
            }
            zos.closeEntry();
        }

        zos.close();

    }

    class WordElementHandler implements ContentHandler {

        private String _qName;
        private AttributesImpl _attributes;

        @SuppressWarnings("unused")
        private boolean _insertPara;

        // Have we run this instance or are we a new child?
        private boolean _areWeStarted;

        // We are using one instance to handle customXml -> property
        private boolean _rollUpStarted;
        private boolean _rollUpFinished;

        // We are using one instance to handle concatination of <w:t>
        // and enclosing elements within property tags but not if the 
        // customXML is a foreach tag.
        private boolean _isForeach; // 
        private boolean _lookingForTxt;
        private boolean _startedTxt;
        private boolean _endedTxt;
        private boolean _ignoreTag;
            
        // Is the current element just a tag (e.g. <br />)?
        private boolean _emptyElement = true;

        // Buffers for appending child element stuff.
        private StringBuffer _myBuf = new StringBuffer();
        private StringBuffer _childBuf = new StringBuffer();

        public WordElementHandler() {
        }

        public WordElementHandler(boolean lookingForTxt, boolean startedTxt, boolean ignoreTag) {

            _lookingForTxt = lookingForTxt;
            _startedTxt = startedTxt;
            _ignoreTag = ignoreTag;
        }

        public void append(String child) {
            _childBuf.append(child);
        } 

        public StringBuffer getParsedData() {
            return _myBuf;
        }

        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) {

            try {

                if (!_areWeStarted) {

                    _qName = qName;
                    _attributes = new AttributesImpl(attributes);
                    _areWeStarted = true;

                    if (qName.equals("w:customXml") && (attributes != null)
                        && (attributes.getValue("w:uri") != null)
                        && (attributes.getValue("w:uri").equals("schemas-STARS2"))) {

                        if (_attributes == null) {
                            _attributes = new AttributesImpl(attributes);
                        }
                        
                        // Prepare to handle a "foreach" loop.
                        if ((attributes.getValue("w:element") != null)
                            && (attributes.getValue("w:element").equals("foreach"))) {
                            _qName = new String("foreach");
                            _isForeach = true;
                        }
                        
                        // Prepare to handle a "property" element.
                        else if ((attributes.getValue("w:element") != null)
                                 && (attributes.getValue("w:element").equals("property"))) {
                            _qName = new String("property");
                        }

                        _rollUpStarted = true;
                        _rollUpFinished = false;

                    }

                }

                else if (_rollUpStarted && !_rollUpFinished) {
                    
                    if (qName.equals("w:customXmlPr")) {
                        // Ignore!
                    }
                        
                    else if (qName.equals("w:attr")) {
                            
                        if (attributes.getValue("w:name") != null
                            && attributes.getValue("w:name").equals("collection")
                            && attributes.getValue("w:val") != null) {
                            _attributes.addAttribute(null, null, "collection", "CDATA",
                                                     attributes.getValue("w:val"));
                        }
                        else if (attributes.getValue("w:name") != null
                                 && attributes.getValue("w:name").equals("cursorVar")
                                 && attributes.getValue("w:val") != null) {
                            _attributes.addAttribute(null, null, "cursorVar", "CDATA",
                                                     attributes.getValue("w:val"));
                        }
                        else if (attributes.getValue("w:name") != null
                                 && attributes.getValue("w:name").equals("var")
                                 && attributes.getValue("w:val") != null) {
                            _attributes.addAttribute(null, null, "var", "CDATA",
                                                     attributes.getValue("w:val"));
                        }
                        else if (attributes.getValue("w:name") != null
                                 && attributes.getValue("w:name").equals("property")
                                 && attributes.getValue("w:val") != null) {
                            _attributes.addAttribute(null, null, "property", "CDATA",
                                                     attributes.getValue("w:val"));
                        }
                        else if (attributes.getValue("w:name") != null
                                 && attributes.getValue("w:name").equals("required")
                                 && attributes.getValue("w:val") != null) {
                            _attributes.addAttribute(null, null, "required", "CDATA",
                                                     attributes.getValue("w:val"));
                        }
                        else if (attributes.getValue("w:name") != null
                                 && attributes.getValue("w:name").equals("needPara")
                                 && attributes.getValue("w:val") != null) {
                            _insertPara = true;
                        }
                        
                    }

                }

                else {

                    if (_lookingForTxt && !_startedTxt && qName.equals("w:t")) {
                        _startedTxt = true;
                    }

                    if (_lookingForTxt && _endedTxt) {
                        _ignoreTag = true;
                    }

                    _emptyElement = false;
                    ContentHandler handler 
                        = new WordElementHandler(_lookingForTxt, _startedTxt, _ignoreTag);

                    wordElementStack.push(this);
                    parser.setContentHandler(handler);
                    handler.startElement(uri, localName, qName, attributes);
                }

            }
            catch (Exception e) {
                logger.error("Could not complete document substitutions: " 
                             + e.getMessage(), e);
            }

        }

        public void endElement(String uri, String localName, String qName) {

            try {

                if (_rollUpStarted && !_rollUpFinished) {
                    if (qName.equals("w:customXmlPr")) {
                        _rollUpFinished = true;
                        if (_isForeach) {
                            _isForeach = false;
                        }
                        else {
                            _lookingForTxt = true;
                        }
                    }
                    return;
                }

                if (_lookingForTxt && _startedTxt && !_endedTxt 
                    && qName.equals("w:t")) {
                    _endedTxt = true;
                }

                if (_qName.equals("property")) {
                    _insertPara = false;
                    _lookingForTxt = false;
                    _startedTxt = false;
                    _endedTxt = false;
                    _ignoreTag = false;
                }

                WordElementHandler parent = null;

                if (!wordElementStack.empty()) {
                    parent = wordElementStack.pop();
                }
                
                if (parent != null) {
                    parser.setContentHandler(parent);
                }

                if (parent != null && _lookingForTxt && _startedTxt && _endedTxt) {
                    parent.setEndedTxt(true);
                }

                if (_ignoreTag) {
                    return;
                }

                _myBuf.append("<" + _qName);
                if (_attributes != null) {
                    for (int i = 0; i < _attributes.getLength(); i++) {
                        _myBuf.append(" " + _attributes.getQName(i) + "=\"" 
                                      + _attributes.getValue(i) + "\"");
                    }
                }
                if (_emptyElement) {
                    _myBuf.append("/>");
                }
                else {
                    _myBuf.append(">");
                    if (_childBuf != null) {
                        _myBuf.append(_childBuf.toString());
                    }
                    _myBuf.append("</" + _qName + ">");
                }

                if (parent != null) {
                    parent.append(_myBuf.toString());
                }

            }
            catch (Exception e) {
                logger.error("Could not complete document substitutions: " 
                             + e.getMessage(), e);
            }

        }

        public void characters(char[] ch, int start, int length) {

            if (_ignoreTag) {
                return;
            }

            try {

                if (length > 0) {
                    _emptyElement = false;
                    _childBuf.append(ch, start, length);
                }
            }
            catch (Exception e) {
                logger.error("Could not complete document substitutions: " 
                             + e.getMessage(), e);
            }

        }

        public void startPrefixMapping(String prefix, String uri) {
        }

        public void endPrefixMapping(String prefix) {
        }

        public void processingInstruction(String target, String data) {
        }

        public void ignorableWhitespace(char[] ch, int start, int length) {
        }

        public void setDocumentLocator(Locator locator) {
        }

        public void skippedEntity(String name) {
        }

        public void setEndedTxt(boolean endedTxt) {
            _endedTxt = endedTxt;
        }

    }

}

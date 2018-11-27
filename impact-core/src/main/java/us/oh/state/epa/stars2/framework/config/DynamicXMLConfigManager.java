package us.oh.state.epa.stars2.framework.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * 
 * Allows you to re-read the xml file while the application is running. We could
 * just watch for the file to change, but the poses some security risks. In
 * order for the ConfigManager to re-load the file:
 * <ol>
 * <li>The configuration must have been initially read from a file rather than
 * directly from an in-memory XML Document</li>
 * <li>The user must authenticate</li>
 * </ol>
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.0
 * @author Andrew Wilcox
 */
public class DynamicXMLConfigManager extends XMLConfigManager implements
        Runnable {
    protected static final String LOCAL_ROOT = "app.config";
    protected static final String PASSWORD = "password";
    protected static final String PORT = "port";
    protected static final int DEFAULT_PORT = 12000;
    private static List<ConfigListener> listeners = new LinkedList<ConfigListener>();
    private static Logger logger = Logger.getLogger(DynamicXMLConfigManager.class);
    private String[] passwords;

    public DynamicXMLConfigManager(String fileName) throws IOException,
            SAXException, ParserConfigurationException {
        super(fileName);
        loadPasswords();
        // new Thread(this).start();
        Thread t = new Thread(this, "DynamicXMLConfigManager");
        t.setDaemon(true);
        t.start();
    }

    // public DynamicXMLConfigManager(Document doc) {
    // super(doc);
    // }

    /**
     * Loads the passwords found at app.config.password. This method expects
     * that the passwords are SHA1 hashed and URL encoded.
     * 
     * @see #run()
     */
    protected final void loadPasswords() {
        Node localRoot = getNode(LOCAL_ROOT);

        if (localRoot == null) {
            passwords = new String[0];
            return;
        }

        Node[] passwdNodes = getChildrenOf(localRoot, PASSWORD);
        passwords = new String[passwdNodes.length];

        try {
            for (int i = 0; i < passwords.length; i++) {
                String encodedHashedPassword = passwdNodes[i].getText();
                passwords[i] = URLDecoder
                        .decode(encodedHashedPassword, "UTF-8");
            }
        } catch (UnsupportedEncodingException uee) {
            logger.error("could not load configuration because:", uee);
        }
    }

    /**
     * @see us.oh.state.epa.stars2.framework.config.XMLConfigManager#registerConfigListener(us.oh.state.epa.stars2.framework.config.ConfigListener)
     */
    public final void registerConfigListener(ConfigListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * 
     */
    protected final void fireConfigChangedEvent() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).configChanged(this);
        }
    }

    /**
     * Listens on the port specified as app.config->port or port 12000 if no
     * port is specified. IF a use telnets into that port AND presents an
     * unhashed password that is in the config file as a SHA1 hash AND the
     * ConfigManager is using a configuration that was loaded from a file THEN
     * the changes from that file will be read AND all listeners will be
     * notified
     * 
     */
    public final void run() {
        logger.debug("Dynamic XML Config Manager: starting");

        // this should never be true since it's only called from the ctor
        // that takes a file name
        //
        if (!isLoadedFromFile()) {
            logger.debug("Not loaded from a file");
            return;
        }

        if (passwords.length == 0) {
            logger
                    .debug("No passwords found. The configuration cannot be change dynamically");
            return;
        }

        Node localRoot = getNode(LOCAL_ROOT);
        int port = localRoot.getAsInt(PORT);

        if (port == 0) {
            port = DEFAULT_PORT;
        }

        ServerSocket socket = null;

        try {
            socket = new ServerSocket(port);
        } catch (IOException ioe) {
            logger
                    .error("Cannot start server socket due to an exception:",
                            ioe);
            return;
        }

        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException nsae) {
            logger.error(
                    "Cannot activate the message digest due to an exception: "
                            + nsae.getMessage(), nsae);
            return;
        }

        while (true) {

            InputStream in = null;
            InputStreamReader reader = null;
            BufferedReader bReader = null;
            try {
                Socket client = socket.accept();
                in = client.getInputStream();
                reader = new InputStreamReader(in);
                bReader = new BufferedReader(reader);
                String passwd = bReader.readLine();

                messageDigest.update(passwd.getBytes());
                byte[] b = messageDigest.digest();
                String digest = new String(b);

                for (int i = 0; i < passwords.length; i++) {
                    if (passwords[i].compareTo(digest) == 0) {
                        loadFromFile();
                        break;
                    }
                }
                client.close();
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            } catch (SAXException saxe) {
                logger.error(saxe.getMessage(), saxe);
            } catch (ParserConfigurationException pce) {
                logger.error(pce.getMessage(), pce);
            } finally {
                try {
                    if (bReader != null) {
                        bReader.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                }
                catch (Exception e) {
                    // Ignore.
                }
            }
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            DynamicXMLConfigManager cfg = new DynamicXMLConfigManager(args[0]);

            logger.debug("Looking at value of "
                    + DynamicXMLConfigManager.LOCAL_ROOT + ".test");

            while (true) {
                Node node = cfg.getNode(DynamicXMLConfigManager.LOCAL_ROOT
                        + ".test");
                logger.debug(node.getText());
                Thread.sleep(1000);
            }
        }
            
        System.out.println("use: DynamicXMLConfigManager <xmlFileName>");
    }
}

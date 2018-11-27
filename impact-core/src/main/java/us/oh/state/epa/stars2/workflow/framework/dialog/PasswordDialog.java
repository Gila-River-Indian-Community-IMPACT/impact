package us.oh.state.epa.stars2.workflow.framework.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.MessageDigest;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;
import us.oh.state.epa.stars2.framework.gui.ErrorListener;
import us.oh.state.epa.stars2.framework.gui.PasswordInput;
import us.oh.state.epa.stars2.framework.gui.StringInput;
import us.oh.state.epa.stars2.framework.userAuth.UserAuth;
import us.oh.state.epa.stars2.framework.util.CheckVariable;

/**
 * <p>
 * Title: PasswordDialog
 * </p>
 * 
 * <p>
 * Description: Creates and displays a dialog window that prompts a user to
 * enter a valid user name and password. These are validated against the
 * underlying database. If these are valid, then the database is queried to
 * determine whether or not this user has an appropriate use-case that allows
 * them to access the associated application. If that turns out to be true, the
 * application is initialized and enabled for use.
 * </p>
 * 
 * <p>
 * I hate Swing.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 */
public class PasswordDialog extends BaseDialog implements ErrorListener {
    protected JPanel editPanel; // Holds editable components
    protected Box buttonPanel; // Panel that holds the buttons
    protected StringInput userId; // User Id
    protected PasswordInput password; // Password
    protected JButton okButton; // The "Ok" button
    protected JButton exitButton; // The "Exit" button
    private boolean accepted; // Set to true when login is accepted
    private JFrame parent; // Parent/owner window

    /**
     * Constructor. The "parent" identifies a parent window to associate this
     * dialog with. If the "parent" is not null, the password dialog will be
     * display on top of the parent application window. If the parent is "null",
     * the login dialog will be displayed where ever the user's system chooses.
     * The "acceptableUseCases" identifies a list of use-cases that grant access
     * to this application. The user must possess at least one of these
     * use-cases in order to be able to use this application.
     * 
     * @param parent
     *            Parent JFrame; can be "null".
     * @param acceptableUseCases
     *            Array of use cases, must have at lease one element.
     * @throws IOException
     */
    public PasswordDialog(JFrame parent, String[] acceptableUseCases) {
        super(parent, "Login to WorkFlow Designer", true);
        this.parent = parent;
        // rqdUseCases = acceptableUseCases;

        // Start building the "guts" of the dialog.

        Container contentPane = getContentPane();
        Box bb = Box.createVerticalBox();
        // bb.setBorder (new LineBorder(Color.RED)) ;
        contentPane.add(bb);

        Box hb1 = Box.createHorizontalBox();
        // hb1.setBorder (new LineBorder (Color.CYAN)) ;
        bb.add(hb1);

        // To get this thing to layout the way I want, I have to group things
        // in their own JPanel and then glue these sub-panels together in
        // a GridBagLayout. At the "glue" level everything gets displayed
        // as a single column of centered data.

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 100;
        gbc.weighty = 100;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel vb1 = new JPanel();
        // vb1.setBorder (new LineBorder (Color.MAGENTA)) ;
        vb1.setLayout(new GridBagLayout());
        hb1.add(vb1);

        Box vb2 = Box.createVerticalBox();
        // vb2.setBorder (new LineBorder (Color.PINK)) ;
        vb1.add(vb2, gbc);

        // Some (hopefully) helpful directions for the user.
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        Node root = cfgMgr.getNode("app.database.default");
        CheckVariable.notNull(root);
        String server = root.getAsString("server");
        CheckVariable.notNull(server);
        String name = root.getAsString("name");
        CheckVariable.notNull(name);

        JLabel dir1 = new JLabel(" To access this application, you must login");
        JLabel dir2 = new JLabel(" using your system user name and password. ");
        JLabel dir3 = new JLabel(" DB server : " + server + " : " + name);

        vb2.add(dir1);

        vb2.add(dir2);
        vb2.add(dir3);
        vb2.add(Box.createVerticalStrut(10));

        // The GuiInput objects will go in the "edit" Panel.

        editPanel = new JPanel();
        // editPanel.setBorder (new LineBorder(Color.BLUE)) ;
        editPanel.setLayout(new GridLayout(0, 1, 5, 5));
        gbc.gridy = 1;
        vb1.add(editPanel, gbc);

        // Create the field and add our listener to it.

        userId = new StringInput("User Id", "", new Integer(40), true, true);
        userId.addErrorListener(this);

        JComponent jcomp = userId.getComponent();
        editPanel.add(jcomp);

        password = new PasswordInput("Password", "", new Integer(40), true,
                true);
        password.addErrorListener(this);

        jcomp = password.getComponent();
        editPanel.add(jcomp);

        // Buttons will go in the "button" panel.

        hb1.add(Box.createHorizontalStrut(5));
        buttonPanel = Box.createVerticalBox();
        // buttonPanel.setBorder (new LineBorder(Color.GREEN)) ;
        hb1.add(buttonPanel);

        addButtons(buttonPanel);
        hb1.add(Box.createHorizontalStrut(5));

        // Status and error messages will go in the "status" panel.

        bb.add(Box.createVerticalStrut(5));

        statusPanel = new JPanel();
        bb.add(statusPanel);
        LayoutManager smgr = new BorderLayout();
        statusPanel.setLayout(smgr);
        statusPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));

        JLabel temp = new JLabel();
        Font font = temp.getFont();

        // Use a JTextArea for status messages so that we can pre-allocated
        // three lines of display. Apparently, JLabel doesn't resize when
        // the text is too big for the current label area.

        statusMsg = new JTextArea();
        statusMsg.setFont(font);
        statusMsg.setEditable(false);
        statusMsg.setRows(2);
        statusPanel.add(statusMsg, BorderLayout.WEST);
    }

    /**
     * Displays the login dialog as a modal dialog. This method will not return
     * until the user has dismissed the dialog using either the "Ok" or "Exit"
     * buttons, or the "exit" icon. Returns "true" if the user successfully
     * logged in to the system and has use-case permissions to execute this
     * application. Returns "false" if the user should not be allowed to access
     * the application. The dialog remains visible if there is a login error or
     * use-case error, until the use selects the "Exit" button.
     * 
     * @return boolean "True" if the user should be allowed to access this
     *         application.
     * 
     */
    public final boolean login() {
        // Position the editor dialog on top of the main application window.

        if (parent != null) {
            Container parentPanel = parent.getContentPane();
            setLocationRelativeTo(parentPanel);
        }

        // Make the dialog visible and usable by the user.

        pack();
        setVisible(true);

        // Tell caller if anything was updated.

        return accepted;
    }

    /**
     * A convenience method for development. Sometimes drawing a border around a
     * JPanel helps to show what is actually going on. This is the "colored
     * border" version.
     * 
     * @param borderColor
     *            Optional color for a line border around the panel.
     * 
     * @return JPanel A panel.
     */
    protected final JPanel addRowPanel(Color borderColor) {
        JPanel panel = new JPanel();

        if (borderColor != null) {
            panel.setBorder(new LineBorder(borderColor));
        }

        return panel;
    }

    /**
     * Validates the user login against the system database. This includes user
     * name and password, plus use-case permissions. Returns "true" if the user
     * should be allowed to access the application or "false" if the user should
     * not be allowed to acces the application.
     * 
     * @return boolean "True" if user should be allowed to access application.
     */
    protected final boolean validateLogin() {
        boolean validated = false; // Be pessimistic about the outcome...

        // Extract the user name and password from fields. Make sure these
        // have values.

        String userName = userId.getString();
        String userPwd = password.getString();

        // Our strategy here will be to progress through a succession of steps
        // as we validate this user's request to access this application.
        // If a validation step fails, it will throw an exception. We will
        // catch the exception and notify the user of the problem.

        try {
            // The password might be encrypted in the database. If so, we need
            // to encrypt the password the user entered.

            String lPassword = encrypt(userPwd);
            // String password = userPwd ;

            // Make sure that both the user Id and password fields have been
            // assigned a value.

            validateField(userName, userId.getLabel());
            validateField(lPassword, this.password.getLabel());

            // Now, see if we have a UserDef record for this user in the
            // database.
            UserAuth userAuth = null;

            try {
                userAuth = (UserAuth) CompMgr.newInstance(UserAuth.DEFAULT);
            } catch (UnableToStartException e) {
                throw new RuntimeException(e);
            }
            // UserAttributes userAttr = userAuth.checkAuthentication(userName,
            // password);

            // At this point, a failure could occur for a variety of reasons.
            // However, from a security stand-point, we want to be deliberately
            // vague about why the failure occurred.

            if (userAuth == null) {
                throw new ApplicationException("Invalid User Id or Password.");
            }

            // If the user successfully logged in to the system, verify that
            // the user has an appropriate use-case permission to access this
            // application. Here, we can be more specific about the cause
            // of the failure.

            // SAM - We will come back to this later
            // validateUseCase(userAttr);

            // If we successfully validated the user's identity and use-case,
            // we are good to go.

            validated = true;
        } catch (Exception e) {
            String errMsg = e.getMessage();

            if ((errMsg == null) || (errMsg.length() == 0)) {
                errMsg = "A " + e.getClass().getName() + " was detected.";
            }

            StackTraceElement[] errT = e.getStackTrace();
            StringBuffer sb = new StringBuffer(errMsg);
            sb.append("\n");
            for (int i = 0; i < errT.length; i++) {
                sb.append(errT[i].toString());
                sb.append("\n");
            }

            setErrorMessage(null, sb.toString());
        }

        accepted = validated;
        return validated;
    }

    /**
     * Verifies that a field has been assigned a non-empty field value. Throws
     * an Exception if the input value is null or empty.
     * 
     * @param fieldValue
     *            The value the user entered.
     * @param fieldLabel
     *            The label associated with the field.
     * 
     * @throws java.lang.Exception
     *             Invalid field value.
     */
    private void validateField(String fieldValue, String fieldLabel)
            throws Exception {
        if ((fieldValue == null) || (fieldValue.length() == 0)) {
            throw new Exception("Field " + fieldLabel + " has not been "
                    + "assigned a value.");
        }
    }

    /*
     *//**
         * Verifies that the "userDef" object returned from the database is
         * valid, i.e., exists and is active. If the "userDef" object does
         * validate, an Exception is thrown with a deliberately nebulous error
         * message. If this method returns without throwing an exception, then
         * the user has been validated against the database.
         * 
         * @param userDef
         *            The UserDef object retrieved from the database.
         * @param password
         *            The password that the user entered.
         * 
         * @throws java.lang.Exception
         *             Invalid User Id or Password.
         */
    /*
     * private void validateUserDef (UserAuth userAuth) throws Exception { // If
     * the "userDef" object is null, then we don't have any idea who // this
     * user is.
     * 
     * if (userAuth == null) { throw new Exception ("Invalid User Id or
     * Password.") ; } // The UserDef object was retrieved based on the user's
     * login name. // Let's see if the password that was entered matches the
     * value in // the database.
     * 
     * String dbPassword = userDef.getPasswordVal() ;
     * 
     * if (!password.equals(dbPassword)) { throw new Exception ("Invalid User Id
     * or Password.") ; } // The user name and password are good. Make sure this
     * user account // is still active.
     * 
     * String actInd = userDef.getActiveInd() ; String activeVal = "A" ;
     * 
     * if (!activeVal.equals(actInd)) { throw new Exception ("Invalid User Id or
     * Password.") ; } }
     */
    /**
     * Verifies that this user possesses a use-case permission that grants
     * access to this application. Throws an exception describing the error if
     * the user does not have permission to access this application. If the
     * method returns without throwing an exception, then the user has
     * permission to access the application.
     * 
     * @param userAttr
     *            UserDef object retrieved from the database.
     * 
     * @throws java.lang.Exception
     *             User does not have permission to execute this application.
     */
    /*
     * private void validateUseCase(UserAttributes userAttr) throws Exception { //
     * Get the user Id and use that value to get a list of all the // use-cases
     * this user has.
     * 
     * Integer userId = userAttr.getUserId();
     * 
     * InfrastructureDAO infraDao = (InfrastructureDAO) (DAOFactory
     * .getDAO("InfrastructureDAO"));
     * 
     * String[] useCases = infraDao.retrieveUseCases(userId);
     *  // If the user has no use-cases in the database, then tell them.
     * 
     * if ((useCases == null) || (useCases.length == 0)) { throw new
     * Exception("User '" + userAttr.getUserName() + "' has no system
     * permissions."); }
     *  // Convert the use-cases list to an ArrayList so we can use a // binary
     * search to speed up the search. ArrayList<String> ucArray =
     * Utility.createArrayList(useCases);
     *  // For each use-case this user possesses, see if it matches any of //
     * them in the list. As soon as we find a match, the user has // permission
     * to use this application, so return.
     * 
     * for (int i = 0; i < rqdUseCases.length; i++) { int idx =
     * Collections.binarySearch(ucArray, rqdUseCases[i]);
     * 
     * if (idx >= 0) return; }
     *  // If we get here, none of the use-cases this user has match the // list
     * of use-cases for this application. Throw an exception.
     * 
     * throw new Exception("User '" + userAttr.getUserName() + "' does " + "not
     * have permission to use this application."); }
     */

    /**
     * Applies any password encryption algorithm to "password" and returns the
     * encrypted version to caller. If no encryption algorithm is specified,
     * then the original value is returned.
     * 
     * @param password
     *            Password to be encrypted.
     * 
     * @return Encrypted "password".
     * 
     * @throws java.lang.Exception
     *             Configuration error.
     */
    private String encrypt(String inPassword) throws Exception {
        // First, see if we can find an encryption algorithm in the
        // configuration tree.

        ConfigManager cfgMgr = ConfigManagerFactory.configManager();

        Node root = cfgMgr.getNode("app.authentication");
        CheckVariable.notNull(root);

        String encryptionAlgorithm = root
                .getAsString("passwordEncryptionAlgorithm");
        CheckVariable.notNull(encryptionAlgorithm);
        encryptionAlgorithm = encryptionAlgorithm.toUpperCase();

        // The encryption algorithm is either "MD5", "SHA", or "None". If
        // it is "None", do not attempt to encrypt the password.

        if ((encryptionAlgorithm.compareTo("MD5") != 0)
                && (encryptionAlgorithm.compareTo("SHA") != 0)) {
            return inPassword;
        }

        // Apply the encryption algorithm to the password and return it.

        MessageDigest md = MessageDigest.getInstance(encryptionAlgorithm);
        inPassword = new String(md.digest(inPassword.getBytes()));

        return inPassword;
    }

    /**
     * Adds our action buttons to "btnArea".
     * 
     * @param btnArea
     *            Container built to hold the buttons.
     */
    private void addButtons(Container btnArea) {
        okButton = new JButton("OK");
        okButton.addActionListener(new DoneListener(this));
        btnArea.add(okButton);

        btnArea.add(Box.createVerticalStrut(5));

        exitButton = new JButton("Exit");
        exitButton.addActionListener(new CancelListener(this));
        btnArea.add(exitButton);

        Dimension d = exitButton.getPreferredSize();
        exitButton.setMinimumSize(d);
        exitButton.setMaximumSize(d);

        okButton.setPreferredSize(d);
        okButton.setMinimumSize(d);
        okButton.setMaximumSize(d);
    }

    /* ************* */
    /* Inner Classes */
    /* ************* */
    // 'Ok' button listener
    private static class DoneListener implements ActionListener {
        private PasswordDialog editor;

        DoneListener(PasswordDialog editor) {
            this.editor = editor;
        }

        public void actionPerformed(ActionEvent evt) {
            if (editor.validateLogin()) {
                editor.dispose();
            }
        }
    }

    // 'Cancel' button listener -- in this case, exits the application.

    private static class CancelListener implements ActionListener {
        private PasswordDialog editor;
        CancelListener(PasswordDialog editor) {
            this.editor = editor;
        }

        public void actionPerformed(ActionEvent evt) {
            editor.dispose();
        }
    }
}

package us.oh.state.epa.stars2.framework.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.wy.state.deq.impact.App;

public class StarsUserParser {

    /**
     * @param args
     */
    public static void main(String[] args) {
        FileReader inFile = null;

        try {
            inFile = new FileReader(args[0]);
        } catch (Exception e) {
            System.out.println("Can't open file");
        }

        BufferedReader inBuf = null;
        if (inFile != null) {
            inBuf = new BufferedReader(inFile);
        }

//        InfrastructureDAO infraDAO = (InfrastructureDAO) DAOFactory
//                .getDAO("InfrastructureDAO");
        InfrastructureDAO infrastructureDAO = 
        		App.getApplicationContext().getBean(InfrastructureDAO.class);

        if (inBuf != null) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.set(3000, 1, 1);
                String inLine = inBuf.readLine();
                while (inLine != null) {
                    StringTokenizer st = new StringTokenizer(inLine);
                    if (st.hasMoreTokens()) {
                        Contact contact = new Contact();
                        UserDef userDef = new UserDef();

                        contact.setAddressId(2);
                        userDef.setActive(true);
                        userDef.setPasswordVal("NoLogin4U");
                        userDef.setPasswordExpDt(new Timestamp(cal
                                .getTimeInMillis()));

                        userDef.setUserId(new Integer(st.nextToken()));
                        contact.setFirstNm(st.nextToken());
                        contact.setLastNm(st.nextToken());
                        userDef.setNetworkLoginNm(st.nextToken());

                        // Add contact...
                        contact = infrastructureDAO.createContact(contact);

                        //userDef.setContactId(contact.getContactId());

                        // Add userDef...
                        infrastructureDAO.createUserDef(userDef);
                    }

                    inLine = inBuf.readLine();
                }
            } catch (Exception e) {
                System.out.println("Error reading file");
            }
        }
        System.exit(0);
    }
}

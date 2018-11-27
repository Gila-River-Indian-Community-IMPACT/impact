package us.oh.state.epa.stars2.database.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountryDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DaemonInfo;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ReportDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SecurityGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.StateDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDefBase;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.userAuth.UseCase;

public class InfrastructureDAOTest {

	@Before
    public void setUp() throws Exception {
        
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        
        infrastructureDAO.removeRows("cm_state_def", "state_cd", new String("FO"));

        infrastructureDAO.removeRows("cm_contact", "first_nm", new String("Tester"));

        infrastructureDAO.removeRows("cm_note", "note_txt", new String("This is a test note"));
        
        infrastructureDAO.removeRows("cm_contact_contact_type_xref", "contact_id", new Integer(1));

        infrastructureDAO.removeRows("cm_security_group", "security_group_nm", new String("FooBar"));

        infrastructureDAO.removeRows("cm_daemon", "daemon_cd", new String("TS"));
        
        infrastructureDAO.removeRows("cm_report_def", "report_nm", new String("Foo"));

        infrastructureDAO.removeRows("cm_user_group_xref", "user_id", new Integer(1));
        infrastructureDAO.removeRows("cm_user_group_xref", "user_id", new Integer(2));
    }

	@Test
	@Ignore
    public void testRetrieveUseCasesStringInt() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        LinkedHashMap<String, UseCase> test;
        
        // Null parameter test...
        try {
            test = infrastructureDAO.retrieveUseCases(1); 
            assertTrue(false); // Oops...
        } catch (DAOException de) {
            assertTrue(true);  // expected result          
        }
        
        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveUseCases(1); 
            assertTrue(test.size() == 0);
            
            test = infrastructureDAO.retrieveUseCases(-1); 
            assertTrue(test.size() == 0);
            
            test = infrastructureDAO.retrieveUseCases(-1); 
            assertTrue(test.size() == 0);
        } catch (DAOException de) {
            assertTrue(false);          
        }
        
        // Good data tests...
        try {
            infrastructureDAO.addUserRole(new Integer(1), new Integer(12));
            test = infrastructureDAO.retrieveUseCases(1);

            assertNotNull(test);
            assertTrue(test.size() > 0);
            assertNotNull(test.get("facilities"));

            infrastructureDAO.removeRows("cm_user_group_xref", "user_id", new Integer(1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveUserName(Integer)'
     */
    public void testRetrieveUserName() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        String test;
        
        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveUserName(-1); 
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);          
        }
        
        // Good data tests...
        try {
            test = infrastructureDAO.retrieveUserName(1);

            assertNotNull(test);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveUserDefByLoginAndPassword(String,
     * String)'
     */
    public void testRetrieveUserDefByLoginAndPassword() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        UserDefBase test;
        
        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveUserDefByLoginAndPassword("Foo",
                                                                       "Admin");
            assertNull(test);
            
            test = infrastructureDAO.retrieveUserDefByLoginAndPassword("Admin",
                                                                       "Foo");
            assertNull(test);
            
            test = infrastructureDAO.retrieveUserDefByLoginAndPassword("Foo",
                                                                       "Bar");
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);          
        }

        // Good data tests...
        try {
            test = infrastructureDAO
                .retrieveUserDefByLoginAndPassword("Admin",
                                                   "Admin");

            assertNotNull(test);
            assertNotNull(test.getNetworkLoginNm());
            assertNotNull(test.getPasswordExpDt());
            assertNotNull(test.getUserId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveUserDefByIdAndPassword(int,
     * String)'
     */
    public void testRetrieveUserDefByIdAndPassword() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        UserDefBase test;
        
        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveUserDefByIdAndPassword(1, "foo");
            assertNull(test);
            
            test = infrastructureDAO.retrieveUserDefByIdAndPassword(-1, "Admin");
            assertNull(test);
            
            test = infrastructureDAO.retrieveUserDefByIdAndPassword(-1, "foo");
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);          
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveUserDefByIdAndPassword(1, "Admin");

            assertNotNull(test);
            assertNotNull(test.getNetworkLoginNm());
            assertNotNull(test.getPasswordExpDt());
            assertNotNull(test.getUserId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveSecurityGroups(String)'
     */
    public void testRetrieveSecurityGroups() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        SecurityGroup[] test;
        
        // Good data tests...
        try {
            test = infrastructureDAO.retrieveSecurityGroups();

            assertNotNull(test);
            assertTrue(test.length > 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveCounty(int)'
     */
    public void testRetrieveCounty() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        CountyDef test;
        
        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveCounty("");
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);          
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveCounty("01");

            assertNotNull(test);
            assertNotNull(test.getCountyCd());
            assertNotNull(test.getCountyNm());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveCounties()'
     */
    public void testRetrieveCounties() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");

        // Good data tests...
        try {
            CountyDef[] test = infrastructureDAO.retrieveCounties();

            assertNotNull(test);
            assertTrue(test.length > 0);
            assertNotNull(test[0].getCountyCd());
            assertNotNull(test[0].getCountyNm());
            assertNotNull(test[0].getLastModified());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveState(String)'
     */
    public void testRetrieveState() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        StateDef test;
        
        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveState("garbage");
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);          
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveState("OH");

            assertNotNull(test);
            assertNotNull(test.getCountryCd());
            assertTrue(test.getStateCd().equals("OH"));
            assertNotNull(test.getLastModified());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveStates()'
     */
    public void testRetrieveStates() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");

        // Good data tests...
        try {
            StateDef[] test = infrastructureDAO.retrieveStates();

            assertNotNull(test);
            assertTrue(test.length > 0);
            assertNotNull(test[0].getCountryCd());
            assertNotNull(test[0].getStateCd());
            assertNotNull(test[0].getLastModified());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveCountries()'
     */
    public void testRetrieveCountries() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");

        // Good data tests...
        try {
            CountryDef[] test = infrastructureDAO.retrieveCountries();

            assertNotNull(test);
            assertTrue(test.length > 0);
            assertNotNull(test[0].getCountryCd());
            assertNotNull(test[0].getCountryNm());
            assertNotNull(test[0].getLastModified());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveCountry(int)'
     */
    public void testRetrieveCountry() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        CountryDef test;
        
        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveCountry("");
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);          
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveCountry("US");

            assertNotNull(test);
            assertNotNull(test.getCountryCd());
            assertNotNull(test.getCountryNm());
            assertNotNull(test.getLastModified());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveDaemonInfo()'
     */
    public void testRetrieveDaemonInfo() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        DaemonInfo test;
        
        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveDaemonInfo("garbage");
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);          
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveDaemonInfo("WF");

            assertNotNull(test);
            assertTrue(test.getDaemonCode().equals("WF"));
            assertNotNull(test.getDescription());
            assertNotNull(test.getLastModified());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.retrieveUserList()'
     */
    public void testRetrieveUserList() {

        InfrastructureDAO infrastructureDAO 
            = (InfrastructureDAO) DAOFactory.getDAO("InfrastructureDAO");

        // Good data tests...
        try {
            SimpleIdDef[] test = infrastructureDAO.retrieveUserList(true);
            assertNotNull(test);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testRetrieveAddress() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        Address test;

        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveAddress(-1);
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveAddress(2);

            assertNotNull(test);
            assertNotNull(test.getAddressId());
            assertNotNull(test.getLastModified());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testRetrieveContact() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        Contact test;

        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveContact(-1);
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveContact(1);

            assertNotNull(test);
            assertNotNull(test.getContactId());
            assertNotNull(test.getAddressId());
            assertNotNull(test.getLastModified());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testRetrieveAllUseCases() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");

        // Good data tests...
        try {
            UseCase[] test = infrastructureDAO.retrieveAllUseCases();

            assertNotNull(test);
            assertTrue(test.length > 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testRetrieveUseCase() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        UseCase test;

        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveUseCase("garbage");
            assertNull(test);
        } catch (DAOException de) {
            assertTrue(false);
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveUseCase("facilities");

            assertNotNull(test);
            assertTrue(test.getUseCaseName().equals("Facilities"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testRetrieveUseCasesStringInteger() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        UseCase[] test;

        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveUseCases(new Integer(1));
            assertTrue(test.length == 0);
            
            test = infrastructureDAO.retrieveUseCases(new Integer(-1));
            assertTrue(test.length == 0);
        } catch (DAOException de) {
            assertTrue(false);
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveUseCases(new Integer(1));

            assertNotNull(test);
            assertTrue(test.length > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testRetrieveUserDef() {

        InfrastructureDAO infrastructureDAO 
            = (InfrastructureDAO) DAOFactory.getDAO("InfrastructureDAO");
            
        UserDef test;

        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveUserDef(new Integer(-1));
            assertNull(test);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveUserDef(new Integer(1));

            assertNotNull(test);
            assertTrue(test.getNetworkLoginNm().compareTo("Admin") == 0);
            assertTrue(test.isActive());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testRetrieveSecurityGroup() {

        InfrastructureDAO infrastructureDAO 
            = (InfrastructureDAO) DAOFactory.getDAO("InfrastructureDAO");
            
        SecurityGroup test;

        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveSecurityGroup(new Integer(-1));
            assertNull(test);
        }
        catch (DAOException de) {
            assertTrue(false);
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveSecurityGroup(new Integer(1));
            assertNotNull(test);

            assertTrue(test.getAppTypeCd().compareTo("stars2") == 0);
            assertTrue(test.getSecurityGroupName().compareTo("1 User Catalog") == 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testRetrieveDoLaas() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        SimpleDef[] test;

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveDoLaas();

            assertNotNull(test);
            assertTrue(test.length > 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testRetrieveContactTypes() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        List<ContactType> test;

        // Bad data tests...
        try {
            test = infrastructureDAO.retrieveContactTypes(-1);
            assertTrue(test.size() == 0);
        } catch (DAOException de) {
            assertTrue(false);
        }

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveContactTypes(1);

            assertNotNull(test);
            //            assertTrue(test.size() > 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testRetrieveReportDefs() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        ReportDef[] test;

        // Good data tests...
        try {
            test = infrastructureDAO.retrieveReportDefs();

            assertNotNull(test);
            assertTrue(test.length > 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }
    
    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.modifyUserDef(UserDef)'
     */
    public void testModifyUserDef() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.updateApplicationDef(ApplicationDef)'
     */
    public void testUpdateApplicationDef() {
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.createUserDef(UserDef)'
     */
    public void testCreateUserDef() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.createAddress(Address)'
     */
    public void testCreateAddress() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.modifyCounty(CountyDef)'
     */
    public void testModifyCounty() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");

        try {
            CountyDef inCounty = infrastructureDAO.retrieveCounty("01");
            inCounty.setCountyNm("Foo County 1");
            assertTrue(infrastructureDAO.modifyCounty(inCounty));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.modifyCountry(CountryDef)'
     */
    public void testModifyCountry() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");

        try {
            CountryDef inCountry = infrastructureDAO.retrieveCountry("MX");
            inCountry.setCountryNm("Mexico 1");
            assertTrue(infrastructureDAO.modifyCountry(inCountry));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            fail();
        }
    }

    /*
     * Test method for 'us.oh.state.epa.stars2.database.dao.InfrastructureDAO.modifyState(StateDef)'
     */
    public void testModifyState() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");

        try {
            StateDef inState = infrastructureDAO.retrieveState("OH");
            inState.setStateNm("Buckeye State");
            assertTrue(infrastructureDAO.modifyState(inState));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testCreateContact() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        Contact contact = new Contact();
        Contact test;
        
        try {
            contact.setAddressId(-1);
            contact.setTitleCd("MR");
            contact.setFirstNm("Tester");
            contact.setLastNm("Dude");
            contact.setSuffixCd("III");
            contact.setPhoneNo("614-555-1212");
            
            test = infrastructureDAO.createContact(contact);
            fail();
        } catch (DAOException de) {
            assertTrue(true);
        }
        
        try {
            contact.setAddressId(2);
            test = infrastructureDAO.createContact(contact);

            assertNotNull(test);
            assertNotNull(test.getContactId());
            assertNotNull(test.getLastModified());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testModifyContact() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        Contact contact;

        try {
            contact = infrastructureDAO.retrieveContact(1);
            contact.setAddressId(-1);

            assertTrue(!infrastructureDAO.modifyContact(contact));
        } catch (DAOException de) {
            assertTrue(true);
        }

        try {
            contact = infrastructureDAO.retrieveContact(1);
            
            contact.setTitleCd("DR");
            assertTrue(infrastructureDAO.modifyContact(contact));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testCreateNote() {

        InfrastructureDAO infrastructureDAO 
            = (InfrastructureDAO) DAOFactory.getDAO("InfrastructureDAO");
            
        Note note = new Note();
        Note test;
        
        try {
            note = new Note();
            note.setNoteTypeCd("dapc");
            note.setNoteTxt("This is a test note");
            note.setUserId(1);
            note.setDateEntered(new Timestamp(System.currentTimeMillis()));
            
            test = infrastructureDAO.createNote(note);

            assertNotNull(test);
            assertNotNull(test.getNoteId());
            assertNotNull(test.getLastModified());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testModifyNote() {

        InfrastructureDAO infrastructureDAO 
            = (InfrastructureDAO) DAOFactory.getDAO("InfrastructureDAO");
            
        Note note = new Note();
        Note test;
        int noteId = 0;
        
        // First create a note
        try {
            note = new Note();
            note.setNoteTypeCd("dapc");
            note.setNoteTxt("This is a test note");
            note.setUserId(1);
            note.setDateEntered(new Timestamp(System.currentTimeMillis()));
            
            test = infrastructureDAO.createNote(note);
            noteId = test.getNoteId();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        
        try {
            note = infrastructureDAO.retrieveNote(noteId);
            note.setNoteTypeCd("appc");
            assertTrue(infrastructureDAO.modifyNote(note));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testAddContactType() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        
        try {            
            infrastructureDAO.addContactType(null, "oper", new Timestamp(System.currentTimeMillis()));
            fail();
        } catch (DAOException de) {
            assertTrue(true);
        }
        
        try {            
            infrastructureDAO.addContactType(-1, "oper", new Timestamp(System.currentTimeMillis()));
            fail();
        } catch (DAOException de) {
            assertTrue(true);
        }
        
        try {
            infrastructureDAO.addContactType(1, "oper", new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testModifyAddress() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        Address test;
        
        // Null parameter test
        try {
            assertTrue(!infrastructureDAO.modifyNote(null));
        } catch (DAOException de) {
            assertTrue(true);
        }
        
        // Bad data test
        try {
            test = infrastructureDAO.retrieveAddress(2);
            
            test.setState("fo");
            
            assertTrue(!infrastructureDAO.modifyAddress(test));
        } catch (DAOException de) {
            assertTrue(true);
        }
        
        // Good data test
        try {
            test = infrastructureDAO.retrieveAddress(2);
            
            test.setAddressLine2("Apt 1");
            
            int lastModified = test.getLastModified();
            
            assertTrue(infrastructureDAO.modifyAddress(test));
            
            test = infrastructureDAO.retrieveAddress(2);

            assertTrue(lastModified == test.getLastModified() - 1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testCreateSecurityGroup() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        SecurityGroup sg = new SecurityGroup();
        SecurityGroup test;

        try {
            sg.setAppTypeCd("foo");
            sg.setSecurityGroupName("FooBar");

            test = infrastructureDAO.createSecurityGroup(sg);
            fail();
        } catch (DAOException de) {
            assertTrue(true);
        }

        try {
            sg.setAppTypeCd("stars2");
            sg.setSecurityGroupName("FooBar");
            
            test = infrastructureDAO.createSecurityGroup(sg);

            assertNotNull(test);
            assertNotNull(test.getSecurityGroupId());
            assertNotNull(test.getLastModified());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testCreateDaemonInfo() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        DaemonInfo di = new DaemonInfo();
        DaemonInfo test;

        try {
            di.setDaemonCode("TS");
            di.setHostName("localhost");
            di.setPortNumber(1024);

            test = infrastructureDAO.createDaemonInfo(di);

            assertNotNull(test);
            assertNotNull(test.getLastModified());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testAddUserRole() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        // Null parameter test
        try {
            infrastructureDAO.addUserRole(null, 2);
            fail();
        } catch (DAOException de) {
            assertTrue(true);
        }
        
        try {
            infrastructureDAO.addUserRole(1004, 10);
            assertTrue(true);
            infrastructureDAO.removeRows("cm_user_group_xref", "user_id", new Integer(1004));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            fail();
        }
    }
    
    public void testCreateReport() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        ReportDef rd = new ReportDef();
        ReportDef test;

        try {
            test = infrastructureDAO.createReport(null);
            fail();
        } catch (DAOException de) {
            assertTrue(true);
        }
        
        try {
            rd.setName("Foo");
            rd.setGroupNm("FooBar");

            test = infrastructureDAO.createReport(rd);

            assertNotNull(test);
            assertNotNull(test.getId());
            assertNotNull(test.getLastModified());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testModifyReport() {
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO) DAOFactory
            .getDAO("InfrastructureDAO");
        ReportDef test;
        
        // Null parameter test
        try {
            assertTrue(!infrastructureDAO.modifyReport(null));
        } catch (DAOException de) {
            assertTrue(true);
        }
        
        // Good data test
        try {
            ReportDef[] tempDefs = infrastructureDAO.retrieveReportDefs();
            
            if (tempDefs.length > 0) {
                test = tempDefs[0];
                int reportId = test.getId();

                int lastModified = test.getLastModified();

                test.setGroupNm("FooBar2");
                
                assertTrue(infrastructureDAO.modifyReport(test));

                tempDefs = infrastructureDAO.retrieveReportDefs();
                
                for (ReportDef tempDef : tempDefs) {
                    if (reportId == tempDef.getId()) {
                        test = tempDef;
                        break;
                    }
                }

                assertTrue(test.getGroupNm().compareTo("FooBar2") == 0);
                assertTrue(lastModified == test.getLastModified() - 1);
            } else {
                fail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}

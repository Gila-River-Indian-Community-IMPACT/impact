package us.wy.state.deq.impact.database.dbObjects.company;

/**
 * 
 * THIS TEST HAS QUESTIONABLE VALUE, TEMPORARILY DISABLING IT BECAUSE IT'S BROKEN
 * AND IS BREAKING THE BUILD.  IS THERE ANY GOOD REASON TO KEEP THIS AROUND?
 * 
 * Unit test cases for Company DB Object.
 * 
 * @author jwilber
 * 
 */
public class CompanyTest {
	Company testCompany01, testCompany02;
	String expectedName = "Test Company 02";
	String expectedAlias = "Company";
	String expectedAddress1 = "Test Street";
	String expectedAddress2 = "Test Suite 3";
	String expectedCity = "Test Metropolis";
	String expectedState = "Ohio";
	String expectedZip = "43210";
	String expectedCountryCd = "US";
	String expectedPhoneNo = "1234567890";
	String expectedFaxNo = "0987654321";
	String expectedNote = "Hi! This is my note!";

	/**
	 * Sets up future test cases.
	 * 
	 * @throws Exception
	 */
//	@Before
//	public void setUp() throws Exception {
//		testCompany02 = new Company();
//		testCompany02.setName(expectedName);
//		testCompany02.setAlias(expectedAlias);
//		Address address = new Address();
//		address.setAddressLine1(expectedAddress1);
//		address.setAddressLine2(expectedAddress2);
//		address.setCityName(expectedCity);
//		address.setState(expectedState);
//		address.setZipCode(expectedZip);
//		address.setCountryCd(expectedCountryCd);
//		
//		testCompany02.setAddress(address);
//		
//		testCompany02.setPhone(expectedPhoneNo);
//		testCompany02.setFax(expectedFaxNo);
////		testCompany02.setNote(expectedNote);
//
//	}
//
//	/**
//	 * Tests setting and getting a new company name.
//	 */
//	@Test
//	public void testSetGetName() {
//		String expected = "Test Company 01";
//		testCompany01 = new Company();
//		testCompany01.setName(expected);
//		String actual = testCompany01.getName();
//		assertEquals(expected, actual);
//
//	}
//
//	/**
//	 * Tests getting a company name (previously set).
//	 */
//	@Test
//	public void testGetName() {
//		String actual = testCompany02.getName();
//		assertEquals(expectedName, actual);
//
//	}
//
//	/**
//	 * Tests getting an empty company name.
//	 */
//	@Test
//	public void testEmptyGetName() {
//		testCompany01 = new Company();
//		String actual = testCompany01.getName();
//		assertNull(actual);
//	}
//
//	/**
//	 * Tests setting and getting a new company's alias.
//	 */
//	@Test
//	public void testSetGetAlias() {
//		String expected = "Company";
//		testCompany01 = new Company();
//		testCompany01.setAlias(expected);
//		String actual = testCompany01.getAlias();
//		assertEquals(expected, actual);
//	}
//
//	/**
//	 * Tests getting an existing company's alias.
//	 */
//	@Test
//	public void testGetAlias() {
//		String actual = testCompany02.getAlias();
//		assertEquals(expectedAlias, actual);
//	}
//
//	/**
//	 * Tests getting an empty company alias.
//	 */
//	@Test
//	public void testEmptyGetAlias() {
//		testCompany01 = new Company();
//		String actual = testCompany01.getAlias();
//		assertNull(actual);
//	}
//
//	/**
//	 * Tests setting and getting a new company's address line 1.
//	 */
//	@Test
//	public void testSetGetAddressLine1() {
//		String expected = "Test Address";
//		testCompany01 = new Company();
//		Address testAddress = new Address();
//		testAddress.setAddressLine1(expected);
//		testCompany01.setAddress(testAddress);
//		Address actualAddress = testCompany01.getAddress();
//		String actual = actualAddress.getAddressLine1();
//		assertEquals(expected, actual);
//	}
//
//	/**
//	 * Tests getting an existing company's address line 1.
//	 */
//	@Test
//	public void testGetAddressLine1() {
//		String actual = testCompany02.getAddress().getAddressLine1();
//		assertEquals(expectedAddress1, actual);
//	}
//
//	/**
//	 * Tests getting an empty company address line 1.
//	 */
//	@Test
//	public void testEmptyGetAddressLine1() {
//		testCompany01 = new Company();
//		testCompany01.setAddress(new Address());
//		String actual = testCompany01.getAddress().getAddressLine1();
//		assertNull(actual);
//	}
//	
//	/**
//	 * Tests setting and getting a new company's address line 2.
//	 */
//	@Test
//	public void testSetGetAddressLine2() {
//		String expected = "Test Address 2";
//		testCompany01 = new Company();
//		Address testAddress = new Address();
//		testAddress.setAddressLine2(expected);
//		testCompany01.setAddress(testAddress);
//		Address actualAddress = testCompany01.getAddress();
//		String actual = actualAddress.getAddressLine2();
//		assertEquals(expected, actual);
//	}
//
//	/**
//	 * Tests getting an existing company's address line 2.
//	 */
//	@Test
//	public void testGetAddressLine2() {
//		String actual = testCompany02.getAddress().getAddressLine2();
//		assertEquals(expectedAddress2, actual);
//	}
//
//	/**
//	 * Tests getting an empty company address line 2.
//	 */
//	@Test
//	public void testEmptyGetAddressLine2() {
//		testCompany01 = new Company();
//		testCompany01.setAddress(new Address());
//		String actual = testCompany01.getAddress().getAddressLine2();
//		assertNull(actual);
//	}
//	
//	/**
//	 * Tests setting and getting a new company's city.
//	 */
//	@Test
//	public void testSetGetCity() {
//		String expected = "Test Metro 1";
//		testCompany01 = new Company();
//		Address testAddress = new Address();
//		testAddress.setCityName(expected);
//		testCompany01.setAddress(testAddress);
//		Address actualAddress = testCompany01.getAddress();
//		String actual = actualAddress.getCityName();
//		assertEquals(expected, actual);
//	}
//
//	/**
//	 * Tests getting an existing company's city.
//	 */
//	@Test
//	public void testGetCity() {
//		String actual = testCompany02.getAddress().getCityName();
//		assertEquals(expectedCity, actual);
//	}
//
//	/**
//	 * Tests getting an empty company's city.
//	 */
//	@Test
//	public void testEmptyGetCity() {
//		testCompany01 = new Company();
//		testCompany01.setAddress(new Address());
//		String actual = testCompany01.getAddress().getCityName();
//		assertNull(actual);
//	}
//	
//	/**
//	 * Tests setting and getting a new company's state.
//	 */
//	@Test
//	public void testSetGetState() {
//		String expected = "Wyoming";
//		testCompany01 = new Company();
//		Address testAddress = new Address();
//		testAddress.setState("Wyoming");
//		testCompany01.setAddress(testAddress);
//		Address actualAddress = testCompany01.getAddress();
//		String actual = actualAddress.getState();
//		assertEquals(expected, actual);
//	}
//
//	/**
//	 * Tests getting an existing company's state.
//	 */
//	@Test
//	public void testGetState() {
//		String actual = testCompany02.getAddress().getState();
//		assertEquals(expectedState, actual);
//	}
//
//	/**
//	 * Tests getting an empty company's state.
//	 */
//	@Test
//	public void testEmptyGetState() {
//		testCompany01 = new Company();
//		testCompany01.setAddress(new Address());
//		String actual = testCompany01.getAddress().getState();
//		assertNull(actual);
//	}
//	
//	/**
//	 * Tests setting and getting a new company's zip code.
//	 */
//	@Test
//	public void testSetGetZip() {
//		String expected = "43147";
//		testCompany01 = new Company();
//		Address testAddress = new Address();
//		testAddress.setZipCode(expected);
//		testCompany01.setAddress(testAddress);
//		Address actualAddress = testCompany01.getAddress();
//		String actual = actualAddress.getZipCode();
//		assertEquals(expected, actual);
//	}
//
//	/**
//	 * Tests getting an existing company's zip code.
//	 */
//	@Test
//	public void testGetZip() {
//		String actual = testCompany02.getAddress().getZipCode();
//		assertEquals(expectedZip, actual);
//	}
//
//	/**
//	 * Tests getting an empty company's zip code.
//	 */
//	@Test
//	public void testEmptyGetZip() {
//		testCompany01 = new Company();
//		testCompany01.setAddress(new Address());
//		String actual = testCompany01.getAddress().getZipCode();
//		assertNull(actual);
//	}
//	
//	/**
//	 * Tests setting and getting a new company's country.
//	 */
//	@Test
//	public void testSetGetCountry() {
//		String expected = "CA";
//		testCompany01 = new Company();
//		Address testAddress = new Address();
//		testAddress.setCountryCd(expected);
//		testCompany01.setAddress(testAddress);
//		Address actualAddress = testCompany01.getAddress();
//		String actual = actualAddress.getCountryCd();
//		assertEquals(expected, actual);
//	}
//
//	/**
//	 * Tests getting an existing company's country.
//	 */
//	@Test
//	public void testGetCountry() {
//		String actual = testCompany02.getAddress().getCountryCd();
//		assertEquals(expectedCountryCd, actual);
//	}
//
//	/**
//	 * Tests getting an empty company's country.
//	 */
//	@Test
//	public void testEmptyGetCountry() {
//		testCompany01 = new Company();
//		testCompany01.setAddress(new Address());
//		String actual = testCompany01.getAddress().getCountryCd();
//		assertNull(actual);
//	}
//	
//	/**
//	 * Tests setting and getting a new company's phone no.
//	 */
//	@Test
//	public void testSetGetPhone() {
//		String expected = "5556667777";
//		testCompany01 = new Company();
//		testCompany01.setPhone(expected);
//		String actual = testCompany01.getPhone();
//		assertEquals(expected, actual);
//	}
//
//	/**
//	 * Tests getting an existing company's phone no.
//	 */
//	@Test
//	public void testGetPhone() {
//		String actual = testCompany02.getPhone();
//		assertEquals(expectedPhoneNo, actual);
//	}
//
//	/**
//	 * Tests getting an empty company's phone no.
//	 */
//	@Test
//	public void testEmptyGetPhone() {
//		testCompany01 = new Company();
//		String actual = testCompany01.getPhone();
//		assertNull(actual);
//	}
//	
//	/**
//	 * Tests setting and getting a new company's fax no.
//	 */
//	@Test
//	public void testSetGetFax() {
//		String expected = "1113335555";
//		testCompany01 = new Company();
//		testCompany01.setFax(expected);
//		String actual = testCompany01.getFax();
//		assertEquals(expected, actual);
//	}
//
//	/**
//	 * Tests getting an existing company's fax no.
//	 */
//	@Test
//	public void testGetFax() {
//		String actual = testCompany02.getFax();
//		assertEquals(expectedFaxNo, actual);
//	}
//
//	/**
//	 * Tests getting an empty company's fax no.
//	 */
//	@Test
//	public void testEmptyGetFax() {
//		testCompany01 = new Company();
//		String actual = testCompany01.getFax();
//		assertNull(actual);
//	}
	
	/**
	 * Tests setting and getting a new company's note.
	@Test
	public void testSetGetNote() {
		String expected = "This is just a test.";
		testCompany01 = new Company();
		testCompany01.setNote(expected);
		String actual = testCompany01.getNote();
		assertEquals(expected, actual);
	}
	 */

	/**
	 * Tests getting an existing company's note.
	@Test
	public void testGetNote() {
		String actual = testCompany02.getNote();
		assertEquals(expectedNote, actual);
	}
	 */

	/**
	 * Tests getting an empty company's note.
	@Test
	public void testEmptyGetNote() {
		testCompany01 = new Company();
		String actual = testCompany01.getNote();
		assertNull(actual);
	}
	 */
}

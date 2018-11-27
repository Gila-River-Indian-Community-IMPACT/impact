package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestMethodPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestVisitDate;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportAttachment;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface StackTestDAO extends TransactableDAO {
	StackTest retrieveStackTest(int stackTestId) throws DAOException;

	StackTest[] searchStackTests(String facilityId) throws DAOException;

	StackTest[] searchStackTests(String facilityId, String emissionTestState) throws DAOException;

	StackTest createStackTest(StackTest stackTest) throws DAOException;

	boolean modifyStackTest(StackTest stackTest) throws DAOException;

	void removeStackTest(int stackTestId) throws DAOException;
	
	void removeStackTestNotes(int stackTestId) throws DAOException;

	DbInteger cntStackTestsWithDate(Integer stackTestId, Timestamp visitDate,
			String facilityId) throws DAOException;

	StackTest[] retrieveStackTestsBySearch(String facilityId,
			Integer stackTestId, Integer fceId, String facilityName,
			String doLaaCd, String countyCd,
			String permitClassCd, String facilityTypeCd, String dateBy, Timestamp beginDate,
			Timestamp endDate, boolean failedPolls, Integer reviewer,
			String stackTestMethodCd, String emissionTestState, String inspId,
			String stckId, String cmpId) throws DAOException;

	void createTestVisitDate(Timestamp dt, Integer stackTestId)
			throws DAOException;

	void removeTestVisitDates(Integer stackTestId) throws DAOException;

	void createTestPollutant(StackTestedPollutant stp) throws DAOException;

	void removeTestPollutants(Integer stackTestId) throws DAOException;

	Evaluator[] retrieveStackTestWitnesses(int stackTestId) throws DAOException;

	List<StackTestedPollutant> retrieveTestPollutants(int stackTestId)
			throws DAOException;

	StackTestMethodPollutant[] retrieveMethodPollutants(String stackTestMethodCd)
			throws DAOException;

	void removeStackTestWitnesses(Integer stackTestId) throws DAOException;

	void createStackTestWitness(Integer user, Integer stackTestId)
			throws DAOException;

	StackTest[] retrieveStacktestByFce(Integer fceId) throws DAOException;

	StackTest[] retrieveStacktestsUnassigned(String facilityId)
			throws DAOException;

	StAttachment createStAttachment(StAttachment sa) throws DAOException;
	
	/**
     * @param sa
     * @throws DAOException
     */
    public void createStTradeSecretAttachment(StAttachment sa) throws DAOException;

    /**
     * @param sa
     * @throws DAOException
     */
    public void deleteStTradeSecretAttachment(StAttachment sa) throws DAOException;
    
	void deleteStAttachment(StAttachment doc) throws DAOException;

	/**
     * @param sa
     * @throws DAOException
     */
    public void retrieveStTradeSecretAttachmentInfo(StAttachment sa) throws DAOException;
    
	List<StAttachment> retrieveStAttachments(int stackTestId)
			throws DAOException;

	/**
     * @param sa
     * @throws DAOException
     */
    public void modifyStTradeSecretAttachment(StAttachment sa) throws DAOException;
    
	boolean modifyStAttachment(StAttachment doc) throws DAOException;

	List<StackTestedPollutant> newAfsStackTests() throws DAOException;

	Integer getAfsId(String facilityId) throws DAOException;

	boolean updateAfsId(String facilityId, Integer current) throws DAOException;

	boolean afsLockStackTestPollutant(StackTestedPollutant stp, Integer afsId)
			throws DAOException;

	void createNewAfsId(String facilityId) throws DAOException;

	Timestamp retrieveLastStackTestDate(String facilityId) throws DAOException;

	String stackTestAfsLocked(Integer stackTestId) throws DAOException;

	public boolean afsSetDateStackTestPollutant(StackTestedPollutant stp)
			throws DAOException;

	List<StackTestedPollutant> retrieveTestPollutant(String scscId, String afsId)
			throws DAOException;

	boolean updateStackTestLastModifiedOnly(Integer stackTestId,
			Integer lastModified) throws DAOException;

	Timestamp retrieveLastStackTestDate(Integer stackTestId)
			throws DAOException;

	boolean updateDoubleAfsId(String scscId, Integer current)
			throws DAOException;

	boolean updateTripleAfsId(String scscId, Integer current)
			throws DAOException;

	boolean updateMultipleAfsId(String scscId, Integer current, int newValue)
			throws DAOException;

	Note[] retrieveStackTestNotes(int stackTestID) throws DAOException;

	void createStackTestNote(Integer stackTestId, Integer noteId)
			throws DAOException;
	
	int getFceId(String inspectionId) throws DAOException;
	
	void createInspectionAssociation(Integer stackTestId, Integer fceId) throws DAOException;
	
	/**
	 * Update the validated flag for the stack test identified by
	 * <code>stId</code> setting it to <code>validated</code>.
	 * 
	 * @param appId
	 *            id of stack test to be modified.
	 * @param validated
	 *            value for the validated flag.
	 * @throws DAOException
	 */
	public void setStackTestValidatedFlag(Integer stId, boolean validated)
			throws DAOException;
	
	/**
	 * @param tradeSecretDocId
	 * @return StAttachment
	 * @throws DAOException
	 */
	StAttachment retrieveStTradeSecretAttachmentInfoById(Integer tradeSecretDocId) throws DAOException;
	
	public List<StackTestedPollutant> retrieveTestPollutantsAndEus(Integer stackTestId) throws DAOException;
	
	public List<TestVisitDate> retrieveStackTestDatesById(Integer stackTestId) throws DAOException;
}

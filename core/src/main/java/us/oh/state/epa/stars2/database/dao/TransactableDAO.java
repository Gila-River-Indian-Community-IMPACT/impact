package us.oh.state.epa.stars2.database.dao;

/**
 * Title: TransactableDAO.
 * 
 * <p>
 * Description: This interface specifies a DAO that can handle external
 * transaction control. DAOs that wish to support this capability must implement
 * this interface.
 * </p>
 * 
 * <p>
 * External transaction control is used whenever an application needs to commit
 * data that spans multiple database objects to persistent storage (equivalent
 * to using multiple DAOs). If any transaction in that sequence fails, then the
 * entire transaction must be cancelled. Under these circumstances, the
 * application creates its own <tt>Transaction</tt> object and provides that
 * <tt>Transaction</tt> to the DAO using this interface. The DAO framework
 * knows how to handle external transaction control.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 */
@Deprecated
public interface TransactableDAO extends DataAccessObject {
    /**
     * Method to set DAO transaction control to an external transaction object.
     * 
     * @param explicit
     *            A <tt>Transaction</tt> object to be used for all DAO
     *            operations.
     */
    void setTransaction(Transaction explicit);
    
    void setDefaultSearchLimit(int defaultSearchLimit);
}

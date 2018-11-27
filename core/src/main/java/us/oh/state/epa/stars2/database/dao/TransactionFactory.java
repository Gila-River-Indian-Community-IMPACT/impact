package us.oh.state.epa.stars2.database.dao;

/**
 * Title: TransactionFactory.
 * 
 * <p>
 * Description: This factory creates <tt>Transaction</tt> objects for use with
 * DAOs. Typically, a <tt>Transaction</tt> is used whenever a command object
 * wants to perform multiple data activities in a single transaction.
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
public final class TransactionFactory {
    private static int nextId = 1; // Used for unique Transaction IDs

    /**
     * This class is a self-managed singleton. Therefore, the constructor is
     * <i>private</i>.
     */
    private TransactionFactory() {
    }

    /**
     * Creates new <tt>Transaction</tt> object. Returns <tt>null</tt> if no
     * database connection is available.
     * 
     * @return A <tt>Transaction</tt> that is usable with DAOs.
     */
    public static Transaction createTransaction() {
        Transaction trans = null;

        trans = new Transaction(nextId++);
        return trans;
    }

    /**
     * Releases a <tt>Transaction</tt> and its associated resources. Note that
     * <tt>Transaction</tt> objects do this themselves.
     * 
     * @param t
     *            Transaction to be released.
     */
    static void releaseTransaction(final Transaction t) {
    }
}

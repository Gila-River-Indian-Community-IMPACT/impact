package us.oh.state.epa.stars2.database.dao;

import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface ApplicationEUTypeDAO extends TransactableDAO {
	/**
	 * Creates the given application EU type.
	 * 
	 * @param euType
	 *            Application emission unit type to be created
	 * @return The created application emission unit type.
	 * @throws DAOException
	 */
	NSRApplicationEUType createApplicationEUType(NSRApplicationEUType euType)
			throws DAOException;

	/**
	 * Removes the application emission unit type with the given application EU
	 * identifier.
	 * 
	 * @param appEuId
	 *            The given EU identifier
	 * @throws DAOException
	 */
	void removeApplicationEUType(Integer appEuId) throws DAOException;

	/**
	 * Modifies an older application emission unit type with the given new one.
	 * 
	 * @param euType
	 *            Newer application emission unit type replacing the older one.
	 * @throws DAOException
	 */
	void modifyApplicationEUType(NSRApplicationEUType euType)
			throws DAOException;

	/**
	 * Retrieves the application emission unit type from the given application
	 * EU identifier.
	 * 
	 * @param appEuId
	 *            The given EU identifier
	 * @return The requested application emission unit type.
	 * @throws DAOException
	 */
	NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException;
}

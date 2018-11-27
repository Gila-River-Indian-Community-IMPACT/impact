package us.oh.state.epa.stars2.database.dao;


import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.Component;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface EmissionUnitTypeDAO extends TransactableDAO {
	/**
	 * Creates the supplied emission unit type.
	 * 
	 * @param EmissionUnitType
	 *            Emission unit Type to create
	 * @return EmissionUnitType created emission unit type.
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EmissionUnitType createEmissionUnitType(EmissionUnitType euType) throws DAOException;

	/**
	 * Removes an emission unit type with the supplied emuId
	 * 
	 * @param emuId
	 *            emission unit id
	 * @throws DAOException
	 */
	void removeEmissionUnitType(Integer emuId) throws DAOException;	
	
	/**
	 * Modifies an older emission unit type with a newer one.
	 * 
	 * @param euType
	 *            Modified emission unit type
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void modifyEmissionUnitType(EmissionUnitType euType) throws DAOException;
	

	/**
	 * Retrieves an emission unit type with the supplied emuId
	 * 
	 * @param emuId
	 *            emission unit id
	 * @return EmissionUnitType an Emission unit type
	 * @throws DAOException
	 */
	EmissionUnitType retrieveEmissionUnitType(Integer emuId)
			throws DAOException;

	void addComponentCount(Component component) throws DAOException;
	
	void deleteComponentCountByEmuId(Integer emuId) throws DAOException;
	
	List<Component> retrieveComponentsByEmuId(Integer emuId) throws DAOException;

}

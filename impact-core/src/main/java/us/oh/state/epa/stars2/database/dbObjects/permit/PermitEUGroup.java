package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

public class PermitEUGroup extends BaseDB {

    private static final String INDIVIDUAL_EU = "0";
    private static final String INSIGNIFICANT_EU = "Insignificant EUs";
    private Integer _permitEUGroupID;
    private Integer _permitID;
    private String _name;
    private List<PermitEU> _permitEUs = new ArrayList<PermitEU>(0);

    /**
     * 
     */
    public PermitEUGroup() {
        super();
        validationMessages.put("permitID", 
                               new ValidationMessage("Permit ID",
                                                     "Missing value.",
                                                     ValidationMessage.Severity.WARNING));
        validationMessages.put("permitEUs", 
                               new ValidationMessage("Permit Emission Units", "EU Group " 
                                                     + _name + " (ID = "
                                                     + _permitEUGroupID 
                                                     + ") has no emission units.",
                                                     ValidationMessage.Severity.WARNING));
        setDirty(false);
    }

    /**
     * @param old
     */
    public PermitEUGroup(PermitEUGroup old) {
        super(old);
        if (old != null) {
            setPermitEUGroupID(old.getPermitEUGroupID());
            setPermitEUs(old.getPermitEUs());
            setName(old.getName());
            setPermitID(old.getPermitID());
            setLastModified(old.getLastModified());
            setDirty(old.isDirty());
        }
    }

    public void populate(ResultSet rs) {

        try {
            setPermitID(AbstractDAO.getInteger(rs, "permit_id"));
            setPermitEUGroupID(AbstractDAO.getInteger(rs, "permit_eu_group_id"));
            setName(rs.getString("name"));
            setLastModified(AbstractDAO.getInteger(rs, "peg_lm"));
        } 
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }

        try {
            Integer permitEuID = AbstractDAO.getInteger(rs, "permit_eu_id");
            if (permitEuID != null) {
                do {
                    PermitEU tempEu = new PermitEU();
                    tempEu.populate(rs);
                    _permitEUs.add(tempEu);
                } while (rs.next());
            }
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.debug("Optional field error: " + sqle.getMessage());
        }

    }

    public final Integer getPermitID() {
        return _permitID;
    }

    public final void setPermitID(Integer permitID) {

        _permitID = permitID;
        if (_permitID == null) {
            validationMessages.put("permitID", 
                                   new ValidationMessage("Permit ID", "Missing value.",
                                                         ValidationMessage.Severity.WARNING));
        } 
        else {
            validationMessages.remove("permitID");
        }
        setDirty(true);
    }

    public final Integer getPermitEUGroupID() {
        return _permitEUGroupID;
    }

    public final void setPermitEUGroupID(Integer permitEUGroupID) {
        _permitEUGroupID = permitEUGroupID;
        if (_permitEUs.isEmpty() && !(getName() != null && getName().equalsIgnoreCase(INSIGNIFICANT_EU))) {
            validationMessages.put("permitEUs",
                                   new ValidationMessage("Permit Emission Units", "EU Group " 
                                                         + _name + " (ID = "
                                                         + _permitEUGroupID 
                                                         + ") has no emission units.",
                                                         ValidationMessage.Severity.WARNING));
        } 
        else {
            validationMessages.remove("permitEUs");
        }
//      2503
        if (INDIVIDUAL_EU.equals(_name))
            validationMessages.remove("permitEUs");
        setDirty(true);
    }

    public final String getName() {
        return _name;
    }

    public final void setName(String name) {
        _name = name;
        if (_permitEUs.isEmpty() && !(getName() != null && getName().equalsIgnoreCase(INSIGNIFICANT_EU))) {
            validationMessages.put("permitEUs",
                                   new ValidationMessage("Permit Emission Units", "EU Group " 
                                                         + _name + " (ID = "
                                                         + _permitEUGroupID 
                                                         + ") has no emission units.",
                                                         ValidationMessage.Severity.WARNING));
        } 
        else {
            validationMessages.remove("permitEUs");
        }
        
        // 2503
        if (INDIVIDUAL_EU.equals(name))
            validationMessages.remove("permitEUs");
        setDirty(true);
    }

    public final List<PermitEU> getPermitEUs() {
        if (_permitEUs.isEmpty() && !(getName() != null && getName().equalsIgnoreCase(INSIGNIFICANT_EU))) {
            validationMessages.put("permitEUs",
                                   new ValidationMessage("Permit Emission Units", "EU Group " 
                                                         + _name + " (ID = "
                                                         + _permitEUGroupID 
                                                         + ") has no emission units.",
                                                         ValidationMessage.Severity.WARNING));
        } 
        else {
            validationMessages.remove("permitEUs");
        }

        // 2503
        if (INDIVIDUAL_EU.equals(_name))
            validationMessages.remove("permitEUs");
        return _permitEUs;
    }

    public final void setPermitEUs(List<PermitEU> permitEUs) {

        _permitEUs = permitEUs;
        if (_permitEUs == null) {
            _permitEUs = new ArrayList<PermitEU>();
        }
        if (_permitEUs.isEmpty() && !(getName() != null && getName().equalsIgnoreCase(INSIGNIFICANT_EU))) {
            validationMessages.put("permitEUs",
                                   new ValidationMessage("Permit Emission Units", "EU Group " 
                                                         + _name + " (ID = "
                                                         + _permitEUGroupID 
                                                         + ") has no emission units.",
                                                         ValidationMessage.Severity.WARNING));
        } 
        else {
            validationMessages.remove("permitEUs");
        }
//      2503
        if (INDIVIDUAL_EU.equals(_name))
            validationMessages.remove("permitEUs");
        setDirty(true);
    }

    public final void addPermitEU(PermitEU permitEU) {

        if (_permitEUs == null) {
            _permitEUs = new ArrayList<PermitEU>();
        }
        if (permitEU != null) {
            _permitEUs.add(permitEU);
        }
        if (_permitEUs.isEmpty() && !(getName() != null && getName().equalsIgnoreCase(INSIGNIFICANT_EU))) {
            validationMessages.put("permitEUs", 
                                   new ValidationMessage("Permit Emission Units", "EU Group " 
                                                         + _name + " (ID = "
                                                         + _permitEUGroupID 
                                                         + ") has no emission units.",
                                                         ValidationMessage.Severity.WARNING));
        } 
        else {
            validationMessages.remove("permitEUs");
        }
        setDirty(true);
    }

    /**
     * @return
     */
    public final boolean isIndividualEUGroup() {
        if (getName() != null) {
            return getName().equalsIgnoreCase(INDIVIDUAL_EU);
        }
        return false;
    }

    public boolean isInsignificantEuGroup() {
        if (getName() != null && getName().equalsIgnoreCase(INSIGNIFICANT_EU)) {
            validationMessages.remove("permitEUs");
            return getName().equalsIgnoreCase(INSIGNIFICANT_EU);
        }
        return false;
    }

    /**
     * @return
     */
    public final void setIndividualEUGroup() {
        setName(INDIVIDUAL_EU);
    }
    
    /**
     * @return
     */
    public final void setInsignificantEuGroup() {
        setName(INSIGNIFICANT_EU);
        validationMessages.remove("permitEUs");
    }

    @Override
        public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();

        result = PRIME * result
            + ((_permitID == null) ? 0 : _permitID.hashCode());
        result = PRIME * result
            + ((_permitEUGroupID == null) ? 0 : _permitEUGroupID.hashCode());
        result = PRIME * result + ((_name == null) ? 0 : _name.hashCode());

        for (PermitEU permitEU : _permitEUs) {
            result = PRIME * result + permitEU.hashCode();
        }

        return result;
    }

    @Override
        public final boolean equals(Object obj) {

        if ((obj == null) || !(super.equals(obj))
            || (getClass() != obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final PermitEUGroup other = (PermitEUGroup) obj;

        // Either both null or equal values.
        if (((_permitEUGroupID == null) && (other.getPermitEUGroupID() != null))
            || ((_permitEUGroupID != null) && (other.getPermitEUGroupID() == null))
            || ((_permitEUGroupID != null)
                && (other.getPermitEUGroupID() != null) 
                && !(_permitEUGroupID.equals(other.getPermitEUGroupID())))) {
                                                             
            return false;
        }

        // Either both null or equal values.
        if (((_permitID == null) && (other.getPermitID() != null))
            || ((_permitID != null) && (other.getPermitID() == null))
            || ((_permitID != null) && (other.getPermitID() != null) 
                && !(_permitID.equals(other.getPermitID())))) {
                                                                          
            return false;
        }

        // Either both null or equal values.
        if (((_name == null) && (other.getName() != null))
            || ((_name != null) && (other.getName() == null))
            || ((_name != null) && (other.getName() != null) 
                && !(_name.equals(other.getName())))) {
                                                                  
            return false;
        }

        // Each of our EUs must have a corresponding EU in theirs and vice
        // versa.
        PermitEU[] ours = _permitEUs.toArray(new PermitEU[0]);
        PermitEU[] theirs = other.getPermitEUs().toArray(new PermitEU[0]);
        for (int i = 0; i < ours.length; i++) {
            boolean found = false;
            for (int j = 0; j < theirs.length; j++) {
                if (theirs[j] == null) {
                    continue;
                }
                if (ours[i].equals(theirs[j])) {
                    found = true;
                    theirs[j] = null;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        for (int j = 0; j < theirs.length; j++) {
            if (theirs[j] != null) {
                return false;
            }
        }
        return true;

    } // END: public final boolean equals(Object obj)


}

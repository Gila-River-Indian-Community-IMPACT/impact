package us.oh.state.epa.stars2.database.dbObjects.permit;

import us.oh.state.epa.stars2.def.PermitTypeDef;

public class RPRPermit extends Permit {

    /**
     * 
     */
    public RPRPermit() {
        super();
        setPermitType(PermitTypeDef.RPR);
        setDirty(false);
    }
}

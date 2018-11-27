package us.oh.state.epa.stars2.database.dbObjects.permit;
/*
import java.util.ArrayList;

import us.oh.state.epa.stars2.def.PermitTypeDef;

public class RPEPermit extends Permit {

    private ArrayList<PermitEU> withFeeEus;
    private Double totalAmount;
    
    
    
    
    public RPEPermit() {
        super();
        setPermitType(PermitTypeDef.RPE);
        setDirty(false);
    }
    
    public final void createEUFees(String maxRPEFee) {

        withFeeEus = new ArrayList<PermitEU>();
        totalAmount = new Double(0);

        for (PermitEUGroup eg : getEuGroups()) {
            for (PermitEU e : eg.getPermitEUs())
                if (e.getEuFee().getEUFeeId() != null) {
                    withFeeEus.add(e);
                    totalAmount += e.getEuFee().getAdjustedAmount();
                }
        }
        
        // Get adjustment from app. AQD said it is not needed to have it.
        //Float oa = ((RPERequest)app).getOtherAdjustment();
        //totalAmount = totalAmount + oa;
        
        // If total is over 200 (system setting)
        Double maxFee = new Double(200);
        try{
            maxFee = Double.parseDouble(maxRPEFee);
        }catch(NumberFormatException e){
            logger.error(e.getMessage(), e);
        }
        
        if (totalAmount.compareTo(maxFee) > 0)
            totalAmount = maxFee;
    }

    public final Double getTotalAmount() {
        return totalAmount;
    }

    public final ArrayList<PermitEU> getWithFeeEus() {
        return withFeeEus;
    }
}
*/

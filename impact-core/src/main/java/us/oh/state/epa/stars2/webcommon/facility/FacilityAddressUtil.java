package us.oh.state.epa.stars2.webcommon.facility;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityIdRef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.bo.FacilityService;

public class FacilityAddressUtil extends AppBase {
    //private HashMap<String, FacilityIdRef> countyFacilityIdRefs;
    //private LinkedHashMap<String, String> cityCodes;    
    private Address phyAddr;
    //private String cityName;
    //private FacilityService facilityService;
    
    public FacilityAddressUtil() {
    }
    
   /* public FacilityAddressUtil(Address phyAddr) {
        this.phyAddr = phyAddr;
        cityName = null;
        //getCityCodes();
    }
    
    public final String getcityName() {
        return cityName;
    }*/
    
    public final void setPhyAddr(String cityName) {
        //this.cityName = cityName;
    }
    
    public final Address getPhyAddr() {
        return phyAddr;
    }
    
    public final void setPhyAddr(Address phyAddr) {
        this.phyAddr = phyAddr;
    }
    
   /* public final LinkedHashMap<String, String> getCityCodes() {
        if (cityName != null) {
            return cityCodes;
        }
        
        FacilityIdRef[] facilityIdRefs;
        cityCodes = new LinkedHashMap<String, String>();
        
        if (phyAddr.getCountyCd() == null) {
            return cityCodes;
        }
        
        try {
            facilityIdRefs = facilityService.retrieveFacilityIdRefs(phyAddr.getCountyCd());
            countyFacilityIdRefs = new HashMap<String, FacilityIdRef>();
            
            for (FacilityIdRef temp : facilityIdRefs) {
                cityCodes.put(temp.getCityName(), temp.getCityName());
                countyFacilityIdRefs.put(temp.getCityName(), temp);
                if (phyAddr.getCityName()!= null && temp.getCityName() != null && 
                        phyAddr.getCityName().replace("(inactive)", "").equalsIgnoreCase(temp.getCityName().replace("(inactive)", ""))) {
                    cityName = temp.getCityName();
                    phyAddr.setCityName(cityName);
                }
            }
        } catch (RemoteException re) {
        	handleException(re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }
        
        return cityCodes;
    }*/

    public final String getCountyCd() {
        return phyAddr.getCountyCd();
    }

    public final void setCountyCd(String countyCd) {
        phyAddr.setCountyCd(countyCd);
        if (countyCd != null && countyCd.equals(State.OUT_STATE_COUNTY)) {
            phyAddr.setCityName(State.OUT_STATE_CITY);
        } else {
            phyAddr.setCityName(null);
        }
        //cityName = null;
        //getCityCodes();
    }

    public final String getState() {
        return phyAddr.getState();
    }

    public final void setState(String state) {
        phyAddr.setState(state);
        if (!state.equals(State.DEFAULT_STATE)) {
            setCountyCd(State.OUT_STATE_COUNTY);            
        } else {
            setCountyCd(null);
        }
    }

    /*public final FacilityIdRef getCityFacilityIdRef() {
        return countyFacilityIdRefs.get(cityName);
    }*/

    public final String getCityName() {
        return phyAddr.getCityName();
    }

    public final void setCityName(String cityName) {
       // this.cityName = cityName;
        phyAddr.setCityName(cityName);
    }
    
    public final LinkedHashMap<String, String> getCounties() {
        InfrastructureDefs  infraDefs;
        LinkedHashMap<String, String> counties;
        infraDefs = (InfrastructureDefs) FacesUtil.getManagedBean("infraDefs");
        counties = infraDefs.getCounties();
	    if (phyAddr.getState().equals(State.DEFAULT_STATE)) {
            counties.remove(State.OUT_STATE_COUNTY_NAME);
        }
        return counties;
    }
    
    protected void finalize() throws Throwable {
        logger = null;

     /*   if (countyFacilityIdRefs != null) {
            Iterator<String> it = countyFacilityIdRefs.keySet().iterator();
            while(it.hasNext()) {
                it.next();
                it.remove();
            }
//            for (String tempStr : countyFacilityIdRefs.keySet()) {
//                countyFacilityIdRefs.remove(tempStr);
//            }
            countyFacilityIdRefs = null;
        }

        if (cityCodes != null) {
            Iterator<String> it = cityCodes.keySet().iterator();
            while(it.hasNext()) {
                it.next();
                it.remove();
            }
//            for (String tempStr : cityCodes.keySet()) {
//                cityCodes.remove(tempStr);
//            }
            cityCodes = null;
        }
*/
        phyAddr = null;
        //cityName = null;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}

package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/* Control Equipment Operating Status */

public class ContEquipTypeDef {
    public static final String ADS = "ADS"; /* Carbon Absorber */
    public static final String CON = "CON"; /* Condenser */
    public static final String CAI = "CAI"; /* Catalytic Incinerator */
    public static final String CYC = "CYC"; /* Cyclones/MultiClones */
    public static final String ESP = "ESP"; /* Electrostatic Preci. */
    public static final String BAG = "BAG"; /* Filter/Baghouse */
    public static final String DSC = "DSC"; /* Dry Scrubber */
    public static final String WSC = "WSC"; /* Wet Scrubber */
    public static final String FLA = "FLA"; /* Flare */
    public static final String PAF = "PAF"; /* Passive Filter */
    public static final String TIN = "TIN"; /* Thermal Incinerator */
    public static final String FDS = "FDS"; /* Fugitive Dust Suppression */
    public static final String OXI = "OXI"; /* Oxidation Catalyst */
    public static final String CNC = "CNC"; /* NOx Reduction Technology */
    public static final String SEC = "SEC"; /* Setting Chamber */
    public static final String OTH = "OTH"; /* Other */
    public static final String OTHERS = "Other"; /* Other sub Type */
    public static final String OXTY = "OxTy"; /* Oxidation Catalyst Enum*/
    public static final Integer IN_GAS_FLOW_RATE = 390; /*
                                                         * Inlet gas flow rate
                                                         * detail ID
                                                         */
    public static final Integer OUT_GAS_FLOW_RATE = 391; /*
                                                             * Outlet gas flow
                                                             * rate detail ID
                                                             */
    public static final Integer IN_GAS_TEMP = 392; /*
                                                     * Inlet gas temperature
                                                     * detail ID
                                                     */
    public static final Integer OUT_GAS_TEMP = 393; /*
                                                     * Outlet gas temperature
                                                     * detail ID
                                                     */

    /* Carbon Absorber */
    public static final String CA_COCENTRAT_TYPE = "Concentrator"; /*
                                                                     * Concentrator
                                                                     * subtype
                                                                     */
    public static final Integer CA_TYPE_DD_ID = 289; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer CA_OTHERS_DD_ID = 290; /*
                                                         * Other field Data
                                                         * Detail ID
                                                         */
    public static final Integer[] CA_CONCEN_DD_IDS = { 295, /*
                                                             * Design Recycle
                                                             * field Data Detail
                                                             * ID
                                                             */
    296, /* Min desorption field Data Detail ID */
    297 /* Rotational rate */
    };
    public static final Integer[] CA_OTHER_CONCEN_DD_IDS = { 291, /*
                                                                     * Max.
                                                                     * Outlet OC
                                                                     * Concentration
                                                                     * field
                                                                     * Data
                                                                     * Detail ID
                                                                     */
    292, /* Adsorption media field Data Detail ID */
    293, /* Media Replacement Frequency field Data Detail ID */
    294, /* Max. Media Bed Temp. after Regeneration field Data Detail ID */
    400 /* Max. Inlet Concentration (ppmv)  field Data Detail ID */

    };

    /* Condenser */
    public static final String CD_FREEBOARD_TYPE = "FreeboardRefrigerationDevice"; /*
                                                                                     * Freeboard
                                                                                     * Refrigeration
                                                                                     * Device
                                                                                     */
    public static final Integer CD_TYPE_DD_ID = 279; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer CD_OTHERS_DD_ID = 280; /*
                                                         * Other field Data
                                                         * Detail ID
                                                         */
    public static final Integer[] CD_MAX_EXH_GAS_TEMP_DD_IDS ={ 284, /*
                                                                     * Max
                                                                     * Exhaust
                                                                     * Gas Temp
                                                                     */
    	401 /* Operating Pressure (psia) field Data Detail ID */
    };

    /* Flare */
    public static final String FR_ELEVATEDOPEN_TYPE = "ElevatedOpen"; /*
                                                                         * Freeboard
                                                                         * Refrigeration
                                                                         * Device
                                                                         */
    public static final String FR_PRES_SENSOR_YES = "Yes"; /* Flame Presence Sensor is 'Yes' */
    
    public static final Integer FR_TYPE_DD_ID = 269; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer FR_OTHERS_DD_ID = 271; /*
                                                         * Other field Data
                                                         * Detail ID
                                                         */
    public static final Integer FR_ELEV_OPEN_DD_ID = 270; /*
                                                             * Elevated Flare
                                                             * Type Data Detaiol
                                                             * ID
                                                             */
    
    public static final Integer FR_FLAME_PRES_SENSOR_DD_ID = 273 ; /* Flame Presence Sensor Data Detaiol ID */
    
    public static final Integer FR_FLAME_PRES_TYPE_DD_ID = 404; /* Flame Presence Type Data Detail ID */
    
    /* Electrostatic Preci. */
    public static final Integer EP_TYPE_DD_ID = 229; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer EP_OTHERS_DD_ID = 230; /*
                                                         * Other field Data
                                                         * Detail ID
                                                         */

    /* Filter/Baghouse */
    public static final Integer FB_TYPE_DD_ID = 209; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer FB_OTHERS_DD_ID = 210; /*
                                                         * Other field Data
                                                         * Detail ID
                                                         */
    public static final Integer FB_AGENT_DD_ID = 214; /*
                                                         * Lime Injection/fabric
                                                         * agent Data Detail ID
                                                         */
    public static final Integer[] FB_AGENT_DD_IDS = { 215, /*
                                                             * Lime
                                                             * Injection/Fabric
                                                             * Coating Type
                                                             * Detail ID
                                                             */
    216 /* Lime injection/fabric coating rate Data Detail ID */

    };

    /* Cyclones/MultiClones */
    public static final Integer CM_TYPE_DD_ID = 200; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer CM_OTHERS_DD_ID = 201; /*
                                                         * Other field Data
                                                         * Detail ID
                                                         */

    /* Wet Scrubber */
    public static final Integer WS_TYPE_DD_ID = 218; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer WS_OTHERS_DD_ID = 219; /*
                                                         * Other field Data
                                                         * Detail ID
                                                         */

    /* Passive Filter */
    public static final String PB_PAINT_BOOTH_TYPE = "PaintBoothFilter"; /*
                                                                             * Paint
                                                                             * Booth
                                                                             * Type
                                                                             */
    public static final Integer PB_TYPE_DD_ID = 309; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer PB_CHG_FRQ_DD_ID = 311; /*
                                                         * Change Frequency Data
                                                         * Detail ID
                                                         */
    public static final Integer[] PB_OTHERS_DD_IDS = { 310, /*
                                                             * Description
                                                             * Detail ID
                                                             */
    311 /* Change Frequency Data Detail ID */

    };

    /* Fugitive Dust Suppression */
    public static final Integer FS_TYPE_DD_ID = 319; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer FS_OTHERS_DD_ID = 320; /*
                                                         * Other field Data
                                                         * Detail ID
                                                         */

    /* NOx Reduction Technology */
    public static final String CR_NONSETECT_TYPE = "NonselectiveCatalytic"; /*
                                                                             * Nonselective
                                                                             * Catalytic
                                                                             * Type
                                                                             */
    public static final Integer CR_TYPE_DD_ID = 330; /*
                                                         * Type field Data
                                                         * Detail ID
                                                         */
    public static final Integer[] CR_SELECT_DD_IDS = { 331, /*
                                                             * Reagent Type
                                                             * Detail ID
                                                             */
    332, /* Reagent Injection Rate Data Detail ID */
    333, /* Reagent Slip Conc. Data Detail ID */
    334, /* At percent oxygen. Data Detail ID */
    390, /* Inlet gas flow rate Data Detail ID */
    392, /* Inlet gas temperature Data Detail ID */
    393 /* Outlet gas temperature Data Detail ID */
    };
    public static final Integer[] CR_NONSELECT_DD_IDS = { 390, /*
                                                                 * Inlet gas
                                                                 * flow rate
                                                                 * Data Detail
                                                                 * ID
                                                                 */
    392, /* Inlet gas temperature Data Detail ID */
    393 /* Outlet gas temperature Data Detail ID */
    };
    private static final String defName = "ContEquipTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_equipment_type", "equipment_type_cd",
                    "equipment_type_dsc", "DEPRECATED", "equipment_type_dsc");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
